package com.anshulvyas.csc780.grocerymanagr.Model;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by av7 on 10/20/15.
 */
public class ProductTable {

    //Columns for the expire items.
    public static final String TABLE_NAME = "product";
    public static final String COLUMN_PRODUCT_ID = "_id";
    public static final String COLUMN_PRODUCT_NAME = "productName";
    public static final String COLUMN_PRODUCT_CATEGORY = "productCategory";
    //public static final String HOME_ITEM_IMG = "homeItemImg";

    public static final String COLUMN_PRODUCT_EXPIRY = "expiryIn";
    public static final String COLUMN_PRODUCT_NOTIFY = "notifyIn";
    public static final String COLUMN_STOCKED = "stocked";
    public static final String COLUMN_CONSUMED = "consumed";
    public static final String COLUMN_EXPIRED = "expired";


    public static final String[] HOME_ALL_COLUMNS = {COLUMN_PRODUCT_ID, COLUMN_PRODUCT_CATEGORY, COLUMN_PRODUCT_NAME, COLUMN_PRODUCT_EXPIRY, COLUMN_PRODUCT_NOTIFY, COLUMN_CONSUMED, COLUMN_EXPIRED, COLUMN_STOCKED};

    public static void onCreate(SQLiteDatabase db) {
        Log.i("~!@#$","onCreateReached");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("CREATE TABLE " + ProductTable.TABLE_NAME + " (");
        stringBuilder.append(COLUMN_PRODUCT_ID + " integer primary key autoincrement, ");
        stringBuilder.append(COLUMN_PRODUCT_NAME + " text not null, ");
        stringBuilder.append(COLUMN_PRODUCT_CATEGORY + " text not null, ");
        stringBuilder.append(COLUMN_PRODUCT_EXPIRY + " text not null, ");
        stringBuilder.append(COLUMN_PRODUCT_NOTIFY + " integer not null, ");
        stringBuilder.append(COLUMN_STOCKED + " boolean not null, ");
        stringBuilder.append(COLUMN_CONSUMED + " boolean not null, ");
        stringBuilder.append(COLUMN_EXPIRED + " boolean not null); ");

        try {
            db.execSQL(stringBuilder.toString());
        }
        catch (android.database.SQLException se) {
            se.printStackTrace();
        }

    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ProductTable.TABLE_NAME);
        ProductTable.onCreate(db);
    }
}
