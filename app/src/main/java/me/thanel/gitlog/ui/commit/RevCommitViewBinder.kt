package me.thanel.gitlog.ui.commit

import android.text.Spannable
import android.text.SpannableStringBuilder
import kotlinx.android.synthetic.main.item_commit_details.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.adapter.ContainerViewBinder
import me.thanel.gitlog.ui.base.adapter.ContainerViewHolder
import me.thanel.gitlog.ui.utils.ClickableShaSpan
import me.thanel.gitlog.ui.utils.SHORT_SHA_LENGTH
import org.eclipse.jgit.revwalk.RevCommit

class RevCommitViewBinder(private val repositoryId: Int) : ContainerViewBinder<RevCommit>() {
    private val commitShaRegex = Regex("""[0-9a-f]{40}""")

    override val layoutResource: Int
        get() = R.layout.item_commit_details

    override fun onBindViewHolder(holder: ContainerViewHolder, item: RevCommit) {
        holder.commitMessageView.text = formatMessage(item)

        // Setting this on each bind fixes issue where text selection stops working
        holder.commitMessageView.setTextIsSelectable(true)
        holder.commitMessageView.isFocusable = true

        holder.commitHeaderView.displayCommitInformation(item)
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
            builder.setSpan(
                span, start, start + SHORT_SHA_LENGTH,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            start += SHORT_SHA_LENGTH
        }
        return builder
    }
}
