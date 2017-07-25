package com.agilie.poster.view.fragments

import android.animation.Animator

interface FragmentContract {

	interface View {
		fun initView()

		fun onAnimationSettings(duration: Long, show: Boolean)

		fun onAnimationAllNavigation()

		fun animateViewElement(view: android.view.View, toYPosition: Float, duration: Long) {
			view.animate()
					.translationY(toYPosition)
					.duration = duration
		}

		fun animateViewElement(view: android.view.View, hiddenView: android.view.View,
		                       visible: Int,
		                       toYPosition: Float, duration: Long) {

			view.animate()
					.setListener(object : Animator.AnimatorListener {
						override fun onAnimationRepeat(animation: Animator?) {
							// Empty
						}

						override fun onAnimationEnd(animation: Animator?) {
							hiddenView.visibility = visible
							animation?.cancel()
						}

						override fun onAnimationCancel(animation: Animator?) {
							// Empty
						}

						override fun onAnimationStart(animation: Animator?) {
							// Empty
						}
					})
					.translationY(toYPosition)
					.duration = duration
		}
	}
}