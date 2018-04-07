package me.thanel.gitlog.ui.repositorylist

import activitystarter.MakeActivityStarter
import android.view.Menu
import android.view.MenuItem
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity
import me.thanel.gitlog.ui.ssh.SshKeyManagementActivityStarter

@MakeActivityStarter
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
        SshKeyManagementActivityStarter.start(this)
    }
}
