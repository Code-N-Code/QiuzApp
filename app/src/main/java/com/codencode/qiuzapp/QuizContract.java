package com.codencode.qiuzapp;

import android.provider.BaseColumns;

public final class QuizContract {

    private QuizContract(){}

    public static class QuestionsTable implements BaseColumns {
        public static final String TABLE_NAME = "quiz_questions";
        public static final String COLUMN_QUESTION = "question";
        public static final String COLUMN_OPTION1 = "options1";
        public static final String COLUMN_OPTION2 = "options2";
        public static final String COLUMN_OPTION3 = "options3";
        public static final String COLUMN_ANSWER_NR = "answer_nr";
    }
}
