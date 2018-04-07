package me.thanel.gitlog.ui.commit

import android.view.LayoutInflater
import android.view.ViewGroup
import me.drakeet.multitype.ItemViewBinder
import me.thanel.gitlog.R

class FormattedDiffEntryViewBinder(
    private val repositoryId: Int,
    private val commitSha: String
) : ItemViewBinder<FormattedDiffEntry, DiffHunkViewHolder>() {
    override fun onCreateViewHolder(
        inflater: LayoutInflater,
        parent: ViewGroup
    ): DiffHunkViewHolder {
        val itemView = inflater.inflate(R.layout.item_diff_hunk, parent, false)
        return DiffHunkViewHolder(itemView, repositoryId, commitSha)
    }

    override fun onBindViewHolder(holder: DiffHunkViewHolder, item: FormattedDiffEntry) {
        holder.bind(item)
    }
}
