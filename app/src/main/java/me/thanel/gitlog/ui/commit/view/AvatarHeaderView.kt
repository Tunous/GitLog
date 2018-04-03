package me.thanel.gitlog.ui.commit.view

import android.content.Context
import android.graphics.Typeface
import android.support.annotation.StringRes
import android.support.constraint.ConstraintLayout
import android.text.format.DateUtils
import android.text.style.StyleSpan
import android.util.AttributeSet
import android.view.View
import androidx.view.isVisible
import kotlinx.android.synthetic.main.view_avatar_header.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.utils.StyleableTag
import me.thanel.gitlog.ui.utils.formatTags
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.util.RelativeDateFormatter
import java.util.*

class AvatarHeaderView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    init {
        View.inflate(context, R.layout.view_avatar_header, this)
        val a = context.obtainStyledAttributes(attrs, R.styleable.AvatarHeaderView)
        val showExpandArrow = a.getBoolean(R.styleable.AvatarHeaderView_showExpandArrow, false)
        expandArrow.isVisible = showExpandArrow
        a.recycle()

        val padding = context.resources.getDimensionPixelSize(R.dimen.small_spacing)
        setPadding(0, padding, 0, padding)
    }

    var isAvatarVisible: Boolean
        get() = avatarView.isVisible
        set(visible) {
            avatarView.isVisible = visible
        }

    var areDetailsVisible: Boolean
        get() = detailsView.isVisible
        set(visible) {
            detailsView.isVisible = visible
        }

    var expandArrowRotation: Float
        get() = expandArrow.rotation
        set(rotation) {
            expandArrow.rotation = rotation
        }

    fun bind(commit: RevCommit) {
        avatarView.setFromCommit(commit)
        titleView.text = commit.shortMessage
        val date = Date(commit.commitTime * 1000L)
        val dateString = RelativeDateFormatter.format(date)
        val message = if (commit.authorIdent?.emailAddress == commit.committerIdent?.emailAddress) {
            context.getString(R.string.committed_alone).formatTags(
                StyleableTag("committer", commit.committerIdent.name, StyleSpan(Typeface.BOLD)),
                StyleableTag("time", dateString)
            )
        } else {
            context.getString(R.string.committed_together).formatTags(
                StyleableTag("author", commit.authorIdent.name, StyleSpan(Typeface.BOLD)),
                StyleableTag("committer", commit.committerIdent.name, StyleSpan(Typeface.BOLD)),
                StyleableTag("time", dateString)
            )
        }
        detailsView.text = message
    }

    fun bind(ident: PersonIdent, @StringRes messageResId: Int) {
        avatarView.setFromIdent(ident)
        titleView.text = context.getString(R.string.user_name_and_email).formatTags(
            StyleableTag("name", ident.name),
            StyleableTag("email", ident.emailAddress)
        )
        val time = DateUtils.formatDateTime(
            context, ident.`when`.time,
            DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_YEAR
        )
        detailsView.text = context.getString(messageResId).formatTags(StyleableTag("time", time))
    }
}
