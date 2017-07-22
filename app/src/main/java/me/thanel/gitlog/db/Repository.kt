package me.thanel.gitlog.db

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "repositories")
data class Repository(
        @PrimaryKey(autoGenerate = true) val id: Int,
        val name: String,
        val url: String
)
