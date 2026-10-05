package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AppUser
import com.example.data.model.FamilyMember
import com.example.data.model.HmsEhrConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM app_users WHERE isLoggedIn = 1 LIMIT 1")
    fun getActiveSession(): Flow<AppUser?>

    @Query("SELECT * FROM app_users WHERE identifier = :identifier AND hospitalCode = :hospitalCode LIMIT 1")
    suspend fun findUser(identifier: String, hospitalCode: String): AppUser?

    @Query("SELECT * FROM app_users WHERE identifier = :identifier LIMIT 1")
    suspend fun findUserByIdentifier(identifier: String): AppUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: AppUser): Long

    @Update
    suspend fun updateUser(user: AppUser)

    @Query("UPDATE app_users SET isLoggedIn = 0")
    suspend fun logoutAll()

    @Query("UPDATE app_users SET isLoggedIn = 1 WHERE id = :userId")
    suspend fun setLoggedIn(userId: Long)

    @Query("UPDATE app_users SET role = :newRole WHERE isLoggedIn = 1")
    suspend fun updateActiveUserRole(newRole: String)

    @Query("SELECT COUNT(*) FROM app_users")
    suspend fun getUserCount(): Int
}

@Dao
interface FamilyMemberDao {
    @Query("SELECT * FROM family_members WHERE primaryUserIdentifier = :identifier ORDER BY id ASC")
    fun getFamilyMembers(identifier: String): Flow<List<FamilyMember>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMember(member: FamilyMember): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMembers(members: List<FamilyMember>)

    @Delete
    suspend fun deleteMember(member: FamilyMember)

    @Query("SELECT COUNT(*) FROM family_members")
    suspend fun getCount(): Int
}

@Dao
interface HmsConfigDao {
    @Query("SELECT * FROM hms_ehr_config WHERE hospitalCode = :hospitalCode LIMIT 1")
    fun getConfig(hospitalCode: String): Flow<HmsEhrConfig?>

    @Query("SELECT * FROM hms_ehr_config LIMIT 1")
    fun getDefaultConfig(): Flow<HmsEhrConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfig(config: HmsEhrConfig)

    @Query("UPDATE hms_ehr_config SET lastSyncTimestamp = :timestamp, totalRecordsSynced = totalRecordsSynced + :added, lastSyncStatus = :status WHERE hospitalCode = :hospitalCode")
    suspend fun updateSyncStatus(hospitalCode: String, timestamp: Long, added: Int, status: String)

    @Query("SELECT COUNT(*) FROM hms_ehr_config")
    suspend fun getCount(): Int
}
