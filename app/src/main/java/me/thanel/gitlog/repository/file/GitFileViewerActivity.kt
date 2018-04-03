package me.thanel.gitlog.repository.file

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.repository.filelist.GitFileListActivity
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.getAbbreviatedName
import me.thanel.gitlog.utils.observe

class GitFileViewerActivity : BaseFragmentActivity() {
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)
    private val refName by stringExtra(EXTRA_REF_NAME)
    private val filePath by stringExtra(EXTRA_FILE_PATH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = filePath.split("/").last()
        toolbarSubtitle

        val viewModel = GitFileViewModel.get(this, repositoryId, refName, filePath)
        viewModel.repository.observe(this) {
            it?.let {
                toolbarSubtitle = it.git.repository.getAbbreviatedName(refName)
            }
        }
    }

    override fun createFragment() = GitFileViewerFragment.newInstance(
        repositoryId, refName,
        filePath
    )

    override fun getSupportParentActivityIntent(): Intent =
        GitFileListActivity.newIntent(this, repositoryId, refName)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    companion object {
        private const val EXTRA_FILE_PATH = "extra.file_path"
        private const val EXTRA_REF_NAME = "extra.ref_name"
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"

        fun newIntent(context: Context, repositoryId: Int, refName: String, filePath: String) =
            context.createIntent<GitFileViewerActivity> {
                putExtra(EXTRA_REPOSITORY_ID, repositoryId)
                putExtra(EXTRA_REF_NAME, refName)
                putExtra(EXTRA_FILE_PATH, filePath)
            }
    }
}
