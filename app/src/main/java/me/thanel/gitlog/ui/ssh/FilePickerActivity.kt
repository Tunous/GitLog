package me.thanel.gitlog.ui.ssh

import activitystarter.MakeActivityStarter
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity

@MakeActivityStarter(includeStartForResult = true)
class FilePickerActivity : BaseFragmentActivity() {
    override fun createFragment() = FilePickerFragment.newInstance()

    companion object {
        const val EXTRA_FILE_PATH = "extra.file_path"
    }
}
