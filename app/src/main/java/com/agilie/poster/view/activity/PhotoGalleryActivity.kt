package com.agilie.poster.view.activity

import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.widget.ImageView
import com.agilie.poster.Constants
import com.agilie.poster.ImageLoader
import com.agilie.poster.R
import com.agilie.poster.adapter.AdapterBehavior
import com.agilie.poster.adapter.GalleryAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.android.synthetic.main.activity_photo_gallery.*
import java.io.File


class PhotoGalleryActivity : BaseActivity(), AdapterBehavior.OnPhotoClickListener {

	private var photoPath: String? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_photo_gallery)

		val adapter = GalleryAdapter(this, getUserImages())
		adapter.photoListener = this
		photoPath = adapter.photos[0]

		setImage(photoPath, photo_preview)

		gallery_recycler.apply {
			layoutManager = GridLayoutManager(this@PhotoGalleryActivity, 3)
			this.adapter = adapter
			this.itemAnimator = DefaultItemAnimator()
		}

		back_to_cam_from_gallery.setOnClickListener { onBackPressed() }

		photo_preview.setOnClickListener {
			val intent = getCallingIntent(this, MainActivity::class.java)
			intent.putExtra(Constants.PHOTO_PATH, photoPath)
			startActivity(intent)
		}
	}

	override fun onPhotoItemClick(path: String?) {
		photoPath = path
		setImage(path, photo_preview)
	}

	override fun onBackPressed() {
		super.onBackPressed()
	}

	private fun getUserImages() = ImageLoader.instance.getAllPhotoLocations(this)

	private fun setImage(path: String?, view: ImageView) =
			Glide.with(this).load(File(path))
					.diskCacheStrategy(DiskCacheStrategy.NONE)
					.crossFade()
					.into(view)
}
