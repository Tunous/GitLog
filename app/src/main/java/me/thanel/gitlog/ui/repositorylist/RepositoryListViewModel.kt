package me.thanel.gitlog.ui.repositorylist

import android.arch.lifecycle.ViewModel
import me.thanel.gitlog.db.RepositoryDao
import me.thanel.gitlog.db.model.Repository

class RepositoryListViewModel(private val repositoryDao: RepositoryDao) : ViewModel() {
    fun addRepository(repository: Repository) = repositoryDao.add(repository)

    fun listRepositories() = repositoryDao.getAllAsync()
}
