package com.yangwenhao.criminalintent.database

import androidx.room.Dao
import androidx.room.Query
import com.yangwenhao.criminalintent.Crime
import java.util.*

@Dao
interface CrimeDao {

    @Query("select * from crime")
    fun getCrimes(): List<Crime>

    @Query("select * from crime where id=(:id)")
    fun getCrime(id: UUID): Crime?
}