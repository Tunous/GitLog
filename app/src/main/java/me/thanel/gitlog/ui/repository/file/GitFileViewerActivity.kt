package me.thanel.gitlog.ui.repository.file

import activitystarter.Arg
import android.content.Intent
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.activity.BaseFragmentActivity
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.repository.filelist.GitFileListActivityStarter
import me.thanel.gitlog.ui.utils.getAbbreviatedName
import me.thanel.gitlog.ui.utils.observe
import org.koin.android.architecture.ext.viewModel

class GitFileViewerActivity : BaseFragmentActivity() {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    @get:Arg
    val filePath: String by argExtra()

    private val repositoryViewModel by viewModel<RepositoryViewModel> {
        RepositoryViewModel.createParams(repositoryId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toolbarTitle = filePath.split("/").last()
        repositoryViewModel.gitRepository.observe(this) {
            toolbarSubtitle = it?.getAbbreviatedName(refName)
        }
    }

    override fun createFragment(): GitFileViewerFragment =
        GitFileViewerFragmentStarter.newInstance(repositoryId, refName, filePath)

    override fun getSupportParentActivityIntent(): Intent {
        val directoryPath = filePath.split('/')
            .dropLast(1) // Get rid of last path part which corresponds to file name
            .joinToString("/")
        return GitFileListActivityStarter.getIntent(this, repositoryId, refName, directoryPath)
            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
}
