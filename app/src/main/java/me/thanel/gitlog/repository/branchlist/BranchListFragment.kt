package me.thanel.gitlog.repository.branchlist

import activitystarter.Arg
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.android.synthetic.main.view_recycler.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.RepositoryViewModel
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.repository.log.BranchListAdapter
import me.thanel.gitlog.utils.observe
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Repository.shortenRefName

class BranchListFragment : BaseFragment<RepositoryViewModel>() {
    @get: Arg
    val repositoryId: Int by argExtra()

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

    override fun onCreateViewModel() = RepositoryViewModel.get(requireActivity())

    override fun observeViewModel(viewModel: RepositoryViewModel) =
        viewModel.getRepository(repositoryId).observe(this) {
            listBranches(it!!)
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = this@BranchListFragment.adapter
    }

    private fun listBranches(repository: Repository) = launch(UI) {
        val branches = withContext(CommonPool) {
            repository.git.branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
        }
        adapter.addAll(branches)
    }
}
