package me.thanel.gitlog.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.eclipse.jgit.api.Git
import java.io.File

@Entity(tableName = "repositories")
data class GitLogRepository(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val url: String,
    val path: String
)

val GitLogRepository.git: Git
    get() = Git.open(File(path))
