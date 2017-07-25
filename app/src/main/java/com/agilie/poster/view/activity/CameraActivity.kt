package com.agilie.poster.view.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.agilie.camera.CameraView
import com.agilie.poster.Constants
import com.agilie.poster.ImageLoader
import com.agilie.poster.R
import com.agilie.poster.dialog.ErrorDialog
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.File
import java.lang.ref.WeakReference


class CameraActivity : BaseActivity() {

	companion object {
		val TAG = "CameraActivity"
		val REQUEST_CAMERA_PERMISSION = 1
		val REQUEST_STORAGE_PERMISSION = 2
		val FRAGMENT_DIALOG = "dialog"

		object CameraResult {
			var imageData: WeakReference<ByteArray>? = null
			var cameraId: Int? = null
			var rotation: Int? = null
		}

		private var savedStreamMuted = true
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)
		if (savedInstanceState == null) {
			requestStoragePermission()
		}

		async(CommonPool) { loadImageAsync() }

		camera?.addCallback(cameraCallback)
		button_snap.setOnClickListener { takePicture() }
		change_cam.setOnClickListener { changeCamera() }
		gallery_icon.setOnClickListener { showUserGallery() }

		initFlashSupportLogic()

		// Add sound when picture is taking
		//enableSound()
	}

	override fun onResume() {
		super.onResume()
		camera.start()
	}

	override fun onPause() {
		camera.stop()
		super.onPause()
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

	private fun loadImageAsync() {
		val imagePath = ImageLoader.instance.getLastPhotoPath(this@CameraActivity) ?: return
		launch(UI) {
			Glide.with(this@CameraActivity)
					.load(File(imagePath))
					.placeholder(R.drawable.ic_load_image)
					.fitCenter()
					.into(this@CameraActivity.gallery_icon)
		}
	}

	private fun showUserGallery() {
		startActivity(getCallingIntent(this, PhotoGalleryActivity::class.java))
	}

	private fun changeCamera() {
		camera?.let {
			val facing = camera.facing
			camera.facing = if (facing == CameraView.FACING_FRONT)
				CameraView.FACING_BACK
			else
				CameraView.FACING_FRONT
		}
	}

	private fun setAspectRatio() {
		val ratios = camera.supportedAspectRatios
		if (ratios.contains(Constants.ASPECT_RATIO_16_9)) {
			camera.setAspectRatio(Constants.ASPECT_RATIO_16_9)
		} else if (ratios.contains(Constants.DEFAULT_ASPECT_RATIO)) {
			camera.setAspectRatio(Constants.DEFAULT_ASPECT_RATIO)
		} else {
			camera.setAspectRatio(ratios.first())
		}
	}

	private fun takePicture() {
		camera.takePicture()
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

	private val cameraCallback = object : CameraView.Callback() {
		override
		fun onCameraOpened(cameraView: CameraView) {
			setAspectRatio()
		}

		override
		fun onCameraClosed(cameraView: CameraView) {
			// Empty
		}

		override
		fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
			Toast.makeText(this@CameraActivity, R.string.picture_taken, Toast.LENGTH_SHORT)
					.show()


			CameraResult.apply {
				imageData = WeakReference<ByteArray>(data)
				cameraId = cameraView.cameraId
				rotation = windowManager.defaultDisplay.rotation
			}

			startActivity(getCallingIntent(this@CameraActivity, PhotoPreviewActivity::class.java))
			/*val intent = Intent(this@CameraActivity, PhotoPreviewActivity::class.java)
			startActivity(intent)*/
			//disableSound()
		}
	}


	private fun enableSound() {
		val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (!audioManager.isStreamMute(AudioManager.STREAM_SYSTEM)) {
				savedStreamMuted = true
				audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0)
			}
		} else {
			audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true)
		}
	}

	private fun disableSound() {
		val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (savedStreamMuted) {
				audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0)
				savedStreamMuted = false
			}
		} else {
			audioManager.setStreamMute(AudioManager.STREAM_SYSTEM, false)
		}
	}

	private fun initFlashSupportLogic() {

		flash_off.setOnClickListener { changeFlashButtonsVisibility(it) }
		flash_on.setOnClickListener { changeFlashButtonsVisibility(it) }
		flash_auto.setOnClickListener { changeFlashButtonsVisibility(it) }

		flash_auto_view.setOnClickListener { updateFlashContainerStyle(it) }
		flash_off_view.setOnClickListener { updateFlashContainerStyle(it) }
		flash_on_view.setOnClickListener { updateFlashContainerStyle(it) }
		close.setOnClickListener {
			finish()
		}
	}

	private fun updateFlashContainerStyle(view: View) {
		when (view) {
			flash_auto_view -> {
				flash_auto.setTextColor(ContextCompat.getColor(this, R.color.button_flash_state_enable))
				flash_on.setTextColor(ContextCompat.getColor(this, R.color.white))
				flash_off.setTextColor(ContextCompat.getColor(this, R.color.white))
			}
			flash_off_view -> {
				flash_off.setTextColor(ContextCompat.getColor(this, R.color.button_flash_state_enable))
				flash_auto.setTextColor(ContextCompat.getColor(this, R.color.white))
				flash_on.setTextColor(ContextCompat.getColor(this, R.color.white))
			}
			flash_on_view -> {
				flash_on.setTextColor(ContextCompat.getColor(this, R.color.button_flash_state_enable))
				flash_auto.setTextColor(ContextCompat.getColor(this, R.color.white))
				flash_off.setTextColor(ContextCompat.getColor(this, R.color.white))
			}
		}

		flash_container.visibility = View.VISIBLE
	}

	private fun changeFlashButtonsVisibility(view: View) {
		when (view) {
			flash_auto -> {
				camera.flash = CameraView.FLASH_AUTO

				flash_auto_view.visibility = View.VISIBLE
				flash_off_view.visibility = View.INVISIBLE
				flash_on_view.visibility = View.INVISIBLE
			}
			flash_on -> {
				camera.flash = CameraView.FLASH_ON

				flash_on_view.visibility = View.VISIBLE
				flash_off_view.visibility = View.INVISIBLE
				flash_auto_view.visibility = View.INVISIBLE
			}
			flash_off -> {
				camera.flash = CameraView.FLASH_OFF

				flash_off_view.visibility = View.VISIBLE
				flash_on_view.visibility = View.INVISIBLE
				flash_auto_view.visibility = View.INVISIBLE
			}
		}
		flash_container.visibility = View.INVISIBLE
	}
}