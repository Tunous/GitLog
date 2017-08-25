package me.thanel.gitlog.explorer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.ssh.SshKeyManagementActivity
import me.thanel.gitlog.utils.createIntent

class FileViewerActivity : BaseFragmentActivity() {
    private val filePath by stringExtra(EXTRA_FILE_PATH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = filePath.split("/").last()
    }

    override fun createFragment() = FileViewerFragment.newInstance(filePath)

    override fun getSupportParentActivityIntent(): Intent =
        SshKeyManagementActivity.newIntent(this)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    companion object {
        private const val EXTRA_FILE_PATH = "extra.file_path"

        fun newIntent(context: Context, filePath: String)
                = context.createIntent<FileViewerActivity> {
            putExtra(EXTRA_FILE_PATH, filePath)
        }
    }
}