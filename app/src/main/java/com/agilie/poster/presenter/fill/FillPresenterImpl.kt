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
	}

	override fun resume() {
		// Empty
	}

	override fun pause() {
		// Empty
	}

	override fun destroy() {
		// Empty
	}

	override fun stop() {
		// Empty
	}

}