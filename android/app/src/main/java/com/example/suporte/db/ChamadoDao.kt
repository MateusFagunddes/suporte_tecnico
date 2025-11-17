package com.example.suporte.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChamadoDao {
    @Query("SELECT * FROM chamados ORDER BY data_abertura DESC")
    suspend fun getAll(): List<ChamadoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ChamadoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: ChamadoEntity)

    @Query("DELETE FROM chamados")
    suspend fun clear()
}
