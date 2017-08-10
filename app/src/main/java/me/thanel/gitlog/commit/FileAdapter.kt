package me.thanel.gitlog.commit

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import org.eclipse.jgit.diff.DiffEntry

class FileAdapter(
        onItemClickListener: (DiffEntry) -> Unit
) : ItemAdapter<DiffEntry, FileAdapter.ViewHolder>(onItemClickListener) {

    override fun getLayoutResId(viewType: Int) = R.layout.item_file

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<DiffEntry>(itemView) {
        private val fileNameView: TextView by lazy { itemView.fileNameView }

        override fun bind(item: DiffEntry) {
            super.bind(item)
            fileNameView.text = "${item.oldPath} -> ${item.newPath}"
        }
    }
}