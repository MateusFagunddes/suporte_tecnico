package com.example.suporte.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [ChamadoEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chamadoDao(): ChamadoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "suporte.db").build()
                INSTANCE = inst
                inst
            }
        }
    }
}
