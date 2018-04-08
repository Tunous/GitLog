package me.thanel.gitlog.ui.base.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import com.pddstudio.highlightjs.models.Language
import com.pddstudio.highlightjs.models.Theme
import kotlinx.android.synthetic.main.fragment_web_viewer.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.utils.StyleParser
import java.io.File

abstract class BaseWebViewerFragment : BaseFragment() {
    override val layoutResId: Int
        get() = R.layout.fragment_web_viewer

    open val language: Language = Language.AUTO_DETECT
    open val syntaxTheme: Theme = Theme.GITHUB
    open val showLineNumbers: Boolean = true

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        webView.apply {
            setZoomSupportEnabled(true)
            setShowLineNumbers(showLineNumbers)
            highlightLanguage = language
            theme = syntaxTheme
        }

        initializeBackgroundColor()
    }

    private fun initializeBackgroundColor() {
        launch {
            val backgroundColor = StyleParser(syntaxTheme).parseBackgroundColor(requireContext())
            if (backgroundColor != null) {
                launch(UI) {
                    webView.setBackgroundColor(backgroundColor)
                }
            }
        }
    }

    protected fun setSource(source: File) = webView.setSource(source)

    protected fun setSource(source: String) = webView.setSource(source)
}
