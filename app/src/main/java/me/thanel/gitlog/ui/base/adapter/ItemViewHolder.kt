package me.thanel.gitlog.ui.base.adapter

import android.content.Context
import android.support.annotation.CallSuper
import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ItemViewHolder<in E>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    protected val context: Context get() = itemView.context

    @CallSuper
    open fun bind(item: E) {
        itemView.tag = item
    }
}

