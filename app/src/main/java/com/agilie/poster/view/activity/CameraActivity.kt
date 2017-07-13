package com.agilie.poster.view.activity

import android.os.Bundle
import com.agilie.poster.R
import com.agilie.poster.view.fragments.camera.CameraNativeFragment

/**Action flow
 * 1. Check current build version
 * 2. Select the desired fragment depending on the build
 * */

class CameraActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)

		addFragment(R.id.camera_container, CameraNativeFragment.newInstance())


		/*	if (null == savedInstanceState) {
				// Check build version
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					// Camera 2
					fragmentManager.beginTransaction()
							.replace(R.id.camera_container, Camera2Fragment())
							.commit()
					Log.d("TAG", "camera 2")
				} else {
					// Camera 1
					fragmentManager.beginTransaction()
							.replace(R.id.camera_container, CameraNativeFragment())
							.commit()
					Log.d("TAG", "camera 1")
				}
			}*/
	}
}