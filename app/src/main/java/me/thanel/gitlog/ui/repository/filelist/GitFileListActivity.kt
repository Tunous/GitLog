package me.thanel.gitlog.ui.repository.filelist

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity
import me.thanel.gitlog.ui.commit.CommitActivityStarter
import me.thanel.gitlog.ui.utils.getAbbreviatedName
import me.thanel.gitlog.ui.utils.observe

class GitFileListActivity : BaseFragmentActivity() {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = GitFileListViewModel.get(this, repositoryId, refName)
        viewModel.repository.observe(this) {
            it?.let {
                toolbarSubtitle = it.git.repository.getAbbreviatedName(refName)
            }
        }
    }

    override fun createFragment(): GitFileListFragment =
        GitFileListFragmentStarter.newInstance(repositoryId, refName)

    override fun getSupportParentActivityIntent(): Intent =
        CommitActivityStarter.getIntent(this, refName, repositoryId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
