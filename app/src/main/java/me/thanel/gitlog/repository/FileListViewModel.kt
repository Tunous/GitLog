package me.thanel.gitlog.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.getViewModel

class FileListViewModel(
        application: Application,
        private val repositoryId: Int
) : ViewModel() {
    private val db = (application as GitLogApplication).database

    val repository: LiveData<Repository>
        get() = db.repositoryDao().getRepository(repositoryId)

    companion object {
        fun get(activity: FragmentActivity, repositoryId: Int) = getViewModel(activity) {
            FileListViewModel(activity.application, repositoryId)
        }
    }
}