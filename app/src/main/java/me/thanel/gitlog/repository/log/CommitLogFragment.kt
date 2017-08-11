package me.thanel.gitlog.repository.log

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_commit_log.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.getViewModel
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import java.io.File

class CommitLogFragment : BaseFragment<CommitLogViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)

    private val adapter = CommitLogAdapter(this::openCommit)

    private lateinit var repository: Repository

    private lateinit var git: Git
    private lateinit var repositoryFile: File

    override val layoutResId: Int
        get() = R.layout.fragment_commit_log

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commitLogRecycler.adapter = adapter
        commitLogRecycler.layoutManager = LinearLayoutManager(context)
    }

    override fun onCreateViewModel() = CommitLogViewModel.get(activity, repositoryId)

    override fun observeViewModel(viewModel: CommitLogViewModel) {
        viewModel.repository.observe(this, this::displayLog)
    }

    private fun displayLog(it: Repository?) {
        if (it != null) {
            repository = it
            repositoryFile = File(context.filesDir, "repos/${it.name}")
            git = Git.open(repositoryFile)
            logCommits()
        }
    }

    private fun openCommit(commit: RevCommit) {
        val intent = CommitActivity.newIntent(context, commit.name, repository.id)
        startActivity(intent)
    }

    private fun logCommits() = launch(UI) {
        val log = run(CommonPool) { git.log().call() }
        adapter.addAll(log)
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = CommitLogFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}

class CommitLogViewModel(
        application: Application,
        private val repositoryId: Int
) : AndroidViewModel(application) {
    private val db = (application as GitLogApplication).database

    val repository: LiveData<Repository>
        get() = db.repositoryDao().getRepository(repositoryId)

    companion object {
        fun get(activity: FragmentActivity, repositoryId: Int) = getViewModel(activity) {
            CommitLogViewModel(activity.application, repositoryId)
        }
    }
}