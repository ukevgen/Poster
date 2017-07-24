package com.agilie.poster.presenter

interface Presenter {
	fun resume()

	fun pause()

	fun destroy()

	fun stop()

	fun onItemClick()

	fun onProgressOk()

	fun onProgressCancel()
}