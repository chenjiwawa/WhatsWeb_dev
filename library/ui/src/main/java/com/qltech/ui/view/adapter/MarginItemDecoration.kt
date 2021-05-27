package com.qltech.ui.view.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MarginItemDecoration(
    edgeSpacing: Int,
    spacing: Int = edgeSpacing
) : RecyclerView.ItemDecoration() {

    private var tbEdgeSpacing = edgeSpacing
    private var lrEdgeSpacing = edgeSpacing

    private var tbSpacing = spacing
    private var lrSpacing = spacing

    var isItemNeedSkip: (item: AdapterData.Data<*>) -> Boolean = { false }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val adapter = parent.adapter as? RecyclerAdapterBase<*, *>
        val childPosition = findViewHolder(view, parent).adapterPosition

        if (isNeedSkip(adapter, childPosition)) return

        val layoutManager = parent.layoutManager

        if (layoutManager is GridLayoutManager) {
            setGridOffset(outRect, layoutManager, adapter, childPosition)
        } else if (layoutManager is LinearLayoutManager) {
            setLinearOffset(outRect, layoutManager, adapter, childPosition)
        }
    }

    private fun setLinearOffset(
        outRect: Rect,
        layoutManager: LinearLayoutManager,
        adapter: RecyclerAdapterBase<*, *>?,
        childPosition: Int
    ) {
        val itemCount = adapter?.itemCount ?: 0

        var top = tbEdgeSpacing
        var left = lrEdgeSpacing
        var right = lrEdgeSpacing
        var bottom = tbEdgeSpacing

        if (layoutManager.canScrollVertically()) {
            //检查上一个是否跳过
            if (!isNeedSkip(adapter, childPosition - 1)) {
                top = 0
            }

            if (childPosition < itemCount - 1) {
                bottom = tbSpacing
            }
        } else {
            //检查上一个是否跳过
            if (!isNeedSkip(adapter, childPosition - 1)) {
                left = 0
            }

            if (childPosition < itemCount - 1) {
                right = lrSpacing
            }
        }

        outRect.top = top
        outRect.left = left
        outRect.right = right
        outRect.bottom = bottom
    }

    private fun setGridOffset(
        outRect: Rect,
        layoutManager: GridLayoutManager,
        adapter: RecyclerAdapterBase<*, *>?,
        childPosition: Int
    ) {
        val spanCount = layoutManager.spanCount

        val itemCount = adapter?.itemCount ?: 0
        val spanIndex = layoutManager.spanSizeLookup.getSpanIndex(childPosition, spanCount)
        val rowCount = itemCount / spanCount + 1
        val rowIndex = layoutManager.spanSizeLookup.getSpanGroupIndex(childPosition, spanCount)

        var top = tbEdgeSpacing
        var left = lrEdgeSpacing
        var right = lrEdgeSpacing
        var bottom = tbEdgeSpacing

        if (layoutManager.canScrollVertically()) {
            if (rowIndex > 0) {
                top = 0
            }

            if (rowIndex < rowCount - 1) {
                bottom = tbSpacing
            }

            if (spanIndex > 0) {
                left = lrSpacing / 2
            }

            if (spanIndex < spanCount - 1) {
                right = lrSpacing / 2
            }
        } else {
            if (rowIndex > 0) {
                left = 0
            }

            if (rowIndex < rowCount - 1) {
                right = lrSpacing
            }

            if (spanIndex > 0) {
                top = lrSpacing / 2
            }

            if (spanIndex < spanCount - 1) {
                bottom = lrSpacing / 2
            }
        }

        outRect.top = top
        outRect.left = left
        outRect.right = right
        outRect.bottom = bottom
    }

    private fun isNeedSkip(adapter: RecyclerAdapterBase<*, *>?, position: Int): Boolean {
        val item = adapter?.getItem(position)
        return null == item || isItemNeedSkip(item)
    }

    private fun findViewHolder(view: View, parent: RecyclerView): RecyclerView.ViewHolder {
        return parent.getChildViewHolder(view)
    }
}