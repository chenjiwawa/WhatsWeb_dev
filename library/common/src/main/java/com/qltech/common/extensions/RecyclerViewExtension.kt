package com.qltech.common.extensions

import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.SCROLL_STATE_IDLE

fun RecyclerView.addLoadMoreListener(block: () -> Unit) {
    //When user stop scroll and scroll to end the last item will toggle load to end
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (!recyclerView.canScrollVertically(RecyclerView.VERTICAL) && newState == SCROLL_STATE_IDLE) {
                block()
            }
        }
    })
}