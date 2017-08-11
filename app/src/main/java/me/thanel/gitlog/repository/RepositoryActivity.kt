package me.thanel.gitlog.repository

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.ActivityResults
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BasePagerActivity
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.repository.log.CommitLogFragment
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.replaceTag
import java.io.File

class RepositoryActivity : BasePagerActivity() {
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)

    private lateinit var repositoryFile: File
    private lateinit var viewModel: RepositoryViewModel

    private var repository: Repository? = null
    private var removeRepositoryItem: MenuItem? = null

    override val title: String?
        get() = "Loading..."

    override val pageTitles: Array<CharSequence>
        get() = arrayOf("Log", "Files")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)
        viewModel.getRepository(repositoryId).observe(this, Observer {
            if (it != null) {
                repositoryFile = File(filesDir, "repos/${it.name}")
                supportActionBar!!.title = it.name
                repository = it
                removeRepositoryItem?.isVisible = true
            }
        })
    }

    override fun createFragment(position: Int) = when (position) {
        0 -> CommitLogFragment.newInstance(repositoryId)
        1 -> FileListFragment.newInstance(repositoryId)
        else -> throw IllegalArgumentException("Invalid fragment position")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.repository, menu)
        removeRepositoryItem = menu.findItem(R.id.remove)
        if (repository != null) {
            removeRepositoryItem?.isVisible = true
        }
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
                .replaceTag("repository", repository!!.name, makeBold = true)

        AlertDialog.Builder(this)
                .setTitle(R.string.remove_repository_dialog_title)
                .setMessage(message)
                .setPositiveButton(R.string.remove) { _, _ ->
                    removeRepository()
                }
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    private fun removeRepository() {
        launch(CommonPool) {
            viewModel.removeRepository(repository!!)
            setResult(ActivityResults.RESULT_REPOSITORY_REMOVED, Intent().apply {
                putExtra(EXTRA_REPOSITORY_ID, repositoryId)
            })
            finish()
        }
    }

    companion object {
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"
        const val EXTRA_REPOSITORY = "extra.repository"

        fun newIntent(context: Context, repositoryId: Int) =
                context.createIntent<RepositoryActivity> {
                    putExtra(EXTRA_REPOSITORY_ID, repositoryId)
                }
    }
}
