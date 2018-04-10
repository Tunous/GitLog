package me.thanel.gitlog.ui.commit

import me.thanel.gitlog.db.GitLogRepositoryDao
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.utils.mapBg
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.revwalk.RevCommit

class CommitViewModel(
    gitLogRepositoryDao: GitLogRepositoryDao,
    repositoryId: Int,
    private val commitSha: String
) : RepositoryViewModel(gitLogRepositoryDao, repositoryId) {
    private val diffEntryManager = gitRepository.mapBg(::DiffEntryManager)
    val formattedDiffEntries = diffEntryManager.mapBg { it.loadFormattedDiffEntries(commitSha) }
    val commit = git.mapBg(::loadCommit)

    private fun loadCommit(git: Git): RevCommit {
        return git.log()
            .add(git.repository.resolve(commitSha))
            .setMaxCount(1)
            .call()
            .first()
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
