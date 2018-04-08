package me.thanel.gitlog.ui.repository.filelist

import activitystarter.Arg
import android.os.Bundle
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.android.synthetic.main.view_recycler.*
import me.drakeet.multitype.MultiTypeAdapter
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.repository.file.GitFileViewerActivityStarter
import me.thanel.gitlog.ui.utils.observe
import me.thanel.gitlog.ui.view.PathBar
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.architecture.ext.viewModel

class GitFileListFragment : BaseFragment() {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    @get:Arg(optional = true)
    val initialPath: String by argExtra(default = "")

    private val gitFileListViewModel by viewModel<GitFileListViewModel> {
        GitFileListViewModel.createParams(refName)
    }
    private val repositoryViewModel by sharedViewModel<RepositoryViewModel> {
        RepositoryViewModel.createParams(repositoryId)
    }

    private val adapter = MultiTypeAdapter().apply {
        register(GitFile::class.java, GitFileViewBinder(::moveDown))
    }
    private lateinit var pathBar: PathBar
    private lateinit var repository: Repository

    private val currentPath = mutableListOf<String>()

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = adapter

        pathBar = PathBar(requireContext())
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
        } else {
            currentPath.addAll(initialPath.split("/"))
        }

        updatePathBar()

        repositoryViewModel.repository.observe(this) {
            repository = it!!
            displayFiles()
        }
    }

    override fun onDestroy() {
        removeHeaderView(pathBar)
        super.onDestroy()
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

    private fun displayFiles(path: String = initialPath) {
        adapter.items = gitFileListViewModel.listFiles(repository, path)
        adapter.notifyDataSetChanged()
    }

    private fun moveDown(file: GitFile) {
        if (!file.isDirectory) {
            GitFileViewerActivityStarter.start(
                requireContext(),
                repositoryId,
                refName,
                file.path
            )
            return
        }

        displayFiles(file.path)

        // Save scroll state
        val scrollState = recyclerView.layoutManager.onSaveInstanceState()
        gitFileListViewModel.pushScrollState(scrollState)

        currentPath.add(file.name)
        updatePathBar()
    }

    private fun updatePathBar() = pathBar.setPath(currentPath)

    private fun moveUp() {
        currentPath.removeAt(currentPath.size - 1)

        displayFiles(currentPath.joinToString("/"))

        // Restore scroll position
        gitFileListViewModel.popScrollState()
            ?.let(recyclerView.layoutManager::onRestoreInstanceState)
        updatePathBar()
    }

    companion object {
        private const val STATE_CURRENT_PATH = "state.current_path"
    }
}
