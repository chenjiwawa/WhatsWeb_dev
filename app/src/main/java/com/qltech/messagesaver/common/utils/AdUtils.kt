package com.qltech.messagesaver.common.utils

import com.qltech.messagesaver.model.enums.HomeEnum
import com.qltech.ui.view.adapter.AdapterData
import java.util.*

object AdUtils {
    fun calculateAdPosition(modelList: List<AdapterData.Data<HomeEnum>>,
                            isAdEnable: Boolean,
                            startIndex: Int,
                            intervalIndex: Int) : List<Int>? {
        if (!isAdEnable || startIndex == -1 || intervalIndex == -1) {
            return null
        }

        val adPositionList = ArrayList<Int>()
        var index: Int
        var i = 0
        while (true) {
            index = startIndex + i * intervalIndex + i
            if (index > modelList.size) {
                break
            }
            adPositionList.add(index)
            i++
        }

        return adPositionList
    }
}