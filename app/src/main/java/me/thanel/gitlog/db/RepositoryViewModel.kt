package me.thanel.gitlog.db

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.arch.persistence.room.Room
import android.content.Context

class RepositoryViewModel : ViewModel() {

    private lateinit var db: Database
    private var isInitialized = false

    fun init(application: Context) {
        if (isInitialized) return
        db = Room.databaseBuilder(application, Database::class.java, "gitlog.db").build()
        isInitialized = true
    }

    fun listRepositories(): LiveData<List<Repository>> {
        return db.repositoryDao().listRepositories()
    }

    fun addRepository(repository: Repository) {
        db.repositoryDao().addRepository(repository)
    }

}