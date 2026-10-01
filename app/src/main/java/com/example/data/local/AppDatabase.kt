package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.FiveSAudit
import com.example.data.model.KaizenProject
import com.example.data.model.LeanWasteLog
import com.example.data.model.PatientRecord

@Database(
    entities = [
        PatientRecord::class,
        LeanWasteLog::class,
        FiveSAudit::class,
        KaizenProject::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun patientDao(): PatientDao
    abstract fun leanWasteDao(): LeanWasteDao
    abstract fun fiveSAuditDao(): FiveSAuditDao
    abstract fun kaizenDao(): KaizenDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "lean_hospital_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
