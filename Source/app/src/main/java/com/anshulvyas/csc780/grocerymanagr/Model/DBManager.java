package com.anshulvyas.csc780.grocerymanagr.Model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.anshulvyas.csc780.grocerymanagr.Product;

import java.util.List;


/**
 * Created by av7 on 10/20/15.
 */
public class DBManager {
    private Context context;
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private ProductDAO productDAO;

    public DBManager(Context context) {
        this.context = context;
        dbOpenHelper = new DBOpenHelper(this.context);
        db = dbOpenHelper.getWritableDatabase();
        productDAO = new ProductDAO(db);
    }

    public long saveProduct(Product productItem) {
        return this.productDAO.save(productItem);
    }

    public boolean updateProduct(Product productItem) {
        return this.productDAO.update(productItem);
    }

    public boolean deleteProduct(Product productItem) {
        return this.productDAO.delete(productItem);
    }

    public Product get(long id) {
        return this.productDAO.get(id);
    }

    public List<Product> getAllProducts() {
        return this.productDAO.getAll();
    }


}
