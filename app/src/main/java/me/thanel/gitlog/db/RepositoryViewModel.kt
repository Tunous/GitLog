package me.thanel.gitlog.db

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.getViewModel

class RepositoryViewModel(application: Application) : AndroidViewModel(application) {
    private val db = (application as GitLogApplication).database

    fun addRepository(repository: Repository) = db.repositoryDao().addRepository(repository)

    fun removeRepository(repository: Repository) = db.repositoryDao().removeRepository(repository)

    fun listRepositories() = db.repositoryDao().listRepositories()

    fun getRepository(id: Int) = db.repositoryDao().getRepository(id)

    companion object {
        fun get(activity: FragmentActivity) = getViewModel<RepositoryViewModel>(activity)
    }
}