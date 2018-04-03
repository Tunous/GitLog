package me.thanel.gitlog.ui.repositorylist

import android.content.Context
import java.io.File

object RepositoryListManager {
    private const val REPOSITORIES_DIRECTORY = "repos"

    fun exists(context: Context, name: String): Boolean {
        val dirs = getRepositoryDirectories(context)
        return dirs.any { it.name == name }
    }

    private fun getRepositoryDirectories(context: Context): Array<out File> {
        val rootFile = File(context.filesDir, REPOSITORIES_DIRECTORY)
        if (!rootFile.exists()) {
            rootFile.mkdir()
        }

        return rootFile.listFiles { dir, _ ->
            dir.isDirectory
        }
    }
}
