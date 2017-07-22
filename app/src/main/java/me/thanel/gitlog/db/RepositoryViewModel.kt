package me.thanel.gitlog.db

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import me.thanel.gitlog.GitLogApplication

class RepositoryViewModel(application: Application) : AndroidViewModel(application) {

    private val db = (application as GitLogApplication).database

    fun addRepository(repository: Repository) = db.repositoryDao().addRepository(repository)

    fun removeRepository(repository: Repository) = db.repositoryDao().removeRepository(repository)

    fun listRepositories() = db.repositoryDao().listRepositories()

}