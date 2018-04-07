package me.thanel.gitlog.ui.base.adapter

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.ViewGroup
import me.drakeet.multitype.ItemViewBinder

abstract class ContainerViewBinder<T> : ItemViewBinder<T, ContainerViewHolder>() {
    @get:LayoutRes
    abstract val layoutResource: Int

    final override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup) =
        ContainerViewHolder(
            inflater.inflate(
                layoutResource,
                parent,
                false
            )
        )
            .also(::onInitViewHolder)

    open fun onInitViewHolder(holder: ContainerViewHolder) = Unit
}
