package com.yangwenhao.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import java.math.RoundingMode
import java.text.DecimalFormat

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_VIS_STATUS = "visStatus"
private const val KEY_SCORE = "score"
private const val KEY_ANSWERED = "answered"


class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var questionTextView: TextView

    private val quizViewModel : QuizViewModel by lazy {
        ViewModelProvider(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        val visStatus = savedInstanceState?.getIntArray(KEY_VIS_STATUS) ?: intArrayOf(
            View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE, View.VISIBLE
        )
        val answered = savedInstanceState?.getInt(KEY_ANSWERED, 0) ?: 0
        val score = savedInstanceState?.getInt(KEY_SCORE, 0) ?: 0

        quizViewModel.currentIndex = currentIndex
        quizViewModel.visStatus = visStatus
        quizViewModel.answered = answered
        quizViewModel.score = score

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        // questionTextView = findViewById(R.id.question_text_view)

        setTrueFalseButtonVis()

        trueButton.setOnClickListener { view: View ->
            quizViewModel.answer()
            checkAnswer(true)
        }

        falseButton.setOnClickListener { view: View ->
            quizViewModel.answer()
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            setTrueFalseButtonVis()
        }

        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
            setTrueFalseButtonVis()
        }

        updateQuestion()
    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        Log.i(TAG, "onSaveInstanceState")
        outState.putInt(KEY_INDEX, quizViewModel.currentIndex)
        outState.putIntArray(KEY_VIS_STATUS, quizViewModel.visStatus)
        outState.putInt(KEY_SCORE, quizViewModel.score)
        outState.putInt(KEY_ANSWERED, quizViewModel.answered)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuuestionText
        questionTextView.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer
        val messageResId = if (userAnswer == correctAnswer) {
            quizViewModel.addScore()
            R.string.correct_toast
        } else {
            R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        setTrueFalseButtonVis()

        if (quizViewModel.answered == quizViewModel.questionCount) {
            showScore()
        }
    }

    private fun setTrueFalseButtonVis() {
        trueButton.setVisibility(quizViewModel.getButVis());
        falseButton.setVisibility(quizViewModel.getButVis());
    }

    private fun showScore() {
        val format = DecimalFormat("0.#")
        format.roundingMode = RoundingMode.HALF_UP
        val s = format.format(quizViewModel.score * 1.0 / quizViewModel.questionCount * 100)
        Toast.makeText(this, "Your total score is $s", Toast.LENGTH_LONG).show()
    }
}