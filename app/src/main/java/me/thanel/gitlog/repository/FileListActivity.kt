package me.thanel.gitlog.repository

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.utils.createIntent
import me.thanel.gitlog.utils.getAbbreviatedName
import me.thanel.gitlog.utils.observe

class FileListActivity : BaseFragmentActivity() {
    private val repositoryId by intExtra(EXTRA_REPOSITORY_ID)
    private val refName by stringExtra(EXTRA_REF_NAME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = FileListViewModel.get(this, repositoryId, refName)
        viewModel.repository.observe(this) {
            it?.let {
                subtitle = it.git.repository.getAbbreviatedName(refName)
            }
        }
    }

    override fun createFragment(): Fragment = FileListFragment.newInstance(repositoryId, refName)

    override fun getSupportParentActivityIntent(): Intent =
        CommitActivity.newIntent(this, repositoryId, refName)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

    companion object {
        private const val EXTRA_REPOSITORY_ID = "extra.repository_id"
        private const val EXTRA_REF_NAME = "extra.ref_name"

        fun newIntent(context: Context, repositoryId: Int, refName: String): Intent
                = context.createIntent<FileListActivity> {
            putExtra(EXTRA_REPOSITORY_ID, repositoryId)
            putExtra(EXTRA_REF_NAME, refName)
        }
    }
}
