package me.thanel.gitlog.ui.repository.filelist.search

import me.thanel.gitlog.db.GitLogRepositoryDao
import me.thanel.gitlog.ui.repository.RepositoryViewModel
import me.thanel.gitlog.ui.repository.filelist.GitFile
import me.thanel.gitlog.ui.utils.mapBg
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.IOException

class SearchViewModel(
    gitLogRepositoryDao: GitLogRepositoryDao,
    repositoryId: Int,
    private val refName: String
) : RepositoryViewModel(gitLogRepositoryDao, repositoryId) {

    val files = gitRepository.mapBg { listFiles(it) }

    private fun listFiles(repo: Repository): List<GitFile> {
        val head = repo.resolve(refName)
        return readFiles(repo, head.name)
    }

    private fun readFiles(repository: Repository, commit: String): List<GitFile> {
        val revCommit = buildRevCommit(repository, commit)

        val items = mutableListOf<GitFile>()
        val treeWalk = TreeWalk(repository).apply {
            addTree(revCommit.tree)
            isRecursive = true
        }

        while (treeWalk.next()) {
            items.add(GitFile(treeWalk.pathString, treeWalk.isSubtree, treeWalk.nameString))
        }

        treeWalk.release()

        items.sort()

        return items
    }

    @Throws(IOException::class)
    private fun buildRevCommit(repository: Repository, commit: String): RevCommit {
        val revWalk = RevWalk(repository)
        val revCommit = revWalk.parseCommit(ObjectId.fromString(commit))
        revWalk.release()
        return revCommit
    }

    companion object {
        const val PARAM_REPOSITORY_ID = "repositoryId"
        const val PARAM_REF_NAME = "refName"

        fun createParams(repositoryId: Int, refName: String) = mapOf(
            PARAM_REPOSITORY_ID to repositoryId,
            PARAM_REF_NAME to refName
        )
    }
}
