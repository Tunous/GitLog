package me.thanel.gitlog

import android.app.Application
import android.arch.persistence.room.Room
import com.chibatching.kotpref.Kotpref
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import me.thanel.gitlog.db.Database

class GitLogApplication : Application() {
    lateinit var database: Database
        private set

    override fun onCreate() {
        super.onCreate()

        database = Room.databaseBuilder(this, Database::class.java, "gitlog.db").build()
        Kotpref.init(this)

        val picasso = Picasso.Builder(this)
            .downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))
            .build()
        Picasso.setSingletonInstance(picasso)
    }
}
