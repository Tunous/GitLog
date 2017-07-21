package me.thanel.gitlog.commit

import android.content.Context
import android.content.Intent
import me.thanel.gitlog.*
import me.thanel.gitlog.model.Commit
import me.thanel.gitlog.model.Repository
import me.thanel.gitlog.model.shortSha
import me.thanel.gitlog.repository.RepositoryActivity

class CommitActivity : BaseActivity() {

    private val commit by lazy { intent.getParcelableExtra<Commit>(EXTRA_COMMIT) }
    private val repository by lazy { intent.getParcelableExtra<Repository>(EXTRA_REPOSITORY) }

    override val title: String?
        get() = "Commit ${commit.shortSha}"

    override val subtitle: String?
        get() = repository.name

    override fun createFragment() = CommitFragment.newInstance(commit)

    override fun getSupportParentActivityIntent() = RepositoryActivity.newIntent(this, repository)

    companion object {
        private const val EXTRA_COMMIT = "extra.commit"
        private const val EXTRA_REPOSITORY = "extra.repository"

        fun newIntent(context: Context, commit: Commit, repository: Repository): Intent {
            return Intent(context, CommitActivity::class.java).apply {
                putExtra(EXTRA_COMMIT, commit)
                putExtra(EXTRA_REPOSITORY, repository)
            }
        }
    }
}
