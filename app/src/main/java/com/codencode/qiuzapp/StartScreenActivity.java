package com.codencode.qiuzapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class StartScreenActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_QUIZ = 1;
    public static final String SHARED_PREFS = "sharedPrefs";
    public static final String HIGHSCORE_KEY = "highScoreKey";

    TextView highScoreTextView;
    int highScore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);
        Button startQuizButton = findViewById(R.id.start_screen_button);
        startQuizButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startQuiz();
            }
        });

        highScoreTextView = findViewById(R.id.start_screen_high_score_text);
        loadHighScore();
    }

    private void startQuiz()
    {
        Intent startQuizIntent = new Intent(this , QuizActivity.class);
        startActivityForResult(startQuizIntent , REQUEST_CODE_QUIZ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE_QUIZ)
        {
            if(resultCode == RESULT_OK)
            {
                int score = data.getIntExtra(QuizActivity.EXTRA_SCORE , 0);
                if(score > highScore)
                    setHighScore(score);
            }
        }
    }

    void setHighScore(int score)
    {
        highScore = score;
        highScoreTextView.setText("High Score : " + score);

        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS , MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putInt(HIGHSCORE_KEY , highScore);
        editor.apply();
    }

    void loadHighScore()
    {
        SharedPreferences prefs = getSharedPreferences(SHARED_PREFS , MODE_PRIVATE);
        highScore = prefs.getInt(HIGHSCORE_KEY , 0);
        highScoreTextView.setText("High Score : " + highScore);
    }
}
