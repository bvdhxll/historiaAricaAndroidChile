package com.example.historiaarica.Data

import com.example.historiaarica.Data.NombreDataBase
import com.example.historiaarica.Data.NombreEntity

class NombreRepository(private val database: NombreDataBase){
    suspend fun insertName(name: NombreEntity){
        database.nombreDao().insertNombre(name)
    }

    suspend fun getUltimosNombres(): List<NombreEntity> {
        return database.nombreDao().getUltimosNombres()
    }
}