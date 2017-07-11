package com.agilie.poster.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.BaseAdapter
import android.widget.ImageView
import com.agilie.poster.R
import com.agilie.poster.utils.dpToPx


class GalleryAdapter(val context: Context) : BaseAdapter() {

	var imageList = ArrayList<Drawable>()

	override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

		var imageView = ImageView(context)

		val width = dpToPx(context, R.dimen.gallery_image_width)
		val height = dpToPx(context, R.dimen.gallery_image_height)
		val padding = dpToPx(context, R.dimen.gallery_image_padding)
		val params = AbsListView.LayoutParams(width, height)

		if (convertView == null) {
			imageView.apply {
				layoutParams = params
				scaleType = ImageView.ScaleType.CENTER_CROP
				  //setPadding(padding, padding, padding, padding)
			}
		} else {
			imageView = convertView as ImageView
		}

		imageView.setImageDrawable(imageList[position])
		return imageView
	}

	override fun getCount(): Int {
		return imageList.size
	}

	override fun getItem(position: Int): Any? {
		return null
	}

	override fun getItemId(position: Int): Long {
		return 0
	}

}