package me.thanel.gitlog.commit

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import me.thanel.gitlog.base.BasePagerActivity
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.createIntent

class CommitActivity : BasePagerActivity() {
    private val commitSha by stringExtra(EXTRA_COMMIT_SHA)
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)

    override val title: String?
        get() = "Commit ${commitSha.substring(0, 7)}"

    override val pageTitles: Array<CharSequence>
        get() = arrayOf("Commit", "Changed Files")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = CommitViewModel.get(this, repositoryId, commitSha)

        viewModel.repository.observe(this, Observer {
            subtitle = it?.name
        })
    }

    override fun createFragment(position: Int) = when (position) {
        0 -> CommitFragment.newInstance(commitSha, repositoryId)
        1 -> CommitFileListFragment.newInstance(commitSha, repositoryId)
        else -> throw IllegalArgumentException("Too many fragments")
    }

    override fun getSupportParentActivityIntent() = RepositoryActivity.newIntent(this, repositoryId)

    companion object {
        private const val EXTRA_COMMIT_SHA = "extra.commit_sha"
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"

        fun newIntent(context: Context, commitSha: String, repositoryId: Int) =
                context.createIntent<CommitActivity> {
                    putExtra(EXTRA_COMMIT_SHA, commitSha)
                    putExtra(EXTRA_REPOSITORY_ID, repositoryId)
                }
    }
}
