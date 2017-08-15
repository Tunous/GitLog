package me.thanel.gitlog.commit.view

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
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
) : FrameLayout(context, attrs, defStyleAttr) {
    private val view = inflate(context, R.layout.view_commit_header, this)
    private val titleView = view.titleView
    private val avatarView = view.avatarView
    private val commitDateView = view.commitDateView
    private val repositoryNameView = view.repositoryNameView

    fun displayCommitInformation(commit: RevCommit?) {
        if (commit == null) {
            // TODO: Loading
            return
        }

        titleView.text = commit.shortMessage
        avatarView.setFromCommit(commit)

        val date = Date(commit.commitTime * 1000L)
        commitDateView.text = RelativeDateFormatter.format(date)
    }

    fun displayRepositoryInformation(repository: Repository?) {
        if (repository == null) {
            // TODO: Loading
            return
        }

        repositoryNameView.text = repository.name
    }
}