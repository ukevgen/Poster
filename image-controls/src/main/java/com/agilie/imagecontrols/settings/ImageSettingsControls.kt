package com.agilie.imagecontrols.settings

import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

abstract class ImageSettingsControls(val bitmap: Bitmap?) {
	abstract fun brightness()
	abstract fun contrast()
	abstract fun details()
	abstract fun blur()
	abstract fun harshness()
	abstract fun warm()

	// Return paint
	protected fun process(colorMatrix: ColorMatrix) = Paint().apply {
		colorFilter = ColorMatrixColorFilter(colorMatrix)
	}

	abstract fun saturation(value: Float): Paint
}