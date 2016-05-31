package com.companyname.ceramicgod;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = DatabaseHelper.class.getCanonicalName();

    private static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Reviews.db";

    public static final String REVIEWS_TABLE = "reviews_table";

    public static final String COL_ID = "_id";
    public static final String COL_NAME = "name";
    public static final String COL_RATING = "rating";

    public static final String[] COL_NAMES = {COL_ID, COL_NAME, COL_RATING};

    private static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE " + REVIEWS_TABLE +
                    "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT, " +
                    COL_RATING + " INTEGER)";

    private static DatabaseHelper instance;

    public static DatabaseHelper getInstance(Context context){
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_REVIEWS_TABLE);
        loadDummyData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + REVIEWS_TABLE);
        this.onCreate(db);
    }

    public void loadDummyData(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(COL_NAME, "GA");
        values.put(COL_RATING, 1);
        db.insert(REVIEWS_TABLE, null, values);

        values = new ContentValues();
        values.put(COL_NAME, "The Craftsman");
        values.put(COL_RATING, 3);
        db.insert(REVIEWS_TABLE, null, values);

        values = new ContentValues();
        values.put(COL_NAME, "Santa Monica Pier");
        values.put(COL_RATING, 2);
        db.insert(REVIEWS_TABLE, null, values);
    }

    public Cursor getAllReviews(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(REVIEWS_TABLE, // a. table
                COL_NAMES, // b. column names
                null, // c. selections
                null, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        return cursor;
    }

    public void insertReview(Review review) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, review.getName());
        values.put(COL_RATING, review.getRating());
        db.insert(REVIEWS_TABLE, null, values);
    }

    public void updateReview(int index, Review review) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, review.getName());
        values.put(COL_RATING, review.getRating());

        String selection = "_id = ?";

        String[] selectionArgs = new String[] {
                String.valueOf(index)
        };

        db.update(REVIEWS_TABLE, values, selection, selectionArgs);

    }

    public void deleteReview(int index) {
        SQLiteDatabase db = getWritableDatabase();

        String selection = "_id = ?";

        String[] selectionArgs = new String[] {
                String.valueOf(index)
        };

        db.delete(REVIEWS_TABLE, selection, selectionArgs);
    }

}
