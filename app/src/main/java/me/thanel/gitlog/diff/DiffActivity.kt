package me.thanel.gitlog.diff

import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.commit.CommitViewModel
import me.thanel.gitlog.utils.createIntent

class DiffActivity : BaseFragmentActivity() {
    private val commitSha: String by stringExtra(EXTRA_COMMIT_SHA)
    private val repositoryId: Int by intExtra(EXTRA_REPOSITORY_ID)
    private val diffFilePath: Array<String>? by stringArrayExtra(EXTRA_DIFF_FILE_PATH)

    override val title: String?
        get() = "Commit ${commitSha.substring(0, 7)} - diff"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = CommitViewModel.get(this, repositoryId, commitSha)

        viewModel.repository.observe(this, Observer {
            subtitle = it?.name
        })
    }

    override fun createFragment() = DiffFragment.newInstance(commitSha, repositoryId, diffFilePath)

    override fun getSupportParentActivityIntent() =
            CommitActivity.newIntent(this, commitSha, repositoryId)

    companion object {
        private const val EXTRA_COMMIT_SHA = "extra.commit_sha"
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"
        private const val EXTRA_DIFF_FILE_PATH = "extra.diff_file_path"

        fun newIntent(context: Context, commitSha: String, repositoryId: Int,
                diffFilePath: Array<String>?) = context.createIntent<DiffActivity> {
            putExtra(EXTRA_COMMIT_SHA, commitSha)
            putExtra(EXTRA_REPOSITORY_ID, repositoryId)
            putExtra(EXTRA_DIFF_FILE_PATH, diffFilePath)
        }
    }
}
