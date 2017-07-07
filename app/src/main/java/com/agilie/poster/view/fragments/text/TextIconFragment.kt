package com.agilie.poster.view.fragments.text

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.agilie.poster.R
import com.agilie.poster.utils.PagerTransformer
import com.agilie.poster.utils.dpToPx
import com.agilie.poster.view.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_text.*

class TextIconFragment : BaseFragment(), View.OnClickListener {

	private var addTextOpen = false
	private lateinit var showTextButton: Animation
	private lateinit var hideTextButton: Animation
	private lateinit var showText: Animation
	private lateinit var hideText: Animation


	enum class AdapterType {
		COLOR, FONT
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_text, container, false)

		return view
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)
		init()
		setViewPagerScrollSpeed(view_pager_text)
	}

	override fun onClick(v: View) {
		when (v) {
			color_text_enable -> {

			}
		}

	}

	private fun init() {
		// Load Animations
		showTextButton = AnimationUtils.loadAnimation(context.applicationContext, R.anim.translate_x_show)
		hideTextButton = AnimationUtils.loadAnimation(context.applicationContext, R.anim.translate_x_hide)
		showText = AnimationUtils.loadAnimation(context.applicationContext, R.anim.text_show)
		hideText = AnimationUtils.loadAnimation(context.applicationContext, R.anim.text_hide)

		// Create View Pager
		view_pager_text.apply {

			clipChildren = false
			setPageTransformer(false, PagerTransformer())
		}
		// Set default animation
		startTextPropertiesAnimation()
		// Set OnClickListeners
		add_text.setOnClickListener { startTextPropertiesAnimation() }
		color_text_enable.setOnClickListener {
			// Setup correct adapter
			setViewPagerAdapter(AdapterType.COLOR)
			// Show or hide other ui elements
			onClick(color_text_enable)
		}
		font_text_enable.setOnClickListener {
			setViewPagerAdapter(AdapterType.FONT)
			onClick(color_text_enable)
		}

	}

	private fun setViewPagerAdapter(any: Any) {
		val layoutParams = view_pager_text.layoutParams
		when (any) {
			AdapterType.COLOR -> {
				layoutParams.height = dpToPx(context, R.dimen.color_view_height)
				layoutParams.width = dpToPx(context, R.dimen.color_view_width)
				view_pager_text.layoutParams = layoutParams
				view_pager_text.adapter = colorViewsAdapter
			}
			AdapterType.FONT -> {
				layoutParams.height = dpToPx(context, R.dimen.font_text_size)
				layoutParams.width = dpToPx(context, R.dimen.font_text_size)
				view_pager_text.layoutParams = layoutParams
				view_pager_text.adapter = fontViewsAdapter
			}
		}
		view_pager_text.offscreenPageLimit = view_pager_text.adapter.count
	}

	private fun startTextPropertiesAnimation() {

		when (addTextOpen) {
			true -> {
				button_details.startAnimation(showTextButton)
				button_title.startAnimation(showTextButton)
				text_title.startAnimation(showText)
				text_details.startAnimation(showText)
				addTextOpen = false
			}
			else -> {
				button_details.startAnimation(hideTextButton)
				button_title.startAnimation(hideTextButton)
				text_title.startAnimation(hideText)
				text_details.startAnimation(hideText)
				addTextOpen = true
			}
		}

	}
}