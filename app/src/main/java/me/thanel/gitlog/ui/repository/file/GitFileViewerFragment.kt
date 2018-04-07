package me.thanel.gitlog.ui.repository.file

import activitystarter.Arg
import android.os.Bundle
import android.text.TextUtils
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.fragment.BaseWebViewerFragment
import me.thanel.gitlog.ui.utils.observe
import org.koin.android.architecture.ext.viewModel

class GitFileViewerFragment : BaseWebViewerFragment() {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    @get:Arg
    val filePath: String by argExtra()

    private val gitFileViewModel by viewModel<GitFileViewModel> {
        GitFileViewModel.createParams(repositoryId, refName, filePath)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        gitFileViewModel.fileContent.observe(this) { content ->
            if (content != null) {
                val escapedContent = TextUtils.htmlEncode(content).replace("\n", "<br>")
                loadData("<body><code><pre>$escapedContent</pre></code></body>")
            }
        }
    }
}
