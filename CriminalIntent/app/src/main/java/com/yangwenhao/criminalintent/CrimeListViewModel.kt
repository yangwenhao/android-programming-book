package com.yangwenhao.criminalintent

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {
//    val crimes = mutableListOf<Crime>()
//
//    init {
//        for (i in 0 until 100) {
//            val crime = Crime()
//            crime.title = "Crime #$i"
//            crime.isSolved = i % 2 == 0
//            crime.requiresPolice = false
//            crimes += crime
//        }
//    }

    private val crimeRepository = CrimeRepository.get()
    val crimes = crimeRepository.getCrimes()
}