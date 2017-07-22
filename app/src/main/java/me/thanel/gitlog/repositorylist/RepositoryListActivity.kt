package me.thanel.gitlog.repositorylist

import android.view.Menu
import android.view.MenuItem
import me.thanel.gitlog.BaseActivity
import me.thanel.gitlog.R
import java.io.File

class RepositoryListActivity : BaseActivity() {

    override val canNavigateUp = false

    override fun createFragment() = RepositoryListFragment()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.repository_list, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.remove_all_repositories -> removeAllRepositories()
            else -> return false
        }
        return true
    }

    private fun removeAllRepositories() {
        val root = File(filesDir, "repos")
        root.deleteRecursively()
    }

}
