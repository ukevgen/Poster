package com.agilie.poster.view.fragments.shapebox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R
import com.agilie.poster.utils.PagerTransformer
import com.agilie.poster.view.fragments.BaseFragment
import kotlinx.android.synthetic.main.fragment_shape_box.*

class ShapeBoxFragment : BaseFragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_shape_box, container, false)

		return view
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		init()
		setViewPagerScrollSpeed(view_pager_shape)
	}

	private fun init() {
		//Create View Pager
		view_pager_shape.apply {
			adapter = colorViewsAdapter
			offscreenPageLimit = adapter.count
			clipChildren = false
			setPageTransformer(false, PagerTransformer())
		}
	}
}