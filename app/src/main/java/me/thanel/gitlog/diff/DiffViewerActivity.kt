package me.thanel.gitlog.diff

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.commit.CommitActivityStarter
import me.thanel.gitlog.commit.CommitViewModel
import me.thanel.gitlog.utils.observe
import org.eclipse.jgit.lib.AbbreviatedObjectId

class DiffViewerActivity : BaseFragmentActivity() {
    @get:Arg
    val commitSha: String by argExtra()

    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val diffId: AbbreviatedObjectId by argExtra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = CommitViewModel.get(this, repositoryId, commitSha)

        viewModel.repository.observe(this) {
            toolbarSubtitle = it?.name
        }
        viewModel.getDiffEntry(diffId).observe(this) {
            toolbarTitle = it?.newPath?.split("/")?.lastOrNull()
        }
    }

    override fun createFragment(): DiffViewerFragment =
        DiffViewerFragmentStarter.newInstance(commitSha, repositoryId, diffId)

    override fun getSupportParentActivityIntent(): Intent =
        CommitActivityStarter.getIntent(this, commitSha, repositoryId)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
