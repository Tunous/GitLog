package me.thanel.gitlog.repositorylist

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import kotlinx.android.synthetic.main.fragment_repository_list.*
import me.thanel.gitlog.AddRepositoryActivity
import me.thanel.gitlog.BaseFragment
import me.thanel.gitlog.R
import me.thanel.gitlog.db.Repository
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.repository.RepositoryActivity

class RepositoryListFragment : BaseFragment() {

    private val adapter = RepositoryListAdapter()

    override val layoutResId: Int
        get() = R.layout.fragment_repository_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        repositoryRecyclerView.adapter = adapter
        repositoryRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter.setOnOpenRepositoryListener(this::openRepository)

        addRepositoryButton.setOnClickListener {
            val intent = AddRepositoryActivity.newIntent(context)
            startActivityForResult(intent, REQUEST_CLONE_REPOSITORY)
        }

        val viewModel = ViewModelProviders.of(this).get(RepositoryViewModel::class.java)
        viewModel.listRepositories().observe(this, Observer { repositories ->
            Log.d("Repositories", "Received: $repositories")
            adapter.clear()
            if (repositories != null) {
                adapter.addAll(repositories)
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
//            REQUEST_CLONE_REPOSITORY -> if (resultCode == Activity.RESULT_OK) {
//                val newRepository = data!!.getParcelableExtra<Repo>(RepositoryActivity.EXTRA_REPOSITORY_ID)
//                adapter.add(newRepository)
//                openRepository(newRepository)
//            }
//
//            REQUEST_OPEN_REPOSITORY -> if (resultCode == ActivityResults.RESULT_REPOSITORY_REMOVED) {
//                val repository = data!!.getParcelableExtra<Repo>(RepositoryActivity.EXTRA_REPOSITORY_ID)
//                adapter.remove(repository)
//            }

            else -> super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun openRepository(repository: Repository) {
        val intent = RepositoryActivity.newIntent(context, repository)
        startActivityForResult(intent, REQUEST_OPEN_REPOSITORY)
    }

    companion object {
        private const val REQUEST_CLONE_REPOSITORY = 1
        private const val REQUEST_OPEN_REPOSITORY = 2
    }
}