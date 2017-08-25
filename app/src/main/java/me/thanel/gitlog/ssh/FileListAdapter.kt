package me.thanel.gitlog.ssh

import android.view.View
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import java.io.File

class FileListAdapter(
        onItemClickListener: (File) -> Unit
) : ItemAdapter<File, FileListAdapter.ViewModel>(onItemClickListener) {
    override fun createViewHolder(itemView: View, viewType: Int) = ViewModel(itemView)

    override fun onBindViewHolder(holder: ViewModel, position: Int) {
        holder.bind(items[position])
    }

    override fun getLayoutResId(viewType: Int) = R.layout.item_file

    class ViewModel(itemView: View) : ItemAdapter.ViewHolder<File>(itemView) {
        private val fileNameView = itemView.fileNameView

        override fun bind(item: File) {
            super.bind(item)
            fileNameView.text = item.name

            val iconResId = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
            fileNameView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        }
    }
}