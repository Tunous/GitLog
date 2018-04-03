package me.thanel.gitlog.ssh

import activitystarter.MakeActivityStarter
import me.thanel.gitlog.base.BaseFragmentActivity

@MakeActivityStarter(includeStartForResult = true)
class FilePickerActivity : BaseFragmentActivity() {
    override fun createFragment() = FilePickerFragment.newInstance()

    companion object {
        const val EXTRA_FILE_PATH = "extra.file_path"
    }
}
