package com.qltech.messagesaver.model

import android.os.Parcelable
import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.messagesaver.model.enums.OperateEnum
import com.qltech.messagesaver.model.enums.MessageEnum
import com.qltech.ui.view.adapter.AdapterData
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Message(
    val path: String = "",
    val name: String = "",
    val imageUri: String = "",
    val type: MessageEnum = MessageEnum.IMAGE,
    val lastModify: Long = 0,
    val isSaved: Boolean = false,
    var anEnum: OperateEnum = OperateEnum.NONE,
    override var isEnable: Boolean = true,
    override var isSelected: Boolean = false,
    var isAdMob: Boolean = false
) : AdapterData.Data<HomeEnum>, Selectable, Parcelable {

    override fun getIdentifier(): Long {
        return path.hashCode().toLong()
    }

    override fun getDataType(): HomeEnum {
        return if (isAdMob) {
            HomeEnum.AD_STATUS
        } else {
            if (isSaved) {
                HomeEnum.LOCAL_STATUS
            } else {
                HomeEnum.STATUS
            }
        }
    }
}
