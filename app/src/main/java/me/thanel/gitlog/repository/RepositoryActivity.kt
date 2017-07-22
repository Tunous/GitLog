package me.thanel.gitlog.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import me.thanel.gitlog.ActivityResults
import me.thanel.gitlog.BaseActivity
import me.thanel.gitlog.R
import me.thanel.gitlog.Repo
import me.thanel.gitlog.repository.log.CommitLogFragment
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.replaceTag
import java.io.File

class RepositoryActivity : BaseActivity() {

    private val repository by parcelableExtra<Repo>(EXTRA_REPOSITORY)

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
            R.id.remove -> promptRemoveRepository()

            else -> return false
        }

        return true
    }

    private fun promptRemoveRepository() {
        val message = getString(R.string.remove_repository_confirm_message)
                .replaceTag("repository", repository.name, makeBold = true)

        AlertDialog.Builder(this)
                .setTitle(R.string.remove_repository_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.action_remove) { _, _ ->
                    removeRepository()
                }
                .setNegativeButton(R.string.action_cancel, null)
                .show()
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

        fun newIntent(context: Context, repository: Repo) =
                context.createIntent<RepositoryActivity> {
                    putExtra(EXTRA_REPOSITORY, repository)
                }
    }
}
