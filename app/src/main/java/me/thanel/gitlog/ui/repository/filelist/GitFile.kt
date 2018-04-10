package me.thanel.gitlog.ui.repository.filelist

import android.support.v7.util.DiffUtil

data class GitFile(
    val path: String,
    val isDirectory: Boolean,
    val name: String
) : Comparable<GitFile> {
    override fun compareTo(other: GitFile): Int {
        if (isDirectory != other.isDirectory) {
            return other.isDirectory.compareTo(isDirectory)
        }
        return path.compareTo(other.path, ignoreCase = true)
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<GitFile>() {
            override fun areItemsTheSame(oldItem: GitFile, newItem: GitFile) =
                oldItem.path == newItem.path

            override fun areContentsTheSame(oldItem: GitFile, newItem: GitFile) = oldItem == newItem
        }
    }
}
