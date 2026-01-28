package com.example.onlineexam;

import android.provider.BaseColumns;

public final class QuizContract {

    private QuizContract() {}

    public static class ExamsTable implements BaseColumns {
        public static final String TABLE_NAME = "exams";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DURATION = "duration"; // in minutes
    }

    public static class QuestionsTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "option1";
        public static final String COLUMN_OPTION2 = "option2";
        public static final String COLUMN_OPTION3 = "option3";
        public static final String COLUMN_OPTION4 = "option4";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
        public static final String COLUMN_EXAM_ID = "exam_id";
    }

    public static class ResultsTable implements BaseColumns {
        public static final String TABLE_NAME = "results";
        public static final String COLUMN_STUDENT_NAME = "student_name";
        public static final String COLUMN_EXAM_NAME = "exam_name";
        public static final String COLUMN_SCORE = "score";
        public static final String COLUMN_TOTAL = "total";
        public static final String COLUMN_DATE = "date";
    }
}
