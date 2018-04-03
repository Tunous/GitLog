package me.thanel.gitlog.ui.explorer

import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.ui.utils.getViewModel

class FileViewModel : ViewModel() {
    companion object {
        fun get(activity: FragmentActivity) = getViewModel<FileViewModel>(activity)
    }
}
