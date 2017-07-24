package me.thanel.gitlog.commit

import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_commit_diff.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.Repository
import me.thanel.gitlog.model.Commit
import me.thanel.gitlog.utils.withArguments

class DiffFragment : BaseFragment() {

    private val commit by parcelableArg<Commit>(ARG_COMMIT)
    private val repository by parcelableArg<Repository>(ARG_REPOSITORY)

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

        val factory = DiffViewModel.Factory(repository.path, commit.sha)
        val viewModel = ViewModelProviders.of(activity, factory)
                .get(DiffViewModel::class.java)

        viewModel.getDiffEntries().observe(this, Observer { diffEntries ->
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
        private const val ARG_COMMIT = "arg.commit"
        private const val ARG_REPOSITORY = "arg.repository"

        fun newInstance(commit: Commit, repository: Repository) = DiffFragment().withArguments {
            putParcelable(ARG_COMMIT, commit)
            putParcelable(ARG_REPOSITORY, repository)
        }
    }

}