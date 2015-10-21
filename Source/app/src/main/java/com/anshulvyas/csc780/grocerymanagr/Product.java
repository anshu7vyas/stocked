package com.anshulvyas.csc780.grocerymanagr;

/**
 * Created by av7 on 10/20/15.
 */
public class Product  {
    String productName;
    int notifyMe, productId;
    String expiryDate, category;
    boolean stocked, consumed, expired;

    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", notifyMe=" + notifyMe +
                ", productId=" + productId +
                ", expiryDate='" + expiryDate + '\'' +
                ", category='" + category + '\'' +
                ", stocked=" + stocked +
                ", consumed=" + consumed +
                ", expired=" + expired +
                '}';
    }

    public Product(String productName, int notifyMe, int productId, String expiryDate, String category, boolean stocked, boolean consumed, boolean expired) {
        this.productName = productName;
        this.notifyMe = notifyMe;
        this.productId = productId;
        this.expiryDate = expiryDate;
        this.category = category;
        this.stocked = stocked;
        this.consumed = consumed;
        this.expired = expired;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getNotifyMe() {
        return notifyMe;
    }

    public void setNotifyMe(int notifyMe) {
        this.notifyMe = notifyMe;
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
}
