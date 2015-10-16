package com.anshulvyas.csc780.grocerymanagr.AppDatabase;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Created by av7 on 10/14/15.
 */
public class CartProvider extends ContentProvider {

    private static final String AUTHORITY = "com.anshulvyas.csc780.grocerymanagr.cartprovider";
    private static final String BASE_PATH = "cart";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int CARTS = 1;
    private static final int CARTS_ID = 2;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(AUTHORITY, BASE_PATH, CARTS);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CARTS_ID);
    }

    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {

        /*
        Database Helper
         */
        DBOpenHelper helper = new DBOpenHelper(getContext());
        database = helper.getWritableDatabase();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return database.query(DBOpenHelper.TABLE_CART, DBOpenHelper.ALL_COLUMNS, selection, null, null, null,
                DBOpenHelper.CART_ITEM_CREATED + " DESC");
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(DBOpenHelper.TABLE_CART, null, values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(DBOpenHelper.TABLE_CART, selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return database.update(DBOpenHelper.TABLE_CART, values,selection, selectionArgs);
    }
}
