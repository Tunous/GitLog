package me.thanel.gitlog.ui.repository.filelist

import kotlinx.android.synthetic.main.item_file.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.adapter.ContainerViewBinder
import me.thanel.gitlog.ui.base.adapter.ContainerViewHolder

class GitFileViewBinder(
    private val onItemClickListener: (GitFile) -> Unit
) : ContainerViewBinder<GitFile>() {
    override val layoutResource: Int
        get() = R.layout.item_file

    override fun onBindViewHolder(holder: ContainerViewHolder, item: GitFile) {
        holder.fileNameView.text = item.name
        val iconResId = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
        holder.fileNameView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        holder.containerView.setOnClickListener {
            onItemClickListener(item)
        }
    }
}
