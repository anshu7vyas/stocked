package com.anshulvyas.csc780.grocerymanagr.Model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * For creating and upgrading the database.
 * extends from: SQLiteOpenHelper
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    //Constants for db name and version
    private static final String DATABASE_NAME = "Fooded.db";
    private static final int DATABASE_VERSION = 4;

    /**
     * Constructor - DBOpenHelper, calls super class constructor
     * @param context
     */
    public DBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    /**
     * Calls the ProductTable.onCreate() method
     * @param db - SQLite database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        ProductTable.onCreate(db);
    }

    /**
     * Calls the ProductTable.onUpgrade() method
     * @param db - SQLite database
     * @param oldVersion - old version of the Database
     * @param newVersion - new version of the Database
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        ProductTable.onUpgrade(db, oldVersion, newVersion);
    }
}
