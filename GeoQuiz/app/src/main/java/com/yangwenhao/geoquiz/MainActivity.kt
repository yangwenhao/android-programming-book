package com.yangwenhao.geoquiz

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.ViewModelProvider
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.collections.HashSet

private const val TAG = "MainActivity"
private const val KEY_INDEX = "index"
private const val KEY_VIS_STATUS = "visStatus"
private const val KEY_SCORE = "score"
private const val KEY_ANSWERED = "answered"
private const val KEY_CHEAT_INDEX = "cheatIndex"
private const val REQUEST_CODE_CHEAT = 0
private const val CHEAT_MAX_COUNT = 3

class MainActivity : AppCompatActivity() {
    private lateinit var trueButton: Button
    private lateinit var falseButton: Button
    private lateinit var nextButton: ImageButton
    private lateinit var prevButton: ImageButton
    private lateinit var cheatButton: Button
    private lateinit var questionTextView: TextView
    private lateinit var remainingCheatTimesTextView: TextView

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
        val cheatIndexArray = savedInstanceState?.getIntArray(KEY_CHEAT_INDEX) ?: intArrayOf()

        quizViewModel.currentIndex = currentIndex
        quizViewModel.visStatus = visStatus
        quizViewModel.answered = answered
        quizViewModel.score = score
        quizViewModel.cheaterSet = cheatIndexArray.toCollection(HashSet())

        trueButton = findViewById(R.id.true_button)
        falseButton = findViewById(R.id.false_button)
        nextButton = findViewById(R.id.next_button)
        prevButton = findViewById(R.id.prev_button)
        cheatButton = findViewById(R.id.cheat_button)
        questionTextView = findViewById(R.id.question_text_view)
        remainingCheatTimesTextView = findViewById(R.id.remaining_cheat_times_text_view)

        remainingCheatTimesTextView.setText("Remaining cheat times: " + (CHEAT_MAX_COUNT - quizViewModel.cheatCount()))
        setButtonVis()

        trueButton.setOnClickListener {
            quizViewModel.answer()
            checkAnswer(true)
        }

        falseButton.setOnClickListener {
            quizViewModel.answer()
            checkAnswer(false)
        }

        nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
            setButtonVis()
        }

        val resultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == Activity.RESULT_OK) {
                    val isCheater =
                        it.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
                    if (isCheater) {
                        quizViewModel.addCheater(quizViewModel.currentIndex)
                        if (quizViewModel.cheatCount() >= CHEAT_MAX_COUNT) {
                            cheatButton.visibility = View.INVISIBLE
                        }
                        remainingCheatTimesTextView.setText("Remaining cheat times: " + (CHEAT_MAX_COUNT - quizViewModel.cheatCount()))
                    }
                }
            }

        cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this@MainActivity, answerIsTrue)
            val options = ActivityOptionsCompat.makeClipRevealAnimation(it,0,0, it.width, it.height)
            resultLauncher.launch(intent, options)
        }
        prevButton.setOnClickListener {
            quizViewModel.moveToPrev()
            updateQuestion()
            setButtonVis()
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
        outState.putIntArray(KEY_CHEAT_INDEX, quizViewModel.cheaterSet.toIntArray())
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
        val messageResId = when {
            quizViewModel.isCheater(quizViewModel.currentIndex) -> R.string.judgment_toast
            userAnswer == correctAnswer -> {
                quizViewModel.addScore()
                R.string.correct_toast
            }
            else -> R.string.incorrect_toast
        }
        Toast.makeText(this, messageResId, Toast.LENGTH_SHORT).show()

        setButtonVis()

        if (quizViewModel.answered == quizViewModel.questionCount) {
            showScore()
        }
    }

    private fun setButtonVis() {
        val butVis = quizViewModel.getButVis()
        trueButton.visibility = butVis
        falseButton.visibility = butVis
        cheatButton.visibility = if (butVis == View.VISIBLE && quizViewModel.cheatCount() >= CHEAT_MAX_COUNT) View.INVISIBLE else butVis
        remainingCheatTimesTextView.visibility = butVis
    }

    private fun showScore() {
        val format = DecimalFormat("0.#")
        format.roundingMode = RoundingMode.HALF_UP
        val s = format.format(quizViewModel.score * 1.0 / quizViewModel.questionCount * 100)
        Toast.makeText(this, "Your total score is $s", Toast.LENGTH_LONG).show()
    }
}