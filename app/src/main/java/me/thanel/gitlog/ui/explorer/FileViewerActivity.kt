package me.thanel.gitlog.ui.explorer

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity
import me.thanel.gitlog.ui.ssh.SshKeyManagementActivityStarter

class FileViewerActivity : BaseFragmentActivity() {
    @get:Arg
    val filePath: String by argExtra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = filePath.split("/").last()
    }

    override fun createFragment(): FileViewerFragment =
        FileViewerFragmentStarter.newInstance(filePath)

    override fun getSupportParentActivityIntent(): Intent =
        SshKeyManagementActivityStarter.getIntent(this)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
