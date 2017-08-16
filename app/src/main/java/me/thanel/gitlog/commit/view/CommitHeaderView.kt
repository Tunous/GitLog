package me.thanel.gitlog.commit.view

import android.content.Context
import android.support.transition.ChangeBounds
import android.support.transition.ChangeTransform
import android.support.transition.Fade
import android.support.transition.TransitionManager
import android.support.transition.TransitionSet
import android.support.v7.widget.LinearLayoutCompat
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_commit_header.view.*
import me.thanel.gitlog.R
import me.thanel.gitlog.utils.isVisible
import org.eclipse.jgit.revwalk.RevCommit

class CommitHeaderView @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private val root = inflate(context, R.layout.view_commit_header, this)
    private val headerView = root.headerView
    private val authorView = root.authorView
    private val committerView = root.committerView

    private var shouldDisplayCommitter = false

    init {
        orientation = VERTICAL

        val smallSpacing = context.resources.getDimensionPixelSize(R.dimen.small_spacing)
        val regularSpacing = context.resources.getDimensionPixelSize(R.dimen.regular_spacing)
        setPadding(0, smallSpacing, 0, regularSpacing)

        headerView.setOnClickListener {
            val wasVisible = authorView.visibility == View.VISIBLE
            setDetailsVisible(!wasVisible)
        }
    }

    fun displayCommitInformation(commit: RevCommit?) {
        if (commit == null) {
            // TODO: Loading
            return
        }

        headerView.bind(commit)

        val authorIdent = commit.authorIdent
        val committerIdent = commit.committerIdent
        shouldDisplayCommitter = committerIdent != null &&
                committerIdent.emailAddress != authorIdent.emailAddress
        if (shouldDisplayCommitter) {
            authorView.bind(authorIdent, R.string.authored)
            committerView.bind(committerIdent, R.string.committed)
        } else {
            authorView.bind(committerIdent, R.string.committed)
        }
    }

    private fun setDetailsVisible(visible: Boolean) {
        TransitionManager.beginDelayedTransition(parent as ViewGroup, TransitionSet()
                .addTransition(ChangeTransform())
                .addTransition(ChangeBounds())
                .addTransition(Fade()))

        authorView.isVisible = visible
        committerView.isVisible = visible && shouldDisplayCommitter
        headerView.isAvatarVisible = !visible
        headerView.areDetailsVisible = !visible
        headerView.expandArrowRotation = if (visible) 180f else 0f
    }
}