package me.thanel.gitlog.repository.filelist

import android.os.Bundle
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.repository.file.GitFileViewerActivity
import me.thanel.gitlog.utils.intArg
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.stringArg
import me.thanel.gitlog.utils.withArguments
import me.thanel.gitlog.view.PathBar

class GitFileListFragment : BaseFragment<GitFileListViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val refName by stringArg(ARG_REF_NAME)
    private val adapter = GitFileListAdapter(this::moveDown)
    private lateinit var pathBar: PathBar
    private lateinit var repository: Repository

    private val currentPath = mutableListOf<String>()

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = adapter

        pathBar = PathBar(context)
        pathBar.onPathEntryClicked {
            displayFiles(it)
            currentPath.clear()
            if (it.isNotEmpty()) {
                currentPath.addAll(it.split("/"))
            }
            updatePathBar()
        }
        addHeaderView(pathBar)

        if (savedInstanceState != null) {
            val path = savedInstanceState.getString(STATE_CURRENT_PATH)
            currentPath.addAll(path.split("/"))
        }

        updatePathBar()
    }

    override fun onDestroy() {
        removeHeaderView(pathBar)
        super.onDestroy()
    }

    override fun onCreateViewModel() = GitFileListViewModel.get(activity, repositoryId, refName)

    override fun observeViewModel(viewModel: GitFileListViewModel) =
        viewModel.repository.observe(this) {
            repository = it!!
            displayFiles()
        }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(STATE_CURRENT_PATH, currentPath.joinToString("/"))
    }

    override fun onBackPressed(): Boolean {
        if (currentPath.isEmpty()) return false
        moveUp()
        return true
    }

    private fun displayFiles(path: String = "") {
        val files = viewModel.listFiles(repository, path)
        adapter.replaceAll(files)
    }

    private fun moveDown(file: GitFile) {
        if (!file.isDirectory) {
            val intent = GitFileViewerActivity.newIntent(context, repositoryId, refName, file.path)
            startActivity(intent)
            return
        }

        displayFiles(file.path)

        // Save scroll state
        val scrollState = recyclerView.layoutManager.onSaveInstanceState()
        viewModel.pushScrollState(scrollState)

        currentPath.add(file.name)
        updatePathBar()
    }

    private fun updatePathBar() = pathBar.setPath(currentPath)

    private fun moveUp() {
        currentPath.removeAt(currentPath.size - 1)

        displayFiles(currentPath.joinToString("/"))

        // Restore scroll position
        viewModel.popScrollState()?.let(recyclerView.layoutManager::onRestoreInstanceState)
        updatePathBar()
    }

    companion object {
        private const val STATE_CURRENT_PATH = "state.current_path"
        private const val ARG_REPOSITORY_ID = "arg.repository_id"
        private const val ARG_REF_NAME = "arg.ref_name"

        fun newInstance(repositoryId: Int, refName: String) = GitFileListFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
            putString(ARG_REF_NAME, refName)
        }
    }
}
