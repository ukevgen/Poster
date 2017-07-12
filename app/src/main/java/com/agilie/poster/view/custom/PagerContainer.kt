package com.agilie.poster.view.custom

import android.content.Context
import android.graphics.PointF
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout


class PagerContainer : RelativeLayout {

	private var pager: ViewPager? = null
	private var needsRedraw = false
	private val center = PointF()
	private val mInitialTouch = PointF()

	constructor(context: Context) : super(context) {
		init(null)
	}

	constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
		init(attrs)
	}

	override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
		center.x = w / 2f
		center.y = h / 2f
	}

	override fun onFinishInflate() {
		pager = getChildAt(0) as ViewPager
		pager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(state: Int) {
				needsRedraw = state != ViewPager.SCROLL_STATE_IDLE
			}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
				if (needsRedraw) invalidate()
			}

			override fun onPageSelected(position: Int) {
				//empty
			}
		})
	}


	override fun onTouchEvent(ev: MotionEvent): Boolean {
		//We capture any touches not already handled by the ViewPager
		// to implement scrolling from a touch outside the pager bounds.
		when (ev.action) {
			MotionEvent.ACTION_DOWN -> {
				mInitialTouch.x = ev.x
				mInitialTouch.y = ev.y
				ev.offsetLocation(center.x - mInitialTouch.x, center.y - mInitialTouch.y)
			}
			else -> ev.offsetLocation(center.x - mInitialTouch.x, center.y - mInitialTouch.y)
		}

		return pager!!.dispatchTouchEvent(ev)
	}


	private fun init(attrs: AttributeSet?) {
		//clipChildren = false
		setLayerType(View.LAYER_TYPE_SOFTWARE, null)
	}

}