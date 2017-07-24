package me.thanel.gitlog.repository

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
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.R
import me.thanel.gitlog.db.Repository
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.repository.log.CommitLogFragment
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.replaceTag
import java.io.File

class RepositoryActivity : BaseFragmentActivity() {

    private val repository by parcelableExtra<Repository>(EXTRA_REPOSITORY)

    private lateinit var repositoryFile: File
    private lateinit var viewModel: RepositoryViewModel

    override val title: String?
        get() = repository.name

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        repositoryFile = File(filesDir, "repos/${repository.name}")
        viewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)
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
        launch(CommonPool) {
            viewModel.removeRepository(repository)
            setResult(ActivityResults.RESULT_REPOSITORY_REMOVED, Intent().apply {
                putExtra(EXTRA_REPOSITORY, repository)
            })
            finish()
        }
    }

    companion object {
        const val EXTRA_REPOSITORY = "extra.repository"

        fun newIntent(context: Context, repository: Repository) =
                context.createIntent<RepositoryActivity> {
                    putExtra(EXTRA_REPOSITORY, repository)
                }
    }
}
