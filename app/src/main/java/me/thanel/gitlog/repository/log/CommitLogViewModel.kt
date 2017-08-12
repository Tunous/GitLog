package me.thanel.gitlog.repository.log

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.getViewModel

class CommitLogViewModel(
        application: Application,
        private val repositoryId: Int
) : AndroidViewModel(application) {
    private val db = (application as GitLogApplication).database

    val repository: LiveData<Repository>
        get() = db.repositoryDao().getRepository(repositoryId)

    companion object {
        fun get(activity: FragmentActivity, repositoryId: Int) = getViewModel(activity) {
            CommitLogViewModel(activity.application, repositoryId)
        }
    }
}