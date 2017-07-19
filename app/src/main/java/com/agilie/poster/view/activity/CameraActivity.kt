package com.agilie.poster.view.activity

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.AudioManager
import android.os.*
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Toast
import com.agilie.googlecamera.CameraView
import com.agilie.poster.R
import com.agilie.poster.dialog.ErrorDialog
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.ref.WeakReference


class CameraActivity : BaseActivity() {

	companion object {
		val PHOTO_FOLDER = "/poster"
		val PHOTO_FORMAT = "%d.jpg"
		val TAG = "MainActivity"
		val REQUEST_CAMERA_PERMISSION = 1
		val REQUEST_STORAGE_PERMISSION = 2
		val FRAGMENT_DIALOG = "dialog"

		object CameraResult {
			var image: WeakReference<Bitmap>? = null
		}

		private var savedStreamMuted = true
		private var backgroundHandler: Handler? = null
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera)
		if (savedInstanceState == null) {
			requestStoragePermission()
		}

		camera?.addCallback(cameraCallback)

		button_snap.setOnClickListener { takePicture() }

		button_change_cam.setOnClickListener { changeCamera() }

		initFlashSupportLogic()

		// Add sound when picture is taking
		//enableSound()
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

	override fun onResume() {
		super.onResume()
		camera.start()
	}

	override fun onPause() {
		camera.stop()
		super.onPause()
	}

	override fun onDestroy() {
		super.onDestroy()
		if (backgroundHandler != null) {
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
				backgroundHandler?.looper?.quitSafely()
			} else {
				backgroundHandler?.looper?.quit()
			}
			backgroundHandler = null
		}
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

	private fun takePicture() {
		camera.takePicture()

		/*camera.setCameraListener(object : CameraListener() {
			override fun onPictureTaken(picture: ByteArray) {
				super.onPictureTaken(picture)

				// Create a bitmap
				val result = BitmapFactory.decodeByteArray(picture, 0, picture.size)

				CameraResult.image = WeakReference<Bitmap>(result)

				val intent = Intent(this@CameraActivity, CameraPreviewActivity::class.java)
				startActivity(intent)
			}
		})
		camera.captureImage()*/
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
			Log.d(TAG, "onCameraOpened")
		}

		override
		fun onCameraClosed(cameraView: CameraView) {
			Log.d(TAG, "onCameraClosed")
		}

		override
		fun onPictureTaken(cameraView: CameraView, data: ByteArray) {
			Log.d(TAG, "onPictureTaken " + data.size)
			Toast.makeText(cameraView.context, R.string.picture_taken, Toast.LENGTH_SHORT)
					.show()
			//disableSound()
			getBackgroundHandler()?.post({
				val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
						"picture.jpg")
				var os: OutputStream? = null
				try {
					os = FileOutputStream(file)
					os.write(data)
					os.close()
				} catch (e: IOException) {
					Log.d(TAG, "Cannot write to " + file, e)
				} finally {
					if (os != null) {
						try {
							os.close()
						} catch (e: IOException) {
							// Ignore
						}

					}
				}
			})
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

	private fun getBackgroundHandler(): Handler? {
		if (backgroundHandler == null) {
			val thread = HandlerThread("background")
			thread.start()
			backgroundHandler = Handler(thread.looper)
		}
		return backgroundHandler
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
				flash_auto.setTextColor(ContextCompat.getColor(this, R.color.white))
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