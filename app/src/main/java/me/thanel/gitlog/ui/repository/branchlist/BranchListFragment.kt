package me.thanel.gitlog.ui.repository.branchlist

import activitystarter.Arg
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.android.synthetic.main.view_recycler.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext
import me.drakeet.multitype.MultiTypeAdapter
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.GitLogRepository
import me.thanel.gitlog.db.model.git
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.utils.observe
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository.shortenRefName
import org.koin.android.architecture.ext.sharedViewModel

class BranchListFragment : BaseFragment() {
    @get:Arg
    val repositoryId: Int by argExtra()

    val viewModel by sharedViewModel<RepositoryViewModel> {
        RepositoryViewModel.createParams(repositoryId)
    }

    private val adapter = MultiTypeAdapter().apply {
        register(Ref::class.java, RefViewBinder(::onRefClicked))
    }

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = this@BranchListFragment.adapter
        viewModel.repository.observe(this) {
            if (it != null) {
                listBranches(it)
            }
        }
    }

    private fun listBranches(gitLogRepository: GitLogRepository) = launch(UI) {
        val branches = withContext(CommonPool) {
            gitLogRepository.git.branchList()
                .setListMode(ListBranchCommand.ListMode.ALL)
                .call()
        }
        adapter.items = branches
        adapter.notifyDataSetChanged()
    }

    private fun onRefClicked(ref: Ref) {
        // TODO:
        //   - Set active branch
        //   - Switch to log view
        //   - Make baseActivity private
        //   - Make toolbarSubtitle protected
        baseActivity.toolbarSubtitle = shortenRefName(ref.name)
    }
}
