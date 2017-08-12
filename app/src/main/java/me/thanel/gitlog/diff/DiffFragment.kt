package me.thanel.gitlog.diff

import android.annotation.SuppressLint
import android.os.Bundle
import kotlinx.android.synthetic.main.fragment_commit_diff.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.commit.CommitViewModel
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.diff.DiffEntry

class DiffFragment : BaseFragment<CommitViewModel>() {

    private val commitSha: String by stringArg(ARG_COMMIT_SHA)
    private val repositoryId: Int by intArg(ARG_REPOSITORY_ID)
    private val diffFilePath: Array<String>? by stringArrayArg(ARG_DIFF_FILE_PATH)

    override val layoutResId: Int
        get() = R.layout.fragment_commit_diff

    @SuppressLint("SetJavaScriptEnabled")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

//        diffWebView.settings.apply {
//            javaScriptEnabled = true
//            builtInZoomControls = true
//            displayZoomControls = false
//            setSupportZoom(true)
//        }
    }

    override fun onCreateViewModel() = CommitViewModel.get(activity, repositoryId, commitSha)

    override fun observeViewModel(viewModel: CommitViewModel) {
        viewModel.diffEntries.observe(this, this::displayDiff)
    }

    private fun displayDiff(diffEntries: List<DiffEntry>?) {
        if (diffEntries == null) {
            // TODO: Loading...
            return
        }

        val entry = diffEntries.first()
        val text = viewModel.formatDiffEntry(entry)
        diffHunkView.setDiff(text)

//        val builder = StringBuilder()
//        val entries = if (diffFilePath == null) diffEntries
//        else diffEntries.filter { diffFilePath!!.contains(it.oldPath) }
//
//        for (diffEntry in entries) {
//            val diffText = viewModel.formatDiffEntry(diffEntry).replace("\n", "<br/>")
//
//            builder.apply {
//                append("<div>")
//                append("<h1>").append(diffEntry.newPath).append("</h1>")
//                append("<p><pre><code>").append(diffText).append("</code></pre></p>")
//                append("</div>")
//            }
//        }
//        val diffText = builder.toString()
//        diffWebView.loadData("<body>$diffText</body>", "text/html", "UTF-8")
    }

    companion object {
        private const val ARG_COMMIT_SHA = "arg.commit_sha"
        private const val ARG_REPOSITORY_ID = "arg.repository_id"
        private const val ARG_DIFF_FILE_PATH = "arg.diff_file_path"

        fun newInstance(commitSha: String, repositoryId: Int, diffFilePath: Array<String>?)
                = DiffFragment().withArguments {
            putString(ARG_COMMIT_SHA, commitSha)
            putInt(ARG_REPOSITORY_ID, repositoryId)
            putStringArray(ARG_DIFF_FILE_PATH, diffFilePath)
        }
    }

}