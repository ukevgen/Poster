package com.agilie.poster.view.fragments.fill

import android.animation.Animator
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.agilie.poster.Constants
import com.agilie.poster.R
import com.agilie.poster.adapter.AdapterBehavior
import com.agilie.poster.adapter.PhotoSettingsAdapter
import com.agilie.poster.presenter.fill.FillPresenterImpl
import com.agilie.poster.view.activity.MainActivity
import com.agilie.poster.view.fragments.BaseFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_fill.*


class FillFragment : BaseFragment(), FillView, AdapterBehavior.OnItemClickListener {

	private val TAG = "FillFragment"
	private var show = false
	lateinit var fillPresenter: FillPresenterImpl

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.fragment_fill, container, false)

		return view
	}

	override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
		super.onViewCreated(view, savedInstanceState)

		initView()

		fillPresenter = FillPresenterImpl(this)
	}

	override fun initView() {
		val adapter = getAdapter<RecyclerView.ViewHolder>()
		fill_recycler.apply {
			this.adapter = adapter
			layoutManager = LinearLayoutManager(this@FillFragment.context, LinearLayoutManager.HORIZONTAL, false)
			scrollToPosition(1)
		}
		fill_progress_ok.setOnClickListener {
			fillPresenter.onProgressOk()

		}
		fill_progress_cancel.setOnClickListener {
			fillPresenter.onProgressCancel()
		}
	}

	override fun onItemClick(position: Int) {
		onAnimationAllNavigation()
		fillPresenter.onItemClick()
	}

	override fun <VH : RecyclerView.ViewHolder?> getAdapter(): RecyclerView.Adapter<VH>? {
		val settingsImage = context.resources.obtainTypedArray(R.array.settingImages)
		val settingsText = context.resources.getStringArray(R.array.settingsText)
		val adapter = PhotoSettingsAdapter(context, settingsText, settingsImage).apply {
			itemListener = this@FillFragment
		}
		return adapter as RecyclerView.Adapter<VH>
	}

	override fun onAnimationSettings(duration: Long, show: Boolean) {
		when (show) {
			true -> {
				animateViewElement(fill_recycler, 0f, duration)
			}
			false -> {
				animateViewElement(fill_recycler, fill_recycler.bottom.toFloat(), duration)
			}
		}
	}

	override fun onAnimationAllNavigation() {

		if (activity !is MainActivity) {
			return
		}
		activity as AppCompatActivity
		val toolBar = activity.toolbar_main
		val tabLayout = activity.tab_layout
		when (show) {
			true -> {
				// Show toolbar and setting container
				fill_progress_container.visibility = View.GONE
				animateViewElement(view = toolBar, toYPosition = 0f, duration = Constants.DURATION)
				animateViewElement(fill_recycler, 0f, Constants.DURATION)
				animateViewElement(activity.setting_container, 0f, Constants.DURATION)
				animateViewElement(tabLayout, 0f, Constants.DURATION)
				this.show = false
			}
			false -> {
				animateViewElement(view = toolBar, toYPosition = (-toolBar.bottom).toFloat(), duration = Constants.DURATION)
				animateViewElement(fill_recycler, fill_recycler.bottom.toFloat(), Constants.DURATION)
				animateViewElement(activity.setting_container, activity.setting_container.bottom.toFloat(), Constants.DURATION)
				animateViewElement(tabLayout, fill_progress_container, View.VISIBLE, tabLayout.bottom.toFloat(), Constants.DURATION)
				tabLayout.clearAnimation()
				this.show = true
			}
		}
	}

	private var animationListener = object : Animator.AnimatorListener {
		override fun onAnimationRepeat(animation: Animator?) {
			// Empty
		}

		override fun onAnimationEnd(animation: Animator?) {
			fill_progress_container.visibility = View.VISIBLE
		}

		override fun onAnimationCancel(animation: Animator?) {
			// Empty
		}

		override fun onAnimationStart(animation: Animator?) {
			// Empty
		}
	}
}