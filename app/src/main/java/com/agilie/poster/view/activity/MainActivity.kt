package com.agilie.poster.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.TabLayout
import android.support.transition.AutoTransition
import android.support.transition.Transition
import android.support.transition.TransitionManager
import android.util.Log
import android.view.View
import com.agilie.poster.Constants
import com.agilie.poster.R
import com.agilie.poster.view.fragments.FragmentContract
import com.agilie.poster.view.fragments.TabsFragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : BaseActivity(), MainView {

	private var TAG = "MainActivity"
	private var show = false

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		intent.extras?.let {
			val photoPath = it.getString(Constants.PHOTO_PATH)
			setUserPhoto(photoPath)
		}
		initViews()
	}

	override fun onResume() {
		super.onResume()
	}

	override fun onBackPressed() {
		val intent = getCallingIntent(this, PhotoGalleryActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
		startActivity(intent)
		finish()
	}

	private fun setUserPhoto(photoPath: String?) {
		Glide.with(this).load(File(photoPath))
				.crossFade()
				.placeholder(R.drawable.ic_load_image)
				.diskCacheStrategy(DiskCacheStrategy.ALL)
				.into(user_photo)
	}

	private fun initViews() {

		tab_layout.addOnTabSelectedListener(tabSelectorListener)

		setSupportActionBar(toolbar_main)
		supportActionBar?.apply {
			setDisplayHomeAsUpEnabled(true)
			setDisplayShowTitleEnabled(false)
			setHomeAsUpIndicator(R.drawable.ic_back_to_cam_icon)
		}

		toolbar_main.setNavigationOnClickListener { onBackPressed() }

		addFragment(R.id.fragment_container, TabsFragment())

	}

	private var tabSelectorListener = object : TabLayout.OnTabSelectedListener {
		override fun onTabReselected(tab: TabLayout.Tab) {
			onTabClick(tab)
			Log.d(TAG, "reselected")
		}

		override fun onTabUnselected(tab: TabLayout.Tab) {

			onTabClick(tab)
			//val layout = tab.customView

			//hideTabIndicator(layout)
		}

		override fun onTabSelected(tab: TabLayout.Tab) {

			Log.d(TAG, "selected")
		}
	}

	private fun onTabClick(tab: TabLayout.Tab) {
		val layout = tab.customView
		val fragment = getCurrentFragment(tab)

		when (show) {
			true -> showTabIndicator(layout)
			false -> hideTabIndicator(layout)
		}

		onAnimateContainer(fragment)
	}

	private fun getCurrentFragment(tab: TabLayout.Tab): FragmentContract.View? {
		val tabsContainer = supportFragmentManager.findFragmentById(R.id.fragment_container)
		when (tabsContainer) {
			is TabsFragment -> {
				val fragment = tabsContainer.adapter.getItem(tab.position) as FragmentContract.View
				return fragment
			}
			else -> return null
		}
	}

	private fun showTabIndicator(layout: View?) {
		val set = ConstraintSet()
		layout?.let {
			it as ConstraintLayout
			set.clone(it)
			set.setVisibility(R.id.line_view, View.VISIBLE)
			set.applyTo(it)
			TransitionManager.beginDelayedTransition(it)
			set.constrainWidth(R.id.line_view, 0)
			set.applyTo(it)
		}
	}

	private fun hideTabIndicator(layout: View?) {

		val set = ConstraintSet()
		layout?.let {
			it as ConstraintLayout
			set.clone(it)
			val transition = AutoTransition()
			transition.addListener(object : Transition.TransitionListener {
				override fun onTransitionEnd(transition: Transition) {
					set.setVisibility(R.id.line_view, View.INVISIBLE)
					set.applyTo(it)
				}

				override fun onTransitionResume(transition: Transition) {
				}

				override fun onTransitionPause(transition: Transition) {
				}

				override fun onTransitionCancel(transition: Transition) {
				}

				override fun onTransitionStart(transition: Transition) {

				}
			})

			TransitionManager.beginDelayedTransition(it, transition)
			set.constrainWidth(R.id.line_view, 1)
			set.applyTo(it)
		}
	}

	private fun onAnimateContainer(fragment: FragmentContract.View?) {

		when (show) {
			true -> {
				fragment?.onAnimationSettings(show)
				this.show = false
			}
			false -> {
				fragment?.onAnimationSettings(show)
				this.show = true
			}
		}
	}
}
