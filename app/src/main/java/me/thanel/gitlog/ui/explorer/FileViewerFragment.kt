package me.thanel.gitlog.ui.explorer

import activitystarter.Arg
import android.os.Bundle
import android.text.TextUtils
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.fragment.BaseWebViewerFragment
import java.io.File

class FileViewerFragment : BaseWebViewerFragment<FileViewModel>() {
    @get:Arg
    val filePath: String by argExtra()

    override fun onCreateViewModel() = FileViewModel.get(requireActivity())

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val file = File(filePath)
        val content = file.readText()
        val escapedContent = TextUtils.htmlEncode(content).replace("\n", "<br>")
        loadData("<body><code><pre>$escapedContent</pre></code></body>")
    }
}
