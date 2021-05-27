package com.qltech.ui

import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar

interface IActionBar {

    fun initActionBar(toolBar: Toolbar?)

    fun setActionBarTitle(@StringRes titleRes: Int)

    fun setActionBarTitle(title: String)

    fun setDisplayHomeAsUpEnabled(enable: Boolean)

}