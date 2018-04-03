package me.thanel.gitlog.ui.ssh

import activitystarter.MakeActivityStarter
import android.content.Intent
import android.os.Bundle
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity
import me.thanel.gitlog.ui.repositorylist.RepositoryListActivityStarter

@MakeActivityStarter(includeStartForResult = true)
class SshKeyManagementActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = getString(R.string.manage_ssh_keys)
    }

    override fun createFragment() = SshKeyManagementFragment.newInstance()

    override fun getSupportParentActivityIntent(): Intent =
        RepositoryListActivityStarter.getIntent(this)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
