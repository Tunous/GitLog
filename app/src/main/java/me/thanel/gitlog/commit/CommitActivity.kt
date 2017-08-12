package me.thanel.gitlog.commit

import android.content.Context
import android.os.Bundle
import me.thanel.gitlog.base.BasePagerActivity
import me.thanel.gitlog.commit.view.CommitHeaderView
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.observe

class CommitActivity : BasePagerActivity() {
    private val commitSha by stringExtra(EXTRA_COMMIT_SHA)
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)

    private lateinit var headerView: CommitHeaderView

    override val pageTitles: Array<CharSequence>
        get() = arrayOf("Commit", "Changed Files")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        title = "Commit ${commitSha.substring(0, 7)}"

        headerView = CommitHeaderView(this)
        addViewAboveToolbar(headerView)

        val viewModel = CommitViewModel.get(this, repositoryId, commitSha)
        observeViewModel(viewModel)
    }

    private fun observeViewModel(viewModel: CommitViewModel) {
        viewModel.repository.observe(this, headerView::displayRepositoryInformation)
        viewModel.commit.observe(this, headerView::displayCommitInformation)
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
