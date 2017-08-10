package me.thanel.gitlog.commit

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BasePagerActivity
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.view.AvatarDrawable

class CommitActivity : BasePagerActivity() {
    private val commitSha by stringExtra(EXTRA_COMMIT_SHA)
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)

    private lateinit var headerView: View

    override val title: String?
        get() = "Commit ${commitSha.substring(0, 7)}"

    override val pageTitles: Array<CharSequence>
        get() = arrayOf("Commit", "Changed Files")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        headerView = layoutInflater.inflate(R.layout.view_commit_header, null)
        addViewAboveToolbar(headerView)

        val viewModel = CommitViewModel.get(this, repositoryId, commitSha)
        observeViewModel(viewModel)
    }

    private fun observeViewModel(viewModel: CommitViewModel) {
        viewModel.repository.observe(this) {
            subtitle = it?.name
            val repositoryNameView = headerView.findViewById(R.id.repositoryNameView) as TextView
            repositoryNameView.text = it?.name
        }
        viewModel.commit.observe(this) {
            val titleView = headerView.findViewById(R.id.titleView) as TextView
            titleView.text = it?.shortMessage

            val authorName = it?.authorIdent?.name ?: ""
            val commitAuthorAvatarView = headerView.findViewById(R.id.commitAuthorAvatarView) as ImageView
            commitAuthorAvatarView.setImageDrawable(AvatarDrawable(authorName, commitSha))
        }
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
