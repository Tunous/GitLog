package me.thanel.gitlog.commit

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_commit.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.diff.DiffActivity
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import me.thanel.gitlog.view.AvatarDrawable
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.util.RelativeDateFormatter
import java.text.SimpleDateFormat
import java.util.*

class CommitFragment : BaseFragment<CommitViewModel>() {
    private val commitSha by stringArg(ARG_COMMIT_SHA)
    private val repositoryId by intArg(ARG_REPOSITORY_ID)

    override val layoutResId: Int
        get() = R.layout.fragment_commit

    private val adapter = FileAdapter {
        // TODO: Open file diff
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fileRecyclerView.adapter = adapter
        fileRecyclerView.layoutManager = LinearLayoutManager(context)

        viewWholeDiffButton.setOnClickListener { showWholeDiff() }
    }

    override fun onCreateViewModel() = CommitViewModel.get(activity, repositoryId, commitSha)

    override fun observeViewModel(viewModel: CommitViewModel) {
        viewModel.diffEntries.observe(this, this::displayDiffEntries)
        viewModel.commit.observe(this, this::displayCommitInformation)
    }

    private fun displayDiffEntries(it: List<DiffEntry>?) {
        if (it == null) {
            // TODO: Loading indicator
            return
        }
        adapter.replaceAll(it)
    }

    private fun displayCommitInformation(it: RevCommit?) {
        if (it == null) {
            // TODO: Loading indicator
            return
        }
        val authorName = it.authorIdent.name
        commitAuthorAvatarView.setImageDrawable(AvatarDrawable(authorName, commitSha))
        commitAuthorView.text = authorName
        commitShaView.text = it.name.substring(0, 7)
        commitMessageView.text = it.fullMessage.trim()

        val date = Date(it.commitTime * 1000L)
        commitDateView.text = RelativeDateFormatter.format(date)
        val format = SimpleDateFormat("'authored' yyyy-MM-dd '@' HH:mm", Locale.getDefault())
        val longDate = format.format(date)
        commitDateView.setOnClickListener {
            Toast.makeText(context, longDate, Toast.LENGTH_SHORT).show()
        }
    }

    private fun showWholeDiff() {
        startActivity(DiffActivity.newIntent(context, commitSha, repositoryId))
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