package me.thanel.gitlog.ui.repository.filelist.search

import activitystarter.Arg
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.repository.file.GitFileViewerActivityStarter
import me.thanel.gitlog.ui.repository.filelist.GitFile
import me.thanel.gitlog.ui.utils.observe
import org.koin.android.architecture.ext.viewModel

class SearchFragment : BaseFragment(), SearchActivity.SearchListener {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    private val searchViewModel by viewModel<SearchViewModel> {
        SearchViewModel.createParams(repositoryId, refName)
    }

    private val newAdapter = GitFileAdapter(::openFile)

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = newAdapter
        searchViewModel.files.observe(this) {
            newAdapter.submitList(it)
        }
    }

    override fun onTextChanged(text: CharSequence?) {
        newAdapter.filterItems(text)
    }

    private fun openFile(file: GitFile) {
        require(!file.isDirectory)
        GitFileViewerActivityStarter.start(requireContext(), repositoryId, refName, file.path)
    }
}
