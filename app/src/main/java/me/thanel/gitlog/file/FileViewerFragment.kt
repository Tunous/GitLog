package me.thanel.gitlog.file

import android.text.TextUtils
import me.thanel.gitlog.base.BaseWebViewerFragment
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments

class FileViewerFragment : BaseWebViewerFragment<FileViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val refName by stringArg(ARG_REF_NAME)
    private val filePath: String by stringArg(ARG_FILE_PATH)

    override fun onCreateViewModel() = FileViewModel.get(activity, repositoryId, refName,
            filePath)

    override fun observeViewModel(viewModel: FileViewModel) {
        viewModel.repository.observe(this) {
            it?.let {
                val content = viewModel.readFileContent(it.git.repository)
                val escapedContent = TextUtils.htmlEncode(content).replace("\n", "<br>")
                loadData("<body><code><pre>$escapedContent</pre></code></body>")
            }
        }
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"
        private const val ARG_REF_NAME = "arg.ref_name"
        private const val ARG_FILE_PATH = "arg.file_path"

        fun newInstance(repositoryId: Int, refName: String, filePath: String)
                = FileViewerFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
            putString(ARG_REF_NAME, refName)
            putString(ARG_FILE_PATH, filePath)
        }
    }
}