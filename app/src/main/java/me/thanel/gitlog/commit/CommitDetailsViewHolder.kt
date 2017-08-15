package me.thanel.gitlog.commit

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.view.View
import kotlinx.android.synthetic.main.item_commit_details.view.*
import me.thanel.gitlog.base.ItemAdapter
import me.thanel.gitlog.utils.ClickableShaSpan
import me.thanel.gitlog.utils.SHORT_SHA_LENGTH
import org.eclipse.jgit.revwalk.RevCommit

class CommitDetailsViewHolder(
        itemView: View,
        private val repositoryId: Int
) : ItemAdapter.ViewHolder<RevCommit>(itemView) {
    private val commitMessageView = itemView.commitMessageView
    private var commitShaRegex = Regex("""[0-9a-f]{40}""")

    override fun bind(item: RevCommit) {
        super.bind(item)
        commitMessageView.text = formatMessage(item)

        // Setting this on each bind fixes issue where text selection stops working
        commitMessageView.setTextIsSelectable(true)
        commitMessageView.isFocusable = true
    }

    private fun formatMessage(item: RevCommit): CharSequence {
        val message = item.fullMessage.trim()
        val builder = SpannableStringBuilder(message)
        var start = 0
        while (true) {
            val match = commitShaRegex.find(builder, start) ?: break
            start = match.range.start
            val end = match.range.endInclusive + 1
            val sha = match.value
            val span = ClickableShaSpan(repositoryId, sha)
            builder.replace(start, end, match.value.substring(0, SHORT_SHA_LENGTH))
            builder.setSpan(span, start, start + SHORT_SHA_LENGTH,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            start += SHORT_SHA_LENGTH
        }
        return builder
    }
}
