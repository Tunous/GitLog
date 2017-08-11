package me.thanel.gitlog.repository

import android.view.View
import android.widget.TextView
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import java.io.File

class FileListAdapter(
        onItemClickListener: (File) -> Unit
) : ItemAdapter<File, FileListAdapter.ViewHolder>(onItemClickListener) {
    override fun getLayoutResId(viewType: Int) = R.layout.item_file

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    fun displayContents(file: File) {
        val files = file.listFiles().asIterable()
        replaceAll(files)
    }

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<File>(itemView) {
        private val fileNameView: TextView by lazy { itemView.fileNameView }

        override fun bind(item: File) {
            super.bind(item)
            fileNameView.text = item.name
        }
    }
}