package me.thanel.gitlog.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import me.thanel.gitlog.db.model.GitLogRepository

@Dao
interface GitLogRepositoryDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun add(gitLogRepository: GitLogRepository)

    @Query("SELECT * FROM repositories")
    fun getAllAsync(): LiveData<List<GitLogRepository>>

    @Query("SELECT * FROM repositories WHERE id = :id")
    fun getByIdAsync(id: Int): LiveData<GitLogRepository>

    @Query("DELETE FROM repositories WHERE id = :id")
    fun deleteById(id: Int): Int
}
