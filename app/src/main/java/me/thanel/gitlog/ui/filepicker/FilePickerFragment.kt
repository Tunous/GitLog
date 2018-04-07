package me.thanel.gitlog.ui.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import kotlinx.android.synthetic.main.view_recycler.*
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import java.io.File

class FilePickerFragment : BaseFragment() {
    private lateinit var fileListAdapter: FileListAdapter

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        fileListAdapter = FileListAdapter {
            if (it.isDirectory) {
                displayFiles(it)
            } else {
                requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                    putExtra(FilePickerActivity.EXTRA_FILE_PATH, it.absolutePath)
                })
                requireActivity().finish()
            }
        }

        recyclerView.adapter = fileListAdapter

        displayFiles(Environment.getExternalStorageDirectory())
    }

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
