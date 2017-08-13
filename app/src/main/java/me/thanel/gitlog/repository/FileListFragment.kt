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
import java.io.File

class FileListFragment : BaseFragment<FileListViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val adapter = FileListAdapter(this::moveDown)
    private var currentFile: File? = null
    private var rootFile: File? = null
    private lateinit var pathBar: PathBar

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
            rootFile = it?.file
            displayFiles(it)
        }
    }

    override fun onBackPressed(): Boolean {
        if (currentFile == null || currentFile == rootFile) return false
        moveUp()
        return true
    }

    private fun displayFiles(it: Repository?) {
        if (it == null) {
            // TODO: Loading
            return
        }
        displayContents(it.file)
    }

    private fun moveDown(file: File) {
        if (!file.isDirectory) {
            val intent = FileViewerActivity.newIntent(context, repositoryId, file.absolutePath)
            startActivity(intent)
            return
        }
        val scrollState = recyclerView.layoutManager.onSaveInstanceState()
        viewModel.pushScrollState(scrollState)
        displayContents(file)
    }

    private fun moveUp() {
        displayContents(currentFile!!.parentFile)
        viewModel.popScrollState()?.let {
            recyclerView.layoutManager.onRestoreInstanceState(it)
        }
    }

    private fun displayContents(file: File) {
        adapter.displayContents(file)
        currentFile = file

        val rootPath = rootFile!!.absolutePath
        val path = file.absolutePath
        val resultPath = path.removePrefix(rootPath)
        pathBar.setPath(resultPath)
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = FileListFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}
