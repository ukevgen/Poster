package com.agilie.poster.view.fragments.camera

import android.app.Fragment
import android.hardware.Camera
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R
import com.agilie.poster.presenter.camera.CameraPresenterImpl
import kotlinx.android.synthetic.main.fragment_native_camera.*


class CameraNativeFragment : Fragment() {

	private lateinit var cameraPresenterImpl: CameraPresenterImpl
	private var FACING = "FACING"
	private val FLASH_MODE = "FLASH_MODE"

	companion object {
		fun newInstance() = CameraNativeFragment()
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		savedInstanceState?.let {
			cameraPresenterImpl.apply {
				lensFacing = it.getInt(FACING)
				flashMode = it.getString(FLASH_MODE)
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
			inflater.inflate(R.layout.fragment_native_camera, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		cameraPresenterImpl = CameraPresenterImpl(this)

		button_snap.setOnClickListener { cameraPresenterImpl.takePicture() }

		// Check front camera is exist
		button_change_cam.setOnClickListener {
			if (cameraPresenterImpl.getNumberOfCameras() == 1) {
				button_change_cam.visibility = View.INVISIBLE
			} else {
				cameraPresenterImpl.changeCamera()
			}
		}

		flash_off.setOnClickListener { cameraPresenterImpl.flashMode = Camera.Parameters.FLASH_MODE_OFF }
		flash_on.setOnClickListener { cameraPresenterImpl.flashMode = Camera.Parameters.FLASH_MODE_ON }
		flash_auto.setOnClickListener { cameraPresenterImpl.flashMode = Camera.Parameters.FLASH_MODE_AUTO }
		close.setOnClickListener {
			cameraPresenterImpl.destroy()
			activity.finish()
		}

	}

	override fun onResume() {
		if (!cameraPresenterImpl.opened) {
			if (!checkCameraHardware() || !cameraOpen(view)) {
				cameraPresenterImpl.showErrorDialog()
			}
		}
		cameraPresenterImpl.opened = true
		super.onResume()
	}

	override fun onDestroy() {
		cameraPresenterImpl.destroy()
		super.onDestroy()
	}

	override fun onSaveInstanceState(outState: Bundle?) {
		super.onSaveInstanceState(outState)
		outState?.apply {
			putInt(FACING, cameraPresenterImpl.lensFacing)
			putString(FLASH_MODE, cameraPresenterImpl.flashMode)
		}
	}

	/** Check if this device has a camera  */
	private fun checkCameraHardware() = cameraPresenterImpl.checkCamera()

	/** Check the camera open  */
	private fun cameraOpen(view: View) = cameraPresenterImpl.safeCameraOpenInView(view)
}
