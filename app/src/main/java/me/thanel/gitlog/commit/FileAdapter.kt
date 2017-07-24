package me.thanel.gitlog.commit

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.inflate
import org.eclipse.jgit.diff.DiffEntry

class FileAdapter(private val onItemClickListener: (DiffEntry) -> Unit) : RecyclerView.Adapter<FileAdapter.ViewHolder>() {

    private val items = mutableListOf<DiffEntry>()

    fun addAll(newItems: List<DiffEntry>) {
        val positionStart = items.size
        items.addAll(newItems)
        notifyItemRangeInserted(positionStart, items.size - positionStart)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(parent.inflate(R.layout.item_file), onItemClickListener)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(
            itemView: View,
            onItemClickListener: (DiffEntry) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                val diffEntry = it.tag as DiffEntry
                onItemClickListener(diffEntry)
            }
        }

        private val fileNameView: TextView by lazy { itemView.fileNameView }

        fun bind(entry: DiffEntry) {
            itemView.tag = entry
            fileNameView.text = entry.newPath
        }
    }
}