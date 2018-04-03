package me.thanel.gitlog.commit

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.SHORT_SHA_LENGTH
import me.thanel.gitlog.utils.StyleableTag
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.formatTags

class CommitActivity : BaseFragmentActivity() {
    private val commitSha by stringExtra(EXTRA_COMMIT_SHA)
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val shortSha = commitSha.substring(0, SHORT_SHA_LENGTH)
        toolbarTitle = "Commit [sha]".formatTags(StyleableTag("sha", shortSha))
    }

    override fun createFragment() = CommitFragment.newInstance(commitSha, repositoryId)

    override fun getSupportParentActivityIntent(): Intent =
        RepositoryActivity.newIntent(this, repositoryId)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    companion object {
        private const val EXTRA_COMMIT_SHA = "extra.commit_sha"
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"

        fun newIntent(context: Context, repositoryId: Int, commitSha: String) =
            context.createIntent<CommitActivity> {
                putExtra(EXTRA_COMMIT_SHA, commitSha)
                putExtra(EXTRA_REPOSITORY_ID, repositoryId)
            }
    }
}
