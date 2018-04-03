package me.thanel.gitlog.db

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import me.thanel.gitlog.db.model.Repository

@Dao
interface RepositoryDao {
    @Insert(onConflict = OnConflictStrategy.FAIL)
    fun addRepository(repository: Repository)

    @Delete
    fun removeRepository(repository: Repository)

    @Query("SELECT * FROM repositories")
    fun listRepositories(): LiveData<List<Repository>>

    @Query("SELECT * FROM repositories WHERE id = :id")
    fun getRepository(id: Int): LiveData<Repository>
}
