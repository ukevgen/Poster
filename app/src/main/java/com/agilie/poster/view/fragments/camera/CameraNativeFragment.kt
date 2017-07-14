package com.agilie.poster.view.fragments.camera

import android.app.Fragment
import android.os.Bundle
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

	companion object {
		fun newInstance() = CameraNativeFragment()
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
			inflater.inflate(R.layout.fragment_native_camera, container, false)

	override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

		cameraPresenterImpl = CameraPresenterImpl(this)

		// Create our Preview view and set it as the content of our activity.
		if (!checkCameraHardware() || !cameraOpen(view)) {
			cameraPresenterImpl.showErrorDialog()
		}

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

		cameraPresenterImpl.resume()
		super.onResume()
	}

	override fun onDestroy() {
		cameraPresenterImpl.destroy()
		super.onDestroy()
	}


	/** Check if this device has a camera  */
	private fun checkCameraHardware() = cameraPresenterImpl.checkCamera()

	/** Check the camera open  */
	private fun cameraOpen(view: View) = cameraPresenterImpl.safeCameraOpenInView(view)


}
