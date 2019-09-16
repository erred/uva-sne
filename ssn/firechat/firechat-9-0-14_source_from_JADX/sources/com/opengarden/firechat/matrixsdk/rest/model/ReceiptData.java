package com.opengarden.firechat.matrixsdk.rest.model;

import java.io.Serializable;
import java.util.Comparator;

public class ReceiptData implements Serializable {
    public static final Comparator<ReceiptData> ascComparator = new Comparator<ReceiptData>() {
        public int compare(ReceiptData receiptData, ReceiptData receiptData2) {
            return (int) (receiptData.originServerTs - receiptData2.originServerTs);
        }
    };
    public static final Comparator<ReceiptData> descComparator = new Comparator<ReceiptData>() {
        public int compare(ReceiptData receiptData, ReceiptData receiptData2) {
            return (int) (receiptData2.originServerTs - receiptData.originServerTs);
        }
    };
    public String eventId;
    public long originServerTs;
    public String userId;

    public ReceiptData(String str, String str2, long j) {
        this.userId = str;
        this.eventId = str2;
        this.originServerTs = j;
    }
}
