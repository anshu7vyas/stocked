package com.anshulvyas.csc780.grocerymanagr.Model;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.anshulvyas.csc780.grocerymanagr.Product;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines the Database Access Object (DAO) for our Product DB
 */
public class ProductDAO {
    String[] productEntryArray = new String[]{ProductTable.COLUMN_PRODUCT_ID, ProductTable.COLUMN_PRODUCT_NAME, ProductTable
            .COLUMN_PRODUCT_CATEGORY, ProductTable.COLUMN_PRODUCT_EXPIRY, ProductTable
            .COLUMN_SHOPPING_CHECK, ProductTable
            .COLUMN_STOCKED, ProductTable.COLUMN_CONSUMED, ProductTable.COLUMN_EXPIRED};

    private SQLiteDatabase db;

    /**
     * Constructor - ProductDAO
     * @param db - SQLite database
     */
    public ProductDAO(SQLiteDatabase db) {
        this.db = db;
    }

    /**
     * This will save the mentioned product item
     * @param productItem - specific product item
     * @return inserts in DB
     */
    public Long save(Product productItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductTable.COLUMN_PRODUCT_NAME, productItem.getProductName());
        contentValues.put(ProductTable.COLUMN_PRODUCT_CATEGORY, productItem.getCategory());
        contentValues.put(ProductTable.COLUMN_PRODUCT_EXPIRY, productItem.getExpiryDate());
        contentValues.put(ProductTable.COLUMN_SHOPPING_CHECK, productItem.isShoppingCheck());
        contentValues.put(ProductTable.COLUMN_STOCKED, productItem.isStocked());
        contentValues.put(ProductTable.COLUMN_CONSUMED, productItem.isConsumed());
        contentValues.put(ProductTable.COLUMN_EXPIRED, productItem.isExpired());

        Log.d("DEMO=====>", "INSERT reached");
        return db.insert(ProductTable.TABLE_NAME, null, contentValues);
    }

    /**
     * This will update the mentioned product item
     * @param productItem - specific product item
     * @return updates the DB
     */
    public boolean update(Product productItem) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductTable.COLUMN_PRODUCT_NAME, productItem.getProductName());
        contentValues.put(ProductTable.COLUMN_PRODUCT_CATEGORY, productItem.getCategory());
        contentValues.put(ProductTable.COLUMN_PRODUCT_EXPIRY, productItem.getExpiryDate());
        contentValues.put(ProductTable.COLUMN_SHOPPING_CHECK, productItem.isShoppingCheck());
        contentValues.put(ProductTable.COLUMN_STOCKED, productItem.isStocked());
        contentValues.put(ProductTable.COLUMN_CONSUMED, productItem.isConsumed());
        contentValues.put(ProductTable.COLUMN_EXPIRED, productItem.isExpired());
        Log.d("DEMO=====>", "UPDATE reached");
        // the db.update() method will return INT for number of rows updated. and so return db.update()>0 will check
        // for whether its true or false.
        return db.update(ProductTable.TABLE_NAME, contentValues, ProductTable.COLUMN_PRODUCT_ID + "=?", new
                String[]{productItem.getProductId() + ""}) > 0;
    }

    /**
     * This will delete the ProductItem based on Title.
     * @param productItem - specific item
     * @return deletes item from DB
     */
    public boolean delete(Product productItem) {
        return db.delete(ProductTable.TABLE_NAME, ProductTable.COLUMN_PRODUCT_ID + "=?", new String[]{productItem.getProductId() +
                ""}) > 0;
    }

    /**
     * This will get product item by ID
     * @param id - product ID
     * @return productItem
     */
    public Product get(long id) {
        Product productItem = null;
        Cursor c = db.query(true, ProductTable.TABLE_NAME, productEntryArray, ProductTable.COLUMN_PRODUCT_ID + "=?", new String[]{id +
                ""}, null, null, null, null, null);

        if (c != null && c.moveToFirst()) {
            productItem = buildFromCursor(c);
            if (!c.isClosed()) {
                c.close();
            }
        }
        return productItem;
    }

    /**
     * Get all product items as a list from DB
     * @return List
     */
    public List<Product> getAll() {
        List<Product> productList = new ArrayList<Product>();

        //Cursor c = db.query(ProductTable.TABLE_NAME, productEntryArray, null, null, null, null, null);
        String dbQuery = "SELECT * FROM " + ProductTable.TABLE_NAME + " ORDER BY " + ProductTable.COLUMN_PRODUCT_EXPIRY + " ASC;";
        Cursor c = db.rawQuery(dbQuery, null);

        if (c != null && c.moveToFirst()) {
            do {
                Product productItem = buildFromCursor(c);
                if (productItem != null) {
                    productList.add(productItem);
                }
            } while (c.moveToNext());
        }
        return productList;
    }

    /**
     *
     * @param c
     * @return
     */
    private Product buildFromCursor(Cursor c) {
        Product productItem = null;
        if (c != null) {
            productItem = new Product();
            productItem.setProductId(c.getInt(0));
            productItem.setProductName(c.getString(1));
            productItem.setCategory(c.getString(2));
            productItem.setExpiryDate(c.getString(3));
            productItem.setShoppingCheck(c.getInt(4) > 0);
            productItem.setStocked(c.getInt(5) > 0);
            productItem.setConsumed(c.getInt(6) > 0);
            productItem.setExpired(c.getInt(7) > 0);
        }
        return productItem;
    }
}