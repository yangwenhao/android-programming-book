package com.yangwenhao.criminalintent.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.yangwenhao.criminalintent.Crime
import java.util.*

@Dao
interface CrimeDao {

    @Query("select * from crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("select * from crime where id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Update
    fun updateCrime(crime: Crime)

    @Insert
    fun addCrime(crime: Crime)
}