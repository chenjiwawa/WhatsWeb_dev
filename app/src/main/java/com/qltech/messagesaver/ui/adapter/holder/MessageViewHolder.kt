package com.qltech.messagesaver.ui.adapter.holder

import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.qltech.common.extensions.visible
import com.qltech.whatsweb.R
import com.qltech.messagesaver.common.utils.DensityUtils
import com.qltech.messagesaver.common.utils.TimeUtils
import com.qltech.messagesaver.common.view.CheckableView
import com.qltech.firebase.analytics.FirebaseAnalyticHelper
import com.qltech.firebase.analytics.FirebaseEvent
import com.qltech.messagesaver.model.Message
import com.qltech.messagesaver.model.enums.OperateEnum
import com.qltech.messagesaver.model.enums.MessageEnum
import com.qltech.messagesaver.ui.adapter.MessageAdapter
import com.qltech.ui.view.adapter.RecyclerAdapterBase
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.android.synthetic.main.item_message.*

class MessageViewHolder(itemView: View) : RecyclerAdapterBase.ViewHolder<Message>(itemView) {

    companion object {
        const val LAYOUT_RES = R.layout.item_message
    }

    var  onMessageClickListener: MessageAdapter.OnMessageClickListener? = null

    init {
        itemView.setOnClickListener {
            lastData?.let { data ->
                onItemClickListener?.onClick(it, data)
            }
        }
        itemView.setOnLongClickListener {
            lastData?.let { data ->
                 onMessageClickListener?.onSelected(data, !select.isSelected)
            }
            true
        }
        download.setOnClickListener {
            FirebaseAnalyticHelper.logEvent(FirebaseEvent.STATUS_DOWNLOAD)
            lastData?.let { data ->
                 onMessageClickListener?.onDownloadBtnClick(data)
            }
        }
        select.setOnCheckedChangeListener(object : CheckableView.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CheckableView, isChecked: Boolean) {
                lastData?.let { data ->
                    if (data.isSelected != isChecked) {
                         onMessageClickListener?.onSelected(data, isChecked)
                    }
                }
            }

        })
    }

    override fun onBindData(data: Message) {
        super.onBindData(data)

        val multi = MultiTransformation(
            CenterCrop(),
            RoundedCornersTransformation(
                DensityUtils.dp2px(
                    itemView.context,
                    16f
                ), 0, RoundedCornersTransformation.CornerType.ALL
            )
        )

        Glide.with(itemView)
            .load(data.imageUri)
            .apply(RequestOptions.bitmapTransform(multi))
            .into(content)

        time.text = TimeUtils.getDuration(data.lastModify)
        play.visible = MessageEnum.VIDEO == data.type

        when (data.anEnum) {
            OperateEnum.MULTI_DOWNLOAD -> {
                download.visible = false
                select.visible = true
                select.isChecked = data.isSelected
            }
            else -> {
                download.visible = true
                select.visible = false
            }
        }
    }

    override val containerView: View
        get() = super.containerView
}
