package me.thanel.gitlog.file

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.repository.FileListActivity
import me.thanel.gitlog.utils.createIntent

class FileViewerActivity : BaseFragmentActivity() {
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)
    private val filePath by stringExtra(EXTRA_FILE_PATH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = filePath.split("/").last()

    }

    override fun createFragment() = FileViewerFragment.newInstance(filePath)

    override fun getSupportParentActivityIntent(): Intent =
            FileListActivity.newIntent(this, repositoryId)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    companion object {
        private const val EXTRA_FILE_PATH = "extra.file_path"
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"

        fun newIntent(context: Context, repositoryId: Int, filePath: String) =
                context.createIntent<FileViewerActivity> {
            putExtra(EXTRA_REPOSITORY_ID, repositoryId)
            putExtra(EXTRA_FILE_PATH, filePath)
        }
    }
}
