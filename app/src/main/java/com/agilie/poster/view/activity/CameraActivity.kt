package com.agilie.poster.view.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.agilie.poster.R
import com.agilie.poster.dialog.ErrorDialog
import com.flurgle.camerakit.CameraListener
import kotlinx.android.synthetic.main.activity_camera.*
import java.lang.ref.WeakReference

class CameraActivity : BaseActivity() {


	companion object {
		val PHOTO_FOLDER = "/poster"
		val PHOTO_FORMAT = "%d.jpg"

		object CameraResult {
			var image: WeakReference<Bitmap>? = null
		}

		private val REQUEST_CAMERA_PERMISSION = 1
		private val REQUEST_STORAGE_PERMISSION = 2
		private val REQUEST_PORTRAIT_FFC = 3
		private val FRAGMENT_DIALOG = "dialog"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)
		if (savedInstanceState == null) {
			requestStoragePermission()
		}

		button_snap.setOnClickListener { takePicture() }
	}

	override fun onResume() {
		super.onResume()
		//camera.start()
	}

	override fun onPause() {
		//camera.stop()
		super.onPause()
	}

	override fun onDestroy() {
		//camera.stop()
		super.onDestroy()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

		when (requestCode) {
			REQUEST_CAMERA_PERMISSION -> {
				if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					showCamera()
				} else {
					ErrorDialog.newInstance(getString(R.string.request_camera_permission))
							.show(fragmentManager, FRAGMENT_DIALOG)
				}
			}
			REQUEST_STORAGE_PERMISSION -> {
				if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					showCamera()
				} else {
					ErrorDialog.newInstance(getString(R.string.request_storage_permission))
							.show(fragmentManager, FRAGMENT_DIALOG)
				}
			}
			else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		}
	}

	private fun takePicture() {
		camera.setCameraListener(object : CameraListener() {
			override fun onPictureTaken(picture: ByteArray) {
				super.onPictureTaken(picture)

				// Create a bitmap
				val result = BitmapFactory.decodeByteArray(picture, 0, picture.size)

				CameraResult.image = WeakReference<Bitmap>(result)

				val intent = Intent(this@CameraActivity, CameraPreviewActivity::class.java)
				startActivity(intent)
			}
		})
		camera.captureImage()
	}

	private fun requestStoragePermission() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
				PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
		} else {
			showCamera()
		}
	}

	private fun showCamera() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				== PackageManager.PERMISSION_GRANTED) {
			return
		} else {
			requestCameraPermission()
		}
	}

	private fun requestCameraPermission() {
		ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
	}
}