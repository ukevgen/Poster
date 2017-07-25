package com.agilie.poster.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.agilie.poster.R
import com.agilie.poster.dialog.ErrorDialog

class SplashActivity : BaseActivity() {

	companion object {
		val TAG = "SplashActivity"
		val REQUEST_CAMERA_PERMISSION = 1
		val REQUEST_STORAGE_PERMISSION = 2
		val FRAGMENT_DIALOG = "dialog"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_splash)

		requestStoragePermission()
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

		when (requestCode) {
			REQUEST_CAMERA_PERMISSION -> {
				if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					startCameraActivity()
				} else {
					ErrorDialog.newInstance(getString(R.string.request_camera_permission))
							.show(fragmentManager, FRAGMENT_DIALOG)
				}
			}
			REQUEST_STORAGE_PERMISSION -> {
				if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					requestCameraPermission()
				} else {
					ErrorDialog.newInstance(getString(R.string.request_storage_permission))
							.show(fragmentManager, FRAGMENT_DIALOG)
				}
			}
			else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		}
	}

	private fun startCameraActivity() {
		startActivity(getCallingIntent(this, CameraActivity::class.java))
		finish()
	}

	private fun requestStoragePermission() {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
				PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_STORAGE_PERMISSION)
		} else {
			requestCameraPermission()
		}
	}

	private fun requestCameraPermission() {
		ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
	}
}
