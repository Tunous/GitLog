package me.thanel.gitlog.base

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ItemAdapter<in E, VH : ItemAdapter.ViewHolder<E>> : RecyclerView.Adapter<VH>() {
    private val items = mutableListOf<E>()

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])

    fun addAll(newItems: Iterable<E>) {
        val positionStart = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(positionStart, items.size - positionStart)
    }

    fun add(item: E) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun remove(item: E) {
        val position = items.indexOf(item)
        if (position >= 0) {
            items.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun clear() {
        val itemCount = items.size
        items.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    abstract class ViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }
}