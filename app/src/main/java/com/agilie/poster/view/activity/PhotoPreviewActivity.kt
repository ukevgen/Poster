package com.agilie.poster.view.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.Camera
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Surface
import android.view.ViewTreeObserver
import com.agilie.poster.Constants
import com.agilie.poster.ImageLoader
import com.agilie.poster.R
import kotlinx.android.synthetic.main.activity_photo_preview.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import java.io.File

class PhotoPreviewActivity : BaseActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_photo_preview)

		val imageData = CameraActivity.Companion.CameraResult.imageData?.get()
		val cameraId = CameraActivity.Companion.CameraResult.cameraId
		val rotation = CameraActivity.Companion.CameraResult.rotation

		if (imageData == null || cameraId == null) {
			finish()
			return
		}

		image_preview.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
			override fun onPreDraw(): Boolean {
				image_preview.viewTreeObserver.removeOnPreDrawListener(this)
				val resizeBitmap = resizingImage(imageData)
				onPreparePicture(resizeBitmap, cameraId, rotation, preview = true)
				return false
			}
		})

		back_to_cam.setOnClickListener { onBackPressed() }

		done.setOnClickListener {
			val bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)
			onPreparePicture(bitmap, cameraId, rotation, preview = false)
		}
	}

	override fun onBackPressed() {
		super.onBackPressed()
	}

	private fun resizingImage(data: ByteArray): Bitmap {
		// Get the dimensions of the View
		val targetW = image_preview.width.toDouble()
		val targetH = image_preview.height.toDouble()

		// Get the dimensions of the bitmap
		val bmOptions = BitmapFactory.Options()
		//inJustDecodeBounds = true <-- will not load the bitmap into memory
		bmOptions.inJustDecodeBounds = true
		BitmapFactory.decodeByteArray(data, 0, data.size, bmOptions)

		val photoW = bmOptions.outWidth
		val photoH = bmOptions.outHeight

		// Determine how much to scale down the image
		val scaleFactor = Math.min(photoW / targetW, photoH / targetH)

		// Decode the image file into a Bitmap sized to fill the View
		bmOptions.inJustDecodeBounds = false
		bmOptions.inSampleSize = (Math.ceil(scaleFactor)).toInt()
		bmOptions.inPurgeable = true

		// Resizing bitmap with new options
		return BitmapFactory.decodeByteArray(data, 0, data.size, bmOptions)
	}

	private fun onPreparePicture(bitmap: Bitmap, cameraId: Int, rotation: Int?, preview: Boolean) {
		val angleToRotate = getRotationAngle(cameraId, rotation) //+ 180
		//val orignalImage = BitmapFactory.decodeByteArray(imageData, 0, imageData.size)

		val bitmapImage = rotate(bitmap, angleToRotate)
		if (preview) {
			image_preview.setImageBitmap(bitmapImage)
		} else {
			saveImage(bitmapImage)
		}
	}

	private fun rotate(bitmap: Bitmap, degree: Float): Bitmap {
		val w = bitmap.width
		val h = bitmap.height

		val mtx = Matrix()
		mtx.postRotate(degree)
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true)
	}

	private fun getRotationAngle(cameraId: Int, rotation: Int?): Float {
		val info = Camera.CameraInfo()
		Camera.getCameraInfo(cameraId, info)

		var degrees = 0
		when (rotation) {
			Surface.ROTATION_0 -> degrees = 0
			Surface.ROTATION_90 -> degrees = 90
			Surface.ROTATION_180 -> degrees = 180
			Surface.ROTATION_270 -> degrees = 270
		}

		return ((info.orientation - degrees + 360) % 360).toFloat()
	}

	private fun saveImage(bitmap: Bitmap) {

		val sdCard = Environment.getExternalStorageDirectory()
		val dir = File(sdCard.absolutePath + Constants.PHOTO_FOLDER)
		dir.mkdirs()
		val fileName = String.format(Constants.PHOTO_FORMAT, System.currentTimeMillis())

		val path = ImageLoader.instance.saveImage(dir, fileName, bitmap)
		// Start Photo Editor
		async(CommonPool) {
			launch(UI) {
				launchPhotoEditor(path)
			}
			path?.let {
				MediaScannerConnection.scanFile(this@PhotoPreviewActivity, arrayOf(path),
						null) { _, _ ->
					Log.d("TAG", "media")
				}
			}
		}
	}

	private fun launchPhotoEditor(path: String?) {
		val intent = getCallingIntent(this, MainActivity::class.java)
		intent.putExtra(Constants.PHOTO_PATH, path)
		startActivity(intent)
		finish()
	}
}
