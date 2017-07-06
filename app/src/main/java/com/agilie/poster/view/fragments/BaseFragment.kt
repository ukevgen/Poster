package com.agilie.poster.view.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.agilie.poster.R
import com.agilie.poster.adapter.PosterPagerAdapter
import com.agilie.poster.utils.FixedSpeedScroller
import java.lang.reflect.Field


open class BaseFragment : Fragment() {

	protected var colorViewsAdapter = PosterPagerAdapter()

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		initColorViews()
	}

	protected fun setViewPagerScrollSpeed(viewPager: ViewPager) {
		try {
			val mScroller: Field = ViewPager::class.java.getDeclaredField("mScroller")
			mScroller.isAccessible = true
			val scroller = FixedSpeedScroller(viewPager.context, LinearInterpolator())
			// scroller.setFixedDuration(5000);
			mScroller.set(viewPager, scroller)
		} catch (e: NoSuchFieldException) {
		} catch (e: IllegalArgumentException) {
		} catch (e: IllegalAccessException) {
		}

	}

	private fun initColorViews() {

		val colors = (
				context.resources.getIntArray(R.array.colorsArray))

		colors.forEach {
			val image = ImageView(context)
			image.setImageResource(R.drawable.rect)
			image.setColorFilter(it, PorterDuff.Mode.SRC_IN)
			colorViewsAdapter.addView(image)
		}
	}

}

