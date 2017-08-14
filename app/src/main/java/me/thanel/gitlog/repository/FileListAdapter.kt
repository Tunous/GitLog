package me.thanel.gitlog.repository

import android.view.View
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
        private val fileNameView = itemView.fileNameView

        override fun bind(item: File) {
            super.bind(item)
            fileNameView.text = item.name

            val iconResId = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
            fileNameView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        }
    }
}
