package me.thanel.gitlog

import android.app.Application
import com.chibatching.kotpref.Kotpref
import com.jakewharton.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import me.thanel.gitlog.di.repositoryModule
import org.koin.android.ext.android.startKoin

class GitLogApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(repositoryModule))
        Kotpref.init(this)

        val picasso = Picasso.Builder(this)
            .downloader(OkHttp3Downloader(this, Integer.MAX_VALUE.toLong()))
            .build()
        Picasso.setSingletonInstance(picasso)
    }
}
