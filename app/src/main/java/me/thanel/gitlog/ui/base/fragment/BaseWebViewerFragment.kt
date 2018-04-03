package me.thanel.gitlog.ui.base.fragment

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModel
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_web_viewer.*
import me.thanel.gitlog.R

abstract class BaseWebViewerFragment<T : ViewModel> : BaseFragment<T>() {
    override val layoutResId: Int
        get() = R.layout.fragment_web_viewer

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        webView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
        }
    }

    protected fun loadData(data: String) = webView.loadData(data, "text/html", "UTF-8")
}
