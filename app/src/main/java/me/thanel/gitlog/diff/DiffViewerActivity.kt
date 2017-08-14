package me.thanel.gitlog.diff

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.commit.CommitViewModel
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.observe
import org.eclipse.jgit.lib.AbbreviatedObjectId

class DiffViewerActivity : BaseFragmentActivity() {
    private val commitSha by stringExtra(EXTRA_COMMIT_SHA)
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)
    private val diffId by serializableExtra<AbbreviatedObjectId>(EXTRA_DIFF_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = CommitViewModel.get(this, repositoryId, commitSha)

        viewModel.repository.observe(this) {
            subtitle = it?.name
        }
        viewModel.getDiffEntry(diffId).observe(this) {
            title = it?.newPath?.split("/")?.lastOrNull()
        }
    }

    override fun createFragment() = DiffViewerFragment.newInstance(commitSha, repositoryId, diffId)

    override fun getSupportParentActivityIntent(): Intent =
            CommitActivity.newIntent(this, repositoryId, commitSha)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    companion object {
        private const val EXTRA_COMMIT_SHA = "extra.commit_sha"
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"
        private const val EXTRA_DIFF_ID = "extra.diff_id"

        fun newIntent(context: Context, repositoryId: Int, commitSha: String,
                diffId: AbbreviatedObjectId) = context.createIntent<DiffViewerActivity> {
            putExtra(EXTRA_COMMIT_SHA, commitSha)
            putExtra(EXTRA_REPOSITORY_ID, repositoryId)
            putExtra(EXTRA_DIFF_ID, diffId)
        }
    }
}
