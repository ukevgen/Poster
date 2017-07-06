package com.agilie.poster.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import java.util.*

class TabsFragmentAdapter(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {


	private val fragmentList = ArrayList<Fragment>()
	private val fragmentTitleList = ArrayList<String>()

	override fun getItem(position: Int): Fragment {
		return fragmentList[position]
	}

	override fun getCount(): Int {
		return fragmentList.size
	}

	fun addFragment(fragment: Fragment, title: String) {
		fragmentList.add(fragment)
		fragmentTitleList.add(title)
	}

	override fun getPageTitle(position: Int): CharSequence {
		return fragmentTitleList[position]
	}

}
