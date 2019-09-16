package com.amplitude.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Revenue {
    public static final String TAG = "com.amplitude.api.Revenue";
    private static AmplitudeLog logger = AmplitudeLog.getLogger();
    protected Double price = null;
    protected String productId = null;
    protected JSONObject properties = null;
    protected int quantity = 1;
    protected String receipt = null;
    protected String receiptSig = null;
    protected String revenueType = null;

    /* access modifiers changed from: protected */
    public boolean isValidRevenue() {
        if (this.price != null) {
            return true;
        }
        logger.mo9089w(TAG, "Invalid revenue, need to set price");
        return false;
    }

    public Revenue setProductId(String str) {
        if (C0532Utils.isEmptyString(str)) {
            logger.mo9089w(TAG, "Invalid empty productId");
            return this;
        }
        this.productId = str;
        return this;
    }

    public Revenue setQuantity(int i) {
        this.quantity = i;
        return this;
    }

    public Revenue setPrice(double d) {
        this.price = Double.valueOf(d);
        return this;
    }

    public Revenue setRevenueType(String str) {
        this.revenueType = str;
        return this;
    }

    public Revenue setReceipt(String str, String str2) {
        this.receipt = str;
        this.receiptSig = str2;
        return this;
    }

    public Revenue setRevenueProperties(JSONObject jSONObject) {
        logger.mo9089w(TAG, "setRevenueProperties is deprecated, please use setEventProperties instead");
        return setEventProperties(jSONObject);
    }

    public Revenue setEventProperties(JSONObject jSONObject) {
        this.properties = C0532Utils.cloneJSONObject(jSONObject);
        return this;
    }

    /* access modifiers changed from: protected */
    public JSONObject toJSONObject() {
        JSONObject jSONObject = this.properties == null ? new JSONObject() : this.properties;
        try {
            jSONObject.put(Constants.AMP_REVENUE_PRODUCT_ID, this.productId);
            jSONObject.put(Constants.AMP_REVENUE_QUANTITY, this.quantity);
            jSONObject.put(Constants.AMP_REVENUE_PRICE, this.price);
            jSONObject.put(Constants.AMP_REVENUE_REVENUE_TYPE, this.revenueType);
            jSONObject.put(Constants.AMP_REVENUE_RECEIPT, this.receipt);
            jSONObject.put(Constants.AMP_REVENUE_RECEIPT_SIG, this.receiptSig);
        } catch (JSONException e) {
            logger.mo9078e(TAG, String.format("Failed to convert revenue object to JSON: %s", new Object[]{e.toString()}));
        }
        return jSONObject;
    }

    public boolean equals(Object obj) {
        boolean z = true;
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Revenue revenue = (Revenue) obj;
        if (this.quantity != revenue.quantity) {
            return false;
        }
        if (this.productId == null ? revenue.productId != null : !this.productId.equals(revenue.productId)) {
            return false;
        }
        if (this.price == null ? revenue.price != null : !this.price.equals(revenue.price)) {
            return false;
        }
        if (this.revenueType == null ? revenue.revenueType != null : !this.revenueType.equals(revenue.revenueType)) {
            return false;
        }
        if (this.receipt == null ? revenue.receipt != null : !this.receipt.equals(revenue.receipt)) {
            return false;
        }
        if (this.receiptSig == null ? revenue.receiptSig != null : !this.receiptSig.equals(revenue.receiptSig)) {
            return false;
        }
        if (this.properties == null ? revenue.properties != null : !C0532Utils.compareJSONObjects(this.properties, revenue.properties)) {
            z = false;
        }
        return z;
    }

    public int hashCode() {
        int i = 0;
        int hashCode = (((((((((((this.productId != null ? this.productId.hashCode() : 0) * 31) + this.quantity) * 31) + (this.price != null ? this.price.hashCode() : 0)) * 31) + (this.revenueType != null ? this.revenueType.hashCode() : 0)) * 31) + (this.receipt != null ? this.receipt.hashCode() : 0)) * 31) + (this.receiptSig != null ? this.receiptSig.hashCode() : 0)) * 31;
        if (this.properties != null) {
            i = this.properties.hashCode();
        }
        return hashCode + i;
    }
}
