package com.qltech.messagesaver.common.view

import android.app.Activity
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.databinding.DataBindingUtil
import com.qltech.whatsweb.R
import com.qltech.whatsweb.databinding.PopupWindowDeleteBinding

class DeletePopupWindow constructor(activity: Activity) :
    PopupWindow() {

    private var listener: OnDetermineListener? = null

    init {
        val binding = DataBindingUtil.inflate<PopupWindowDeleteBinding>(
            LayoutInflater.from(activity),
            R.layout.popup_window_delete,
            null,
            false
        )

        //设置弹窗的宽度和高度
        this.width = LinearLayout.LayoutParams.MATCH_PARENT
        this.height = LinearLayout.LayoutParams.WRAP_CONTENT
        // 实例化一个ColorDrawable颜色为半透明
        val dw = ColorDrawable(-0x00000000)
        this.setBackgroundDrawable(dw)
        //设置可以获得焦点
        this.isFocusable = true
        //设置弹窗内可点击
        this.isTouchable = true
        //设置弹窗外可点击
        this.isOutsideTouchable = true
        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.animationStyle = R.style.popup_window_anim
        this.contentView = binding.root

        binding.delete.setOnClickListener {
            listener?.onDetermine()
        }
    }

    fun setOnDetermineListener(l: OnDetermineListener) {
        listener = l
    }

    interface OnDetermineListener {
        fun onDetermine()
    }
}