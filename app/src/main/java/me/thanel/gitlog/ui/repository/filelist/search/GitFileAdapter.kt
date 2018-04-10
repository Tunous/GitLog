package me.thanel.gitlog.ui.repository.filelist.search

import android.support.v7.recyclerview.extensions.ListAdapter
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_file.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.adapter.ContainerViewHolder
import me.thanel.gitlog.ui.repository.filelist.GitFile
import me.thanel.gitlog.ui.utils.inflate

class GitFileAdapter(
    private val onItemClickListener: (GitFile) -> Unit
) : ListAdapter<GitFile, ContainerViewHolder>(
    GitFile.DIFF_CALLBACK
) {
    private var originalList: List<GitFile>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContainerViewHolder =
        ContainerViewHolder(parent.inflate(R.layout.item_file))

    override fun onBindViewHolder(holder: ContainerViewHolder, position: Int) {
        val item = getItem(position)
        holder.fileNameView.text = item.name
        val iconResId = if (item.isDirectory) R.drawable.ic_folder else R.drawable.ic_file
        holder.fileNameView.setCompoundDrawablesWithIntrinsicBounds(iconResId, 0, 0, 0)
        holder.containerView.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun submitList(list: List<GitFile>?) {
        originalList = list
        super.submitList(list)
    }

    fun filterItems(text: CharSequence?) {
        val newItems = if (text != null) {
            originalList?.filter { it.name.contains(text, true) }
        } else {
            originalList
        }
        super.submitList(newItems)
    }
}
