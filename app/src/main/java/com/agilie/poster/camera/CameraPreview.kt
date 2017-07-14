package com.agilie.poster.camera

import android.content.Context
import android.hardware.Camera
import android.view.*
import java.io.IOException

class CameraPreview : SurfaceView, SurfaceHolder.Callback {

	var camera: Camera?
	private val mCameraView: View?
	private var mPreviewSize: Camera.Size? = null
	private lateinit var mSupportedPreviewSizes: List<Camera.Size>
	private var mSupportedFlashModes: List<String>? = null

	constructor(context: Context, camera: Camera?, cameraView: View?) : super(context) {

		this.camera = camera
		mCameraView = cameraView
		setCamera()
		holder.addCallback(this)
		holder.setKeepScreenOn(true)
		// deprecated setting, but required on Android versions prior to 3.0
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
	}

	override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
		if (holder?.surface == null) {
			return
		}
		try {
			val parameters = camera?.parameters

			parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

			mPreviewSize?.let {
				parameters?.setPreviewSize(it.width, it.height)
			}

			camera?.parameters = parameters
			setCameraDisplayOrientation()

			camera?.startPreview()

		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	override fun surfaceDestroyed(holder: SurfaceHolder?) {
		camera?.let {
			it.stopPreview()
		}
	}

	override fun surfaceCreated(holder: SurfaceHolder?) {
		try {
			camera?.setPreviewDisplay(holder)
		} catch (e: IOException) {
			e.printStackTrace()
		}
	}

	/**	 * Update the layout based on rotation and orientation changes.	 */
	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		if (changed) {
			val width = right - left
			val height = bottom - top

			var previewWidth = width
			var previewHeight = height

			if (mPreviewSize != null) {
				val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

				/*when (display.rotation) {
					Surface.ROTATION_0 -> {
						previewWidth = mPreviewSize!!.height
						previewHeight = mPreviewSize!!.width
						camera?.setDisplayOrientation(90)
						Log.d("TAG", "ROTATION_0" )
					}
					Surface.ROTATION_90 -> {
						previewWidth = mPreviewSize!!.width
						previewHeight = mPreviewSize!!.height
						Log.d("TAG", "ROTATION_90" )
					}
					Surface.ROTATION_180 -> {
						previewWidth = mPreviewSize!!.height
						previewHeight = mPreviewSize!!.width
						Log.d("TAG", "ROTATION_180" )
					}
					Surface.ROTATION_270 -> {
						previewWidth = mPreviewSize!!.width
						previewHeight = mPreviewSize!!.height
						Log.d("TAG", "ROTATION_270" )
						camera?.setDisplayOrientation(180)
					}
				}*/
			}
			val scaledChildHeight = previewHeight * width / previewWidth
			mCameraView?.layout(0, height - scaledChildHeight, width, height)
		}
	}

	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
		val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
		setMeasuredDimension(width, height)

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height)
		}
	}

	private fun setCameraDisplayOrientation() {
		val camInfo = Camera.CameraInfo()
		Camera.getCameraInfo(getBackFacingCameraId(), camInfo)

		val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
		val rotation = display.rotation
		var degrees = 0
		when (rotation) {
			Surface.ROTATION_0 -> degrees = 0
			Surface.ROTATION_90 -> degrees = 90
			Surface.ROTATION_180 -> degrees = 180
			Surface.ROTATION_270 -> degrees = 270
		}

		var result: Int
		if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (camInfo.orientation + degrees) % 360
			result = (360 - result) % 360  // compensate the mirror
		} else {  // back-facing
			result = (camInfo.orientation - degrees + 360) % 360
		}
		camera?.setDisplayOrientation(result)
	}

	private fun getBackFacingCameraId(): Int {
		var cameraId = -1
		// Search for the front facing camera
		val numberOfCameras = Camera.getNumberOfCameras()
		for (i in 0..numberOfCameras - 1) {
			val info = Camera.CameraInfo()
			Camera.getCameraInfo(i, info)
			if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {

				cameraId = i
				break
			}
		}
		return cameraId
	}

	/** Begin the preview of the camera input. */
	fun startCameraPreview() {
		try {
			camera?.setPreviewDisplay(holder)
			camera?.startPreview()
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

	/** Extract supported preview and flash modes from the camera.*/
	fun setCamera() {
		mSupportedPreviewSizes = camera?.parameters!!.supportedPreviewSizes
		mSupportedFlashModes = camera?.parameters!!.supportedFlashModes

		// Set the camera to Auto Flash mode.
		if (mSupportedFlashModes != null && mSupportedFlashModes!!.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
			val parameters = this.camera?.parameters
			parameters?.flashMode = Camera.Parameters.FLASH_MODE_AUTO
			this.camera?.parameters = parameters
		}
		requestLayout()
	}

	private fun getOptimalPreviewSize(sizes: List<Camera.Size>, width: Int, height: Int): Camera.Size? {
		var optimalSize: Camera.Size? = null

		val ASPECT_TOLERANCE = 0.1
		val targetRatio = height.toDouble() / width
		// Try to find a size match which suits the whole screen minus the menu on the left.
		for (size in sizes) {

			if (size.height != width) continue
			val ratio = size.width.toDouble() / size.height
			if (ratio <= targetRatio + ASPECT_TOLERANCE && ratio >= targetRatio - ASPECT_TOLERANCE) {
				optimalSize = size
			}
		}
		// If we cannot find the one that matches the aspect ratio, ignore the requirement.
		if (optimalSize == null) {
			// TODO : Backup in case we don't get a size.
		}

		return optimalSize
	}

}