package com.agilie.poster.view.activity

import android.hardware.Camera
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.FrameLayout
import com.agilie.poster.R
import com.agilie.poster.camera.CameraView
import kotlinx.android.synthetic.main.activity_camera.*

class Camera1Activity : AppCompatActivity() {

	private var camera: Camera? = null
	private var preview: CameraView? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)

		// Open the camera
		camera = getCameraInstance()
		// Create surface view
		preview = CameraView(this, camera)

		camera_surface.addView(preview)

		preview?.startCameraPreview()
	}

	fun getCameraInstance(): Camera? {
		var c: Camera? = null
		try {
			c = Camera.open() // attempt to get a Camera instance
		} catch (e: Exception) {
			// Camera is not available (in use or does not exist)
		}

		return c // returns null if camera is unavailable
	}

}
