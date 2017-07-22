package me.thanel.gitlog.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun addRepository(repository: Repository)

    @Query("SELECT * FROM repositories")
    fun listRepositories(): LiveData<List<Repository>>
}