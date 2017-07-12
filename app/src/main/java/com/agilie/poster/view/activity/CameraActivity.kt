package com.agilie.poster.view.activity

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.camera2.*
import android.media.Image
import android.media.ImageReader
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.util.Size
import android.util.SparseIntArray
import android.view.Surface
import android.view.TextureView
import android.widget.Toast
import com.agilie.poster.R
import kotlinx.android.synthetic.main.activity_camera.*
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class CameraActivity : BaseActivity(), EasyPermissions.PermissionCallbacks {


	/**
	 * Conversion from screen rotation to JPEG orientation.
	 */

	private val ORIENTATIONS = SparseIntArray().apply {
		append(Surface.ROTATION_0, 90)
		append(Surface.ROTATION_90, 0)
		append(Surface.ROTATION_180, 270)
		append(Surface.ROTATION_270, 180)
	}

	private enum class FlashMode {
		AUTO, ON, OFF
	}

	private var flashMode = FlashMode.AUTO

	private
	val REQUEST_CAMERA_PERMISSION = 1
	private val FRAGMENT_DIALOG = "dialog"
	private val TAG = "Camera2BasicFragment"

	/**
	 * Camera state: Showing camera preview.
	 */
	private val STATE_PREVIEW = 0

	/**
	 * Camera state: Waiting for the focus to be locked.
	 */
	private val STATE_WAITING_LOCK = 1

	/**
	 * Camera state: Waiting for the exposure to be precapture state.
	 */
	private val STATE_WAITING_PRECAPTURE = 2

	/**
	 * Camera state: Waiting for the exposure state to be something other than precapture.
	 */
	private val STATE_WAITING_NON_PRECAPTURE = 3

	/**
	 * Camera state: Picture was taken.
	 */
	private val STATE_PICTURE_TAKEN = 4

	/**
	 * Max preview width that is guaranteed by Camera2 API
	 */
	private val MAX_PREVIEW_WIDTH = 1920

	/**
	 * Max preview height that is guaranteed by Camera2 API
	 */
	private val MAX_PREVIEW_HEIGHT = 1080


	/**
	 * [TextureView.SurfaceTextureListener] handles several lifecycle events on a
	 * [TextureView].
	 */
	private val mSurfaceTextureListener = object : TextureView.SurfaceTextureListener {

		override fun onSurfaceTextureAvailable(texture: SurfaceTexture, width: Int, height: Int) {
			openCamera(width, height)
		}

		override fun onSurfaceTextureSizeChanged(texture: SurfaceTexture, width: Int, height: Int) {
			configureTransform(width, height)
		}

		override fun onSurfaceTextureDestroyed(texture: SurfaceTexture): Boolean {
			return true
		}

		override fun onSurfaceTextureUpdated(texture: SurfaceTexture) {}

	}

	/**
	 * ID of the current [CameraDevice].
	 */
	private var mCameraId: String? = null

	/**
	 * An [AutoFitTextureView] for camera preview.
	 */

	/**
	 * A [CameraCaptureSession] for camera preview.
	 */
	private var mCaptureSession: CameraCaptureSession? = null

	/**
	 * A reference to the opened [CameraDevice].
	 */
	private var mCameraDevice: CameraDevice? = null

	/**
	 * The [android.util.Size] of camera preview.
	 */
	private var mPreviewSize: Size? = null

	/**
	 * [CameraDevice.StateCallback] is called when [CameraDevice] changes its state.
	 */
	private val mStateCallback = object : CameraDevice.StateCallback() {

		override fun onOpened(cameraDevice: CameraDevice) {
			// This method is called when the camera is opened.  We start camera preview here.
			mCameraOpenCloseLock.release()
			mCameraDevice = cameraDevice
			createCameraPreviewSession()
		}

		override fun onDisconnected(cameraDevice: CameraDevice) {
			mCameraOpenCloseLock.release()
			cameraDevice.close()
			mCameraDevice = null
		}

		override fun onError(cameraDevice: CameraDevice, error: Int) {
			mCameraOpenCloseLock.release()
			cameraDevice.close()
			mCameraDevice = null
			this@CameraActivity.finish()
		}

	}

	/**
	 * An additional thread for running tasks that shouldn't block the UI.
	 */
	private var mBackgroundThread: HandlerThread? = null

	/**
	 * A [Handler] for running tasks in the background.
	 */
	private var mBackgroundHandler: Handler? = null

	/**
	 * An [ImageReader] that handles still image capture.
	 */
	private var mImageReader: ImageReader? = null

	/**
	 * This is the output file for our picture.
	 */
	private var mFile: File? = null

	/**
	 * This a callback object for the [ImageReader]. "onImageAvailable" will be called when a
	 * still image is ready to be saved.
	 */
	private val mOnImageAvailableListener = ImageReader.OnImageAvailableListener {
		reader ->
		mBackgroundHandler!!.post(ImageSaver(reader.acquireNextImage(), mFile))
	}

	/**
	 * [CaptureRequest.Builder] for the camera preview
	 */
	private var mPreviewRequestBuilder: CaptureRequest.Builder? = null

	/**
	 * [CaptureRequest] generated by [.mPreviewRequestBuilder]
	 */
	private var mPreviewRequest: CaptureRequest? = null

	/**
	 * The current state of camera state for taking pictures.

	 * @see .mCaptureCallback
	 */
	private var mState = STATE_PREVIEW

	/**
	 * A [Semaphore] to prevent the app from exiting before closing the camera.
	 */
	private val mCameraOpenCloseLock = Semaphore(1)

	/**
	 * Whether the current camera device supports Flash or not.
	 */
	private var mFlashSupported: Boolean = false

	/**
	 * Orientation of the camera sensor
	 */
	private var mSensorOrientation: Int = 0

	/**
	 * A [CameraCaptureSession.CaptureCallback] that handles events related to JPEG capture.
	 */
	private val mCaptureCallback = object : CameraCaptureSession.CaptureCallback() {

		private fun process(result: CaptureResult) {
			when (mState) {
				STATE_PREVIEW -> {
				}// We have nothing to do when the camera preview is working normally.
				STATE_WAITING_LOCK -> {
					val afState = result.get(CaptureResult.CONTROL_AF_STATE)
					if (afState == null) {
						captureStillPicture()
					} else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState || CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
						// CONTROL_AE_STATE can be null on some devices
						val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
						if (aeState == null || aeState === CaptureResult.CONTROL_AE_STATE_CONVERGED) {
							mState = STATE_PICTURE_TAKEN
							captureStillPicture()
						} else {
							runPrecaptureSequence()
						}
					}
				}
				STATE_WAITING_PRECAPTURE -> {
					// CONTROL_AE_STATE can be null on some devices
					val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
					if (aeState == null ||
							aeState === CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
							aeState === CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
						mState = STATE_WAITING_NON_PRECAPTURE
					}
				}
				STATE_WAITING_NON_PRECAPTURE -> {
					// CONTROL_AE_STATE can be null on some devices
					val aeState = result.get(CaptureResult.CONTROL_AE_STATE)
					if (aeState == null || aeState !== CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
						mState = STATE_PICTURE_TAKEN
						captureStillPicture()
					}
				}
			}
		}

		override fun onCaptureProgressed(session: CameraCaptureSession,
		                                 request: CaptureRequest,
		                                 partialResult: CaptureResult) {
			process(partialResult)
		}

		override fun onCaptureCompleted(session: CameraCaptureSession,
		                                request: CaptureRequest,
		                                result: TotalCaptureResult) {
			process(result)
		}

	}

	/**
	 * Given `choices` of `Size`s supported by a camera, choose the smallest one that
	 * is at least as large as the respective texture view size, and that is at most as large as the
	 * respective max size, and whose aspect ratio matches with the specified value. If such size
	 * doesn't exist, choose the largest one that is at most as large as the respective max size,
	 * and whose aspect ratio matches with the specified value.

	 * @param choices           The list of sizes that the camera supports for the intended output
	 * *                          class
	 * *
	 * @param textureViewWidth  The width of the texture view relative to sensor coordinate
	 * *
	 * @param textureViewHeight The height of the texture view relative to sensor coordinate
	 * *
	 * @param maxWidth          The maximum width that can be chosen
	 * *
	 * @param maxHeight         The maximum height that can be chosen
	 * *
	 * @param aspectRatio       The aspect ratio
	 * *
	 * @return The optimal `Size`, or an arbitrary one if none were big enough
	 */
	private fun chooseOptimalSize(choices: Array<Size>, textureViewWidth: Int,
	                              textureViewHeight: Int, maxWidth: Int, maxHeight: Int, aspectRatio: Size): Size {

		// Collect the supported resolutions that are at least as big as the preview Surface
		val bigEnough = ArrayList<Size>()
		// Collect the supported resolutions that are smaller than the preview Surface
		val notBigEnough = ArrayList<Size>()
		val w = aspectRatio.width
		val h = aspectRatio.height
		choices
				.filter { it.width <= maxWidth && it.height <= maxHeight && it.height == it.width * h / w }
				.forEach {
					if (it.width >= textureViewWidth && it.height >= textureViewHeight) {
						bigEnough.add(it)
					} else {
						notBigEnough.add(it)
					}
				}

		// Pick the smallest of those big enough. If there is no one big enough, pick the
		// largest of those not big enough.
		if (bigEnough.size > 0) {
			return Collections.min(bigEnough, CompareSizesByArea())
		} else if (notBigEnough.size > 0) {
			return Collections.max(notBigEnough, CompareSizesByArea())
		} else {
			Log.e(TAG, "Couldn't find any suitable preview size")
			return choices[0]
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)

		manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager

		button_snap.setOnClickListener { takePicture() }
		button_change_cam.setOnClickListener { changeCamera() }
		flash_off.setOnClickListener { flashMode = FlashMode.OFF }
		flash_on.setOnClickListener { flashMode = FlashMode.ON }
		flash_auto.setOnClickListener { flashMode = FlashMode.AUTO }

		mFile = File(getExternalFilesDir(null), "pic.jpg")
	}

	override fun onResume() {
		super.onResume()
		startBackgroundThread()
		// When the screen is turned off and turned back on, the SurfaceTexture is already
		// available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
		// a camera and start preview from here (otherwise, we wait until the surface is ready in
		// the SurfaceTextureListener).
		if (camera_preview.isAvailable) {
			openCamera(camera_preview.width, camera_preview.height)
		} else {
			camera_preview.surfaceTextureListener = mSurfaceTextureListener
		}
	}

	override fun onPause() {
		closeCamera()
		stopBackgroundThread()
		super.onPause()
	}

	private fun requestCameraPermission() {
		if (Build.VERSION.SDK_INT >= 23) {
			if (ContextCompat.checkSelfPermission(this,
					Manifest.permission.CAMERA)
					!= PackageManager.PERMISSION_GRANTED) {
				ActivityCompat.requestPermissions(this,
						arrayOf(Manifest.permission.CAMERA),
						REQUEST_CAMERA_PERMISSION)
			}
		}
	}

	override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>?) {

	}

	override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>?) {
	}

	private lateinit var manager: CameraManager

	/**
	 * Sets up member variables related to camera.

	 * @param width  The width of available size for camera preview
	 * *
	 * @param height The height of available size for camera preview
	 */
	private fun setUpCameraOutputs(width: Int, height: Int) {
		manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
		try {
			for (cameraId in manager.cameraIdList) {
				val characteristics = manager.getCameraCharacteristics(cameraId)

				// We don't use a front facing camera in this sample.
				val facing = characteristics.get(CameraCharacteristics.LENS_FACING)
				if (facing != null && facing === lensFacing) {
					continue
				}

				val map = characteristics.get(
						CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ?: continue

				// For still image captures, we use the largest available size.
				val largest = Collections.max(
						Arrays.asList(*map.getOutputSizes(ImageFormat.JPEG)),
						CompareSizesByArea())
				mImageReader = ImageReader.newInstance(largest.width, largest.height,
						ImageFormat.JPEG, /*maxImages*/2)
				mImageReader?.setOnImageAvailableListener(
						mOnImageAvailableListener, mBackgroundHandler)

				// Find out if we need to swap dimension to get the preview size relative to sensor
				// coordinate.
				val displayRotation = windowManager.defaultDisplay.rotation

				mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION)!!
				var swappedDimensions = false
				when (displayRotation) {
					Surface.ROTATION_0, Surface.ROTATION_180 -> if (mSensorOrientation == 90 || mSensorOrientation == 270) {
						swappedDimensions = true
					}
					Surface.ROTATION_90, Surface.ROTATION_270 -> if (mSensorOrientation == 0 || mSensorOrientation == 180) {
						swappedDimensions = true
					}
					else -> Log.e(TAG, "Display rotation is invalid: " + displayRotation)
				}

				val displaySize = Point()
				windowManager.defaultDisplay.getSize(displaySize)
				var rotatedPreviewWidth = width
				var rotatedPreviewHeight = height
				var maxPreviewWidth = displaySize.x
				var maxPreviewHeight = displaySize.y

				if (swappedDimensions) {
					rotatedPreviewWidth = height
					rotatedPreviewHeight = width
					maxPreviewWidth = displaySize.y
					maxPreviewHeight = displaySize.x
				}

				if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
					maxPreviewWidth = MAX_PREVIEW_WIDTH
				}

				if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
					maxPreviewHeight = MAX_PREVIEW_HEIGHT
				}

				// Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
				// bus' bandwidth limitation, resulting in gorgeous previews but the storage of
				// garbage capture data.
				mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture::class.java),
						rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
						maxPreviewHeight, largest)

				// We fit the aspect ratio of TextureView to the size of preview we picked.
				val orientation = resources.configuration.orientation

				// Check if the flash is supported.
				val available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE)
				mFlashSupported = available ?: false

				mCameraId = cameraId
				return
			}
		} catch (e: CameraAccessException) {
			e.printStackTrace()
		} catch (e: NullPointerException) {
			// Currently an NPE is thrown when the Camera2API is used but not supported on the
			// device this code runs.
			showToast("Camera2API is used but not supported on the device this code runs")
		}
	}

	private fun setCurrentFlash(requestBuilder: CaptureRequest.Builder?) {
		if (mFlashSupported) {
			when (flashMode) {
				FlashMode.AUTO -> requestBuilder?.set(CaptureRequest.CONTROL_AE_MODE,
						CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)
				FlashMode.OFF -> requestBuilder?.set(CaptureRequest.FLASH_MODE,
						CaptureRequest.FLASH_MODE_OFF)
				FlashMode.ON -> requestBuilder?.set(CaptureRequest.CONTROL_AE_MODE,
						CaptureRequest.CONTROL_AE_MODE_ON_ALWAYS_FLASH);
			}
		}
	}

	/**
	 * Opens the camera specified by [Camera2BasicFragment.mCameraId].
	 */
	private fun openCamera(width: Int, height: Int) {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
			requestCameraPermission()
			return
		}
		setUpCameraOutputs(width, height)
		configureTransform(width, height)
		//val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
		try {
			if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
				throw RuntimeException("Time out waiting to lock camera opening.")
			}
			manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler)
		} catch (e: CameraAccessException) {
			e.printStackTrace()
		} catch (e: InterruptedException) {
			throw RuntimeException("Interrupted while trying to lock camera opening.", e)
		}

	}

	/**
	 * Closes the current [CameraDevice].
	 */
	private fun closeCamera() {
		try {
			mCameraOpenCloseLock.acquire()

			if (null != mCaptureSession) {
				mCaptureSession?.close()
				mCaptureSession = null
			}
			if (null != mCameraDevice) {
				mCameraDevice?.close()
				mCameraDevice = null
			}
			if (null != mImageReader) {
				mImageReader?.close()
				mImageReader = null
			}
		} catch (e: InterruptedException) {
			throw RuntimeException("Interrupted while trying to lock camera closing.", e)
		} finally {
			mCameraOpenCloseLock.release()
		}
	}

	/**
	 * Starts a background thread and its [Handler].
	 */
	private fun startBackgroundThread() {
		mBackgroundThread = HandlerThread("CameraBackground")
		mBackgroundThread?.start()
		mBackgroundHandler = Handler(mBackgroundThread?.looper)
	}

	/**
	 * Stops the background thread and its [Handler].
	 */
	private fun stopBackgroundThread() {
		mBackgroundThread?.quitSafely()
		try {
			mBackgroundThread?.join()
			mBackgroundThread = null
			mBackgroundHandler = null
		} catch (e: InterruptedException) {
			e.printStackTrace()
		}

	}

	/**
	 * Creates a new [CameraCaptureSession] for camera preview.
	 */
	private fun createCameraPreviewSession() {
		try {
			val texture = camera_preview.surfaceTexture

			// We configure the size of default buffer to be the size of camera preview we want.
			texture.setDefaultBufferSize(mPreviewSize!!.width, mPreviewSize!!.height)

			// This is the output Surface we need to start preview.
			val surface = Surface(texture)

			// We set up a CaptureRequest.Builder with the output Surface.
			mPreviewRequestBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
			mPreviewRequestBuilder?.addTarget(surface)

			// Here, we create a CameraCaptureSession for camera preview.
			mCameraDevice?.createCaptureSession(Arrays.asList(surface, mImageReader?.surface),
					object : CameraCaptureSession.StateCallback() {

						override fun onConfigured(cameraCaptureSession: CameraCaptureSession) {
							// The camera is already closed
							if (null == mCameraDevice) {
								return
							}

							// When the session is ready, we start displaying the preview.
							mCaptureSession = cameraCaptureSession
							try {
								// Auto focus should be continuous for camera preview.
								mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_MODE,
										CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
								// Flash is automatically enabled when necessary.
								setAutoFlash(mPreviewRequestBuilder)

								// Finally, we start displaying the camera preview.
								mPreviewRequest = mPreviewRequestBuilder?.build()
								mCaptureSession?.setRepeatingRequest(mPreviewRequest,
										mCaptureCallback, mBackgroundHandler)
							} catch (e: CameraAccessException) {
								e.printStackTrace()
							}

						}

						override fun onConfigureFailed(
								cameraCaptureSession: CameraCaptureSession) {
							showToast("Failed")
						}
					}, null
			)
		} catch (e: CameraAccessException) {
			e.printStackTrace()
		}

	}

	private fun showToast(text: String) {
		this@CameraActivity.runOnUiThread({ Toast.makeText(this, text, Toast.LENGTH_SHORT).show() })
	}


	/**
	 * Configures the necessary [android.graphics.Matrix] transformation to `mTextureView`.
	 * This method should be called after the camera preview size is determined in
	 * setUpCameraOutputs and also the size of `mTextureView` is fixed.

	 * @param viewWidth  The width of `mTextureView`
	 * *
	 * @param viewHeight The height of `mTextureView`
	 */
	private fun configureTransform(viewWidth: Int, viewHeight: Int) {
		if (null == camera_preview || null == mPreviewSize) {
			return
		}
		val rotation = windowManager.defaultDisplay.rotation
		val matrix = Matrix()
		val viewRect = RectF(0f, 0f, viewWidth.toFloat(), viewHeight.toFloat())
		val bufferRect = RectF(0f, 0f, mPreviewSize?.height!!.toFloat(), mPreviewSize?.width!!.toFloat())
		val centerX = viewRect.centerX()
		val centerY = viewRect.centerY()
		if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
			bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY())
			matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL)
			val scale = Math.max(
					viewHeight.toFloat() / mPreviewSize!!.height,
					viewWidth.toFloat() / mPreviewSize!!.width)
			matrix.postScale(scale, scale, centerX, centerY)
			matrix.postRotate((90 * (rotation - 2)).toFloat(), centerX, centerY)
		} else if (Surface.ROTATION_180 == rotation) {
			matrix.postRotate(180f, centerX, centerY)
		}
		camera_preview.setTransform(matrix)
	}

	/**
	 * Initiate a still image capture.
	 */
	private fun takePicture() {
		lockFocus()
	}

	private var lensFacing = CameraCharacteristics.LENS_FACING_FRONT

	private fun changeCamera() {
		when (lensFacing) {
			CameraCharacteristics.LENS_FACING_BACK -> lensFacing = CameraCharacteristics.LENS_FACING_FRONT
			CameraCharacteristics.LENS_FACING_FRONT -> lensFacing = CameraCharacteristics.LENS_FACING_BACK
		}
		closeCamera()
		openCamera(camera_preview.width, camera_preview.height)
	}

	/**
	 * Lock the focus as the first step for a still image capture.
	 */
	private fun lockFocus() {
		try {
			// This is how to tell the camera to lock focus.
			mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER,
					CameraMetadata.CONTROL_AF_TRIGGER_START)
			// Tell #mCaptureCallback to wait for the lock.
			mState = STATE_WAITING_LOCK
			mCaptureSession?.capture(mPreviewRequestBuilder?.build(), mCaptureCallback,
					mBackgroundHandler)
		} catch (e: CameraAccessException) {
			e.printStackTrace()
		}

	}

	/**
	 * Run the precapture sequence for capturing a still image. This method should be called when
	 * we get a response in [.mCaptureCallback] from [.lockFocus].
	 */
	private fun runPrecaptureSequence() {
		try {
			//setCurrentFlash(mPreviewRequestBuilder)
			// This is how to tell the camera to trigger.
			mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
					CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START)
			// Tell #mCaptureCallback to wait for the precapture sequence to be set.
			mState = STATE_WAITING_PRECAPTURE
			mCaptureSession?.capture(mPreviewRequestBuilder?.build(), mCaptureCallback,
					mBackgroundHandler)
		} catch (e: CameraAccessException) {
			e.printStackTrace()
		}

	}

	/**
	 * Capture a still picture. This method should be called when we get a response in
	 * [.mCaptureCallback] from both [.lockFocus].
	 */
	private fun captureStillPicture() {
		try {
			if (null == mCameraDevice) {
				return
			}
			// This is the CaptureRequest.Builder that we use to take a picture.
			val captureBuilder = mCameraDevice?.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE)
			captureBuilder?.addTarget(mImageReader?.surface)

			// Use the same AE and AF modes as the preview.
			captureBuilder?.set(CaptureRequest.CONTROL_AF_MODE,
					CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE)
			setAutoFlash(captureBuilder)

			// Orientation
			val rotation = windowManager.defaultDisplay.rotation
			captureBuilder?.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation))

			val CaptureCallback = object : CameraCaptureSession.CaptureCallback() {

				override fun onCaptureCompleted(session: CameraCaptureSession,
				                                request: CaptureRequest,
				                                result: TotalCaptureResult) {
					showToast("Saved: " + mFile)
					Log.d(TAG, mFile.toString())
					unlockFocus()
				}
			}

			mCaptureSession?.stopRepeating()
			mCaptureSession?.capture(captureBuilder?.build(), CaptureCallback, null)
		} catch (e: CameraAccessException) {
			e.printStackTrace()
		}

	}

	/**
	 * Retrieves the JPEG orientation from the specified screen rotation.

	 * @param rotation The screen rotation.
	 * *
	 * @return The JPEG orientation (one of 0, 90, 270, and 360)
	 */
	private fun getOrientation(rotation: Int): Int {
		// Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
		// We have to take that into account and rotate JPEG properly.
		// For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
		// For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
		return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360
	}

	/**
	 * Unlock the focus. This method should be called when still image capture sequence is
	 * finished.
	 */
	private fun unlockFocus() {
		try {
			// Reset the auto-focus trigger
			mPreviewRequestBuilder?.set(CaptureRequest.CONTROL_AF_TRIGGER,
					CameraMetadata.CONTROL_AF_TRIGGER_CANCEL)
			setAutoFlash(mPreviewRequestBuilder)
			mCaptureSession?.capture(mPreviewRequestBuilder?.build(), mCaptureCallback,
					mBackgroundHandler)
			// After this, the camera will go back to the normal state of preview.
			mState = STATE_PREVIEW
			mCaptureSession?.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
					mBackgroundHandler)
		} catch (e: CameraAccessException) {
			e.printStackTrace()
		}

	}


	private fun setAutoFlash(requestBuilder: CaptureRequest.Builder?) {
		if (mFlashSupported) {
			setCurrentFlash(requestBuilder)
			/*requestBuilder?.set(CaptureRequest.CONTROL_AE_MODE,
					CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH)*/
		}
	}

	/**
	 * Saves a JPEG [Image] into the specified [File].
	 */
	private class ImageSaver(
			/**
			 * The JPEG image
			 */
			private val mImage: Image,
			/**
			 * The file we save the image into.
			 */
			private val mFile: File?) : Runnable {

		override fun run() {
			val buffer = mImage.planes[0].buffer
			val bytes = ByteArray(buffer.remaining())
			buffer.get(bytes)
			var output: FileOutputStream? = null
			try {
				output = FileOutputStream(mFile)
				output.write(bytes)
			} catch (e: IOException) {
				e.printStackTrace()
			} finally {
				mImage.close()
				if (null != output) {
					try {
						output.close()
					} catch (e: IOException) {
						e.printStackTrace()
					}

				}
			}
		}

	}

	/**
	 * Compares two `Size`s based on their areas.
	 */
	internal class CompareSizesByArea : Comparator<Size> {

		override fun compare(lhs: Size, rhs: Size): Int {
			// We cast here to ensure the multiplications won't overflow
			return java.lang.Long.signum(lhs.width.toLong() * lhs.height - rhs.width.toLong() * rhs.height)
		}

	}

	/**
	 * Shows an error message dialog.
	 */
	class ErrorDialog : DialogFragment() {

		override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
			val activity = activity
			return AlertDialog.Builder(activity)
					.setMessage(arguments.getString(ARG_MESSAGE))
					.setPositiveButton(android.R.string.ok) { dialogInterface, i -> activity.finish() }
					.create()
		}

		companion object {

			private val ARG_MESSAGE = "message"

			fun newInstance(message: String): ErrorDialog {
				val dialog = ErrorDialog()
				val args = Bundle()
				args.putString(ARG_MESSAGE, message)
				dialog.arguments = args
				return dialog
			}
		}

	}

	/**
	 * Shows OK/Cancel confirmation dialog about camera permission.
	 */
	/*class ConfirmationDialog : DialogFragment() {

		override fun onCreateDialog(savedInstanceState: Bundle): Dialog {
			val parent = parentFragment
			return AlertDialog.Builder(activity)
					.setMessage(R.string.request_permission)
					.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
						FragmentCompat.requestPermissions(parent,
								arrayOf(Manifest.permission.CAMERA),
								REQUEST_CAMERA_PERMISSION)
					})
					.setNegativeButton(android.R.string.cancel,
							DialogInterface.OnClickListener { dialog, which ->
								val activity = parent.activity
								activity?.finish()
							})
					.create()
		}
	}*/
}
