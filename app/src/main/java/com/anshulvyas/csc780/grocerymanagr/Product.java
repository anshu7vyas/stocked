package com.anshulvyas.csc780.grocerymanagr;


import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 */
public class Product implements Parcelable {

    public final static String PRODUCT_KEY = "product_key";

    String productName;
    int productId;
    String category, expiryDate;
    boolean stocked, consumed, expired, shoppingCheck;

    /**
     * Default constructor - Product
     */
    public Product() {
    }

    /**
     * Constructor - called in HomeFragment
     * @param in
     */
    protected Product(Parcel in) {
        productName = in.readString();
        productId = in.readInt();
        expiryDate = in.readString();
        category = in.readString();
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR field that
     * generates instances of Parcelable class from a Parcel.
     */
    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    /**
     * Constructor - called in ShoppingFragment
     * @param productName
     * @param isShoppingCheck
     */
    public Product(String productName, boolean isShoppingCheck) {
        this.productName = productName;
        this.shoppingCheck = isShoppingCheck;
    }

    /**
     * Overrides the toString() method of java.Object
     * @return
     */
    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", productId=" + productId +
                ", expiryDate='" + expiryDate + '\'' +
                ", category='" + category + '\'' +
                ", stocked=" + stocked +
                ", consumed=" + consumed +
                ", expired=" + expired +
                ", shoppingCheck=" + shoppingCheck +
                '}';
    }

    /**
     * Constructor - product
     * @param productName
     * @param productId
     * @param expiryDate
     * @param category
     * @param stocked
     * @param consumed
     * @param expired
     * @param shoppingCheck
     */
    public Product(String productName, int productId, String expiryDate, String category, boolean stocked,
                   boolean consumed, boolean expired, boolean shoppingCheck) {
        this.productName = productName;
        this.productId = productId;
        this.expiryDate = expiryDate;
        this.category = category;
        this.stocked = stocked;
        this.consumed = consumed;
        this.expired = expired;
        this.shoppingCheck = shoppingCheck;
    }

    /**
     * Constructor - Product
     * @param productName
     * @param category
     * @param expiryDate
     * @param stocked
     * @param consumed
     * @param expired
     * @param shoppingCheck
     */
    public Product(String productName, String category, String expiryDate, boolean stocked, boolean consumed,
                   boolean expired, boolean shoppingCheck) {
        this.productName = productName;
        this.category = category;
        this.expiryDate = expiryDate;
        this.stocked = stocked;
        this.consumed = consumed;
        this.expired = expired;
        this.shoppingCheck = shoppingCheck;

    }

    /**
     * For checking items that are in the shopping cart but not in the homeFragment
     * @return true
     */
    public boolean isShoppingCheck() {
        return shoppingCheck;
    }

    /**
     * Set the value of shoppingCheck in DB
     * @param shoppingCheck
     */
    public void setShoppingCheck(boolean shoppingCheck) {
        this.shoppingCheck = shoppingCheck;
    }

    /**
     * Get product name from DB
     * @return productName
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Set product name of a product in DB
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Get product ID from DB
     * @return productId
     */
    public int getProductId() {
        return productId;
    }

    /**
     * Set product ID in DB
     * @param productId
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     * Get the product expiry date from DB
     * @return expiryDate
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     * Set the expiry date of a product in DB
     * @param expiryDate
     */
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Get product category from the DB
     * @return productCategory
     */
    public String getCategory() {
        return category;
    }

    /**
     * Set product category in DB
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Check whether a specific product is stocked or not (Defaults: true)
     * @return true/false
     */
    public boolean isStocked() {
        return stocked;
    }

    /**
     * To set a specific product item as stocked
     * @param stocked
     */
    public void setStocked(boolean stocked) {
        this.stocked = stocked;
    }

    /**
     * Check whether a specific product is consumed or not (Defaults: false)
     * @return true/false
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     * To set a specific product item as consumed
     * @param consumed
     */
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    /**
     * Check whether a specific product item is expired or not (Defaults: false)
     * @return true/false
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     * To set a specific product item as expired
     * @param expired
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable's marshalled representation.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productName);
        dest.writeInt(productId);
        dest.writeString(expiryDate);
        dest.writeString(category);
        dest.writeByte((byte) (stocked ? 1 : 0));
        dest.writeByte((byte) (expired ? 1 : 0));
        dest.writeByte((byte) (consumed ? 1 : 0));
        dest.writeByte((byte) (shoppingCheck ? 1 : 0));
    }
}
