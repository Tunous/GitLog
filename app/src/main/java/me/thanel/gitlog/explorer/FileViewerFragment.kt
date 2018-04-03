package me.thanel.gitlog.explorer

import android.os.Bundle
import android.text.TextUtils
import me.thanel.gitlog.base.BaseWebViewerFragment
import me.thanel.gitlog.utils.stringArg
import me.thanel.gitlog.utils.withArguments
import java.io.File

class FileViewerFragment : BaseWebViewerFragment<FileViewModel>() {
    private val filePath by stringArg(ARG_FILE_PATH)

    override fun onCreateViewModel() = FileViewModel.get(requireActivity())

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val file = File(filePath)
        val content = file.readText()
        val escapedContent = TextUtils.htmlEncode(content).replace("\n", "<br>")
        loadData("<body><code><pre>$escapedContent</pre></code></body>")
    }

    companion object {
        private const val ARG_FILE_PATH = "arg.file_path"

        fun newInstance(filePath: String) = FileViewerFragment().withArguments {
            putString(ARG_FILE_PATH, filePath)
        }
    }
}
