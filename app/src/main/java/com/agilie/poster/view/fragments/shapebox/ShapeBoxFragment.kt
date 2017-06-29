package com.agilie.poster.view.fragments.shapebox

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R

class ShapeBoxFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_shape_box, container, false)

		return view
	}
}