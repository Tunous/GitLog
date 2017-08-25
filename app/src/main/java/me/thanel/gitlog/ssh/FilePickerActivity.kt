package me.thanel.gitlog.ssh

import android.content.Context
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.utils.createIntent

class FilePickerActivity : BaseFragmentActivity() {
    override fun createFragment() = FilePickerFragment.newInstance()

    companion object {
        const val EXTRA_FILE_PATH = "extra.file_path"

        fun newIntent(context: Context) = context.createIntent<FilePickerActivity>()
    }
}
