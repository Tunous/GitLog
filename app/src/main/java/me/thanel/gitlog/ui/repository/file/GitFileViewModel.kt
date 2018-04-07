package me.thanel.gitlog.ui.repository.file

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.arch.lifecycle.ViewModel
import me.thanel.gitlog.db.RepositoryDao
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.ByteArrayOutputStream

class GitFileViewModel(
    repositoryDao: RepositoryDao,
    repositoryId: Int,
    private val refName: String,
    private val filePath: String
) : ViewModel() {
    val repository = repositoryDao.getByIdAsync(repositoryId)

    val fileContent: LiveData<String> = Transformations.map(repository) {
        readFileContent(it.git.repository)
    }

    private fun readFileContent(repository: Repository): String {
        val head = repository.resolve(refName)

        val revWalk = RevWalk(repository)
        val commit = revWalk.parseCommit(head)
        val tree = commit.tree

        val treeWalk = TreeWalk.forPath(repository, filePath, tree)
        val objectId = treeWalk.getObjectId(0)
        val loader = repository.open(objectId)

        val stream = ByteArrayOutputStream()
        loader.copyTo(stream)
        val result = stream.toString()
        stream.close()
        treeWalk.release()

        revWalk.release()

        return result
    }

    companion object {
        const val PARAM_REPOSITORY_ID = "repositoryId"
        const val PARAM_REF_NAME = "refName"
        const val PARAM_FILE_PATH = "filePath"

        fun createParams(repositoryId: Int, refName: String, filePath: String) = mapOf(
            PARAM_REPOSITORY_ID to repositoryId,
            PARAM_REF_NAME to refName,
            PARAM_FILE_PATH to filePath
        )
    }
}
