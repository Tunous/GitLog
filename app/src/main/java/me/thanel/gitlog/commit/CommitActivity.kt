package me.thanel.gitlog.commit

import android.content.Context
import me.thanel.gitlog.BaseActivity
import me.thanel.gitlog.Repo
import me.thanel.gitlog.model.Commit
import me.thanel.gitlog.model.shortSha
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.createIntent

class CommitActivity : BaseActivity() {

    private val commit by parcelableExtra<Commit>(EXTRA_COMMIT)
    private val repository by parcelableExtra<Repo>(EXTRA_REPOSITORY)

    override val title: String?
        get() = "Commit ${commit.shortSha}"

    override val subtitle: String?
        get() = repository.name

    override fun createFragment() = CommitFragment.newInstance(commit)

    override fun getSupportParentActivityIntent() = RepositoryActivity.newIntent(this, repository)

    companion object {
        private const val EXTRA_COMMIT = "extra.commit"
        private const val EXTRA_REPOSITORY = "extra.repository"

        fun newIntent(context: Context, commit: Commit, repository: Repo) =
                context.createIntent<CommitActivity> {
                    putExtra(EXTRA_COMMIT, commit)
                    putExtra(EXTRA_REPOSITORY, repository)
                }
    }
}
