package me.thanel.gitlog.file

import android.content.Context
import android.os.Bundle
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.utils.createIntent

class FileActivity : BaseFragmentActivity() {
    private val filePath: String by stringExtra(EXTRA_FILE_PATH)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = filePath.split("/").last()

    }

    override fun createFragment() = FileFragment.newInstance(filePath)

    companion object {
        private const val EXTRA_FILE_PATH = "extra.file_path"

        fun newIntent(context: Context, filePath: String) = context.createIntent<FileActivity> {
            putExtra(EXTRA_FILE_PATH, filePath)
        }
    }
}
