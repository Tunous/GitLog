package me.thanel.gitlog.diff

import me.thanel.gitlog.base.BaseWebViewerFragment
import me.thanel.gitlog.commit.CommitViewModel
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.AbbreviatedObjectId

class DiffViewerFragment : BaseWebViewerFragment<CommitViewModel>() {
    private val commitSha by stringArg(ARG_COMMIT_SHA)
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val diffId by serializableArg<AbbreviatedObjectId>(ARG_DIFF_ID)

    override fun onCreateViewModel() = CommitViewModel.get(activity, repositoryId, commitSha)

    override fun observeViewModel(viewModel: CommitViewModel) = viewModel.getDiffEntry(diffId).observe(this, this::displayDiff)

    private fun displayDiff(diffEntry: DiffEntry?) {
        if (diffEntry == null) {
            // TODO: Loading or error...
            return
        }

        val diffText = viewModel.formatDiffEntry(diffEntry)
                .split("\n")
                .dropWhile { !it.startsWith("@@") }
                .joinToString("<br/>")
        loadData(StringBuilder().apply {
            append("<body>")
            append("<div>")
            append("<p><pre><code>").append(diffText).append("</code></pre></p>")
            append("</div>")
            append("</body>")
        }.toString())
    }

    companion object {
        private const val ARG_COMMIT_SHA = "arg.commit_sha"
        private const val ARG_REPOSITORY_ID = "arg.repository_id"
        private const val ARG_DIFF_ID = "arg.diff_id"

        fun newInstance(commitSha: String, repositoryId: Int, diffId: AbbreviatedObjectId)
                = DiffViewerFragment().withArguments {
            putString(ARG_COMMIT_SHA, commitSha)
            putInt(ARG_REPOSITORY_ID, repositoryId)
            putSerializable(ARG_DIFF_ID, diffId)
        }
    }
}