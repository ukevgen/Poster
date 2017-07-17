package com.agilie.poster.presenter.camera

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.media.MediaScannerConnection
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.agilie.poster.R
import com.agilie.poster.camera.CameraPreview
import com.agilie.poster.view.activity.CameraActivity
import com.agilie.poster.view.fragments.camera.CameraNativeFragment
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.async
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class CameraPresenterImpl(val view: CameraNativeFragment) : CameraContract.CameraBehavior {


	private val TAG = "CameraPresenterImpl"
	var preview: CameraPreview? = null
	private var camera: Camera? = null
	private val pictureCallback = Camera.PictureCallback { data, camera ->

		saveImageAsync(data, preview!!.cameraOrientation)
		preview?.startCameraPreview()
	}

	var flashMode = Camera.Parameters.FLASH_MODE_TORCH
	var opened = false
	var lensFacing = Camera.CameraInfo.CAMERA_FACING_BACK


	override fun resume() {
		reOpenCamera()
	}

	override fun pause() {
		releaseCameraAndPreview()
	}

	override fun destroy() {
		releaseCameraAndPreview()
	}

	override fun stop() {
	}

	override fun changeCamera() {
		when (lensFacing) {
			Camera.CameraInfo.CAMERA_FACING_BACK -> lensFacing = Camera.CameraInfo.CAMERA_FACING_FRONT
			Camera.CameraInfo.CAMERA_FACING_FRONT -> lensFacing = Camera.CameraInfo.CAMERA_FACING_BACK
		}
		reOpenCamera()
	}

	override fun closeCamera() {
		releaseCameraAndPreview()
		view.activity.finish()
	}

	override fun takePicture() {
		camera?.takePicture(null, null, pictureCallback)
	}

	override fun changeFlashMode(value: String) {
		val param = camera?.parameters?.apply {
			flashMode = value
		}
		camera?.parameters = param
	}

	override fun startPreview() {
		preview?.startCameraPreview()
	}

	private fun reOpenCamera() {
		releaseCameraAndPreview()
		camera = Camera.open(lensFacing).apply { setPreviewDisplay(preview?.holder) }

		preview?.camera = camera
		preview?.apply {
			// Set correct orientation
			setCameraDisplayOrientation()
			startCameraPreview()
		}
	}

	fun addCameraPreview(view: View) {
		releaseCameraAndPreview()

		camera = getCameraInstance()

		if (preview == null) {
			preview = CameraPreview(this.view.activity.baseContext, camera, view)
			val frameLayout = view?.findViewById(R.id.camera_preview) as FrameLayout
			frameLayout.addView(this.preview)
		}
	}

	/** Clear any existing preview / camera. */
	private fun releaseCameraAndPreview() {

		camera?.apply {
			stopPreview()
			release()
		}
		camera = null

		preview?.apply {
			destroyDrawingCache()
			camera = null
		}
	}

	/** Safe method for getting a camera instance. */
	fun getCameraInstance(): Camera? {
		var camera: Camera? = null
		try {
			camera = Camera.open(lensFacing) // attempt to get a Camera instance
		} catch (e: Exception) {
			e.printStackTrace()
		}
		return camera // returns null if camera is unavailable
	}

	fun checkCamera() = view.activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

	fun showErrorDialog() {
		view.onShowErrorDialog()
	}

	fun getNumberOfCameras() = Camera.getNumberOfCameras()

	private fun saveImageAsync(data: ByteArray, cameraOrientation: Int) = async(CommonPool) {

		var bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
		val matrix = Matrix().apply { postRotate(cameraOrientation.toFloat()) }

		bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
		// Write to SD Card
		try {
			val sdCard = Environment.getExternalStorageDirectory()
			val dir = File(sdCard.absolutePath + CameraActivity.PHOTO_FOLDER)
			dir.mkdirs()

			val fileName = String.format(CameraActivity.PHOTO_FORMAT, System.currentTimeMillis())
			val outFile = File(dir, fileName)

			val fos = FileOutputStream(outFile)
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

			fos.apply {
				flush()
				close()
			}
			// Update user gallery
			MediaScannerConnection.scanFile(view.activity, arrayOf<String>(outFile.absolutePath),
					null) { path, uri ->
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.size + " to " + outFile.absolutePath)
			}

		} catch (e: FileNotFoundException) {
			e.printStackTrace()
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
		}
	}
}