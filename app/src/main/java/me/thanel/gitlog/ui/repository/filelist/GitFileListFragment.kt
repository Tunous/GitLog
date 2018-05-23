package me.thanel.gitlog.ui.repository.filelist

import activitystarter.Arg
import android.os.Bundle
import android.support.v7.widget.PopupMenu
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.marcinmoskala.activitystarter.argExtra
import kotlinx.android.synthetic.main.view_recycler.*
import me.drakeet.multitype.MultiTypeAdapter
import me.thanel.gitlog.R
import me.thanel.gitlog.db.model.GitLogRepository
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.repository.file.GitFileViewerActivityStarter
import me.thanel.gitlog.ui.repository.filelist.search.SearchActivityStarter
import me.thanel.gitlog.ui.utils.copyToClipboard
import me.thanel.gitlog.ui.utils.observe
import me.thanel.gitlog.ui.view.PathBar
import org.koin.android.architecture.ext.sharedViewModel
import org.koin.android.architecture.ext.viewModel

class GitFileListFragment : BaseFragment(saveArgumentsState = true) {
    @get:Arg
    val repositoryId: Int by argExtra()

    @get:Arg
    val refName: String by argExtra()

    @get:Arg(optional = true)
    var currentPath: String by argExtra(default = "")

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
    private lateinit var gitLogRepository: GitLogRepository

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        recyclerView.adapter = adapter

        pathBar = PathBar(requireContext())
        pathBar.setOnPathEntryClickListener {
            displayFiles(it)
        }
        pathBar.setOnPathEntryLongClickListener { view, path ->
            PopupMenu(requireContext(), view).apply {
                inflate(R.menu.path_bar_entry)
                setOnMenuItemClickListener {
                    when (it.itemId) {
                        R.id.copy_path -> {
                            requireContext().copyToClipboard("Path", path)
                        }
                        R.id.copy_name -> {
                            val name = path.split("/").last()
                            requireContext().copyToClipboard("Name", name)
                        }
                    }
                    true
                }
            }.show()

        }
        addHeaderView(pathBar)
        updatePathBar()

        repositoryViewModel.repository.observe(this) {
            gitLogRepository = it!!
            displayFiles()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.git_file_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> SearchActivityStarter.start(requireContext(), repositoryId, refName)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        removeHeaderView(pathBar)
        super.onDestroy()
    }

    override fun onBackPressed(): Boolean {
        if (currentPath.isBlank()) return false
        moveUp()
        return true
    }

    private fun displayFiles(path: String = currentPath) {
        adapter.items = gitFileListViewModel.listFiles(gitLogRepository, path)
        adapter.notifyDataSetChanged()

        currentPath = path
        updatePathBar()
    }

    private fun moveDown(file: GitFile) {
        if (!file.isDirectory) {
            GitFileViewerActivityStarter.start(requireContext(), repositoryId, refName, file.path)
            return
        }

        displayFiles(file.path)

        // Save scroll state
        recyclerView.layoutManager?.onSaveInstanceState()?.let {
            gitFileListViewModel.pushScrollState(it)
        }

        updatePathBar()
    }

    private fun updatePathBar() = pathBar.setPath(currentPath)

    private fun moveUp() {
        displayFiles(
            currentPath.split("/")
                .dropLast(1)
                .joinToString("/")
        )

        // Restore scroll position
        gitFileListViewModel.popScrollState()
            ?.let { recyclerView.layoutManager?.onRestoreInstanceState(it) }
    }
}
