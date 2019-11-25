package com.codencode.qiuzapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import com.codencode.qiuzapp.QuizContract.*;

import java.util.ArrayList;
import java.util.List;

public class QuizDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "MyQuestionBank.db";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase db;

    public QuizDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;
        final String CREATE_TABLE_QUERY = "CREATE TABLE " + QuestionsTable.TABLE_NAME +
                " ( " + QuestionsTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                QuestionsTable.COLUMN_QUESTION + " TEXT, " +
                QuestionsTable.COLUMN_OPTION1 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION2 + " TEXT, " +
                QuestionsTable.COLUMN_OPTION3 + " TEXT, " +
                QuestionsTable.COLUMN_ANSWER_NR + " INTEGER " + ")";
        db.execSQL(CREATE_TABLE_QUERY);
        fillQuestionsTable();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + QuestionsTable.TABLE_NAME);
        onCreate(db);
    }

    private void fillQuestionsTable()
    {
        Question q1 = new Question("First question , guess the answer" , "A" , "B" , "C" , 1);
        addQuestion(q1);
        Question q2 = new Question("Second question , guess the answer" , "A" , "B" , "C" , 2);
        addQuestion(q2);
        Question q3 = new Question("Third question , guess the answer" , "A" , "B" , "C" , 3);
        addQuestion(q3);
        Question q4 = new Question("Fourth question , guess the answer" , "A" , "B" , "C" , 3);
        addQuestion(q4);
        Question q5 = new Question("Fifth question , guess the answer" , "A" , "B" , "C" , 2);
        addQuestion(q5);

    }

    private void addQuestion(Question q)
    {
        ContentValues cv = new ContentValues();
        cv.put(QuestionsTable.COLUMN_QUESTION , q.getQuestion());
        cv.put(QuestionsTable.COLUMN_OPTION1 , q.getOption1());
        cv.put(QuestionsTable.COLUMN_OPTION2 , q.getOption2());
        cv.put(QuestionsTable.COLUMN_OPTION3 , q.getOption3());
        cv.put(QuestionsTable.COLUMN_ANSWER_NR , q.getAnswerNr());
        db.insert(QuestionsTable.TABLE_NAME , null , cv);
    }

    public ArrayList<Question> getAllQuestions()
    {
        ArrayList<Question> QList = new ArrayList<>();
        db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + QuestionsTable.TABLE_NAME , null);

        if(c.moveToFirst())
        {
            do{
                Question q = new Question();
                q.setQuestion(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_QUESTION)));
                q.setOption1(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION1)));
                q.setOption2(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION2)));
                q.setOption3(c.getString(c.getColumnIndex(QuestionsTable.COLUMN_OPTION3)));
                q.setAnswerNr(c.getInt(c.getColumnIndex(QuestionsTable.COLUMN_ANSWER_NR)));

                QList.add(q);
            }while(c.moveToNext());
        }

        c.close();
        return QList;
    }
}
