package me.thanel.gitlog.commit

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_commit_diff.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.utils.withArguments

class DiffFragment : BaseFragment() {

    private val commitSha by stringArg(ARG_COMMIT_SHA)
    private val repositoryId by intArg(ARG_REPOSITORY_ID)

    override val layoutResId: Int
        get() = R.layout.fragment_commit_diff

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        diffWebView.settings.apply {
            javaScriptEnabled = true
            builtInZoomControls = true
            displayZoomControls = false
            setSupportZoom(true)
        }

        val viewModel = CommitViewModel.get(activity, repositoryId, commitSha)

        viewModel.diffEntries.observe(this, Observer { diffEntries ->
            if (diffEntries == null) {
                // TODO: Loading...
            } else {
                val builder = StringBuilder()
                for (diffEntry in diffEntries) {
                    val diffText = viewModel.formatDiffEntry(diffEntry).replace("\n", "<br/>")

                    builder.apply {
                        append("<div>")
                        append("<h1>").append(diffEntry.newPath).append("</h1>")
                        append("<p><pre><code>").append(diffText).append("</code></pre></p>")
                        append("</div>")
                    }
                }
                val diffText = builder.toString()
                diffWebView.loadData("<body>$diffText</body>", "text/html", "UTF-8")
            }
        })
    }

    companion object {
        private const val ARG_COMMIT_SHA = "arg.commit_sha"
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(commitSha: String, repositoryId: Int) = DiffFragment().withArguments {
            putString(ARG_COMMIT_SHA, commitSha)
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }

}