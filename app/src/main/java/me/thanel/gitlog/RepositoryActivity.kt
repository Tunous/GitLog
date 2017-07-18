package me.thanel.gitlog

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_repository.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import org.eclipse.jgit.api.Git
import java.io.File

class RepositoryActivity : AppCompatActivity() {

    private val repository by lazy { intent.getParcelableExtra<Repository>(EXTRA_REPOSITORY) }

    private val adapter = CommitLogAdapter()

    private lateinit var git: Git

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository)

        supportActionBar?.title = repository.name

        recycler.adapter = adapter
        recycler.layoutManager = LinearLayoutManager(this)

        val repositoryFile = File(filesDir, "repos/${repository.name}")
        git = Git.open(repositoryFile)
        logCommits()
    }

    private fun logCommits() = launch(UI) {
        val log = run(CommonPool) { git.log().call() }
        adapter.addAll(log)
    }

    companion object {
        private const val EXTRA_REPOSITORY = "extra.repository"

        fun newIntent(context: Context, repository: Repository): Intent {
            return Intent(context, RepositoryActivity::class.java).apply {
                putExtra(EXTRA_REPOSITORY, repository)
            }
        }
    }
}
