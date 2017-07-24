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
    private val outputStream = ByteArrayOutputStream()
    private val diffFormatter = DiffFormatter(outputStream)

    private var diffEntriesData: MutableLiveData<List<DiffEntry>>? = null

    fun getDiffEntries(): LiveData<List<DiffEntry>> {
        if (diffEntriesData == null) {
            diffEntriesData = MutableLiveData()
            loadDiffEntries()
        }

        return diffEntriesData!!
    }

    override fun onCleared() {
        super.onCleared()

        diffFormatter.release()
    }

    fun formatDiffEntry(diffEntry: DiffEntry): String {
        if (diffEntriesData == null) {
            diffEntriesData = MutableLiveData()
            loadDiffEntries()
        }

        outputStream.reset()
        diffFormatter.format(diffEntry)
        diffFormatter.flush()
        return outputStream.toString("UTF-8")
    }

    private fun loadDiffEntries() = launch(CommonPool) {
        val file = File(repositoryPath)
        val git = Git.open(file)
        val repo = git.repository
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