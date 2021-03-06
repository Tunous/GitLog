package me.thanel.gitlog.ui.repository

import activitystarter.Arg
import android.app.ProgressDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.text.style.StyleSpan
import android.view.Menu
import android.view.MenuItem
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.GitLogRepository
import me.thanel.gitlog.db.model.git
import me.thanel.gitlog.ui.base.activity.BaseBottomNavigationActivity
import me.thanel.gitlog.ui.repository.branchlist.BranchListFragmentStarter
import me.thanel.gitlog.ui.repository.filelist.GitFileListFragmentStarter
import me.thanel.gitlog.ui.repository.log.CommitLogFragmentStarter
import me.thanel.gitlog.ui.repositorylist.RepositoryListActivityStarter
import me.thanel.gitlog.ui.ssh.TransportCallback
import me.thanel.gitlog.ui.utils.StyleableTag
import me.thanel.gitlog.ui.utils.formatTags
import me.thanel.gitlog.ui.utils.getAbbreviatedName
import me.thanel.gitlog.ui.utils.observe
import org.eclipse.jgit.lib.Constants
import org.koin.android.architecture.ext.viewModel

class RepositoryActivity : BaseBottomNavigationActivity() {
    @get:Arg
    val repositoryId: Int by argExtra()

    private val repositoryViewModel by viewModel<RepositoryViewModel> {
        RepositoryViewModel.createParams(repositoryId)
    }

    private var gitLogRepository: GitLogRepository? = null

    override val menuResId: Int
        get() = R.menu.repository_navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        repositoryViewModel.repository.observe(this) {
            if (it != null) {
                toolbarTitle = it.name
                toolbarSubtitle = it.git.repository.getAbbreviatedName(Constants.HEAD)
                gitLogRepository = it
                invalidateOptionsMenu()
            }
        }
    }

    override fun createFragment(itemId: Int): Fragment = when (itemId) {
        R.id.log -> CommitLogFragmentStarter.newInstance(repositoryId)
        R.id.files -> GitFileListFragmentStarter.newInstance(repositoryId, Constants.HEAD)
        R.id.branches -> BranchListFragmentStarter.newInstance(repositoryId)
        else -> throw IllegalAccessException("Unknown fragment id: $itemId")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.repository, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.findItem(R.id.remove)?.isVisible = gitLogRepository != null
        menu.findItem(R.id.fetch)?.isVisible = gitLogRepository != null
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.remove -> promptRemoveRepository()
            R.id.fetch -> fetch()
            else -> return false
        }

        return true
    }

    private fun fetch() = launch(UI) {
        val dialog = ProgressDialog.show(this@RepositoryActivity, "Fetch", "Fetching...", true)
        async(CommonPool) {
            gitLogRepository!!.git.fetch()
                .setTransportConfigCallback(TransportCallback(this@RepositoryActivity))
                .call()
        }.await()
        dialog.dismiss()
        recreate()
    }

    override fun getSupportParentActivityIntent(): Intent =
        RepositoryListActivityStarter.getIntent(this)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    private fun promptRemoveRepository() {
        val message = getString(R.string.remove_confirm_message)
            .formatTags(StyleableTag("target", gitLogRepository!!.name, StyleSpan(Typeface.BOLD)))

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
        repositoryViewModel.deleteRepositoryAsync()
        finish()
    }
}
