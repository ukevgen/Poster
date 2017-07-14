package com.agilie.poster.presenter.camera

import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.agilie.poster.R
import com.agilie.poster.camera.CameraPreview
import com.agilie.poster.dialog.ErrorDialog
import com.agilie.poster.view.fragments.camera.CameraNativeFragment
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import android.media.MediaScannerConnection
import android.net.Uri
import android.view.Surface


class CameraPresenterImpl(val fragment: CameraNativeFragment) : CameraContract.Behavior {

	private val FRAGMENT_DIALOG = "dialog"
	private val TAG = "CameraPresenterImpl"
	var opened = false
	private var camera: Camera? = null
	private var preview: CameraPreview? = null
	private var cameraView: View? = null
	private val pictureCallback = Camera.PictureCallback { data, camera ->
		preview?.startCameraPreview()
		SaveImageTask().execute(data)
	}

	var lensFacing = Camera.CameraInfo.CAMERA_FACING_BACK

	override fun resume() {
		reOpenCamera()
	}

	override fun pause() {
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
		fragment.activity.finish()
	}

	override fun takePicture() {
		camera?.takePicture(null, null, pictureCallback)
	}

	private fun reOpenCamera() {
		releaseCameraAndPreview()
		camera = Camera.open(lensFacing)
		camera?.setPreviewDisplay(preview?.holder)
		// Set correct orientation
		preview?.camera = camera
		preview?.setCameraDisplayOrientation()
		preview?.startCameraPreview()
	}


	fun safeCameraOpenInView(view: View?): Boolean {
		releaseCameraAndPreview()

		camera = getCameraInstance()
		cameraView = view

		opened = camera != null

		if (opened) {
			preview = CameraPreview(fragment.activity.baseContext, camera, view)
			val preview = view?.findViewById(R.id.camera_preview) as FrameLayout
			preview.addView(this.preview)
			this@CameraPresenterImpl.preview?.startCameraPreview()
		}
		return opened
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

	fun checkCamera() = fragment.activity.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

	fun showErrorDialog() {
		ErrorDialog.newInstance(fragment.activity.getString(R.string.cannot_open_camera))
				.show(fragment.childFragmentManager, FRAGMENT_DIALOG)
	}

	fun getNumberOfCameras() = Camera.getNumberOfCameras()

	private inner class SaveImageTask : AsyncTask<ByteArray, Void, Void>() {

		override fun doInBackground(vararg data: ByteArray): Void? {
			// Write to SD Card
			try {
				val sdCard = Environment.getExternalStorageDirectory()
				val dir = File(sdCard.absolutePath + "/poster")
				dir.mkdirs()

				val fileName = String.format("%d.jpg", System.currentTimeMillis())
				val outFile = File(dir, fileName)

				val outStream = FileOutputStream(outFile)
				outStream.write(data[0])
				outStream.flush()
				outStream.close()

				MediaScannerConnection.scanFile(fragment.activity, arrayOf<String>(outFile.absolutePath),
						null) { path, uri ->
					Log.i(TAG, "Scanned $path:")
					Log.i(TAG, "-> uri=" + uri)
				}
				Log.d(TAG, "onPictureTaken - wrote bytes: " + data.size + " to " + outFile.absolutePath)

			} catch (e: FileNotFoundException) {
				e.printStackTrace()
			} catch (e: IOException) {
				e.printStackTrace()
			} finally {
			}
			return null
		}
	}

}