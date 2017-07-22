package me.thanel.gitlog

import android.app.Application
import android.arch.persistence.room.Room
import me.thanel.gitlog.db.Database

class GitLogApplication : Application() {

    lateinit var database: Database
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(this, Database::class.java, "gitlog.db").build()
    }

}
