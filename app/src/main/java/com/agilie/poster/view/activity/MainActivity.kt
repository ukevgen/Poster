package com.agilie.poster.view.activity

import android.os.Bundle
import android.support.design.widget.TabLayout
import com.agilie.poster.Constants
import com.agilie.poster.R
import com.agilie.poster.view.fragments.FragmentContract
import com.agilie.poster.view.fragments.TabsFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainView {

	private var TAG = "MainActivity"
	private var show = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		initViews()
	}

	private fun initViews() {

		tab_layout.addOnTabSelectedListener(tabSelectorListener)

		setSupportActionBar(toolbar_main)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)

		addFragment(R.id.fragment_container, TabsFragment())
		/*supportFragmentManager
				.beginTransaction()
				.replace(R.id.fragment_container, TabsFragment())
				.commit()*/
	}

	private var tabSelectorListener = object : TabLayout.OnTabSelectedListener {
		override fun onTabReselected(tab: TabLayout.Tab) {
			val tabsContainer = supportFragmentManager.findFragmentById(R.id.fragment_container)
			when (tabsContainer) {
				is TabsFragment -> {
					val fragment = tabsContainer.adapter.getItem(tab.position) as FragmentContract.View
					onAnimateContainer(fragment)
				}
			}
		}

		override fun onTabUnselected(tab: TabLayout.Tab) {
			// Empty
		}

		override fun onTabSelected(tab: TabLayout.Tab) {
			// Empty
		}
	}

	private fun onAnimateContainer(fragment: FragmentContract.View) {
		when (show) {
			true -> {
				setting_container.animate()
						.translationY(0f)
						.duration = Constants.DURATION
				fragment.onAnimationSettings(Constants.DURATION, show)
				this.show = false
			}
			false -> {
				setting_container.animate()
						.translationY(setting_container.bottom.toFloat())
						.duration = Constants.DURATION
				fragment.onAnimationSettings(Constants.DURATION, show)
				this.show = true
			}
		}
	}
}
