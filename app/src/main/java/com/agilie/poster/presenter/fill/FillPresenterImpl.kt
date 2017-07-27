package com.agilie.poster.presenter.fill

import com.agilie.imagecontrols.settings.ImageSettings
import com.agilie.poster.view.fragments.fill.FillView

class FillPresenterImpl(val view: FillView, val imageSettings: ImageSettings) : FillPresenter {

	override fun onProgressOk() {
		view.onAnimationSettings()
	}

	override fun onProgressCancel() {
		view.onAnimationSettings()
	}

	override fun onItemClick() {
		//view.onAnimationAllNavigation()
	}

	override fun resume() {
		view.hideSeekBar()
		view.hideRecycler()
	}

	override fun pause() {
		//view.onAnimationAllNavigation()
	}

	override fun destroy() {
		// Empty
	}

	override fun stop() {
		// Empty
	}

	fun onProgressChanged(value: Float) {
		val paint = imageSettings.saturation(value)
		view.updateImage(paint, imageSettings.bitmap)
	}

}