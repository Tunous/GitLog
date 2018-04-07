package me.thanel.gitlog.ui.diff

import activitystarter.Arg
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import me.thanel.gitlog.ui.base.fragment.BaseWebViewerFragment
import me.thanel.gitlog.ui.commit.CommitViewModel
import me.thanel.gitlog.ui.utils.observe
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.lib.AbbreviatedObjectId
import org.koin.android.architecture.ext.viewModel

class DiffViewerFragment : BaseWebViewerFragment() {
    @get:Arg
    val commitSha: String by argExtra()

    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val diffId: AbbreviatedObjectId by argExtra()

    private val viewModel by viewModel<CommitViewModel> {
        CommitViewModel.createParams(repositoryId, commitSha)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getDiffEntry(diffId).observe(this, this::displayDiff)
    }

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
}
