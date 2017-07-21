package me.thanel.gitlog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import java.io.File

class RepositoryActivity : BaseActivity() {

    private val repository by lazy { intent.getParcelableExtra<Repository>(EXTRA_REPOSITORY) }

    private lateinit var repositoryFile: File

    override val title: String?
        get() = repository.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        repositoryFile = File(filesDir, "repos/${repository.name}")
    }

    override fun createFragment() = CommitLogFragment.newInstance(repository)

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.repository, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.remove -> removeRepository()

            else -> return false
        }

        return true
    }

    private fun removeRepository() {
        val deleted = repositoryFile.deleteRecursively()
        if (deleted) {
            setResult(ActivityResults.RESULT_REPOSITORY_REMOVED, Intent().apply {
                putExtra(EXTRA_REPOSITORY, repository)
            })
            finish()
        } else {
            Toast.makeText(this, "Failed removing repository...", Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        const val EXTRA_REPOSITORY = "extra.repository"

        fun newIntent(context: Context, repository: Repository): Intent {
            return Intent(context, RepositoryActivity::class.java).apply {
                putExtra(EXTRA_REPOSITORY, repository)
            }
        }
    }
}
