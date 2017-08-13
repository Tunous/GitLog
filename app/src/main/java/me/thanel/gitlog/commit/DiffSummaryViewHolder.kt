package me.thanel.gitlog.commit

import android.graphics.Typeface
import android.support.v4.content.ContextCompat
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.View
import android.widget.TextView
import me.thanel.gitlog.R
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.utils.StyleableTag
import me.thanel.gitlog.utils.formatTags
import org.eclipse.jgit.diff.DiffEntry

class DiffSummaryViewHolder(
        itemView: View,
        private val viewModel: CommitViewModel
) : ItemAdapter.ViewHolder<List<DiffEntry>>(itemView) {
    private val summaryTextView = itemView.findViewById<TextView>(R.id.summaryTextView)

    override fun bind(item: List<DiffEntry>) {
        super.bind(item)

        val changedFilesCount = item.size
        val resources = context.resources
        val changedFilesText = resources.getQuantityString(R.plurals.changed_files,
                changedFilesCount, changedFilesCount)
        val diffs = item.flatMap { viewModel.formatDiffEntry(it).split("\n") }
                .dropWhile { !it.startsWith("@@") }
        val additions = diffs.count { it.startsWith("+") }
        val deletions = diffs.count { it.startsWith("-") }
        val additionsText = resources.getQuantityString(R.plurals.additions, additions, additions)
        val deletionsText = resources.getQuantityString(R.plurals.deletions, deletions, deletions)

        val addColor = ContextCompat.getColor(context, R.color.diffAddForeground)
        val removeColor = ContextCompat.getColor(context, R.color.diffRemoveForeground)

        summaryTextView.text = context.getString(R.string.diff_summary).formatTags(
                StyleableTag("changed_files", changedFilesText, StyleSpan(Typeface.BOLD)),
                StyleableTag("additions", additionsText, StyleSpan(Typeface.BOLD),
                        ForegroundColorSpan(addColor)),
                StyleableTag("deletions", deletionsText, StyleSpan(Typeface.BOLD),
                        ForegroundColorSpan(removeColor)))
    }
}

