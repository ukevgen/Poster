package com.agilie.poster.view.activity

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import com.agilie.poster.ImageLoader
import com.agilie.poster.R
import com.agilie.poster.adapter.GalleryAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_photo_gallery.*
import java.io.File


class PhotoGalleryActivity : BaseActivity(), GalleryAdapter.OnItemClickListener {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_photo_gallery)

		val adapter = GalleryAdapter(this, getUserImages())
		adapter.itemListener = this

		val layoutManager = GridLayoutManager(this, 3)

		gallery_recycler.apply {
			this.layoutManager = layoutManager
			this.adapter = adapter
			this.itemAnimator = DefaultItemAnimator()
		}
	}

	override fun onItemClick(path: String?) {
		Glide.with(this).load(File(path))
				.diskCacheStrategy(DiskCacheStrategy.NONE)
				.crossFade()
				.into(photo_preview)
	}

	private fun getUserImages() = ImageLoader.instance.getAllPhotoLocations(this)

}
