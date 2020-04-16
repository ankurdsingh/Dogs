package com.android.dogs.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao //data access object
interface DogDao {
    /**
     * it will insert multiple variable of type DogBreed
     * suspend will make sure it will access from different thread
     * so that will not get compile error
     * @return List of primary key create for all each row in db
     */
    @Insert
    suspend fun insertAll(vararg  dogBreed: DogBreed): List<Long>

    @Query("SELECT  * FROM dogbreed")
    suspend fun getAllDogs():List<DogBreed>

    @Query("SELECT * FROM dogbreed where uuid= :dogId")
    suspend fun getDog(dogId : Int) : DogBreed

    @Query("DELETE FROM dogbreed")
    suspend fun deleteAllDogs()
}