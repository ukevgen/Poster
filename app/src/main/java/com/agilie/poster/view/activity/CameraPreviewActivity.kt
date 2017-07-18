package com.agilie.poster.view.activity

import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.os.Bundle
import android.os.Environment
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.agilie.poster.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_camera_preview.*
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class CameraPreviewActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_camera_preview)

		val bitmap = CameraActivity.Companion.CameraResult.image?.get()
		if (bitmap == null) {
			finish()
			return
		}

		saveImageAsync(bitmap)

		Glide.with(this)
				.load(bitmap)
				.into(image_preview)
		/*image_preview.setImageBitmap(bitmap)*/

		back_to_cam.setOnClickListener { onBackPressed() }

		done.setOnClickListener {
			//TODO start main activity
		}
	}

	private fun saveImageAsync(bitmap: Bitmap) {
		try {
			val sdCard = Environment.getExternalStorageDirectory()
			val dir = File(sdCard.absolutePath + CameraActivity.PHOTO_FOLDER)
			dir.mkdirs()

			val fileName = String.format(CameraActivity.PHOTO_FORMAT, System.currentTimeMillis())
			val outFile = File(dir, fileName)

			val fos = FileOutputStream(outFile)
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

			fos.apply {
				flush()
				close()
			}
			// Update user gallery
			MediaScannerConnection.scanFile(this, arrayOf<String>(outFile.absolutePath),
					null) { path, uri ->
				Log.d("TAG", "$uri")
			}

		} catch (e: FileNotFoundException) {
			e.printStackTrace()
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
		}
	}

	private fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap {
		var image = image
		if (maxHeight > 0 && maxWidth > 0) {
			val width = image.width
			val height = image.height
			val ratioBitmap = width.toFloat() / height.toFloat()
			val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

			var finalWidth = maxWidth
			var finalHeight = maxHeight
			if (ratioMax > 1) {
				finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
			} else {
				finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
			}
			image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
			return image
		} else {
			return image
		}
	}
}
