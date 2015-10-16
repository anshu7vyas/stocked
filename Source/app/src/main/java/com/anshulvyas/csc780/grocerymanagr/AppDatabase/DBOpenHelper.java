package com.anshulvyas.csc780.grocerymanagr.AppDatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by av7 on 10/14/15.
 */
public class DBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "fooded.db";
    private static final int DATABASE_VERSION = 1;

    //Constants for identifying table and columns
    public static final String TABLE_CART = "cart";
    public static final String CART_ID = "_id";
    public static final String CART_ITEM_TEXT = "cartItemText";
    public static final String CART_ITEM_CREATED = "cartItemCreated";

    public static final String[] ALL_COLUMNS = {CART_ID, CART_ITEM_TEXT, CART_ITEM_CREATED};

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_CART + " (" +
                    CART_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    CART_ITEM_TEXT + " TEXT, " +
                    CART_ITEM_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        onCreate(db);
    }
}
