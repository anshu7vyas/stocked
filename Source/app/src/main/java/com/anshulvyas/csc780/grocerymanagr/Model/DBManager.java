package com.anshulvyas.csc780.grocerymanagr.Model;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.anshulvyas.csc780.grocerymanagr.Product;
import java.util.List;


/**
 * Manages the database by defining different methods for the CRUD operations
 */
public class DBManager {
    private Context context;
    private DBOpenHelper dbOpenHelper;
    private SQLiteDatabase db;
    private ProductDAO productDAO;

    /**
     * Constructor for DBManager
     * @param context
     */
    public DBManager(Context context) {
        this.context = context;
        dbOpenHelper = new DBOpenHelper(this.context);
        db = dbOpenHelper.getWritableDatabase();
        productDAO = new ProductDAO(db);
    }

    /**
     * Save a product item in DB
     * @param productItem - specific item
     * @return product
     */
    public long saveProduct(Product productItem) {
        return this.productDAO.save(productItem);
    }

    /**
     * Update a product item in DB
     * @param productItem - specific item
     * @return updated product = true, false
     */
    public boolean updateProduct(Product productItem) {
        return this.productDAO.update(productItem);
    }

    /**
     * Delete a product item in DB
     * @param productItem - specific item
     * @return product deleted = true, else false
     */
    public boolean deleteProduct(Product productItem) {
        return this.productDAO.delete(productItem);
    }

    /**
     * Get a product by ID from DB
     * @param id - product ID
     * @return Specific Product entry
     */
    public Product get(long id) {
        return this.productDAO.get(id);
    }

    /**
     * Get all product items as a list from DB
     * @return List
     */
    public List<Product> getAllProducts() {
        return this.productDAO.getAll();
    }


}
