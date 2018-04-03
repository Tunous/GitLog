package me.thanel.gitlog.ui.repositorylist

import android.os.Bundle
import android.view.View
import androidx.view.isVisible
import kotlinx.android.synthetic.main.fragment_repository_list.*
import me.thanel.gitlog.R
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.ui.AddRepositoryActivityStarter
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.repository.RepositoryActivityStarter
import me.thanel.gitlog.ui.utils.observe

class RepositoryListFragment : BaseFragment<RepositoryViewModel>() {
    private val adapter = RepositoryListAdapter(this::openRepository)

    override val layoutResId: Int
        get() = R.layout.fragment_repository_list

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        emptyView.setText(R.string.no_repositories)

        repositoryRecyclerView.adapter = adapter

        addRepositoryButton.setOnClickListener {
            showAddRepositoryScreen()
        }
    }

    override fun onCreateViewModel() = RepositoryViewModel.get(requireActivity())

    override fun observeViewModel(viewModel: RepositoryViewModel) =
        viewModel.listRepositories().observe(this, this::displayRepositories)

    private fun displayRepositories(repositories: List<Repository>?) {
        if (repositories != null) {
            loadingProgressBar.hide()
            adapter.replaceAll(repositories)
            emptyView.isVisible = repositories.isEmpty()
        }
    }

    private fun showAddRepositoryScreen() {
        AddRepositoryActivityStarter.start(requireContext())
    }

    private fun openRepository(repository: Repository) {
        RepositoryActivityStarter.start(requireContext(), repository.id)
    }
}