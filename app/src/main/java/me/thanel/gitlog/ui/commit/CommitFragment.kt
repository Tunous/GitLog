package me.thanel.gitlog.ui.commit

import activitystarter.Arg
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.android.synthetic.main.view_recycler.*
import me.drakeet.multitype.MultiTypeAdapter
import me.thanel.gitlog.R
import me.thanel.gitlog.preferences.Preferences
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.repository.filelist.GitFileListActivityStarter
import me.thanel.gitlog.ui.utils.observe
import org.eclipse.jgit.revwalk.RevCommit
import org.koin.android.architecture.ext.sharedViewModel

class CommitFragment : BaseFragment() {
    @get:Arg
    val commitSha: String by argExtra()

    @get:Arg
    val repositoryId: Int by argExtra()

    private val viewModel by sharedViewModel<CommitViewModel> {
        CommitViewModel.createParams(repositoryId, commitSha)
    }

    private val adapterItems = mutableListOf<Any>()
    private val adapter by lazy {
        MultiTypeAdapter().apply {
            register(
                FormattedDiffEntry::class.java,
                FormattedDiffEntryViewBinder(repositoryId, commitSha)
            )
            register(RevCommit::class.java, RevCommitViewBinder(repositoryId))
            register(DiffSummary::class.java, DiffSummaryViewBinder())
            items = adapterItems
        }
    }

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.adapter = adapter

        viewModel.commit.observe(this, this::displayCommitInformation)
        viewModel.formattedDiffEntries.observe(this, this::displayDiffs)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.commit, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        menu.findItem(R.id.line_numbers).isChecked = Preferences.showLineNumbers
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.browse_files -> {
                GitFileListActivityStarter.start(
                    requireContext(),
                    repositoryId,
                    commitSha
                )
            }
            R.id.line_numbers -> {
                item.isChecked = !item.isChecked
                Preferences.showLineNumbers = item.isChecked
                adapter.notifyDataSetChanged()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun displayCommitInformation(commit: RevCommit?) {
        if (commit == null) {
            // TODO: Loading indicator
            return
        }
        adapterItems.add(0, commit)
    }

    private fun displayDiffs(diffEntries: List<FormattedDiffEntry>?) {
        if (diffEntries == null) {
            // TODO: Loading indicator
            return
        }
        adapterItems.add(DiffSummary(diffEntries))
        adapterItems.addAll(diffEntries)
        adapter.notifyDataSetChanged()
    }
}
