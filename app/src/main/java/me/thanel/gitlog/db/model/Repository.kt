package me.thanel.gitlog.db.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.eclipse.jgit.api.Git
import java.io.File

@Entity(tableName = "repositories")
data class Repository(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val name: String,
    val url: String,
    val path: String
)

val Repository.git: Git
    get() = Git.open(File(path))
