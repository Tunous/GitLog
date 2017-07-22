package me.thanel.gitlog.db

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import me.thanel.gitlog.GitLogApplication

class RepositoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as GitLogApplication).database

    fun listRepositories(): LiveData<List<Repository>> {
        return db.repositoryDao().listRepositories()
    }

    fun addRepository(repository: Repository) {
        db.repositoryDao().addRepository(repository)
    }

}