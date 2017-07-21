package me.thanel.gitlog

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_commit_log.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import org.eclipse.jgit.api.Git
import java.io.File

class CommitLogFragment : BaseFragment() {

    private val repository by lazy { arguments.getParcelable<Repository>(ARG_REPOSITORY) }

    private val adapter by lazy { CommitLogAdapter(repository) }

    private lateinit var git: Git
    private lateinit var repositoryFile: File

    override val layoutResId: Int
        get() = R.layout.fragment_commit_log

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        commitLogRecycler.adapter = adapter
        commitLogRecycler.layoutManager = LinearLayoutManager(context)

        repositoryFile = File(context.filesDir, "repos/${repository.name}")
        git = Git.open(repositoryFile)
        logCommits()
    }

    private fun logCommits() = launch(UI) {
        val log = kotlinx.coroutines.experimental.run(CommonPool) { git.log().call() }
        adapter.addAll(log)
    }

    companion object {
        const val ARG_REPOSITORY = "arg.repository"

        fun newInstance(repository: Repository): CommitLogFragment {
            return CommitLogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_REPOSITORY, repository)
                }
            }
        }
    }
}