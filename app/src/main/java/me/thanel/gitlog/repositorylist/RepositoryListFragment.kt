package me.thanel.gitlog.repositorylist

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_repository_list.*
import me.thanel.gitlog.AddRepositoryActivity
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.repository.RepositoryActivity
import me.thanel.gitlog.utils.observe

class RepositoryListFragment : BaseFragment<RepositoryViewModel>() {
    private val adapter = RepositoryListAdapter(this::openRepository)

    override val layoutResId: Int
        get() = R.layout.fragment_repository_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        repositoryRecyclerView.adapter = adapter
        repositoryRecyclerView.layoutManager = LinearLayoutManager(context)

        addRepositoryButton.setOnClickListener {
            showAddRepositoryScreen()
        }
    }

    override fun onCreateViewModel() = RepositoryViewModel.get(activity)

    override fun observeViewModel(viewModel: RepositoryViewModel) {
        viewModel.listRepositories().observe(this, this::displayRepositories)
    }

    private fun displayRepositories(repositories: List<Repository>?) {
        if (repositories != null) {
            adapter.replaceAll(repositories)
        } else {
            adapter.clear()
        }
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

    private fun showAddRepositoryScreen() {
        val intent = AddRepositoryActivity.newIntent(context)
        startActivityForResult(intent, REQUEST_CLONE_REPOSITORY)
    }

    private fun openRepository(repository: Repository) {
        val intent = RepositoryActivity.newIntent(context, repository.id)
        startActivityForResult(intent, REQUEST_OPEN_REPOSITORY)
    }

    companion object {
        private const val REQUEST_CLONE_REPOSITORY = 1
        private const val REQUEST_OPEN_REPOSITORY = 2
    }
}