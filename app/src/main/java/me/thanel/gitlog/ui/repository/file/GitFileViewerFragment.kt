package me.thanel.gitlog.ui.repository.file

import activitystarter.Arg
import android.text.TextUtils
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.fragment.BaseWebViewerFragment
import me.thanel.gitlog.ui.utils.observe

class GitFileViewerFragment : BaseWebViewerFragment<GitFileViewModel>() {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    @get:Arg
    val filePath: String by argExtra()

    override fun onCreateViewModel() = GitFileViewModel.get(
        requireActivity(),
        repositoryId,
        refName,
        filePath
    )

    override fun observeViewModel(viewModel: GitFileViewModel) =
        viewModel.repository.observe(this) {
            it?.let {
                val content = viewModel.readFileContent(it.git.repository)
                val escapedContent = TextUtils.htmlEncode(content).replace("\n", "<br>")
                loadData("<body><code><pre>$escapedContent</pre></code></body>")
            }
        }
}
