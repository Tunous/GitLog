package me.thanel.gitlog.repository

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter

class FileListAdapter(
        onItemClickListener: (File) -> Unit
) : ItemAdapter<File, FileListAdapter.ViewHolder>(onItemClickListener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getLayoutResId(viewType: Int) = R.layout.item_file

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<File>(itemView) {
        private val fileNameView: TextView by lazy { itemView.fileNameView }

        override fun bind(item: File) {
            super.bind(item)
            fileNameView.text = item.name
        }
    }
}

data class File(val path: String, val isDirectory: Boolean, val name: String)