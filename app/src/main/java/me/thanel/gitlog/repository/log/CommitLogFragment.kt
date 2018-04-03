package me.thanel.gitlog.repository.log

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.view.isVisible
import kotlinx.android.synthetic.main.view_horizontal_recycler.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import me.thanel.gitlog.Preferences
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.intArg
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.errors.IncorrectObjectTypeException
import org.eclipse.jgit.errors.MissingObjectException
import org.eclipse.jgit.revplot.PlotCommitList
import org.eclipse.jgit.revplot.PlotLane
import org.eclipse.jgit.revplot.PlotWalk
import org.eclipse.jgit.revwalk.RevCommit

class CommitLogFragment : BaseFragment<CommitLogViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private lateinit var commitLogAdapter: CommitLogAdapter
    private lateinit var repository: Repository

    override val layoutResId: Int
        get() = R.layout.view_horizontal_recycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateViewModel() = CommitLogViewModel.get(requireActivity(), repositoryId)

    override fun observeViewModel(viewModel: CommitLogViewModel) =
        viewModel.repository.observe(this, this::onRepositoryLoaded)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyView.setText(R.string.no_commits)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.commit_log, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.show_graph).isChecked = Preferences.showGraph
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.show_graph -> {
                item.isChecked = !item.isChecked
                Preferences.showGraph = item.isChecked
                commitLogAdapter.notifyDataSetChanged()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun onRepositoryLoaded(repo: Repository?) {
        if (repo != null) {
            repository = repo
            logCommits()
        }
    }

    private fun openCommit(commit: RevCommit) {
        val intent = CommitActivity.newIntent(requireContext(), repository.id, commit.name)
        startActivity(intent)
    }

    private fun logCommits() = launch(UI) {
        val plotCommitList = withContext(CommonPool + coroutineContext) {
            val repo = repository.git.repository
            val plotWalk = PlotWalk(repo)

            for (ref in repo.allRefs.values) {
                val peeledRef = if (!ref.isPeeled) repo.peel(ref) else ref
                val objectId = peeledRef.peeledObjectId ?: peeledRef.objectId
                try {
                    val commit = plotWalk.parseCommit(objectId)
                    plotWalk.markStart(plotWalk.lookupCommit(commit))
                } catch (e: MissingObjectException) {
                    // ignore: the ref points to an object that does not exist;
                    // it should be ignored as traversal starting point.
                } catch (e: IncorrectObjectTypeException) {
                    // ignore: the ref points to an object that is not a commit
                    // (e.g. a tree or a blob);
                    // it should be ignored as traversal starting point.
                }
            }

            val plotCommitList = PlotCommitList<PlotLane>()
            plotCommitList.source(plotWalk)
            plotCommitList.fillTo(Int.MAX_VALUE)
            return@withContext plotCommitList
        }

        commitLogAdapter = CommitLogAdapter(plotCommitList, this@CommitLogFragment::openCommit)
        commitLogAdapter.notifyDataSetChanged()
        recyclerView.adapter = commitLogAdapter

        emptyView.isVisible = plotCommitList.isEmpty()
        loadingProgressBar.hide()
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = CommitLogFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}
