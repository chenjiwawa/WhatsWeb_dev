package com.qltech.ui.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IdRes
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.qltech.common.utils.XLog
import kotlinx.android.extensions.LayoutContainer

abstract class RecyclerAdapterBase<TYPE : Enum<TYPE>, DATA : AdapterData.Data<TYPE>> :
    RecyclerView.Adapter<RecyclerAdapterBase.ViewHolder<DATA>>(), AdapterData.TypeProvider<TYPE> {

    private val tag = javaClass.simpleName
    private val diffUtilModel: IDiffUtilModel = DiffUtilModel(tag)
    var onItemClickListener: OnItemClickListener<DATA>? = null
    var lifecycle: Lifecycle? = null

    private var list: Collection<DATA> = emptyList()

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.getDataType()?.ordinal ?: 0
    }

    final override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<DATA> {
        return (onCreateViewHolder(parent, getType(viewType)) ?: EmptyViewHolder(parent.context))
            .apply {
                onItemClickListener = this@RecyclerAdapterBase.onItemClickListener
                lifecycle = this@RecyclerAdapterBase.lifecycle
            }
    }

    override fun onBindViewHolder(holder: ViewHolder<DATA>, position: Int) {
        getItem(position)?.run {
            holder.onBindData(this)
        }
    }

    override fun onBindViewHolder(holder: ViewHolder<DATA>, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            getItem(position)?.run {
                diffUtilModel.putNewData(position, this)
                holder.onBindData(this, payloads)
            }
        }
    }

    override fun onViewRecycled(holder: ViewHolder<DATA>) {
        holder.onUnbindData()
    }

    fun getList(): Collection<DATA> {
        return list
    }

    fun setList(list: Collection<DATA>?) {
        diffUtilModel.setList(list)

        val result = DiffUtil.calculateDiff(diffUtilModel.getDiffResult())
        result.dispatchUpdatesTo(this)

        this.list = list ?: emptyList()
    }

    fun getItem(index: Int): DATA? {
        return if (index < 0 || list.isEmpty()) {
            null
        } else {
            list.elementAt(index % list.size)
        }
    }

    fun notifyItemChanged(data: DATA) {
        notifyItemChanged(data, Any())
    }

    fun notifyItemChanged(data: DATA, payload: Any) {
        val index = list.indexOf(data)
        if (index >= 0) {
            notifyItemChanged(index, payload)
        }
    }

    protected fun ViewGroup.createItemView(layoutRes: Int): View {
        val inflater = LayoutInflater.from(context)
        return inflater.inflate(layoutRes, this, false)
    }

    abstract fun onCreateViewHolder(parent: ViewGroup, type: TYPE): ViewHolder<DATA>?

    abstract class ViewHolder<DATA : AdapterData.Data<*>>(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer, DefaultLifecycleObserver {

        protected val tag = javaClass.simpleName
        val context: Context get() = itemView.context
        var lastData: DATA? = null

        var onItemClickListener: OnItemClickListener<DATA>? = null
        var lifecycle: Lifecycle? = null
            set(value) {
                field = value
                value?.addObserver(this)
            }

        fun <T : View> findViewById(@IdRes id: Int): T = itemView.findViewById(id)

        open fun onBindData(data: DATA, payloads: List<Any>) {
            XLog.d(tag, "[onBindData] pos: $adapterPosition, payloads = ${payloads[0]}")
            onBindData(data)
        }

        open fun onBindData(data: DATA) {
            lastData = data
        }

        open fun onUnbindData() {
            lastData = null
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            owner.lifecycle.removeObserver(this)
            lastData?.run {
                onUnbindData()
            }
        }
    }

    interface OnItemClickListener<in DATA> {
        fun onClick(view: View, data: DATA)
    }

    private class EmptyViewHolder<DATA : AdapterData.Data<*>>(context: Context) : ViewHolder<DATA>(View(context))
}
