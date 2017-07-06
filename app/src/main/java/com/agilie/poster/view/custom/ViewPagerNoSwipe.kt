package com.agilie.poster.view.custom

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent


class ViewPagerNoSwipe : ViewPager {

	var enable = false

	constructor(context: Context?) : super(context)
	constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

	override fun onTouchEvent(event: MotionEvent): Boolean {
		if (enable) {
			return super.onTouchEvent(event)
		}

		return false
	}

	override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
		if (enable) {
			return super.onInterceptTouchEvent(event)
		}
		return false
	}

}