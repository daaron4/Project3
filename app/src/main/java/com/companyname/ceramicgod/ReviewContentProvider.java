package com.companyname.ceramicgod;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ReviewContentProvider extends ContentProvider {
    private static final String AUTHORITY = "com.companyname.ceramicgod.ReviewContentProvider";
    private static final String REVIEWS_TABLE = "reviews_table";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + REVIEWS_TABLE);

    public static final int ALL_REVIEWS = 1;
    public static final int ONE_REVIEW = 2;
    public static final int INSERT_REVIEW = 3;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sURIMatcher.addURI(AUTHORITY, REVIEWS_TABLE, ALL_REVIEWS);
        sURIMatcher.addURI(AUTHORITY, REVIEWS_TABLE + "/#", ONE_REVIEW);
//        sURIMatcher.addURI(AUTHORITY, REVIEWS_TABLE, INSERT_REVIEW);
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(
            Uri uri,
            String[] projection,
            String selection,
            String[] selectionArgs,
            String sortOrder) {
        int uriType = sURIMatcher.match(uri);
        Cursor cursor = null;
        switch (uriType) {
            case ALL_REVIEWS:
                cursor = DatabaseHelper.getInstance(getContext()).getAllReviews();
                break;
            case ONE_REVIEW:
                int id = Integer.parseInt(uri.getLastPathSegment());
                cursor = DatabaseHelper.getInstance(getContext()).getReviewAtIndex(id);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        long id = 0;
        switch (uriType) {
            case ALL_REVIEWS:
                id = DatabaseHelper.getInstance(getContext()).insertApiReview(values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(REVIEWS_TABLE + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        int rowsDeleted = 0;
        switch (uriType) {
            case ALL_REVIEWS:
                DatabaseHelper.getInstance(getContext()).deleteAllReviews();
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    public int update(
            Uri uri,
            ContentValues values,
            String selection,
            String[] selectionArgs) {
        return 0;
    }
}
