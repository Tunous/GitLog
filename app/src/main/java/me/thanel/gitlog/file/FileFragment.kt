package me.thanel.gitlog.file

import android.annotation.SuppressLint
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_file.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.utils.withArguments
import java.io.File

class FileFragment : BaseFragment<FileViewModel>() {
    private val filePath: String by stringArg(ARG_FILE_PATH)

    override val layoutResId: Int
        get() = R.layout.fragment_file

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fileWebView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
        }
    }

    override fun onCreateViewModel() = FileViewModel.get(activity)

    override fun observeViewModel(viewModel: FileViewModel) {
        val file = File(filePath)
        val fileContent = file.readText().replace("\n", "<br/>")
        fileWebView.loadData("<body>$fileContent</body>", "text/html", "UTF-8")
    }

    companion object {
        private const val ARG_FILE_PATH = "arg.file_path"

        fun newInstance(filePath: String) = FileFragment().withArguments {
            putString(ARG_FILE_PATH, filePath)
        }
    }
}