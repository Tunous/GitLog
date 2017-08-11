package me.thanel.gitlog.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.getViewModel
import java.util.*

class FileListViewModel(
        application: Application,
        private val repositoryId: Int
) : ViewModel() {
    private val db = (application as GitLogApplication).database
    private val scrollStateStack = LinkedList<Parcelable>()

    val repository: LiveData<Repository>
        get() = db.repositoryDao().getRepository(repositoryId)

    fun pushScrollState(scrollPosition: Parcelable) = scrollStateStack.push(scrollPosition)

    fun popScrollState(): Parcelable? = scrollStateStack.pop()

    companion object {
        fun get(activity: FragmentActivity, repositoryId: Int) = getViewModel(activity) {
            FileListViewModel(activity.application, repositoryId)
        }
    }
}