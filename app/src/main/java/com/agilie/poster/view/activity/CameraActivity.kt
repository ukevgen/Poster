package com.agilie.poster.view.activity

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.agilie.camera.CameraView
import com.agilie.poster.Constants
import com.agilie.poster.ImageLoader
import com.agilie.poster.R
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
		async(CommonPool) { loadImageAsync() }
		camera.start()
	}

	override fun onPause() {
		camera.stop()
		super.onPause()
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
		val intent = getCallingIntent(this, PhotoGalleryActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
		startActivity(intent)
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