package me.thanel.gitlog.repository

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import kotlinx.android.synthetic.main.fragment_file_list.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.observe
import me.thanel.gitlog.utils.withArguments
import java.io.File

class FileListFragment : BaseFragment<FileListViewModel>() {
    private val repositoryId by intArg(ARG_REPOSITORY_ID)
    private val adapter = FileListAdapter(this::displayContents)

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
            displayFiles(it)
        }
    }

    private fun displayFiles(it: Repository?) {
        if (it == null) {
            // TODO: Loading
            return
        }

        displayContents(it.file)
    }

    private fun displayContents(file: File) {
        if (!file.isDirectory) {
            Toast.makeText(context, "This file is not a directory", Toast.LENGTH_SHORT).show()
            return
        }
        adapter.displayContents(file)
    }

    companion object {
        private const val ARG_REPOSITORY_ID = "arg.repository_id"

        fun newInstance(repositoryId: Int) = FileListFragment().withArguments {
            putInt(ARG_REPOSITORY_ID, repositoryId)
        }
    }
}
