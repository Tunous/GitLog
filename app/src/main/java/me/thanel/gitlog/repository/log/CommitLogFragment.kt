package me.thanel.gitlog.repository.log

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.view_recycler.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.revwalk.RevCommit

class CommitLogFragment : BaseFragment<CommitLogViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val commitLogAdapter = CommitLogAdapter(this::openCommit)
    private lateinit var repository: Repository

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreateViewModel() = CommitLogViewModel.get(activity, repositoryId)

    override fun observeViewModel(viewModel: CommitLogViewModel) =
        viewModel.repository.observe(this, this::onRepositoryLoaded)

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.apply {
            adapter = commitLogAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    private fun onRepositoryLoaded(it: Repository?) {
        if (it == null) {
            // TODO: Loading
            return
        }
        repository = it
        loadRefs()
        logCommits()
    }

    private fun openCommit(commit: RevCommit) {
        val intent = CommitActivity.newIntent(context, repository.id, commit.name)
        startActivity(intent)
    }

    private fun logCommits() = launch(UI) {
        @Suppress("ConvertLambdaToReference")
        val log = run(CommonPool) {
            repository.git.log()
                    .all()
                    .call()
        }
        commitLogAdapter.replaceAll(log)
    }

    private fun loadRefs() = launch(UI) {
        val refs = run(CommonPool) {
            repository.git.repository.allRefsByPeeledObjectId
        }
        commitLogAdapter.replaceRefs(refs)
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = CommitLogFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}
