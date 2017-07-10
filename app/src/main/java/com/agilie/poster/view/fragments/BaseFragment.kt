package com.agilie.poster.view.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.agilie.poster.R
import com.agilie.poster.adapter.ViewsPagerAdapter
import com.agilie.poster.utils.FixedSpeedScroller
import java.lang.reflect.Field


open class BaseFragment : Fragment() {

	protected var colorViewsAdapter = ViewsPagerAdapter()
	protected var fontViewsAdapter = ViewsPagerAdapter()

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		createColorViews()
		createFontViews()
	}

	protected fun setViewPagerScrollSpeed(viewPager: ViewPager) {
		try {
			val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
			mScroller.isAccessible = true
			val scroller = FixedSpeedScroller(viewPager.context, LinearInterpolator())
			mScroller.set(viewPager, scroller)
		} catch (e: NoSuchFieldException) {
		} catch (e: IllegalArgumentException) {
		} catch (e: IllegalAccessException) {
		}

	}

	private fun createColorViews() {
		val colors = context.resources.getIntArray(R.array.colorsArray)

		colors.forEach {
			val image = ImageView(context)
			image.tag = it
			image.setImageResource(R.drawable.rect)
			image.setColorFilter(it, PorterDuff.Mode.SRC_IN)
			colorViewsAdapter.addView(image)
		}
	}

	private fun createFontViews() {
		val fontImages = context.resources.obtainTypedArray(R.array.fontsArray)
		val fonts = context.resources.getStringArray(R.array.assetsArray)
		for (i in 0..fontImages.length() - 1) {
			val image = ImageView(context)
			image.apply {
				setImageResource(fontImages.getResourceId(i, -1))
				tag = fonts[i]
			}
			fontViewsAdapter.addView(image)
		}
	}

}

