package me.thanel.gitlog.ui.repositorylist

import android.os.Bundle
import android.view.View
import androidx.view.isVisible
import kotlinx.android.synthetic.main.fragment_repository_list.*
import me.drakeet.multitype.MultiTypeAdapter
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.GitLogRepository
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.repository.RepositoryActivityStarter
import me.thanel.gitlog.ui.repositorylist.add.AddRepositoryActivityStarter
import me.thanel.gitlog.ui.utils.observe
import org.koin.android.architecture.ext.sharedViewModel

class RepositoryListFragment : BaseFragment() {
    private val adapter = MultiTypeAdapter().apply {
        register(GitLogRepository::class.java, RepositoryViewBinder(::openRepository))
    }

    private val repositoryViewModel by sharedViewModel<RepositoryListViewModel>()

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

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        repositoryViewModel.listRepositories().observe(this, this::displayRepositories)
    }

    private fun displayRepositories(gitLogRepositories: List<GitLogRepository>?) {
        if (gitLogRepositories != null) {
            loadingProgressBar.hide()
            adapter.items = gitLogRepositories
            adapter.notifyDataSetChanged()
            emptyView.isVisible = gitLogRepositories.isEmpty()
        }
    }

    private fun showAddRepositoryScreen() {
        AddRepositoryActivityStarter.start(requireContext())
    }

    private fun openRepository(gitLogRepository: GitLogRepository) {
        RepositoryActivityStarter.start(requireContext(), gitLogRepository.id)
    }
}
