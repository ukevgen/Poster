package com.agilie.poster.utils

import android.content.Context
import android.util.TypedValue


fun dpToPx(context: Context, id: Int): Int {
	val dp = context.resources.getDimension(id)
	val displayMetrics = context.resources.displayMetrics
	val h = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics) / displayMetrics.density
	/*val dp = context.resources.getDimension(id)
	val displayMetrics = context.resources.displayMetrics
	val px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT))*/
	return h.toInt()
}

//int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, <HEIGHT>, getResources().getDisplayMetrics());