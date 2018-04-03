package me.thanel.gitlog.ssh

import android.content.Context
import android.content.Intent
import android.os.Bundle
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.repositorylist.RepositoryListActivity
import me.thanel.gitlog.utils.createIntent

class SshKeyManagementActivity : BaseFragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = getString(R.string.manage_ssh_keys)
    }

    override fun createFragment() = SshKeyManagementFragment.newInstance()

    override fun getSupportParentActivityIntent(): Intent =
        RepositoryListActivity.newIntent(this)
            .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    companion object {
        fun newIntent(context: Context) = context.createIntent<SshKeyManagementActivity>()
    }
}
