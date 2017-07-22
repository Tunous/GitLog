package me.thanel.gitlog.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

@Database(entities = arrayOf(Repository::class), version = 1)
abstract class Database : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao
}