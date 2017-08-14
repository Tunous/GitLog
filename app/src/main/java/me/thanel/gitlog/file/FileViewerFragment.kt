package me.thanel.gitlog.file

import me.thanel.gitlog.base.BaseWebViewerFragment
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments

class FileViewerFragment : BaseWebViewerFragment<FileViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val filePath: String by stringArg(ARG_FILE_PATH)

    override fun onCreateViewModel() = FileViewModel.get(activity, repositoryId, filePath)

    override fun observeViewModel(viewModel: FileViewModel) {
        viewModel.repository.observe(this) {
            it?.let {
                val content = viewModel.readFileContent(it.git.repository).replace("\n", "<br/>")
                loadData("<body>$content</body>")
            }
        }
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"
        private const val ARG_FILE_PATH = "arg.file_path"

        fun newInstance(repositoryId: Int, filePath: String) = FileViewerFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
            putString(ARG_FILE_PATH, filePath)
        }
    }
}