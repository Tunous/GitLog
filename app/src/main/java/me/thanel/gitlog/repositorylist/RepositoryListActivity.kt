package me.thanel.gitlog.repositorylist

import android.content.Context
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.db.RepositoryDao
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.observe
import java.io.File

class RepositoryListActivity : BaseFragmentActivity() {
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

        val dao = (application as GitLogApplication).database.repositoryDao()
        dao.listRepositories().observe(this) {
            if (it != null) {
                removeRepos(it, dao)
            }
        }
    }

    private fun removeRepos(it: List<Repository>, dao: RepositoryDao) {
        launch(CommonPool) {
            for (repo in it) {
                dao.removeRepository(repo)
            }
        }
    }

    companion object {
        fun newIntent(context: Context) = context.createIntent<RepositoryListActivity>()
    }
}
