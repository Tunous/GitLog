package me.thanel.gitlog.ui.repository

import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.db.GitLogRepositoryDao
import me.thanel.gitlog.db.model.git

open class RepositoryViewModel(
    private val gitLogRepositoryDao: GitLogRepositoryDao,
    private val repositoryId: Int
) : ViewModel() {
    val repository = gitLogRepositoryDao.getByIdAsync(repositoryId)

    val git = Transformations.map(repository) { it.git }

    val gitRepository = Transformations.map(git) { it.repository }

    fun deleteRepositoryAsync() = launch(CommonPool) {
        gitLogRepositoryDao.deleteById(repositoryId)
    }

    companion object {
        fun createParams(repositoryId: Int) = mapOf(PARAM_REPOSITORY_ID to repositoryId)

        const val PARAM_REPOSITORY_ID = "repositoryId"
    }
}
