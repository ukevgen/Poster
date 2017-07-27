package com.agilie.poster.view.fragments

import com.agilie.poster.view.activity.MainActivity

interface FragmentContract {

	interface View {
		fun initView()

		fun onAnimationSettings()

		fun showRecycler()

		fun hideRecycler()

		fun showSeekBar()

		fun hideSeekBar()


		fun animateViewElement(view: android.view.View, toYPosition: Float, duration: Long) {
			view.animate()
					.translationY(toYPosition)
					.duration = duration
		}

		fun onAnimationFragment(selected: MainActivity.TabSelectedStatus)
	}
}