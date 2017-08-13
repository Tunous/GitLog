package me.thanel.gitlog.repository

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.ActivityResults
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.repository.log.CommitLogFragment
import me.thanel.gitlog.repositorylist.RepositoryListActivity
import me.thanel.gitlog.utils.StyleableTag
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.formatTags
import java.io.File

class RepositoryActivity : BaseFragmentActivity() {
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)

    private lateinit var repositoryFile: File
    private lateinit var viewModel: RepositoryViewModel

    private var repository: Repository? = null
    private var removeRepositoryItem: MenuItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        viewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)
        viewModel.getRepository(repositoryId).observe(this, Observer {
            if (it != null) {
                repositoryFile = File(filesDir, "repos/${it.name}")
                title = it.name
                repository = it
                removeRepositoryItem?.isVisible = true
            }
        })
    }

    override fun createFragment(): Fragment = CommitLogFragment.newInstance(repositoryId)

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
            R.id.browse_files -> browseFiles()
            R.id.remove -> promptRemoveRepository()
            else -> return false
        }

        return true
    }

    override fun getSupportParentActivityIntent(): Intent =
            RepositoryListActivity.newIntent(this)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    private fun browseFiles() {
        val intent = FileListActivity.newIntent(this, repositoryId)
        startActivity(intent)
    }

    private fun promptRemoveRepository() {
        val message = getString(R.string.remove_repository_confirm_message)
                .formatTags(StyleableTag("repository", repository!!.name, StyleSpan(Typeface.BOLD)))

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
