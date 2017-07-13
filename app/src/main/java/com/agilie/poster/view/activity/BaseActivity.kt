package com.agilie.poster.view.activity

import android.app.Fragment
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.agilie.poster.R

open class BaseActivity : AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_base)
	}

	protected fun addFragment(containerViewId: Int, fragment: Fragment) {
		val fragmentTransaction = this.fragmentManager.beginTransaction()
		fragmentTransaction.replace(containerViewId, fragment)
		fragmentTransaction.commit()
	}
}
