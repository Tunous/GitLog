package me.thanel.gitlog.repository

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_file_list.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.file.FileActivity
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import java.io.File

class FileListFragment : BaseFragment<FileListViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val adapter = FileListAdapter(this::moveDown)
    private var currentFile: File? = null
    private var rootFile: File? = null

    override val layoutResId: Int
        get() = R.layout.fragment_file_list

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fileListRecycler.adapter = adapter
        fileListRecycler.layoutManager = LinearLayoutManager(context)
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
            val intent = FileActivity.newIntent(context, file.absolutePath)
            startActivity(intent)
            return
        }
        val scrollState = fileListRecycler.layoutManager.onSaveInstanceState()
        viewModel.pushScrollState(scrollState)
        displayContents(file)
    }

    private fun moveUp() {
        displayContents(currentFile!!.parentFile)
        viewModel.popScrollState()?.let {
            fileListRecycler.layoutManager.onRestoreInstanceState(it)
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