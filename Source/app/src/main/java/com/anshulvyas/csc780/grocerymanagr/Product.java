package com.anshulvyas.csc780.grocerymanagr;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by av7 on 10/20/15.
 */
public class Product implements Parcelable {

    public final static String PRODUCT_KEY = "product_key";
    public final static String SHOPPING_KEY = "shopping_key";


    String productName;
    int productId;
    String category, expiryDate;
    boolean stocked, consumed, expired, shoppingCheck;

    public Product() {
    }

    protected Product(Parcel in) {
        productName = in.readString();
        productId = in.readInt();
        expiryDate = in.readString();
        category = in.readString();
    }

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

    public Product(String productName, boolean isShoppingCheck) {
        this.productName = productName;
        this.shoppingCheck = isShoppingCheck;
    }

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

    public boolean isShoppingCheck() {
        return shoppingCheck;
    }

    public void setShoppingCheck(boolean shoppingCheck) {
        this.shoppingCheck = shoppingCheck;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isStocked() {
        return stocked;
    }

    public void setStocked(boolean stocked) {
        this.stocked = stocked;
    }

    public boolean isConsumed() {
        return consumed;
    }

    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    public boolean isExpired() {
        return expired;
    }

    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    @Override
    public int describeContents() {
        return 0;
    }

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
