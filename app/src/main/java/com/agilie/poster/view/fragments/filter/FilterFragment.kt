package com.agilie.poster.view.fragments.filter

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.R

class FilterFragment : Fragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_filter, container, false)

		return view
	}
}