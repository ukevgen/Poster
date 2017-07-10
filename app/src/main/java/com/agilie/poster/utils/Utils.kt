package com.agilie.poster.utils

import android.content.Context
import android.util.TypedValue


fun dpToPx(context: Context, id: Int): Int {
	val dp = context.resources.getDimension(id)
	val displayMetrics = context.resources.displayMetrics
	val h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics) / displayMetrics.density
	return h.toInt()
}

