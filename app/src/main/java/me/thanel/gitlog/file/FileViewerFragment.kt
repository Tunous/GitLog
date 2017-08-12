package me.thanel.gitlog.file

import android.os.Bundle
import me.thanel.gitlog.base.BaseWebViewerFragment
import me.thanel.gitlog.utils.withArguments
import java.io.File

class FileViewerFragment : BaseWebViewerFragment<FileViewModel>() {
    private val filePath: String by stringArg(ARG_FILE_PATH)

    override fun onCreateViewModel() = FileViewModel.get(activity)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val file = File(filePath)
        val fileContent = file.readText().replace("\n", "<br/>")
        loadData("<body>$fileContent</body>")
    }

    companion object {
        private const val ARG_FILE_PATH = "arg.file_path"

        fun newInstance(filePath: String) = FileViewerFragment().withArguments {
            putString(ARG_FILE_PATH, filePath)
        }
    }
}