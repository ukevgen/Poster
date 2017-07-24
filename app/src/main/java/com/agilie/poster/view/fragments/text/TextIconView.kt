package com.agilie.poster.view.fragments.text

import com.agilie.poster.view.fragments.FragmentContract
import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

@StateStrategyType(SkipStrategy::class)
interface TextIconView : MvpView, FragmentContract.View{

}