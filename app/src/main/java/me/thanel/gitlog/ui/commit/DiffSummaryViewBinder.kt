package me.thanel.gitlog.ui.commit

import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import kotlinx.android.synthetic.main.item_changes_summary.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.adapter.ContainerViewBinder
import me.thanel.gitlog.ui.base.adapter.ContainerViewHolder
import me.thanel.gitlog.ui.utils.StyleableTag
import me.thanel.gitlog.ui.utils.formatTags

class DiffSummaryViewBinder : ContainerViewBinder<DiffSummary>() {
    override val layoutResource: Int
        get() = R.layout.item_changes_summary

    override fun onBindViewHolder(holder: ContainerViewHolder, item: DiffSummary) {
        val changedFilesCount = item.formattedDiffEntries.size
        val resources = holder.context.resources
        val changedFilesText = resources.getQuantityString(
            R.plurals.changed_files,
            changedFilesCount, changedFilesCount
        )
        val additionsText =
            resources.getQuantityString(R.plurals.additions, item.numAdditions, item.numAdditions)
        val deletionsText =
            resources.getQuantityString(R.plurals.deletions, item.numDeletions, item.numDeletions)
        val addColor = ContextCompat.getColor(holder.context, R.color.diffAddForeground)
        val removeColor = ContextCompat.getColor(holder.context, R.color.diffRemoveForeground)

        holder.summaryTextView.text = holder.context.getString(R.string.diff_summary).formatTags(
            StyleableTag(
                "changed_files",
                changedFilesText,
                StyleSpan(Typeface.BOLD)
            ),
            StyleableTag(
                "additions",
                additionsText,
                StyleSpan(Typeface.BOLD),
                ForegroundColorSpan(addColor)
            ),
            StyleableTag(
                "deletions",
                deletionsText,
                StyleSpan(Typeface.BOLD),
                ForegroundColorSpan(removeColor)
            )
        )
    }
}
