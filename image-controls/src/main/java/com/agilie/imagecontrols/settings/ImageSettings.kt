package com.agilie.imagecontrols.settings

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.Paint
import android.util.Log

class ImageSettings(bitmap: Bitmap?) : ImageSettingsControls(bitmap) {

	private val TAG = "ImageSettings"

	override fun saturation(value: Float): Paint {
		val colorMatrix = ColorMatrix()
		colorMatrix.setSaturation(value)
		Log.d(TAG, "$value")
		return process(colorMatrix)
	}

	override fun brightness() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun contrast() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun details() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun blur() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun harshness() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun warm() {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}