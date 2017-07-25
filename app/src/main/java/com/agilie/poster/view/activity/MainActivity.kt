package com.agilie.poster.view.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.TabLayout
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
