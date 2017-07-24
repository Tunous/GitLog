package me.thanel.gitlog.commit

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.utils.inflate
import org.eclipse.jgit.diff.DiffEntry

class FileAdapter(
        private val onItemClickListener: (DiffEntry) -> Unit
) : ItemAdapter<DiffEntry, FileAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            ViewHolder(parent.inflate(R.layout.item_file), onItemClickListener)

    class ViewHolder(
            itemView: View,
            onItemClickListener: (DiffEntry) -> Unit
    ) : ItemAdapter.ViewHolder<DiffEntry>(itemView) {

        init {
            itemView.setOnClickListener {
                val diffEntry = it.tag as DiffEntry
                onItemClickListener(diffEntry)
            }
        }

        private val fileNameView: TextView by lazy { itemView.fileNameView }

        override fun bind(item: DiffEntry) {
            itemView.tag = item
            fileNameView.text = item.newPath
        }
    }
}