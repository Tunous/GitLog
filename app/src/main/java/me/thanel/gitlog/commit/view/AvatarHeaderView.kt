package me.thanel.gitlog.commit.view

import android.content.Context
import android.support.constraint.ConstraintLayout
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.view_avatar_header.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.isVisible
import org.eclipse.jgit.lib.PersonIdent
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.util.RelativeDateFormatter
import java.util.*

class AvatarHeaderView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val root = View.inflate(context, R.layout.view_avatar_header, this)
    private val avatarView = root.avatarView
    private val titleView = root.titleView
    private val detailsView = root.detailsView
    private val expandArrow = root.expandArrow

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.AvatarHeaderView)
        val showExpandArrow = a.getBoolean(R.styleable.AvatarHeaderView_showExpandArrow, false)
        expandArrow.isVisible = showExpandArrow
        a.recycle()

        val padding = context.resources.getDimensionPixelSize(R.dimen.small_spacing)
        setPadding(0, padding, 0, padding)
    }

    var isAvatarVisible: Boolean
        get() = avatarView.visibility == View.VISIBLE
        set(visible) {
            avatarView.isVisible = visible
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
        detailsView.text = RelativeDateFormatter.format(date)
    }

    fun bind(ident: PersonIdent) {
        avatarView.setFromIdent(ident)
        titleView.text = "${ident.name} <${ident.emailAddress}>"
        detailsView.text = DateUtils.formatDateTime(context, ident.`when`.time,
                DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_SHOW_TIME or DateUtils.FORMAT_SHOW_YEAR)
    }
}