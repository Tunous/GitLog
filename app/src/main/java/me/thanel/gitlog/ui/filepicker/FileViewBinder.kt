package me.thanel.gitlog.ui.filepicker

import kotlinx.android.synthetic.main.item_file.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.adapter.ContainerViewBinder
import me.thanel.gitlog.ui.base.adapter.ContainerViewHolder
import java.io.File

class FileViewBinder(
    private val onItemClickListener: (File) -> Unit
) : ContainerViewBinder<File>() {
    override val layoutResource: Int
        get() = R.layout.item_file

    override fun onBindViewHolder(holder: ContainerViewHolder, item: File) {
        holder.fileNameView.text = item.name
        val iconResId = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
        holder.fileNameView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        holder.containerView.setOnClickListener {
            onItemClickListener(item)
        }
    }
}
