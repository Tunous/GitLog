package me.thanel.gitlog

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_repository_list.*

class RepositoryListFragment : Fragment() {

    private val adapter = RepositoryListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_repository_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        repositoryRecyclerView.adapter = adapter
        repositoryRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter.setOnOpenRepositoryListener(this::openRepository)

        addRepositoryButton.setOnClickListener {
            val intent = AddRepositoryActivity.newIntent(context)
            startActivityForResult(intent, REQUEST_CLONE_REPOSITORY)
        }

        adapter.addAll(RepositoryListManager.listRepositories(context))
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

    private fun openRepository(repository: Repository) {
        val intent = RepositoryActivity.newIntent(context, repository)
        startActivityForResult(intent, REQUEST_OPEN_REPOSITORY)
    }

    companion object {
        private const val REQUEST_CLONE_REPOSITORY = 1
        private const val REQUEST_OPEN_REPOSITORY = 2
    }
}