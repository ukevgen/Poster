package com.agilie.poster.presenter.fill

import com.agilie.poster.view.fragments.fill.FillView

class FillPresenterImpl(val view: FillView) : FillPresenter {

	override fun onProgressOk() {
		view.onAnimationAllNavigation()
	}

	override fun onProgressCancel() {
		view.onAnimationAllNavigation()
	}

	override fun onItemClick() {
		view.onAnimationAllNavigation()
	}

	override fun resume() {
		// Empty
	}

	override fun pause() {
		view.onAnimationAllNavigation()
	}

	override fun destroy() {
		// Empty
	}

	override fun stop() {
		// Empty
	}

	fun onHideSeekBar() {
		view.hideSeekBar()
	}

	fun onHideRecycler() {
		view.hideRecycler()
	}

}