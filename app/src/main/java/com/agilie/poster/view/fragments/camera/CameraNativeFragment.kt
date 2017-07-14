package com.agilie.poster.view.fragments.camera

import android.app.Fragment
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R
import com.agilie.poster.presenter.camera.CameraPresenterImpl
import kotlinx.android.synthetic.main.fragment_native_camera.*


/**Action flow
 *
 * 1. Check camera hardware
 * 2. Get Camera instance
 * 3. Try to open camera
 * */

class CameraNativeFragment : Fragment() {

	private lateinit var cameraPresenterImpl: CameraPresenterImpl
	private var FACING = "FACING"
	private var currentLensFacing = Camera.CameraInfo.CAMERA_FACING_BACK

	companion object {
		fun newInstance() = CameraNativeFragment()
	}

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		savedInstanceState?.let {
			cameraPresenterImpl.lensFacing = it.getInt(FACING, currentLensFacing)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
			inflater.inflate(R.layout.fragment_native_camera, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		Log.d("TAG", "view created")
		cameraPresenterImpl = CameraPresenterImpl(this)

		button_snap.setOnClickListener { cameraPresenterImpl.takePicture() }

		button_change_cam.setOnClickListener {
			if (cameraPresenterImpl.getNumberOfCameras() == 1) {
				button_change_cam.visibility = View.INVISIBLE
			} else {
				cameraPresenterImpl.changeCamera()
			}
		}
	}

	override fun onResume() {
		Log.d("TAG", "${cameraPresenterImpl.opened}")
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
		outState?.putInt(FACING, cameraPresenterImpl.lensFacing)
	}


	/** Check if this device has a camera  */
	private fun checkCameraHardware() = cameraPresenterImpl.checkCamera()

	/** Check the camera open  */
	private fun cameraOpen(view: View) = cameraPresenterImpl.safeCameraOpenInView(view)


}
