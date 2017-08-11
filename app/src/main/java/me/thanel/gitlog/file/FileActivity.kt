package me.thanel.gitlog.file

import android.content.Context
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.utils.createIntent

class FileActivity : BaseFragmentActivity() {
    private val filePath: String by stringExtra(EXTRA_FILE_PATH)

    override val title: String?
        get() = filePath.split("/").last()

    override fun createFragment() = FileFragment.newInstance(filePath)

    companion object {
        private const val EXTRA_FILE_PATH = "extra.file_path"

        fun newIntent(context: Context, filePath: String) = context.createIntent<FileActivity> {
            putExtra(EXTRA_FILE_PATH, filePath)
        }
    }
}
