package com.codencode.qiuzapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class QuizActivity extends AppCompatActivity {

    double counter;
    public static final String EXTRA_SCORE = "score";
    private static final String KEY_SCORE = "keyScore";
    private static final String KEY_MILLIS_LEFT = "keyMillisLeft";
    private static final String KEY_ANSWERED = "keyAnswered";
    private static final String KEY_QUESTION_LIST = "keyQuestionList";
    private static final String KEY_QUESTION_COUNT = "keyQuestionCount";

    TextView mCountDown;
    TextView mQuestion;
    TextView mQuestionCount;
    TextView mScore;

    RadioGroup mRBGroup;
    RadioButton rb1;
    RadioButton rb2;
    RadioButton rb3;

    Button mConfirmButton;
    ColorStateList textColorDefaultRb;
    ColorStateList textColorDefaultCd;

    CountDownTimer countDownTimer;
    long timeLeftInMilliSec;


    ArrayList<Question> QList;
    int questionCounter;
    Question currQuestion;

    int score;
    boolean answered;
    static final String TAG = "QuizActivity() ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mCountDown = findViewById(R.id.quiz_countdown);
        mQuestion = findViewById(R.id.quiz_question_text);
        mQuestionCount = findViewById(R.id.quiz_question_count);
        mScore = findViewById(R.id.quiz_score);

        mRBGroup = findViewById(R.id.quiz_radio_group);
        rb1 = findViewById(R.id.quiz_option1);
        rb2 = findViewById(R.id.quiz_option2);
        rb3 = findViewById(R.id.quiz_option3);

        mConfirmButton = findViewById(R.id.quiz_confirm_button);
        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultCd = mCountDown.getTextColors();


        if(savedInstanceState == null) {
            QuizDbHelper dbHelper = new QuizDbHelper(this);
            QList = dbHelper.getAllQuestions();
            Collections.shuffle(QList);
            showNextQuestion();
        }
        else
        {
            QList = savedInstanceState.getParcelableArrayList(KEY_QUESTION_LIST);
            score = savedInstanceState.getInt(KEY_SCORE);
            answered = savedInstanceState.getBoolean(KEY_ANSWERED);
            questionCounter = savedInstanceState.getInt(KEY_QUESTION_COUNT);
            currQuestion = QList.get(questionCounter-1);
            timeLeftInMilliSec = savedInstanceState.getLong(KEY_MILLIS_LEFT);
            if(!answered)
            {
                startCountDown();
            }
            else
            {
                updateCountDownText();
                showSolution();
            }
        }
        mConfirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!answered)
                {
                    if(rb1.isChecked() || rb2.isChecked() || rb3.isChecked())
                    {
                        checkAnswer();
                    }
                    else
                    {
                        Toast.makeText(QuizActivity.this, "Select an answer", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                    showNextQuestion();
            }
        });
    }

    void showNextQuestion()
    {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        mRBGroup.clearCheck();

        if(questionCounter < QList.size())
        {
            currQuestion = QList.get(questionCounter);
            loadQuestion(questionCounter);
            questionCounter++;
            answered = false;
            mConfirmButton.setText("Confirm");
        }
        else
        {
            finishQuiz();
        }
    }

    private void loadQuestion(int idx)
    {
        if(QList.isEmpty()) return;

        Question q = QList.get(idx);
        mQuestion.setText(q.getQuestion());
        mQuestionCount.setText("Question : "+(idx+1)+"/"+QList.size());
        rb1.setText(q.getOption1());
        rb2.setText(q.getOption2());
        rb3.setText(q.getOption3());
        timeLeftInMilliSec = 30000;
        startCountDown();
    }

    void startCountDown()
    {
        countDownTimer = new CountDownTimer(timeLeftInMilliSec , 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMilliSec = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMilliSec = 0;
                updateCountDownText();
                checkAnswer();
            }
        }.start();
    }

    void updateCountDownText()
    {
        int sec = (int) (timeLeftInMilliSec / 1000);
        mCountDown.setText("0:" + sec);
        if(timeLeftInMilliSec < 10000)
        {
            mCountDown.setTextColor(Color.RED);
        }
        else
        {
            mCountDown.setTextColor(textColorDefaultCd);
        }
    }

    void checkAnswer()
    {
        answered = true;
        countDownTimer.cancel();

        RadioButton selectedRadioButton = findViewById(mRBGroup.getCheckedRadioButtonId());
        int answerNr = mRBGroup.indexOfChild(selectedRadioButton) + 1;

        if(answerNr == currQuestion.getAnswerNr())
        {
            score++;
            mScore.setText("Score : " + score);
        }

        showSolution();
    }

    void showSolution()
    {
        rb1.setTextColor(Color.RED);
        rb2.setTextColor(Color.RED);
        rb3.setTextColor(Color.RED);

        switch (currQuestion.getAnswerNr())
        {
            case 1 :
                rb1.setTextColor(Color.GREEN);
                mQuestion.setText("Answer 1 is correct");
                break;
            case 2 :
                rb2.setTextColor(Color.GREEN);
                mQuestion.setText("Answer 2 is correct");
                break;
            case 3 :
                rb3.setTextColor(Color.GREEN);
                mQuestion.setText("Answer 3 is correct");
                break;
        }

        if(questionCounter < QList.size())
            mConfirmButton.setText("Next");
        else
            mConfirmButton.setText("Finish");
    }
    void finishQuiz()
    {
        Intent resIntent = new Intent();
        resIntent.putExtra(EXTRA_SCORE , score);
        setResult(RESULT_OK , resIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if(counter + 2000 > System.currentTimeMillis())
        {
            finishQuiz();
        }
        else
        {
            Toast.makeText(this, "Press one more time to exit", Toast.LENGTH_SHORT).show();
        }
        counter = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(countDownTimer != null) countDownTimer.cancel();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(KEY_QUESTION_COUNT , questionCounter);
        outState.putInt(KEY_SCORE , score);
        outState.putLong(KEY_MILLIS_LEFT , timeLeftInMilliSec);
        outState.putBoolean(KEY_ANSWERED , answered);
        outState.putParcelableArrayList(KEY_QUESTION_LIST , QList);
    }
}
