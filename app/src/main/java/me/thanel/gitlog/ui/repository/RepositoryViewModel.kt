package me.thanel.gitlog.ui.repository

import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import me.thanel.gitlog.db.RepositoryDao

class RepositoryViewModel(
    private val repositoryDao: RepositoryDao,
    private val repositoryId: Int
) : ViewModel() {
    val repository = repositoryDao.getByIdAsync(repositoryId)

    val gitRepository = Transformations.map(repository) { it.git.repository }

    fun deleteRepositoryAsync() = launch(CommonPool) {
        repositoryDao.deleteById(repositoryId)
    }

    companion object {
        fun createParams(repositoryId: Int) = mapOf(PARAM_REPOSITORY_ID to repositoryId)

        const val PARAM_REPOSITORY_ID = "repositoryId"
    }
}
