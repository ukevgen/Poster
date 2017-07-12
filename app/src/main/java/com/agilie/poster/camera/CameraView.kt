package com.agilie.poster.camera

import android.content.Context
import android.hardware.Camera
import android.view.*
import java.io.IOException

class CameraView : SurfaceView, SurfaceHolder.Callback {

	private var TAG = "CameraView"
	private val camera: Camera?
	private var mPreviewSize: Camera.Size? = null

	// List of supported preview sizes
	private var mSupportedPreviewSizes: List<Camera.Size>? = null

	constructor(context: Context, camera: Camera?) : super(context) {
		this.camera = camera
		setupCameraProperties(camera)
		// Install a SurfaceHolder.Callback so we get notified when the
		// underlying surface is created and destroyed.
		holder.addCallback(this)
		// deprecated setting, but required on Android versions prior to 3.0
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
		holder.setKeepScreenOn(true)
	}


	override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
		// If your preview can change or rotate, take care of those events here.
		// Make sure to stop the preview before resizing or reformatting it.
		if (holder?.surface == null) {
			// preview surface does not exist
			return
		}
		// stop preview before making changes
		try {
			val parameters = camera?.parameters

			// Set the auto-focus mode to "continuous"
			parameters?.focusMode = Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE

			// Preview size must exist.
			if (mPreviewSize != null) {
				val previewSize = mPreviewSize
				parameters?.setPreviewSize(previewSize!!.width, previewSize.height)
			}
			camera?.let {
				it.parameters = parameters
				it.startPreview()
			}
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

	fun startCameraPreview() {
		try {
			camera?.setPreviewDisplay(holder)
			camera?.startPreview()
		} catch (e: Exception) {
			e.printStackTrace()
		}

	}

	private fun setupCameraProperties(camera: Camera?) {
		mSupportedPreviewSizes = camera?.parameters?.supportedPreviewSizes
		val mSupportedFlashModes = camera?.parameters?.supportedFlashModes

		// Set the camera to Auto Flash mode.
		if (mSupportedFlashModes != null && mSupportedFlashModes.contains(Camera.Parameters.FLASH_MODE_AUTO)) {
			val parameters = camera.parameters
			parameters?.flashMode = Camera.Parameters.FLASH_MODE_AUTO
			camera.parameters = parameters
		}
	}

	/**
	 * Calculate the measurements of the layout
	 * @param widthMeasureSpec
	 * *
	 * @param heightMeasureSpec
	 */
	override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
		// Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
		val width = View.resolveSize(suggestedMinimumWidth, widthMeasureSpec)
		val height = View.resolveSize(suggestedMinimumHeight, heightMeasureSpec)
		setMeasuredDimension(width, height)

		if (mSupportedPreviewSizes != null) {
			mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes!!, width, height)
		}
	}

	override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
		// Source: http://stackoverflow.com/questions/7942378/android-camera-will-not-work-startpreview-fails
		if (changed) {
			val width = right - left
			val height = bottom - top

			var previewWidth = width
			var previewHeight = height

			if (mPreviewSize != null) {
				val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay

				when (display.rotation) {
					Surface.ROTATION_0 -> {
						previewWidth = mPreviewSize!!.height
						previewHeight = mPreviewSize!!.width
						camera?.setDisplayOrientation(90)
					}
					Surface.ROTATION_90 -> {
						previewWidth = mPreviewSize!!.width
						previewHeight = mPreviewSize!!.height
					}
					Surface.ROTATION_180 -> {
						previewWidth = mPreviewSize!!.height
						previewHeight = mPreviewSize!!.width
					}
					Surface.ROTATION_270 -> {
						previewWidth = mPreviewSize!!.width
						previewHeight = mPreviewSize!!.height
						camera?.setDisplayOrientation(180)
					}
				}
			}

			val scaledChildHeight = previewHeight * width / previewWidth
			layout(0, height - scaledChildHeight, width, height)
		}
	}

	/**

	 * @param sizes
	 * *
	 * @param width
	 * *
	 * @param height
	 * *
	 * @return
	 */
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