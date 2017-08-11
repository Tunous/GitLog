package me.thanel.gitlog.file

import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.utils.getViewModel

class FileViewModel : ViewModel() {
    companion object {
        fun get(activity: FragmentActivity) = getViewModel<FileViewModel>(activity)
    }
}