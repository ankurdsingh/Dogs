package com.android.dogs.model

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = arrayOf(DogBreed::class),version = 1)//define multiple entities separated by comma
abstract class DogDatabase : RoomDatabase(){
    abstract fun dogDao():DogDao
    //to create static reference
    companion object{
        //volatile to restrict multiple copy of instance at thread level
        // and will give unique db instance
        @Volatile private var instance : DogDatabase? = null
        private val LOCK = Any()
        //will return instance of DogDatabase if not using by any thread and if not null
        //if already using by any thread then it will wait in synchronised lock state
        //if it is null the using builder method will get and an instance
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also{
                instance = it
            }
        }
        // build database instance with application context
        // and will require type of class and db name
        private fun buildDatabase(context: Context)= Room.databaseBuilder(
            context.applicationContext
            ,DogDatabase::class.java,
            "dogdatabase").build()
    }
}