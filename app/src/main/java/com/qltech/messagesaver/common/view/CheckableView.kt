package com.qltech.messagesaver.common.view

import android.content.Context
import android.util.AttributeSet
import android.view.View.OnClickListener
import android.widget.Checkable
import androidx.appcompat.widget.AppCompatImageView

class CheckableView : AppCompatImageView, Checkable {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    )

    private var mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mChecked = false
    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        return drawableState
    }

    fun setOnCheckedChangeListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeListener = listener
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()
        }
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener!!.onCheckedChanged(this, mChecked)
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun setOnClickListener(l: OnClickListener?) {
        val onClickListener = OnClickListener { v ->
            toggle()
            l!!.onClick(v)
        }
        super.setOnClickListener(onClickListener)
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(buttonView: CheckableView, isChecked: Boolean)
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }
}