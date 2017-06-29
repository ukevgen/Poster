package com.agilie.poster.view.fragments

import android.content.Context
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.agilie.poster.R
import com.agilie.poster.adapter.TabsFragmentAdapter
import com.agilie.poster.view.fragments.fill.FillFragment
import com.agilie.poster.view.fragments.filter.FilterFragment
import com.agilie.poster.view.fragments.icons.IconsFragment
import com.agilie.poster.view.fragments.shapebox.ShapeBoxFragment
import com.agilie.poster.view.fragments.text.TextIconFragment

class TabsFragment : Fragment(), TabLayout.OnTabSelectedListener {


	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.fragment_tab, container, false)

		val adapter = TabsFragmentAdapter(childFragmentManager)
		val pager = rootView.findViewById(R.id.view_pager) as ViewPager
		val tabLayout = activity.findViewById(R.id.tab_layout) as TabLayout
		//Add fragments
		adapter.apply {
			addFragment(FillFragment(), "Fill")
			addFragment(FilterFragment(), "Filter")
			addFragment(TextIconFragment(), "TextIcon")
			addFragment(ShapeBoxFragment(), "ShapeBox")
			addFragment(IconsFragment(), "Icons")
		}
		//Set adapter
		pager.adapter = adapter
		//Init TabLayout
		tabLayout.apply {
			setOnTabSelectedListener(this@TabsFragment)
			setupWithViewPager(pager)
			getTabAt(0)?.customView = getTabIndicator(context, R.drawable.ic_fill)
			getTabAt(1)?.customView = getTabIndicator(context, R.drawable.ic_filter)
			getTabAt(2)?.customView = getTabIndicator(context, R.drawable.ic_text)
			getTabAt(3)?.customView = getTabIndicator(context, R.drawable.ic_shape_box)
			getTabAt(4)?.customView = getTabIndicator(context, R.drawable.ic_icons_icon)

		}
		return rootView
	}

	private fun getTabIndicator(context: Context, icon: Int): View {
		val view = LayoutInflater.from(context).inflate(R.layout.tab_header, null) as TextView
		view.setCompoundDrawablesWithIntrinsicBounds(0, icon, 0, 0)
		return view
	}


	override fun onTabReselected(tab: TabLayout.Tab?) {
	}

	override fun onTabUnselected(tab: TabLayout.Tab?) {
	}

	override fun onTabSelected(tab: TabLayout.Tab?) {
	}
}