package me.thanel.gitlog.repository.log

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
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
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit
import java.io.File

class CommitLogFragment : BaseFragment() {

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

        val application = context.applicationContext as Application
        val factory = CommitLogViewModel.Factory(application, repositoryId)
        val viewModel = ViewModelProviders.of(this, factory).get(CommitLogViewModel::class.java)
        viewModel.getRepository().observe(this, Observer {
            if (it != null) {
                repository = it
                repositoryFile = File(context.filesDir, "repos/${it.name}")
                git = Git.open(repositoryFile)
                logCommits()
            }
        })
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

    fun getRepository() = db.repositoryDao().getRepository(repositoryId)

    class Factory(
            private val application: Application,
            private val repositoryId: Int
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return CommitLogViewModel(application, repositoryId) as T
        }
    }
}