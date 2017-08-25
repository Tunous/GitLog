package me.thanel.gitlog.ssh

import android.content.Context
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.utils.createIntent

class SshKeyManagementActivity : BaseFragmentActivity() {
    override fun createFragment() = SshKeyManagementFragment.newInstance()

    companion object {
        fun newIntent(context: Context) = context.createIntent<SshKeyManagementActivity>()
    }
}
