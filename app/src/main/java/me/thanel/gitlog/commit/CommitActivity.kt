package me.thanel.gitlog.commit

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.createIntent

class CommitActivity : BaseFragmentActivity() {
    private val commitSha by stringExtra(EXTRA_COMMIT_SHA)
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)

    override val title: String?
        get() = "Commit ${commitSha.substring(0, 7)}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = CommitViewModel.get(this, repositoryId, commitSha)

        viewModel.repository.observe(this, Observer {
            subtitle = it?.name
        })
    }

    override fun createFragment() = CommitFragment.newInstance(commitSha, repositoryId)

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
