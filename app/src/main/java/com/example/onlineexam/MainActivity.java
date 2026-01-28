package com.example.onlineexam;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.material.progressindicator.LinearProgressIndicator;

public class MainActivity extends AppCompatActivity {

    private TextView textViewQuestion, textViewScore, textViewQuestionCount, textViewTimer, textViewExamName;
    private RadioGroup rbGroup;
    private RadioButton rb1, rb2, rb3, rb4;
    private Button buttonConfirmNext;
    private LinearProgressIndicator questionProgress;

    private ColorStateList textColorDefaultRb;
    private ColorStateList textColorDefaultTimer;
    private CountDownTimer countDownTimer;
    private long timeLeftInMillis;

    private List<Question> questionList;
    private int questionCounter;
    private int questionCountTotal;
    private Question currentQuestion;

    private int score;
    private boolean answered;
    private int examId;
    private String examName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewQuestion = findViewById(R.id.text_view_question);
        textViewScore = findViewById(R.id.text_view_score);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);
        textViewTimer = findViewById(R.id.text_view_timer);
        textViewExamName = findViewById(R.id.text_view_exam_name);
        questionProgress = findViewById(R.id.progress_questions);
        rbGroup = findViewById(R.id.radio_group);
        rb1 = findViewById(R.id.radio_button1);
        rb2 = findViewById(R.id.radio_button2);
        rb3 = findViewById(R.id.radio_button3);
        rb4 = findViewById(R.id.radio_button4);
        buttonConfirmNext = findViewById(R.id.button_confirm_next);

        textColorDefaultRb = rb1.getTextColors();
        textColorDefaultTimer = textViewTimer.getTextColors();

        examId = getIntent().getIntExtra("EXAM_ID", -1);
        examName = getIntent().getStringExtra("EXAM_NAME");
        int durationMinutes = getIntent().getIntExtra("EXAM_DURATION", 10);
        timeLeftInMillis = durationMinutes * 60000;

        if (examName != null && !examName.isEmpty()) {
            textViewExamName.setText(examName);
        }

        QuizDbHelper dbHelper = new QuizDbHelper(this);
        questionList = dbHelper.getQuestionsForExam(examId);
        questionCountTotal = questionList.size();
        if (questionProgress != null) {
            int max = questionCountTotal > 0 ? questionCountTotal : 1;
            questionProgress.setMax(max);
            questionProgress.setProgress(0);
        }
        Collections.shuffle(questionList);

        showNextQuestion();
        startTimer();

        buttonConfirmNext.setOnClickListener(v -> {
            if (!answered) {
                if (rb1.isChecked() || rb2.isChecked() || rb3.isChecked() || rb4.isChecked()) {
                    checkAnswer();
                } else {
                    Toast.makeText(MainActivity.this, "Please select an answer", Toast.LENGTH_SHORT).show();
                }
            } else {
                showNextQuestion();
            }
        });
    }

    private void showNextQuestion() {
        rb1.setTextColor(textColorDefaultRb);
        rb2.setTextColor(textColorDefaultRb);
        rb3.setTextColor(textColorDefaultRb);
        rb4.setTextColor(textColorDefaultRb);
        rbGroup.clearCheck();

        if (questionCounter < questionCountTotal) {
            currentQuestion = questionList.get(questionCounter);

            textViewQuestion.setText(currentQuestion.getQuestion());
            rb1.setText(currentQuestion.getOption1());
            rb2.setText(currentQuestion.getOption2());
            rb3.setText(currentQuestion.getOption3());
            rb4.setText(currentQuestion.getOption4());

            questionCounter++;
            textViewQuestionCount.setText("Question: " + questionCounter + "/" + questionCountTotal);
            if (questionProgress != null) {
                questionProgress.setProgressCompat(questionCounter, true);
            }
            answered = false;
            buttonConfirmNext.setText("Confirm");
        } else {
            finishQuiz();
        }
    }

    private void checkAnswer() {
        answered = true;
        RadioButton rbSelected = findViewById(rbGroup.getCheckedRadioButtonId());
        int answerNr = rbGroup.indexOfChild(rbSelected) + 1;

        if (answerNr == currentQuestion.getAnswerNr()) {
            score++;
            textViewScore.setText("Score: " + score);
        }

        showSolution();
    }

    private void showSolution() {
        int dangerColor = ContextCompat.getColor(this, R.color.danger);
        int successColor = ContextCompat.getColor(this, R.color.success);

        rb1.setTextColor(dangerColor);
        rb2.setTextColor(dangerColor);
        rb3.setTextColor(dangerColor);
        rb4.setTextColor(dangerColor);

        switch (currentQuestion.getAnswerNr()) {
            case 1: rb1.setTextColor(successColor); break;
            case 2: rb2.setTextColor(successColor); break;
            case 3: rb3.setTextColor(successColor); break;
            case 4: rb4.setTextColor(successColor); break;
        }

        if (questionCounter < questionCountTotal) {
            buttonConfirmNext.setText("Next");
        } else {
            buttonConfirmNext.setText("Finish");
        }
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                timeLeftInMillis = 0;
                updateCountDownText();
                Toast.makeText(MainActivity.this, "Time's up!", Toast.LENGTH_SHORT).show();
                finishQuiz();
            }
        }.start();
    }

    private void updateCountDownText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds);
        textViewTimer.setText(timeFormatted);

        if (timeLeftInMillis < 10000) {
            textViewTimer.setTextColor(ContextCompat.getColor(this, R.color.danger));
        } else {
            textViewTimer.setTextColor(textColorDefaultTimer);
        }
    }

    private void finishQuiz() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
        
        // Save result
        String date = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        dbHelper.addResult("Student", examName, score, questionCountTotal, date);

        Toast.makeText(this, "Exam finished! Score: " + score + "/" + questionCountTotal, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
