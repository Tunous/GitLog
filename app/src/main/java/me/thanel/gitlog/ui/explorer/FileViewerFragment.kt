package me.thanel.gitlog.ui.explorer

import activitystarter.Arg
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.fragment.BaseWebViewerFragment
import java.io.File

class FileViewerFragment : BaseWebViewerFragment() {
    @get:Arg
    val filePath: String by argExtra()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setSource(File(filePath))
    }
}
