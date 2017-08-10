package me.thanel.gitlog.commit

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.getViewModel
import me.thanel.gitlog.utils.mapBg
import org.eclipse.jgit.diff.DiffEntry
import org.eclipse.jgit.diff.DiffFormatter
import org.eclipse.jgit.revwalk.RevCommit
import java.io.ByteArrayOutputStream

class CommitViewModel(
        application: Application,
        repositoryId: Int,
        private val commitSha: String
) : ViewModel() {
    private val db = (application as GitLogApplication).database
    private val outputStream = ByteArrayOutputStream()
    private val diffFormatter = DiffFormatter(outputStream)

    val repository = db.repositoryDao().getRepository(repositoryId)
    val diffEntries = repository.mapBg(this::loadDiffEntries)
    val commit = repository.mapBg(this::loadCommit)

    override fun onCleared() {
        super.onCleared()

        diffFormatter.release()
    }

    fun formatDiffEntry(diffEntry: DiffEntry): String {
        outputStream.reset()
        diffFormatter.format(diffEntry)
        diffFormatter.flush()
        return outputStream.toString("UTF-8")
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

    companion object {
        fun get(activity: FragmentActivity, repositoryId: Int, commitSha: String) =
                getViewModel(activity) {
                    CommitViewModel(activity.application, repositoryId, commitSha)
                }
    }
}
