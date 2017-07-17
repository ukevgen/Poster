package com.agilie.poster.view.fragments.camera

import android.app.Fragment
import android.hardware.Camera
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R
import com.agilie.poster.dialog.ErrorDialog
import com.agilie.poster.presenter.camera.CameraPresenterImpl
import kotlinx.android.synthetic.main.fragment_native_camera.*


class CameraNativeFragment : Fragment() {

	private lateinit var cameraPresenterImpl: CameraPresenterImpl

	companion object {
		fun newInstance() = CameraNativeFragment()
		private var FACING = "FACING"
		private val FLASH_MODE = "FLASH_MODE"
		private val FRAGMENT_DIALOG = "dialog"
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		savedInstanceState?.let {
			cameraPresenterImpl.apply {
				lensFacing = it.getInt(FACING)
				changeFlashMode(it.getString(FLASH_MODE))
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
			inflater.inflate(R.layout.fragment_native_camera, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		cameraPresenterImpl = CameraPresenterImpl(this)

		button_snap.setOnClickListener { cameraPresenterImpl.takePicture() }

		// Check camera hardware
		if (!checkCameraHardware()) {
			cameraPresenterImpl.showErrorDialog()
		} else {
			cameraPresenterImpl.addCameraPreview(view)
			cameraPresenterImpl.startPreview()
		}

		// Check front camera is exist
		button_change_cam.setOnClickListener {
			if (cameraPresenterImpl.getNumberOfCameras() == 1) {
				button_change_cam.visibility = View.INVISIBLE
			} else {
				cameraPresenterImpl.changeCamera()
			}
		}
		initFlashSupportLogic()
	}

	override fun onPause() {
		cameraPresenterImpl.pause()
		super.onPause()
	}

	override fun onResume() {
		if (!cameraPresenterImpl.opened) {
			cameraPresenterImpl.resume()
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

	fun onShowErrorDialog() {
		ErrorDialog.newInstance(activity.getString(R.string.cannot_open_camera))
				.show(fragmentManager, FRAGMENT_DIALOG)
	}

	private fun initFlashSupportLogic() {

		flash_off.setOnClickListener { changeFlashButtonsVisibility(it) }
		flash_on.setOnClickListener { changeFlashButtonsVisibility(it) }
		flash_auto.setOnClickListener { changeFlashButtonsVisibility(it) }

		flash_auto_view.setOnClickListener { updateFlashContainerStyle(it) }
		flash_off_view.setOnClickListener { updateFlashContainerStyle(it) }
		flash_on_view.setOnClickListener { updateFlashContainerStyle(it) }
		close.setOnClickListener {
			cameraPresenterImpl.destroy()
			activity.finish()
		}
	}

	private fun updateFlashContainerStyle(view: View) {
		when (view) {
			flash_auto_view -> {
				flash_auto.setTextColor(ContextCompat.getColor(activity, R.color.button_flash_state_enable))
				flash_on.setTextColor(ContextCompat.getColor(activity, R.color.white))
				flash_off.setTextColor(ContextCompat.getColor(activity, R.color.white))
			}
			flash_off_view -> {
				flash_off.setTextColor(ContextCompat.getColor(activity, R.color.button_flash_state_enable))
				flash_auto.setTextColor(ContextCompat.getColor(activity, R.color.white))
				flash_on.setTextColor(ContextCompat.getColor(activity, R.color.white))
			}
			flash_on_view -> {
				flash_on.setTextColor(ContextCompat.getColor(activity, R.color.button_flash_state_enable))
				flash_auto.setTextColor(ContextCompat.getColor(activity, R.color.white))
				flash_auto.setTextColor(ContextCompat.getColor(activity, R.color.white))
			}
		}

		flash_container.visibility = View.VISIBLE
	}

	private fun changeFlashButtonsVisibility(view: View) {
		when (view) {
			flash_auto -> {
				cameraPresenterImpl.changeFlashMode(Camera.Parameters.FLASH_MODE_AUTO)

				flash_auto_view.visibility = View.VISIBLE
				flash_off_view.visibility = View.INVISIBLE
				flash_on_view.visibility = View.INVISIBLE
			}
			flash_on -> {
				cameraPresenterImpl.changeFlashMode(Camera.Parameters.FLASH_MODE_ON)

				flash_on_view.visibility = View.VISIBLE
				flash_off_view.visibility = View.INVISIBLE
				flash_auto_view.visibility = View.INVISIBLE
			}
			flash_off -> {
				cameraPresenterImpl.changeFlashMode(Camera.Parameters.FLASH_MODE_OFF)

				flash_off_view.visibility = View.VISIBLE
				flash_on_view.visibility = View.INVISIBLE
				flash_auto_view.visibility = View.INVISIBLE
			}
		}
		flash_container.visibility = View.INVISIBLE
	}
}
