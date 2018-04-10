package me.thanel.gitlog.ui.repository.filelist

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
}
