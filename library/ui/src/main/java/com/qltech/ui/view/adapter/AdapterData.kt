package com.qltech.ui.view.adapter

interface AdapterData {
    interface TypeProvider<TYPE : Enum<TYPE>> {
        val dataTypes: Array<TYPE>

        fun getType(viewType: Int): TYPE = dataTypes[viewType]
    }

    interface Data<TYPE : Enum<TYPE>> : DiffUtilModel.IDiffDataHook {

        override fun getDataType(): TYPE

        @Suppress("UNCHECKED_CAST")
        fun <T> getData(): T = this as T
    }
}

data class AdapterDataListWrapper<TYPE : Enum<TYPE>>(
    private val type: TYPE,
    private var list: List<AdapterData.Data<TYPE>> = emptyList()
) : AdapterData.Data<TYPE> {

    override fun getIdentifier(): Long = getDataType().hashCode().toLong()

    override fun getDataType(): TYPE {
        return type
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> getData(): T {
        return list as T
    }
}