package com.agilie.poster.presenter.camera

import com.agilie.poster.presenter.Presenter

interface CameraContract {
	interface Behavior : Presenter {
		fun changeCamera()
		fun closeCamera()
		fun takePicture()
	}
}