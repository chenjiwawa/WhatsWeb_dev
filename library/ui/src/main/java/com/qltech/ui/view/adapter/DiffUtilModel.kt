package com.qltech.ui.view.adapter

import androidx.recyclerview.widget.DiffUtil
import com.qltech.common.utils.XLog

class DiffUtilModel(
    private val tag: String
) : IDiffUtilModel {

    private var oldItemList: List<DiffItem> = emptyList()
    private var newItemList: MutableList<DiffItem> = ArrayList()

    override fun setList(list: Collection<IDiffDataHook>?) {
        oldItemList = newItemList
        newItemList = toItemList(list).toMutableList()
    }

    override fun putNewData(index: Int, data: IDiffDataHook) {
        if (index < newItemList.size) {
            val oldData = newItemList[index]
            val newData = data.toDiffItem()
            XLog.d(tag, "[putNewData] old: $oldData -> new: $newData")
            newItemList[index] = newData
        }
    }

    override fun getDiffResult(): DiffUtil.Callback {
        val result = DiffResult(oldItemList, newItemList)
        XLog.d(tag, "[getDiffResult] old: ${result.oldListSize} -> new: ${result.newListSize}")
        return result
    }

    private fun toItemList(list: Collection<IDiffDataHook>?): List<DiffItem> {
        return list?.map {
            it.toDiffItem()
        } ?: emptyList()
    }

    private class DiffResult(
        private val oldList: List<DiffItem>,
        private val newList: List<DiffItem>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].identifier == newList[newItemPosition].identifier
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].hashCode == newList[newItemPosition].hashCode &&
                oldList[oldItemPosition].source === newList[newItemPosition].source
        }

        override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
            return if (oldList[oldItemPosition].dataType == newList[newItemPosition].dataType) {
                Any()
            } else {
                null
            }
        }
    }

    private data class DiffItem(val identifier: Long, val dataType: Any, val hashCode: Int, val source: IDiffDataHook)

    interface IDiffDataHook {
        fun getIdentifier(): Long
        fun getDataType(): Any
    }

    private fun IDiffDataHook.toDiffItem(): DiffItem = DiffItem(getIdentifier(), getDataType(), hashCode(), this)
}

interface IDiffUtilModel {
    fun setList(list: Collection<DiffUtilModel.IDiffDataHook>?)
    fun putNewData(index: Int, data: DiffUtilModel.IDiffDataHook)
    fun getDiffResult(): DiffUtil.Callback
}