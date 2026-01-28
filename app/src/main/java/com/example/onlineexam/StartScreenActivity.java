package com.example.onlineexam;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class StartScreenActivity extends AppCompatActivity {

    private Spinner spinnerExams;
    private QuizDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_screen);

        spinnerExams = findViewById(R.id.spinner_exams);
        Button buttonTeacherMode = findViewById(R.id.button_teacher_mode);
        Button buttonStartExam = findViewById(R.id.button_start_exam);

        dbHelper = new QuizDbHelper(this);
        loadExams();

        buttonTeacherMode.setOnClickListener(v -> {
            Intent intent = new Intent(StartScreenActivity.this, TeacherActivity.class);
            startActivity(intent);
        });

        buttonStartExam.setOnClickListener(v -> {
            Exam selectedExam = (Exam) spinnerExams.getSelectedItem();
            if (selectedExam != null) {
                Intent intent = new Intent(StartScreenActivity.this, MainActivity.class);
                intent.putExtra("EXAM_ID", selectedExam.getId());
                intent.putExtra("EXAM_NAME", selectedExam.getName());
                intent.putExtra("EXAM_DURATION", selectedExam.getDuration());
                startActivity(intent);
            } else {
                Toast.makeText(StartScreenActivity.this, "Please select an exam", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadExams() {
        List<Exam> examList = dbHelper.getAllExams();
        ArrayAdapter<Exam> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, examList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerExams.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadExams();
    }
}
