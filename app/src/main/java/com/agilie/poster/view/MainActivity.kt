package com.agilie.poster.view

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.agilie.poster.R

class MainActivity : AppCompatActivity(), MainView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
