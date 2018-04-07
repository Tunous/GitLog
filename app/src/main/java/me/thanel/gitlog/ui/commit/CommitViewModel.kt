package me.thanel.gitlog.ui.commit

import android.arch.lifecycle.ViewModel
import me.thanel.gitlog.db.RepositoryDao
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.ui.utils.mapBg
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.lib.AbbreviatedObjectId
import org.eclipse.jgit.revwalk.RevCommit
import java.io.ByteArrayOutputStream

class CommitViewModel(
    repositoryDao: RepositoryDao,
    val repositoryId: Int,
    val commitSha: String
) : ViewModel() {
    private val outputStream = ByteArrayOutputStream()
    private val diffFormatter = DiffFormatter(outputStream)

    val repository = repositoryDao.getByIdAsync(repositoryId)
    val diffEntries = repository.mapBg { loadDiffEntries(it) }
    val commit = repository.mapBg { loadCommit(it) }

    fun formatDiffEntry(diffEntry: DiffEntry): String {
        outputStream.reset()
        diffFormatter.format(diffEntry)
        diffFormatter.flush()
        return outputStream.toString("UTF-8")
    }

    fun getDiffEntry(newId: AbbreviatedObjectId) = diffEntries.mapBg { diffEntries ->
        diffEntries.find { it.newId == newId }
    }

    private fun loadDiffEntries(repository: Repository): List<DiffEntry> {
        val git = repository.git
        val gitRepository = git.repository
        diffFormatter.setRepository(gitRepository)

        val previousCommit = gitRepository.resolve("$commitSha^")
        val currentCommit = gitRepository.resolve(commitSha)
        return diffFormatter.scan(previousCommit, currentCommit)
    }

    private fun loadCommit(repository: Repository): RevCommit {
        val git = repository.git
        val gitRepository = git.repository
        return git.log()
            .add(gitRepository.resolve(commitSha))
            .setMaxCount(1)
            .call()
            .first()
    }

    override fun onCleared() {
        super.onCleared()

        diffFormatter.release()
    }

    companion object {
        const val PARAM_REPOSITORY_ID = "repositoryId"
        const val PARAM_COMMIT_SHA = "commitSha"

        fun createParams(repositoryId: Int, commitSha: String) = mapOf(
            PARAM_REPOSITORY_ID to repositoryId,
            PARAM_COMMIT_SHA to commitSha
        )
    }
}
