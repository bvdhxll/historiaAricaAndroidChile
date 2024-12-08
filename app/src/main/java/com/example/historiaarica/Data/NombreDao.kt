package com.example.historiaarica.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NombreDao {
    @Insert
    suspend fun  insertNombre(nombre: NombreEntity)

    @Query("SELECT * FROM nombres ORDER BY id DESC limit 5")
    suspend fun getUltimosNombres() : List<NombreEntity>
}