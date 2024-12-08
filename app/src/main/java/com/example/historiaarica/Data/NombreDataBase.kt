package com.example.historiaarica.Data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [NombreEntity::class], version = 1, exportSchema = false)
abstract class NombreDataBase : RoomDatabase(){
    abstract fun nombreDao(): NombreDao

    companion object{
        @Volatile
        private var INSTANCE: NombreDataBase? = null

        fun getDatabase(context: Context): NombreDataBase{
            return INSTANCE ?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context,
                    NombreDataBase::class.java,
                    "nombres"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}