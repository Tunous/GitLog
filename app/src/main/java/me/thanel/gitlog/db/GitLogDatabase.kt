package me.thanel.gitlog.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import me.thanel.gitlog.db.model.Repository

@Database(
    entities = [
        Repository::class
    ],
    version = 2
)
abstract class GitLogDatabase : RoomDatabase() {
    abstract fun repositoryDao(): RepositoryDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, GitLogDatabase::class.java, "gitlog.db").build()
    }
}
