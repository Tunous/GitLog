package me.thanel.gitlog.file

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.support.v4.app.FragmentActivity
import me.thanel.gitlog.GitLogApplication
import me.thanel.gitlog.utils.getViewModel
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.Repository
import org.eclipse.jgit.revwalk.RevWalk
import org.eclipse.jgit.treewalk.TreeWalk
import java.io.ByteArrayOutputStream

class FileViewModel(
        application: Application,
        repositoryId: Int,
        private val filePath: String
) : ViewModel() {
    private val db = (application as GitLogApplication).database

    val repository = db.repositoryDao().getRepository(repositoryId)

    fun readFileContent(repository: Repository): String {
        val head = repository.resolve(Constants.HEAD)

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
        fun get(activity: FragmentActivity, repositoryId: Int, filePath: String)
                = getViewModel(activity) {
            FileViewModel(activity.application, repositoryId, filePath)
        }
    }
}