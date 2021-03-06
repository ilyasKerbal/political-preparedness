package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    @Insert
    suspend fun insert(election: Election)

    @Query("SELECT * FROM election_table ORDER BY id DESC")
    fun getAllElections(): LiveData<List<Election>>

    @Query("SELECT * from election_table WHERE id = :id")
    suspend fun get(id: Int): Election?

    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteByElectionId(id: Int)

    @Query("DELETE FROM election_table")
    suspend fun clear()

    @Query("SELECT EXISTS(SELECT * from election_table WHERE id = :id)")
    fun exists(id: Int): LiveData<Boolean>
}