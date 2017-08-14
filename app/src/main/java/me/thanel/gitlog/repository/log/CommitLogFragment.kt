package me.thanel.gitlog.repository.log

import android.os.Bundle
import android.support.design.widget.BottomSheetBehavior
import android.support.v7.widget.LinearLayoutManager
import android.view.ViewGroup
import kotlinx.android.synthetic.main.view_bottom_sheet_branch_list.*
import kotlinx.android.synthetic.main.view_recycler.*
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.run
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.commit.CommitActivity
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import org.eclipse.jgit.api.ListBranchCommand
import org.eclipse.jgit.lib.Ref
import org.eclipse.jgit.lib.Repository.shortenRefName
import org.eclipse.jgit.revwalk.RevCommit


class CommitLogFragment : BaseFragment<CommitLogViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val commitLogAdapter = CommitLogAdapter(this::openCommit)
    private val branchListAdapter = BranchListAdapter(this::checkout)

    private lateinit var repository: Repository
    private lateinit var branchesBottomSheetBehavior: BottomSheetBehavior<ViewGroup>

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreateViewModel() = CommitLogViewModel.get(activity, repositoryId)

    override fun observeViewModel(viewModel: CommitLogViewModel) {
        viewModel.repository.observe(this, this::onRepositoryLoaded)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.apply {
            adapter = commitLogAdapter
            layoutManager = LinearLayoutManager(context)
        }
//        branchListRecyclerView.apply {
//            adapter = branchListAdapter
//            layoutManager = LinearLayoutManager(context)
//        }
//        branchesBottomSheetBehavior = BottomSheetBehavior.from(branchListBottomSheet)
//        val menu = PopupMenu(context, branchListBottomSheetHeader)
//        with(menu.menu) {
//            add("Local").apply {
//                isCheckable = true
//                isChecked = true
//            }
//            add("Remote").apply {
//                isCheckable = true
//                isChecked = true
//            }
//            add("Tags").apply {
//                isCheckable = true
//            }
//        }
//        branchListBottomSheetHeader.setOnClickListener {
////            toggleBranchListBottomSheet()
//            menu.show()
//        }
//
//        val adapter = ArrayAdapter.createFromResource(context,
//                R.array.branch_filter, android.R.layout.simple_spinner_item)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        branchFilter.adapter = adapter
    }

    private fun toggleBranchListBottomSheet() {
        with(branchesBottomSheetBehavior) {
            if (state == BottomSheetBehavior.STATE_EXPANDED) {
                state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    private fun onRepositoryLoaded(it: Repository?) {
        if (it == null) {
            // TODO: Loading
            return
        }
        repository = it
//        branchListBottomSheetHeader.text = repository.git.repository.branch
        logCommits()
        listBranches()
    }

    private fun openCommit(commit: RevCommit) {
        val intent = CommitActivity.newIntent(context, repository.id, commit.name)
        startActivity(intent)
    }

    private fun logCommits() = launch(UI) {
        val log = run(CommonPool) {
            repository.git.log().call()
        }
        commitLogAdapter.replaceAll(log)
    }

    private fun listBranches() = launch(UI) {
        val branches = run(CommonPool) {
            repository.git.branchList()
                    .setListMode(ListBranchCommand.ListMode.ALL)
                    .call()
        }
        branchListAdapter.addAll(branches)
    }

    private fun checkout(ref: Ref) {
        branchesBottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        launch(UI) {
            val git = repository.git
            run(CommonPool) {
                git.checkout()
                        .setName(shortenRefName(ref.name))
                        .call()
            }
            branchListBottomSheetHeader.text = git.repository.branch
            logCommits()
        }
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = CommitLogFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}
