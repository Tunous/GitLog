package me.thanel.gitlog.repository

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.file.FileViewerActivity
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import me.thanel.gitlog.view.PathBar

class FileListFragment : BaseFragment<FileListViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val adapter = FileListAdapter(this::moveDown)
    private lateinit var pathBar: PathBar
    private lateinit var repository: Repository

    private val pathSegments = mutableListOf<String>()

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        pathBar = PathBar(context)
        addHeaderView(pathBar)
    }

    override fun onCreateViewModel() = FileListViewModel.get(activity, repositoryId)

    override fun observeViewModel(viewModel: FileListViewModel) {
        viewModel.repository.observe(this) {
            repository = it!!
            displayFiles()
        }
    }

    override fun onBackPressed(): Boolean {
        if (pathSegments.isEmpty()) return false
        moveUp()
        return true
    }

    private fun displayFiles(path: String = "") {
        val files = viewModel.listFiles(repository, path)
        adapter.replaceAll(files)
    }

    private fun moveDown(file: File) {
        if (!file.isDirectory) {
            val intent = FileViewerActivity.newIntent(context, repositoryId, file.path)
            startActivity(intent)
            return
        }

        displayFiles(file.path)

        // Save scroll state
        val scrollState = recyclerView.layoutManager.onSaveInstanceState()
        viewModel.pushScrollState(scrollState)

        pathSegments.add(file.name)
    }

    private fun moveUp() {
        pathSegments.removeAt(pathSegments.size - 1)

        displayFiles(pathSegments.joinToString("/"))

        // Restore scroll position
        viewModel.popScrollState()?.let {
            recyclerView.layoutManager.onRestoreInstanceState(it)
        }
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = FileListFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}
