package me.thanel.gitlog.commit

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.revwalk.RevCommit

class CommitFragment : BaseFragment<CommitViewModel>() {
    private val commitSha by stringArg(ARG_COMMIT_SHA)
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private lateinit var adapter: DiffHunkAdapter

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreateViewModel() = CommitViewModel.get(activity, repositoryId, commitSha)

    override fun observeViewModel(viewModel: CommitViewModel) {
        viewModel.commit.observe(this, this::displayCommitInformation)
        viewModel.diffEntries.observe(this, this::displayDiffs)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = DiffHunkAdapter(viewModel)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
    }

    private fun displayCommitInformation(commit: RevCommit?) {
        if (commit == null) {
            // TODO: Loading indicator
            return
        }
        adapter.commit = commit
    }

    private fun displayDiffs(diffEntries: List<DiffEntry>?) {
        if (diffEntries == null) {
            // TODO: Loading indicator
            return
        }

        adapter.replaceAll(diffEntries)
    }

    companion object {
        private const val ARG_COMMIT_SHA = "arg.commit_sha"
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(commitSha: String, repositoryId: Int) = CommitFragment().withArguments {
            putString(ARG_COMMIT_SHA, commitSha)
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}
