package com.example.suporte.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import android.content.Context

@Database(entities = [ChamadoEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chamadoDao(): ChamadoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE chamados ADD COLUMN email_usuario TEXT")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "suporte.db")
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}
