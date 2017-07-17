package com.agilie.poster.presenter.camera

import com.agilie.poster.presenter.Presenter

interface CameraContract {
	interface CameraBehavior : Presenter {
		fun changeCamera()
		fun closeCamera()
		fun takePicture()
		fun changeFlashMode(value: String)
		fun startPreview()
	}
}