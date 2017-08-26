package me.thanel.gitlog.repository.branchlist

import android.os.Bundle
import kotlinx.android.synthetic.main.view_recycler.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.repository.log.BranchListAdapter
import me.thanel.gitlog.utils.intArg
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Repository.shortenRefName

class BranchListFragment : BaseFragment<RepositoryViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)

    private val adapter = BranchListAdapter {
        // TODO:
        //   - Set active branch
        //   - Switch to log view
        //   - Make baseActivity private
        //   - Make toolbarSubtitle protected
        baseActivity.toolbarSubtitle = shortenRefName(it.name)
    }

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreateViewModel() = RepositoryViewModel.get(activity)

    override fun observeViewModel(viewModel: RepositoryViewModel) =
        viewModel.getRepository(repositoryId).observe(this) {
            listBranches(it!!)
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = this@BranchListFragment.adapter
    }

    private fun listBranches(repository: Repository) = launch(UI) {
        val branches = run(CommonPool) {
            repository.git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()
        }
        adapter.addAll(branches)
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = BranchListFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}