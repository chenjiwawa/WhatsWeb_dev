package com.qltech.whatsweb.view

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import com.qltech.whatsweb.R

class InternetTipDialog: AlertDialog {

    lateinit var dialogProperties: DialogProperties
    lateinit var ivClose: AppCompatImageView
    lateinit var ivMainIcon: AppCompatImageView
    lateinit var tvTitle: AppCompatTextView
    lateinit var tvMessage: AppCompatTextView
    lateinit var tvOkButton: AppCompatTextView

    constructor(context: Context):super(context, R.style.TipDialogTheme) {
    }

    fun dialogProperties(properties: DialogProperties) {
        this.dialogProperties = properties
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tip_dialog)
        var params = window?.attributes
        params?.width = WindowManager.LayoutParams.WRAP_CONTENT
        params?.height = WindowManager.LayoutParams.WRAP_CONTENT
        params?.gravity = Gravity.CENTER
        params?.horizontalMargin = 0f
        params?.verticalMargin = 0f
        window?.attributes = params
        initView()
    }

    private fun initView() {
        ivClose = findViewById(R.id.iv_close)!!
        ivMainIcon = findViewById(R.id.iv_main_icon)!!
        tvTitle = findViewById(R.id.tv_title)!!
        tvMessage = findViewById(R.id.tv_message)!!
        tvOkButton = findViewById(R.id.tv_ok)!!

        ivClose.setImageResource(dialogProperties.closeId)
        ivMainIcon.setImageResource(dialogProperties.mainIcon)
        tvTitle.setText(dialogProperties.titleTextId)
        tvMessage.setText(dialogProperties.messageTextId)
        tvOkButton.setText(dialogProperties.buttonTextId)
        ivClose.setOnClickListener {
            this.dismiss()
        }
        tvOkButton.setOnClickListener {
            dialogProperties.onButtonClickListener?.onClick(this, 0)
        }
    }

    class DialogProperties {
        internal var closeId:Int = R.drawable.ic_clear
        internal var mainIcon:Int = R.drawable.ic_message_downloader_warning
        internal var titleTextId:Int = R.string.wooops
        internal var messageTextId:Int = R.string.network_reconnect_info
        internal var buttonTextId:Int = R.string.ok
        internal var onButtonClickListener: DialogInterface.OnClickListener? = null
    }

    class Builder {
        internal var dialogProperties: DialogProperties

        constructor() {
            this.dialogProperties = DialogProperties()
        }

        fun closeId(closeId: Int): Builder {
            this.dialogProperties.closeId = closeId
            return this
        }

        fun mainIcon(mainIcon: Int): Builder {
            this.dialogProperties.mainIcon = mainIcon
            return this
        }

        fun titleTextId(titleTextId: Int): Builder {
            this.dialogProperties.titleTextId = titleTextId
            return this
        }

        fun messageTextId(messageTextId: Int): Builder {
            this.dialogProperties.messageTextId = messageTextId
            return this
        }

        fun buttonTextId(buttonTextId: Int): Builder {
            this.dialogProperties.buttonTextId = buttonTextId
            return this
        }

        fun onButtonClickListener(onButtonClickListener: DialogInterface.OnClickListener): Builder {
            this.dialogProperties.onButtonClickListener = onButtonClickListener
            return this
        }

        fun build(context: Context?): InternetTipDialog? {
            context?.let {
                var dialog = InternetTipDialog(it)
                dialog.dialogProperties = dialogProperties
                return dialog
            }
            return null
        }
    }
}