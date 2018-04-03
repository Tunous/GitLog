package me.thanel.gitlog.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import me.thanel.gitlog.db.model.Repository

@Database(
    entities = [
        Repository::class
    ],
    version = 2
)
abstract class Database : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
}
