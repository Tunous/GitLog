package me.thanel.gitlog.commit

import android.content.Context
import me.thanel.gitlog.base.BasePagerActivity
import me.thanel.gitlog.db.Repository
import me.thanel.gitlog.model.Commit
import me.thanel.gitlog.model.shortSha
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.createIntent

class CommitActivity : BasePagerActivity() {

    private val commit by parcelableExtra<Commit>(EXTRA_COMMIT)
    private val repository by parcelableExtra<Repository>(EXTRA_REPOSITORY)

    override val pageTitles: Array<CharSequence>
        get() = arrayOf("Details", "Diff")

    override val title: String?
        get() = "Commit ${commit.shortSha}"

    override val subtitle: String?
        get() = repository.name

    override fun createFragment(position: Int) = when (position) {
        0 -> CommitFragment.newInstance(commit, repository)
        1 -> DiffFragment.newInstance(commit, repository)
        else -> throw IllegalArgumentException("Incorrect fragment position: $position.")
    }

    override fun getSupportParentActivityIntent() = RepositoryActivity.newIntent(this, repository)

    companion object {
        private const val EXTRA_COMMIT = "extra.commit"
        private const val EXTRA_REPOSITORY = "extra.repository"

        fun newIntent(context: Context, commit: Commit, repository: Repository) =
                context.createIntent<CommitActivity> {
                    putExtra(EXTRA_COMMIT, commit)
                    putExtra(EXTRA_REPOSITORY, repository)
                }
    }
}
