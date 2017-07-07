package com.agilie.poster.utils

import android.support.v4.view.ViewPager
import android.view.View


class PagerTransformer : ViewPager.PageTransformer {

	private lateinit var viewPager: ViewPager

	override fun transformPage(view: View, position: Float) {

		viewPager = view.parent as ViewPager

		val leftInScreen = view.left - viewPager.scrollX

		val centerXInViewPager = leftInScreen + view.measuredWidth / 2
		val offsetX = centerXInViewPager - viewPager.measuredWidth / 2
		val offsetRate = offsetX.toFloat() * 0.3f / viewPager.measuredWidth
		val scaleFactor = 1 - Math.abs(offsetRate)

		if (scaleFactor in 0.0..1.0) {
			view.scaleX = scaleFactor
			view.scaleY = scaleFactor
		}
	}
}