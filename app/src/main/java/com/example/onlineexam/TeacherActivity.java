package com.example.onlineexam;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class TeacherActivity extends AppCompatActivity {

    private EditText editTextExamName, editTextDuration;
    private EditText editTextQuestion, editTextOp1, editTextOp2, editTextOp3, editTextOp4, editTextAnswer;
    private TextView textViewQuestionCount;
    private List<Question> tempQuestionList = new ArrayList<>();
    private QuizDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        dbHelper = new QuizDbHelper(this);

        editTextExamName = findViewById(R.id.edit_text_exam_name);
        editTextDuration = findViewById(R.id.edit_text_duration);
        editTextQuestion = findViewById(R.id.edit_text_question);
        editTextOp1 = findViewById(R.id.edit_text_option1);
        editTextOp2 = findViewById(R.id.edit_text_option2);
        editTextOp3 = findViewById(R.id.edit_text_option3);
        editTextOp4 = findViewById(R.id.edit_text_option4);
        editTextAnswer = findViewById(R.id.edit_text_answer);
        textViewQuestionCount = findViewById(R.id.text_view_question_count);

        Button buttonAddQuestion = findViewById(R.id.button_add_question);
        Button buttonSaveExam = findViewById(R.id.button_save_exam);

        buttonAddQuestion.setOnClickListener(v -> addQuestionToList());
        buttonSaveExam.setOnClickListener(v -> saveExamToDb());
    }

    private void addQuestionToList() {
        String qText = editTextQuestion.getText().toString().trim();
        String op1 = editTextOp1.getText().toString().trim();
        String op2 = editTextOp2.getText().toString().trim();
        String op3 = editTextOp3.getText().toString().trim();
        String op4 = editTextOp4.getText().toString().trim();
        String ansStr = editTextAnswer.getText().toString().trim();

        if (qText.isEmpty() || op1.isEmpty() || op2.isEmpty() || op3.isEmpty() || op4.isEmpty() || ansStr.isEmpty()) {
            Toast.makeText(this, "Please fill all question fields", Toast.LENGTH_SHORT).show();
            return;
        }

        int answer = Integer.parseInt(ansStr);
        if (answer < 1 || answer > 4) {
            Toast.makeText(this, "Answer must be between 1 and 4", Toast.LENGTH_SHORT).show();
            return;
        }

        tempQuestionList.add(new Question(qText, op1, op2, op3, op4, answer));
        textViewQuestionCount.setText("Questions added: " + tempQuestionList.size());

        // Clear question fields
        editTextQuestion.setText("");
        editTextOp1.setText("");
        editTextOp2.setText("");
        editTextOp3.setText("");
        editTextOp4.setText("");
        editTextAnswer.setText("");
    }

    private void saveExamToDb() {
        String examName = editTextExamName.getText().toString().trim();
        String durationStr = editTextDuration.getText().toString().trim();

        if (examName.isEmpty() || durationStr.isEmpty() || tempQuestionList.isEmpty()) {
            Toast.makeText(this, "Please provide exam details and at least one question", Toast.LENGTH_SHORT).show();
            return;
        }

        int duration = Integer.parseInt(durationStr);
        Exam exam = new Exam(examName, duration);
        long examId = dbHelper.addExam(exam);

        if (examId != -1) {
            for (Question q : tempQuestionList) {
                dbHelper.addQuestion(q, (int) examId);
            }
            Toast.makeText(this, "Exam created successfully!", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error creating exam", Toast.LENGTH_SHORT).show();
        }
    }
}
