package me.thanel.gitlog.repository.file

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.base.BaseFragmentActivity
import me.thanel.gitlog.repository.filelist.GitFileListActivityStarter
import me.thanel.gitlog.utils.getAbbreviatedName
import me.thanel.gitlog.utils.observe

class GitFileViewerActivity : BaseFragmentActivity() {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    @get:Arg
    val filePath: String by argExtra()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = filePath.split("/").last()
        toolbarSubtitle

        val viewModel = GitFileViewModel.get(this, repositoryId, refName, filePath)
        viewModel.repository.observe(this) {
            it?.let {
                toolbarSubtitle = it.git.repository.getAbbreviatedName(refName)
            }
        }
    }

    override fun createFragment(): GitFileViewerFragment =
        GitFileViewerFragmentStarter.newInstance(repositoryId, refName, filePath)

    override fun getSupportParentActivityIntent(): Intent =
        GitFileListActivityStarter.getIntent(this, repositoryId, refName)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
}
