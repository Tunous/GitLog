package me.thanel.gitlog.ui.repository.filelist

import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.db.model.git
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class GitFileListViewModel(
    private val refName: String
) : ViewModel() {
    private val scrollStateStack = LinkedList<Parcelable>()

    fun pushScrollState(scrollPosition: Parcelable) = scrollStateStack.push(scrollPosition)

    fun popScrollState(): Parcelable? = scrollStateStack.pop()

    fun listFiles(repo: Repository, path: String = ""): List<GitFile> {
        val gitRepo = repo.git.repository
        val head = gitRepo.resolve(refName)
        return readFiles(gitRepo, head.name, path)
    }

    private fun readFiles(
        repository: org.eclipse.jgit.lib.Repository, commit: String,
        path: String
    ): List<GitFile> {
        val revCommit = buildRevCommit(repository, commit)

        val items = mutableListOf<GitFile>()
        val tree = if (path.isNotEmpty()) {
            val walk = TreeWalk.forPath(repository, path, revCommit.tree)
                    ?: throw FileNotFoundException("Did not find expected file '$path'.")
            if (walk.getFileMode(0).bits and FileMode.TYPE_TREE == 0) {
                throw IllegalStateException(
                    "Tried to read the elements of a non-tree for " +
                            "commit '$commit' and path '$path', had filemode " +
                            "${walk.getFileMode(0).bits}"
                )
            }
            val result = walk.getObjectId(0)
            walk.release()
            result
        } else {
            revCommit.tree
        }
        val treeWalk = TreeWalk(repository).apply {
            addTree(tree)
            isRecursive = false
        }

        while (treeWalk.next()) {
            val name = treeWalk.nameString
            val filePath = "$path/$name".trim { it == '/' }
            items.add(GitFile(filePath, treeWalk.isSubtree, name))
        }

        treeWalk.release()

        items.sortWith(Comparator { (leftPath, leftIsDir), (rightPath, rightIsDir) ->
            if (leftIsDir != rightIsDir) {
                return@Comparator rightIsDir.compareTo(leftIsDir)
            }
            return@Comparator leftPath.toUpperCase().compareTo(rightPath.toUpperCase())
        })

        return items
    }

    @Throws(IOException::class)
    private fun buildRevCommit(
        repository: org.eclipse.jgit.lib.Repository,
        commit: String
    ): RevCommit {
        val revWalk = RevWalk(repository)
        val revCommit = revWalk.parseCommit(ObjectId.fromString(commit))
        revWalk.release()
        return revCommit
    }

    companion object {
        const val PARAM_REF_NAME = "refName"

        fun createParams(refName: String) = mapOf(PARAM_REF_NAME to refName)
    }
}
