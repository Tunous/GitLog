package me.thanel.gitlog.repositorylist

import android.content.Context
import me.thanel.gitlog.Repo
import java.io.File

object RepositoryListManager {
    private const val REPOSITORIES_DIRECTORY = "repos"

    fun listRepositories(context: Context): List<Repo> {
        val dirs = getRepositoryDirectories(context)
        val repositories = mutableListOf<Repo>()

        for (dir in dirs) {
            if (dir.list().isEmpty()) {
                dir.deleteRecursively()
            } else {
                repositories.add(Repo(dir.name))
            }
        }

        return repositories
    }

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