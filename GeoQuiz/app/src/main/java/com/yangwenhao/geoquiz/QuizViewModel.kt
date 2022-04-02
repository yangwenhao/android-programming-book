package com.yangwenhao.geoquiz

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {

    var currentIndex = 0
    var cheaterSet = hashSetOf<Int>()

    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    var visStatus = intArrayOf(
        View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE
    )
    var score = 0
    var answered = 0

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer
    val currentQuuestionText: Int
        get() = questionBank[currentIndex].textResId
    val questionCount = questionBank.size

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        currentIndex = (currentIndex + (questionBank.size - 1)) % questionBank.size
    }

    fun addScore() {
        score++
    }

    fun answer() {
        answered++
        visStatus[currentIndex] = View.INVISIBLE
    }

    fun getButVis(): Int {
        return visStatus[currentIndex]
    }

    fun addCheater(index: Int) {
        cheaterSet.add(index)
    }

    fun isCheater(index: Int) : Boolean {
        return cheaterSet.contains(index)
    }

    fun cheatCount() : Int {
        return cheaterSet.count()
    }
}