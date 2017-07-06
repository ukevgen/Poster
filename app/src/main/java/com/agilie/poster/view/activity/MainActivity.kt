package com.agilie.poster.view.activity

import android.os.Bundle
import com.agilie.poster.R
import com.agilie.poster.view.fragments.TabsFragment

class MainActivity : BaseActivity(), MainView {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		initViews()

	}

	private fun initViews() {

		supportFragmentManager
				.beginTransaction()
				.replace(R.id.fragment_container, TabsFragment())
				.commit()
	}
}
