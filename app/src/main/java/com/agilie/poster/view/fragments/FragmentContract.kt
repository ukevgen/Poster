package com.agilie.poster.view.fragments

interface FragmentContract {

	interface View {
		fun initView()

		fun onAnimationSettings(show: Boolean)

		fun showRecycler()

		fun hideRecycler()

		fun showSeekBar()

		fun hideSeekBar()

		fun onAnimationAllNavigation()

		fun animateViewElement(view: android.view.View, toYPosition: Float, duration: Long) {
			view.animate()
					.translationY(toYPosition)
					.duration = duration
		}
	}
}