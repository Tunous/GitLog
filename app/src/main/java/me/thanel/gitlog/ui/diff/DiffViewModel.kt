package me.thanel.gitlog.ui.diff

import me.thanel.gitlog.db.GitLogRepositoryDao
import me.thanel.gitlog.ui.commit.DiffEntryManager
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.utils.mapBg
import org.eclipse.jgit.lib.AbbreviatedObjectId

class DiffViewModel(
    gitLogRepositoryDao: GitLogRepositoryDao,
    repositoryId: Int,
    private val commitSha: String,
    private val diffId: AbbreviatedObjectId
) : RepositoryViewModel(gitLogRepositoryDao, repositoryId) {
    private val diffEntryManager = gitRepository.mapBg(::DiffEntryManager)
    val diffEntry = diffEntryManager.mapBg { it.loadDiffEntry(commitSha, diffId) }
    val formattedDiffEntry = diffEntryManager.mapBg { it.loadFormattedDiffEntry(commitSha, diffId) }

    companion object {
        const val PARAM_REPOSITORY_ID = "repositoryId"
        const val PARAM_COMMIT_SHA = "commitSha"
        const val PARAM_DIFF_ID = "diffId"

        fun createParams(repositoryId: Int, commitSha: String, diffId: AbbreviatedObjectId) = mapOf(
            PARAM_REPOSITORY_ID to repositoryId,
            PARAM_COMMIT_SHA to commitSha,
            PARAM_DIFF_ID to diffId
        )
    }
}
