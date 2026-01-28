package com.example.onlineexam;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.onlineexam.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "OnlineExamApp.db";
    private static final int DATABASE_VERSION = 2;

    private SQLiteDatabase db;

    public QuizDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        final String SQL_CREATE_EXAMS_TABLE = "CREATE TABLE " +
                ExamsTable.TABLE_NAME + " ( " +
                ExamsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ExamsTable.COLUMN_NAME + " TEXT, " +
                ExamsTable.COLUMN_DURATION + " INTEGER" +
                ")";

        final String SQL_CREATE_QUESTIONS_TABLE = "CREATE TABLE " +
                QuestionsTable.TABLE_NAME + " ( " +
                QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION4 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER, " +
                QuestionsTable.COLUMN_EXAM_ID + " INTEGER, " +
                "FOREIGN KEY(" + QuestionsTable.COLUMN_EXAM_ID + ") REFERENCES " +
                ExamsTable.TABLE_NAME + "(" + ExamsTable._ID + ") ON DELETE CASCADE" +
                ")";

        final String SQL_CREATE_RESULTS_TABLE = "CREATE TABLE " +
                ResultsTable.TABLE_NAME + " ( " +
                ResultsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ResultsTable.COLUMN_STUDENT_NAME + " TEXT, " +
                ResultsTable.COLUMN_EXAM_NAME + " TEXT, " +
                ResultsTable.COLUMN_SCORE + " INTEGER, " +
                ResultsTable.COLUMN_TOTAL + " INTEGER, " +
                ResultsTable.COLUMN_DATE + " TEXT" +
                ")";

        db.execSQL(SQL_CREATE_EXAMS_TABLE);
        db.execSQL(SQL_CREATE_QUESTIONS_TABLE);
        db.execSQL(SQL_CREATE_RESULTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ExamsTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ResultsTable.TABLE_NAME);
        onCreate(db);
    }

    public long addExam(Exam exam) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ExamsTable.COLUMN_NAME, exam.getName());
        cv.put(ExamsTable.COLUMN_DURATION, exam.getDuration());
        return db.insert(ExamsTable.TABLE_NAME, null, cv);
    }

    public void addQuestion(Question question, int examId) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION, question.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1, question.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2, question.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3, question.getOption3());
        cv.put(QuestionsTable.COLUMN_OPTION4, question.getOption4());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR, question.getAnswerNr());
        cv.put(QuestionsTable.COLUMN_EXAM_ID, examId);
        db.insert(QuestionsTable.TABLE_NAME, null, cv);
    }

    public List<Exam> getAllExams() {
        List<Exam> examList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + ExamsTable.TABLE_NAME, null);
        if (c.moveToFirst()) {
            do {
                Exam exam = new Exam();
                exam.setId(c.getInt(c.getColumnIndexOrThrow(ExamsTable._ID)));
                exam.setName(c.getString(c.getColumnIndexOrThrow(ExamsTable.COLUMN_NAME)));
                exam.setDuration(c.getInt(c.getColumnIndexOrThrow(ExamsTable.COLUMN_DURATION)));
                examList.add(exam);
            } while (c.moveToNext());
        }
        c.close();
        return examList;
    }

    public List<Question> getQuestionsForExam(int examId) {
        List<Question> questionList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME + " WHERE " + QuestionsTable.COLUMN_EXAM_ID + " = " + examId, null);
        if (c.moveToFirst()) {
            do {
                Question question = new Question(
                        c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_QUESTION)),
                        c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION1)),
                        c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION2)),
                        c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION3)),
                        c.getString(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_OPTION4)),
                        c.getInt(c.getColumnIndexOrThrow(QuestionsTable.COLUMN_ANSWER_NR))
                );
                questionList.add(question);
            } while (c.moveToNext());
        }
        c.close();
        return questionList;
    }

    public void addResult(String studentName, String examName, int score, int total, String date) {
        db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ResultsTable.COLUMN_STUDENT_NAME, studentName);
        cv.put(ResultsTable.COLUMN_EXAM_NAME, examName);
        cv.put(ResultsTable.COLUMN_SCORE, score);
        cv.put(ResultsTable.COLUMN_TOTAL, total);
        cv.put(ResultsTable.COLUMN_DATE, date);
        db.insert(ResultsTable.TABLE_NAME, null, cv);
    }
}
