package me.thanel.gitlog.ui.filepicker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import kotlinx.android.synthetic.main.view_recycler.*
import me.drakeet.multitype.MultiTypeAdapter
import me.thanel.gitlog.R
import me.thanel.gitlog.ui.base.fragment.BaseFragment
import java.io.File

class FilePickerFragment : BaseFragment() {
    private val fileListAdapter = MultiTypeAdapter().apply {
        register(File::class.java, FileViewBinder(::onFileClicked))
    }

    private fun onFileClicked(file: File) {
        if (file.isDirectory) {
            displayFiles(file)
        } else {
            requireActivity().setResult(Activity.RESULT_OK, Intent().apply {
                putExtra(FilePickerActivity.EXTRA_FILE_PATH, file.absolutePath)
            })
            requireActivity().finish()
        }
    }

    override val layoutResId: Int
        get() = R.layout.view_recycler

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        recyclerView.adapter = fileListAdapter

        displayFiles(Environment.getExternalStorageDirectory())
    }

    private fun displayFiles(file: File) {
        if (!file.isDirectory) return
        fileListAdapter.items = file.listFiles().sortedBy { it.name }
    }

    companion object {
        fun newInstance() = FilePickerFragment()
    }
}
