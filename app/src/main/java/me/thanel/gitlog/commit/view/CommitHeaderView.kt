package me.thanel.gitlog.commit.view

import android.animation.LayoutTransition
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.view.View
import kotlinx.android.synthetic.main.view_commit_header.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.util.RelativeDateFormatter
import java.util.*

class CommitHeaderView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {
    private val view = inflate(context, R.layout.view_commit_header, this)
    private val titleView = view.titleView
    private val avatarView = view.avatarView
    private val commitDateView = view.commitDateView
    private val repositoryNameView = view.repositoryNameView
    private val authorView = view.authorDetails
    private val committerView = view.committerDetails
    private val expandDropDown = view.expandDropDown

    private var shouldDisplayCommitter = false

    init {
        val smallSpacing = context.resources.getDimensionPixelSize(R.dimen.small_spacing)
        val regularSpacing = context.resources.getDimensionPixelSize(R.dimen.regular_spacing)

        setPadding(0, smallSpacing, 0, regularSpacing)

        layoutTransition = LayoutTransition()

        setOnClickListener {
            val wasVisible = authorView.visibility == View.VISIBLE
            setDetailsVisible(!wasVisible)
        }
    }

    fun displayCommitInformation(commit: RevCommit?) {
        if (commit == null) {
            // TODO: Loading
            return
        }

        titleView.text = commit.shortMessage
        avatarView.setFromCommit(commit)

        val date = Date(commit.commitTime * 1000L)
        commitDateView.text = RelativeDateFormatter.format(date)

        val authorIdent = commit.authorIdent
        val committerIdent = commit.committerIdent
        authorView.bind(authorIdent)
        committerView.bind(committerIdent)
        shouldDisplayCommitter = committerIdent != null &&
                committerIdent.emailAddress != authorIdent.emailAddress
    }

    fun displayRepositoryInformation(repository: Repository?) {
        if (repository == null) {
            // TODO: Loading
            return
        }

        repositoryNameView.text = repository.name
    }

    private fun setDetailsVisible(visible: Boolean) {
        authorView.visibility = if (visible) View.VISIBLE else View.GONE
        committerView.visibility = if (visible && shouldDisplayCommitter) View.VISIBLE else View.GONE

        val resId = if (visible) R.drawable.ic_arrow_drop_up_white
        else R.drawable.ic_arrow_drop_down_white
        expandDropDown.expandDropDown.setImageResource(resId)

        avatarView.visibility = if (visible) View.GONE else View.VISIBLE
    }
}