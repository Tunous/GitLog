package me.thanel.gitlog.repository.filelist

import android.view.View
import kotlinx.android.synthetic.main.item_file.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter

class GitFileListAdapter(
        onItemClickListener: (GitFile) -> Unit
) : ItemAdapter<GitFile, GitFileListAdapter.ViewHolder>(onItemClickListener) {
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getLayoutResId(viewType: Int) = R.layout.item_file

    override fun createViewHolder(itemView: View, viewType: Int) = ViewHolder(itemView)

    class ViewHolder(itemView: View) : ItemAdapter.ViewHolder<GitFile>(itemView) {
        private val fileNameView = itemView.fileNameView

        override fun bind(item: GitFile) {
            super.bind(item)
            fileNameView.text = item.name

            val iconResId = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
            fileNameView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        }
    }
}
