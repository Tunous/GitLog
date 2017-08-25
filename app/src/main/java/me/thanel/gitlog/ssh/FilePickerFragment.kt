package me.thanel.gitlog.ssh

import android.app.Activity
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.base.BaseFragment
import me.thanel.gitlog.utils.getViewModel
import java.io.File

class FilePickerFragment : BaseFragment<FilePickerViewModel>() {
    private lateinit var fileListAdapter: FileListAdapter

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fileListAdapter = FileListAdapter {
            if (it.isDirectory) {
                displayFiles(it)
            } else {
                activity.setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(FilePickerActivity.EXTRA_FILE_PATH, it.absolutePath)
                })
                activity.finish()
            }
        }

        recyclerView.apply {
            adapter = fileListAdapter
            layoutManager = LinearLayoutManager(context)
        }

        displayFiles(Environment.getExternalStorageDirectory())
    }

    override fun onCreateViewModel() = getViewModel<FilePickerViewModel>(activity)

    private fun displayFiles(file: File) {
        if (!file.isDirectory) return

        val childFiles = file.listFiles()
                .sortedBy { it.name }
        fileListAdapter.replaceAll(childFiles)
    }

    companion object {
        fun newInstance() = FilePickerFragment()
    }
}

class FilePickerViewModel : ViewModel()
