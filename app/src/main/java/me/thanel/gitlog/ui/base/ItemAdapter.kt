package me.thanel.gitlog.ui.base

import android.content.Context
import android.support.annotation.CallSuper
import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import me.thanel.gitlog.ui.utils.inflate

abstract class ItemAdapter<E, VH : RecyclerView.ViewHolder>(
    private val onItemClickListener: ((E) -> Unit)? = null
) : RecyclerView.Adapter<VH>() {
    protected val items = mutableListOf<E>()

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = parent.inflate(getLayoutResId(viewType))
        onItemClickListener?.let { listener ->
            view.setOnClickListener {
                @Suppress("UNCHECKED_CAST")
                listener(it.tag as E)
            }
        }

        return createViewHolder(view, viewType)
    }

    @LayoutRes
    abstract fun getLayoutResId(viewType: Int): Int

    abstract fun createViewHolder(itemView: View, viewType: Int): VH

    fun addAll(newItems: Iterable<E>) {
        val positionStart = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(positionStart, items.size - positionStart)
    }

    fun add(item: E) {
        items.add(item)
        notifyItemInserted(items.size)
    }

    fun clear() {
        val itemCount = items.size
        items.clear()
        notifyItemRangeRemoved(0, itemCount)
    }

    fun replaceAll(newItems: Iterable<E>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    abstract class ViewHolder<in E>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        protected val context: Context get() = itemView.context

        @CallSuper
        open fun bind(item: E) {
            itemView.tag = item
        }
    }
}
