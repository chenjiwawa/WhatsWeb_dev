package com.qltech.ui.view

import android.content.Context
import android.util.AttributeSet
import android.view.WindowInsets
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.qltech.ui.R

class XToolbar(
    context: Context,
    attrs: AttributeSet?,
    defStyleAttr: Int
) : Toolbar(context, attrs, defStyleAttr) {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(
        context,
        attrs,
        androidx.appcompat.R.attr.toolbarStyle
    )

    private val titleTextView: TextView

    companion object {
        private var windowInsets: WindowInsets? = null
    }

    init {
        inflate(context, R.layout.view_toolbar, this)

        titleTextView = findViewById(R.id.toolbar_title)
        windowInsets?.run { onApplyWindowInsets(this) }
    }

    override fun setTitle(title: CharSequence?) {
        titleTextView.text = title
    }

    override fun onApplyWindowInsets(insets: WindowInsets?): WindowInsets {
        windowInsets = WindowInsets(insets)
        return super.onApplyWindowInsets(insets)
    }


    fun setTitleTextOnClickListener(call: () -> Unit) {
        titleTextView.setOnClickListener {
            call.invoke()
        }
    }

}