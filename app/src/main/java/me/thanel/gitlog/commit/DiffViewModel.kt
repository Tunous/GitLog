package me.thanel.gitlog.commit

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import kotlinx.coroutines.experimental.CommonPool
import kotlinx.coroutines.experimental.launch
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import java.io.ByteArrayOutputStream
import java.io.File

class DiffViewModel(
        private val repositoryPath: String,
        private val commitSha: String
) : ViewModel() {
    private var diffEntriesData: MutableLiveData<List<DiffEntry>>? = null

    fun getDiffEntries(): LiveData<List<DiffEntry>> {
        if (diffEntriesData == null) {
            diffEntriesData = MutableLiveData()
            loadDiffEntries()
        }

        return diffEntriesData!!
    }

    private fun loadDiffEntries() = launch(CommonPool) {
        val file = File(repositoryPath)
        val git = Git.open(file)
        val repo = git.repository
        val outputStream = ByteArrayOutputStream()
        val diffFormatter = DiffFormatter(outputStream)
        diffFormatter.setRepository(repo)
        val diffEntries = diffFormatter.scan(repo.resolve("$commitSha^"), repo.resolve(commitSha))

        diffEntriesData!!.postValue(diffEntries)
    }

    class Factory(
            private val repositoryPath: String,
            private val commitSha: String
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DiffViewModel(repositoryPath, commitSha) as T
        }
    }

}