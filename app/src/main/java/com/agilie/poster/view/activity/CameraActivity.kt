package com.agilie.poster.view.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import com.agilie.poster.R
import com.agilie.poster.dialog.ErrorDialog
import com.agilie.poster.view.fragments.camera.CameraNativeFragment

/**Action flow
 * 1. Check current build version
 * 2. Select the desired fragment depending on the build
 * */

class CameraActivity : BaseActivity() {

	private val REQUEST_CAMERA_PERMISSION = 1
	private val REQUEST_STORAGE_PERMISSION = 2
	private val FRAGMENT_DIALOG = "dialog"

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)

		requestStoragePermission()
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
				!= PackageManager.PERMISSION_GRANTED) {
			requestCameraPermission()
		} else {
			showCameraPreview()
		}
	}

	private fun showCameraPreview() {
		// Check build version
		/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			// Camera 2
			fragmentManager.beginTransaction()
					.replace(R.id.camera_container, Camera2Fragment())
					.commit()
		} else {
			// Camera 1
			fragmentManager.beginTransaction()
					.replace(R.id.camera_container, CameraNativeFragment())
					.commit()
		}*/
		addFragment(R.id.camera_container, CameraNativeFragment.newInstance())
	}

	private fun requestCameraPermission() {
		ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), REQUEST_CAMERA_PERMISSION)
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
			else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}


}