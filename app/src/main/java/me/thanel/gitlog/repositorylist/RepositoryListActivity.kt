package me.thanel.gitlog.repositorylist

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.ssh.SshKeyManagementActivity
import me.thanel.gitlog.utils.createIntent

class RepositoryListActivity : BaseFragmentActivity() {
    override val canNavigateUp = false

    override fun createFragment() = RepositoryListFragment()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.repository_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.manage_ssh_keys -> browseSshKeys()
            else -> return false
        }
        return true
    }

    private fun browseSshKeys() {
        val intent = SshKeyManagementActivity.newIntent(this)
        startActivityForResult(intent, REQUEST_BROWSE_SSH)
    }

    companion object {
        private const val REQUEST_BROWSE_SSH = 1
        fun newIntent(context: Context) = context.createIntent<RepositoryListActivity>()
    }
}
