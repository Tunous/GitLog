package me.thanel.gitlog.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import me.thanel.gitlog.db.model.Repository

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun add(repository: Repository)

    @Query("SELECT * FROM repositories")
    fun getAllAsync(): LiveData<List<Repository>>

    @Query("SELECT * FROM repositories WHERE id = :id")
    fun getByIdAsync(id: Int): LiveData<Repository>

    @Query("DELETE FROM repositories WHERE id = :id")
    fun deleteById(id: Int): Int
}
