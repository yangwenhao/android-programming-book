package com.yangwenhao.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider

const val EXTRA_ANSWER_SHOWN = "com.yangwenhao.geoquiz.answer"
private const val EXTRA_ANSWER_IS_TRUE = "com.yangwenhao.geoquiz.answer_is_true"
private const val KEY_IS_CHEAT = "isCheat"
private const val KEY_ANSWER_TEXT = "answerText"

class CheatActivity : AppCompatActivity() {

    private lateinit var answerTextView: TextView
    private lateinit var showAnswerButton: Button
    private lateinit var apiLevelTextView: TextView
    private var isCheat = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cheat)

        isCheat = savedInstanceState?.getBoolean(KEY_IS_CHEAT, false) ?: false
        if (isCheat) {
            setAnswerShownResult(true)
        }
        val answerText = savedInstanceState?.getCharSequence(KEY_ANSWER_TEXT, "") ?: ""
        var answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        answerTextView = findViewById(R.id.answer_text_view)
        showAnswerButton = findViewById(R.id.show_answer_button)
        apiLevelTextView = findViewById(R.id.api_level_view)

        apiLevelTextView.setText("API Level " + Build.VERSION.SDK_INT.toString())
        answerTextView.setText(answerText)

        showAnswerButton.setOnClickListener {
            val answerText = when {
                answerIsTrue -> R.string.true_button
                else -> R.string.false_button
            }
            answerTextView.setText(answerText)
            isCheat = true
            setAnswerShownResult(true)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(KEY_IS_CHEAT, isCheat)
        outState.putCharSequence(KEY_ANSWER_TEXT, answerTextView.text)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        var data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}