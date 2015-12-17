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
     *
     */
    public Product() {
    }

    /**
     *
     * @param in
     */
    protected Product(Parcel in) {
        productName = in.readString();
        productId = in.readInt();
        expiryDate = in.readString();
        category = in.readString();
    }

    /**
     *
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
     *
     * @param productName
     * @param isShoppingCheck
     */
    public Product(String productName, boolean isShoppingCheck) {
        this.productName = productName;
        this.shoppingCheck = isShoppingCheck;
    }

    /**
     *
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
     *
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
     *
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
     *
     * @return
     */
    public boolean isShoppingCheck() {
        return shoppingCheck;
    }

    /**
     *
     * @param shoppingCheck
     */
    public void setShoppingCheck(boolean shoppingCheck) {
        this.shoppingCheck = shoppingCheck;
    }

    /**
     *
     * @return
     */
    public String getProductName() {
        return productName;
    }

    /**
     *
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     *
     * @return
     */
    public int getProductId() {
        return productId;
    }

    /**
     *
     * @param productId
     */
    public void setProductId(int productId) {
        this.productId = productId;
    }

    /**
     *
     * @return
     */
    public String getExpiryDate() {
        return expiryDate;
    }

    /**
     *
     * @param expiryDate
     */
    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     *
     * @return
     */
    public String getCategory() {
        return category;
    }

    /**
     *
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     *
     * @return
     */
    public boolean isStocked() {
        return stocked;
    }

    /**
     *
     * @param stocked
     */
    public void setStocked(boolean stocked) {
        this.stocked = stocked;
    }

    /**
     *
     * @return
     */
    public boolean isConsumed() {
        return consumed;
    }

    /**
     *
     * @param consumed
     */
    public void setConsumed(boolean consumed) {
        this.consumed = consumed;
    }

    /**
     *
     * @return
     */
    public boolean isExpired() {
        return expired;
    }

    /**
     *
     * @param expired
     */
    public void setExpired(boolean expired) {
        this.expired = expired;
    }

    /**
     *
     * @return
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     *
     * @param dest
     * @param flags
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
