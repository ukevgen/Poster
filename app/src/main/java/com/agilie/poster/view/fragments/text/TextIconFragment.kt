package com.agilie.poster.view.fragments.text

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.util.Log
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


class TextIconFragment : BaseFragment(), View.OnClickListener, ViewPager.OnPageChangeListener {

	private var addTextOpen = false
	private lateinit var showTextButton: Animation
	private lateinit var hideTextButton: Animation
	private lateinit var showText: Animation
	private lateinit var hideText: Animation
	private var viewPagerState = PropertiesType.COLOR

	enum class PropertiesType {
		COLOR, FONT, SIZE
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

	override fun onPageScrollStateChanged(state: Int) {
		// Empty
	}

	override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
		// Empty
	}

	private var colorAdapterPosition = 0
	private var fontAdapterPosition = 0

	override fun onPageSelected(position: Int) {
		if (viewPagerState == PropertiesType.COLOR) {
			val color = colorViewsAdapter.getView(position).tag as Int
			text_description.setTextColor(color)
			colorAdapterPosition = position
			Log.d("TAG", "$position + color  $colorAdapterPosition")
		}
		if (viewPagerState == PropertiesType.FONT) {
			val font = fontViewsAdapter.getView(position).tag as String
			val typeface = Typeface.createFromAsset(context.assets, font)
			text_description.typeface = typeface
			fontAdapterPosition = position
			Log.d("TAG", "$position + color  $colorAdapterPosition  font $fontAdapterPosition")
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
			addOnPageChangeListener(this@TextIconFragment)
		}
		// Set default animation
		startTextPropertiesAnimation()
		// Set OnClickListeners
		add_text.setOnClickListener { startTextPropertiesAnimation() }
		// Show or hide other ui elements
		color_text_disable.setOnClickListener {
			changePropertiesVisibility(PropertiesType.COLOR)
		}

		font_text_disable.setOnClickListener {
			changePropertiesVisibility(PropertiesType.FONT)
		}

		button_details.setOnClickListener { showEditText() }
		button_title.setOnClickListener { showEditText() }

		button_trash.setOnClickListener { clearCurrentChanges() }

		text_description.setOnClickListener { showPropertiesPanel() }

	}

	private fun changePropertiesVisibility(type: PropertiesType) {
		when (type) {
			PropertiesType.COLOR -> {
				font_text_enable.visibility = View.INVISIBLE
				font_text_disable.visibility = View.VISIBLE
				color_text_enable.visibility = View.VISIBLE
				color_text_disable.visibility = View.INVISIBLE
			}
			PropertiesType.FONT -> {
				font_text_disable.visibility = View.INVISIBLE
				font_text_enable.visibility = View.VISIBLE
				color_text_enable.visibility = View.INVISIBLE
				color_text_disable.visibility = View.VISIBLE
			}
		}
		viewPagerState = type
		setViewPagerAdapter(type)
	}

	private fun showPropertiesPanel() {
		size_text_disable.visibility = View.VISIBLE
		font_text_disable.visibility = View.VISIBLE
		color_text_enable.visibility = View.VISIBLE
		pager_container_text.visibility = View.VISIBLE
		// Setup correct adapter
		setViewPagerAdapter(PropertiesType.COLOR)

	}

	private fun clearCurrentChanges() {
		text_description.apply {
			visibility = View.GONE
			setText(context.resources.getText(R.string.title))
		}
		button_trash.visibility = View.INVISIBLE
		size_text_disable.visibility = View.INVISIBLE
		font_text_disable.visibility = View.INVISIBLE
		color_text_enable.visibility = View.INVISIBLE
		pager_container_text.visibility = View.INVISIBLE
		color_text_disable.visibility = View.INVISIBLE
		font_text_enable.visibility = View.INVISIBLE
		// Show text properties
		//startTextPropertiesAnimation()
	}

	private fun showEditText() {
		text_description.visibility = View.VISIBLE
		// Hide text properties
		startTextPropertiesAnimation()
		// Show undo button
		button_trash.visibility = View.VISIBLE
	}

	private fun setViewPagerAdapter(any: Any) {
		val layoutParams = view_pager_text.layoutParams
		when (any) {
			PropertiesType.COLOR -> {
				layoutParams.height = dpToPx(context, R.dimen.color_view_height)
				layoutParams.width = dpToPx(context, R.dimen.color_view_width)
				view_pager_text.apply {
					this.layoutParams = layoutParams
					adapter = colorViewsAdapter
					// Set previous position
					currentItem = colorAdapterPosition
				}
			}
			PropertiesType.FONT -> {
				layoutParams.height = dpToPx(context, R.dimen.font_text_size)
				layoutParams.width = dpToPx(context, R.dimen.font_text_size)
				view_pager_text.apply {
					this.layoutParams = layoutParams
					adapter = fontViewsAdapter
					currentItem = fontAdapterPosition
				}
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