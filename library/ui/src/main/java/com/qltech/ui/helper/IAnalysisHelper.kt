package com.qltech.ui.helper

import android.content.Context
import java.io.Serializable

interface IAnalysisHelper {

    /**
     * 页面统计
     * @param pageName 画面名称
     */
    fun onPageBegin(context: Context, pageName: String)

    /**
     * 页面统计
     * @param pageName 画面名称
     */
    fun onPageEnd(context: Context, pageName: String)

    /**
     * 事件统计
     * @param bean 分析专用Bean
     * @see AnalysisEvent 详细内容请看
     */
    fun send(context: Context, bean: AnalysisEvent)

}

data class AnalysisEvent(
    var event: String = "",
    var label: String = "",
    var propertyMap: Map<String, Any> = HashMap()
) : Serializable {

    fun addProperty(vararg property: Pair<String, Any>) {
        propertyMap = propertyMap.toMutableMap().apply {
            putAll(property)
        }
    }

}
