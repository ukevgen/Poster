package com.agilie.poster.utils

import android.content.Context
import android.media.ExifInterface
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import java.io.ByteArrayInputStream
import java.io.File


fun dpToPx(context: Context, id: Int): Int {
	val dp = context.resources.getDimension(id)
	val displayMetrics = context.resources.displayMetrics
	val h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics) / displayMetrics.density
	return h.toInt()
}

fun getCameraPhotoOrientation(context: Context, imageUri: Uri, imagePath: String): Int {
	var rotate = 0
	try {
		context.contentResolver.notifyChange(imageUri, null)
		val imageFile = File(imagePath)

		val exif = ExifInterface(imageFile.absolutePath)
		val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)

		when (orientation) {
			ExifInterface.ORIENTATION_ROTATE_270 -> rotate = 270
			ExifInterface.ORIENTATION_ROTATE_180 -> rotate = 180
			ExifInterface.ORIENTATION_ROTATE_90 -> rotate = 90
		}

		Log.i("RotateImage", "Exif orientation: " + orientation)
		Log.i("RotateImage", "Rotate value: " + rotate)
	} catch (e: Exception) {
		e.printStackTrace()
	}

	return rotate
}
