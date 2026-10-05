package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.FiveSCapaItem
import com.example.data.model.FiveSRedTagItem
import kotlinx.coroutines.flow.Flow

@Dao
interface FiveSRedTagDao {
    @Query("SELECT * FROM five_s_red_tags ORDER BY id DESC")
    fun getAllRedTags(): Flow<List<FiveSRedTagItem>>

    @Query("SELECT * FROM five_s_red_tags WHERE status != 'Resolved / Removed' ORDER BY id DESC")
    fun getActiveRedTags(): Flow<List<FiveSRedTagItem>>

    @Query("SELECT * FROM five_s_red_tags WHERE department = :dept ORDER BY id DESC")
    fun getRedTagsByDepartment(dept: String): Flow<List<FiveSRedTagItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedTag(item: FiveSRedTagItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRedTags(items: List<FiveSRedTagItem>)

    @Update
    suspend fun updateRedTag(item: FiveSRedTagItem)

    @Delete
    suspend fun deleteRedTag(item: FiveSRedTagItem)

    @Query("SELECT COUNT(*) FROM five_s_red_tags")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM five_s_red_tags WHERE status != 'Resolved / Removed'")
    suspend fun getActiveCount(): Int
}

@Dao
interface FiveSCapaDao {
    @Query("SELECT * FROM five_s_capas ORDER BY id DESC")
    fun getAllCapas(): Flow<List<FiveSCapaItem>>

    @Query("SELECT * FROM five_s_capas WHERE status != 'Closed' ORDER BY id DESC")
    fun getOpenCapas(): Flow<List<FiveSCapaItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapa(item: FiveSCapaItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCapas(items: List<FiveSCapaItem>)

    @Update
    suspend fun updateCapa(item: FiveSCapaItem)

    @Delete
    suspend fun deleteCapa(item: FiveSCapaItem)

    @Query("SELECT COUNT(*) FROM five_s_capas")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM five_s_capas WHERE status != 'Closed'")
    suspend fun getOpenCount(): Int
}
