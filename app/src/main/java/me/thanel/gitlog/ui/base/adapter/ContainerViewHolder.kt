package me.thanel.gitlog.ui.base.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import kotlinx.android.extensions.LayoutContainer

class ContainerViewHolder(override val containerView: View) :
    RecyclerView.ViewHolder(containerView), LayoutContainer {
    val context: Context get() = containerView.context
}
