package me.thanel.gitlog.ui.repositorylist

import android.arch.lifecycle.ViewModel
import me.thanel.gitlog.db.GitLogRepositoryDao
import me.thanel.gitlog.db.model.GitLogRepository

class RepositoryListViewModel(private val gitLogRepositoryDao: GitLogRepositoryDao) : ViewModel() {
    fun addRepository(gitLogRepository: GitLogRepository) =
        gitLogRepositoryDao.add(gitLogRepository)

    fun listRepositories() = gitLogRepositoryDao.getAllAsync()
}
