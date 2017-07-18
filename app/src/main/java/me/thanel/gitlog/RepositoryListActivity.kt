package me.thanel.gitlog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_repository_list.*

class RepositoryListActivity : AppCompatActivity() {

    private val adapter = RepositoryListAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_repository_list)

        repositoryRecyclerView.adapter = adapter
        repositoryRecyclerView.layoutManager = LinearLayoutManager(this)
        adapter.setOnOpenRepositoryListener(this::openRepository)

        addRepositoryButton.setOnClickListener {
            val intent = AddRepositoryActivity.newIntent(this)
            startActivityForResult(intent, REQUEST_CLONE_REPOSITORY)
        }

        adapter.addAll(RepositoryListManager.listRepositories(this))
    }

    private fun openRepository(repository: Repository) {
        val intent = RepositoryActivity.newIntent(this, repository)
        startActivityForResult(intent, REQUEST_OPEN_REPOSITORY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            REQUEST_CLONE_REPOSITORY -> if (resultCode == Activity.RESULT_OK) {
                val newRepository = data!!.getParcelableExtra<Repository>(RepositoryActivity.EXTRA_REPOSITORY)
                adapter.add(newRepository)
                openRepository(newRepository)
            }

            REQUEST_OPEN_REPOSITORY -> if (resultCode == ActivityResults.RESULT_REPOSITORY_REMOVED) {
                val repository = data!!.getParcelableExtra<Repository>(RepositoryActivity.EXTRA_REPOSITORY)
                adapter.remove(repository)
            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    companion object {
        private const val REQUEST_CLONE_REPOSITORY = 1
        private const val REQUEST_OPEN_REPOSITORY = 2
    }
}
