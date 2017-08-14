package me.thanel.gitlog.repository

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.os.Parcelable
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.db.model.Repository
import me.thanel.gitlog.utils.getViewModel
import org.eclipse.jgit.lib.FileMode
import org.eclipse.jgit.lib.ObjectId
import org.eclipse.jgit.revwalk.RevCommit
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*

class FileListViewModel(
        application: Application,
        private val repositoryId: Int,
        private val refName: String
) : ViewModel() {
    private val db = (application as GitLogApplication).database
    private val scrollStateStack = LinkedList<Parcelable>()

    val repository: LiveData<Repository>
        get() = db.repositoryDao().getRepository(repositoryId)

    fun pushScrollState(scrollPosition: Parcelable) = scrollStateStack.push(scrollPosition)

    fun popScrollState(): Parcelable? = scrollStateStack.pop()

    fun listFiles(repo: Repository, path: String = ""): List<File> {
        val gitRepo = repo.git.repository
        val head = gitRepo.resolve(refName)
        return readFiles(gitRepo, head.name, path)
    }

    private fun readFiles(repository: org.eclipse.jgit.lib.Repository, commit: String,
            path: String): List<File> {
        val revCommit = buildRevCommit(repository, commit)

        val items = mutableListOf<File>()
        val tree = if (path.isNotEmpty()) {
            val walk = TreeWalk.forPath(repository, path, revCommit.tree) ?:
                    throw FileNotFoundException("Did not find expected file '$path'.")
            if (walk.getFileMode(0).bits and FileMode.TYPE_TREE == 0) {
                throw IllegalStateException("Tried to read the elements of a non-tree for " +
                        "commit '$commit' and path '$path', had filemode " +
                        "${walk.getFileMode(0).bits}")
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
            items.add(File(filePath, treeWalk.isSubtree, name))
        }

        treeWalk.release()

        items.sortWith(Comparator { left, right ->
            if (left.isDirectory != right.isDirectory) {
                return@Comparator right.isDirectory.compareTo(left.isDirectory)
            }
            return@Comparator left.path.toUpperCase().compareTo(right.path.toUpperCase())
        })

        return items
    }

    @Throws(IOException::class)
    private fun buildRevCommit(repository: org.eclipse.jgit.lib.Repository,
            commit: String): RevCommit {
        val revWalk = RevWalk(repository)
        val revCommit = revWalk.parseCommit(ObjectId.fromString(commit))
        revWalk.release()
        return revCommit
    }

    companion object {
        fun get(activity: FragmentActivity, repositoryId: Int, refName: String)
                = getViewModel(activity) {
            FileListViewModel(activity.application, repositoryId, refName)
        }
    }
}