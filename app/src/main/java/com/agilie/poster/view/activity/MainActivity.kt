package com.agilie.poster.view.activity

import android.os.Bundle
import com.agilie.poster.R

class MainActivity : BaseActivity(), MainView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
