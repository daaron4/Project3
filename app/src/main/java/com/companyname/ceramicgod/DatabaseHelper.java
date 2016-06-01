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
    public static final String COL_DATE = "date";
    public static final String COL_COMMENTS = "comments";
    public static final String COL_LATITUDE = "latitude";
    public static final String COL_LONGITUDE = "longitude";
    public static final String COL_ADDRESS = "address";
    public static final String COL_PICTURE = "picture";

    public static final String[] COL_NAMES = {COL_ID, COL_NAME, COL_RATING, COL_DATE,
            COL_COMMENTS, COL_LATITUDE, COL_LONGITUDE, COL_ADDRESS, COL_PICTURE};

    private static final String CREATE_REVIEWS_TABLE =
            "CREATE TABLE " + REVIEWS_TABLE +
                    "(" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_NAME + " TEXT, " +
                    COL_RATING + " INTEGER, " +
                    COL_DATE + " TEXT, " +
                    COL_COMMENTS + " TEXT, " +
                    COL_LATITUDE + " REAL, " +
                    COL_LONGITUDE + " REAL," +
                    COL_ADDRESS + " TEXT," +
                    COL_PICTURE + " BLOB)";

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
        values.put(COL_DATE, "5/4/16");
        values.put(COL_COMMENTS, "Second floor bathroom is handicap accessible, however " +
                "you have to go up stairs to get there...");
        values.put(COL_LATITUDE, 34.012982);
        values.put(COL_LONGITUDE, -118.495196);
        values.put(COL_ADDRESS, "123 GA way");
        db.insert(REVIEWS_TABLE, null, values);

        values = new ContentValues();
        values.put(COL_NAME, "The Craftsman");
        values.put(COL_RATING, 3);
        values.put(COL_DATE, "5/31/16");
        values.put(COL_COMMENTS, "Average bathroom, not very exciting, kinds gross at times");
        values.put(COL_LATITUDE, 34.013235);
        values.put(COL_LONGITUDE, -118.496131);
        values.put(COL_ADDRESS, "456 craftsman st");
        db.insert(REVIEWS_TABLE, null, values);

        values = new ContentValues();
        values.put(COL_NAME, "Santa Monica");
        values.put(COL_RATING, 2);
        values.put(COL_DATE, "1/1/1");
        values.put(COL_COMMENTS, "Crowded, smelly, fartz");
        values.put(COL_LATITUDE, 34.02);
        values.put(COL_LONGITUDE, -118.49);
        values.put(COL_ADDRESS, "1 e pier blvd");
        db.insert(REVIEWS_TABLE, null, values);

        values = new ContentValues();
        values.put(COL_NAME, "My bathroom");
        values.put(COL_RATING, 4);
        values.put(COL_DATE, "10/10/10");
        values.put(COL_COMMENTS, "Okay... at times...");
        values.put(COL_LATITUDE, 34.01);
        values.put(COL_LONGITUDE, -118.40);
        values.put(COL_ADDRESS, "058 farting rd");
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

    public Cursor getReviewAtIndex(int index) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selections = "_id = ?";
        String[] selectionArgs = new String[] {
                String.valueOf(index)
        };
        Cursor cursor = db.query(REVIEWS_TABLE, // a. table
                COL_NAMES, // b. column names
                selections, // c. selections
                selectionArgs, // d. selections args
                null, // e. group by
                null, // f. having
                null, // g. order by
                null); // h. limit
        cursor.moveToFirst();
        return cursor;
    }

    public void insertReview(Review review) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, review.getName());
        values.put(COL_RATING, review.getRating());
        values.put(COL_DATE, review.getDate());
        values.put(COL_COMMENTS, review.getCommments());
        values.put(COL_LATITUDE, review.getLatitude());
        values.put(COL_LONGITUDE, review.getLongitude());
        db.insert(REVIEWS_TABLE, null, values);
    }

    public void updateReview(int index, Review review) {
        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_NAME, review.getName());
        values.put(COL_RATING, review.getRating());
        values.put(COL_DATE, review.getDate());
        values.put(COL_COMMENTS, review.getCommments());
        values.put(COL_LATITUDE, review.getLatitude());
        values.put(COL_LONGITUDE, review.getLongitude());

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
