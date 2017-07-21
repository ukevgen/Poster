package com.agilie.poster

import android.content.Context
import android.graphics.Bitmap
import android.provider.MediaStore
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException


class ImageLoader private constructor() {

	private object Holder {
		val INSTANCE = ImageLoader()
	}

	companion object {
		val instance: ImageLoader by lazy { Holder.INSTANCE }
	}

	fun saveImage(dir: File, fileName: String, bitmap: Bitmap): String? {
		var outFile: File? = null
		try {
			outFile = File(dir, fileName)

			val fos = FileOutputStream(outFile)
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

			fos.apply {
				flush()
				close()
			}

		} catch (e: FileNotFoundException) {
			e.printStackTrace()
		} catch (e: IOException) {
			e.printStackTrace()
		} finally {
		}
		return outFile?.absolutePath
	}

	fun getLastPhotoPath(context: Context): String? {
		val projection = arrayOf(
				MediaStore.Images.ImageColumns.DATA,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.DATE_TAKEN)

		// content:// style URI for the "primary" external storage volume
		val images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

		val firstN = "LIMIT " + 1
		val orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC " + firstN

		val cursor = context.contentResolver.query(images,
				projection, // Selection arguments (none)
				null,
				null,
				orderBy // Ordering
		)

		var imageLocation: String? = null
		if (cursor.moveToFirst()) {

			val imageColumn = cursor.getColumnIndex(
					MediaStore.Images.ImageColumns.DATA)

			do {
				imageLocation = cursor.getString(imageColumn)
			} while (cursor.moveToNext())
		}
		return imageLocation
	}

	fun getAllPhotoLocations(context: Context): List<String> {
		var photosLocation = ArrayList<String>()

		val projection = arrayOf(
				MediaStore.Images.ImageColumns.DATA,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.DATE_TAKEN)

		// content:// style URI for the "primary" external storage volume
		val images = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

		val orderBy = MediaStore.Images.Media.DATE_TAKEN + " DESC "

		val cursor = context.contentResolver.query(images,
				projection, // Selection arguments (none)
				null,
				null,
				orderBy // Ordering
		)

		var imageLocation: String? = null
		if (cursor.moveToFirst()) {

			val imageColumn = cursor.getColumnIndex(
					MediaStore.Images.ImageColumns.DATA)

			do {
				imageLocation = cursor.getString(imageColumn)
				photosLocation.add(imageLocation)
			} while (cursor.moveToNext())
		}
		return photosLocation
	}
}