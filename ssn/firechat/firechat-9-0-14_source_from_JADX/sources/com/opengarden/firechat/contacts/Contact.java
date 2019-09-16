package com.opengarden.firechat.contacts;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.PhoneNumberUtils;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Contact implements Serializable {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "Contact";
    private String mContactId = "";
    private String mDisplayName = "";
    private final ArrayList<String> mEmails = new ArrayList<>();
    private final Map<String, MXID> mMXIDsByElement = new HashMap();
    private final ArrayList<PhoneNumber> mPhoneNumbers = new ArrayList<>();
    private transient Bitmap mThumbnail;
    private String mThumbnailUri;

    public static class MXID implements Serializable {
        public final String mAccountId;
        public final String mMatrixId;
        public User mUser;

        public MXID(String str, String str2) {
            if (str == null) {
                str = "";
            }
            this.mMatrixId = str;
            this.mAccountId = str2;
            this.mUser = null;
        }
    }

    public static class PhoneNumber implements Serializable {
        public final String mCleanedPhoneNumber;
        public final String mE164PhoneNumber;
        public String mMsisdnPhoneNumber;
        public final String mRawPhoneNumber;

        public PhoneNumber(String str, String str2) {
            this.mRawPhoneNumber = str;
            this.mCleanedPhoneNumber = str.replaceAll("[\\D]", "");
            if (!TextUtils.isEmpty(str2)) {
                if (str2.startsWith("+")) {
                    str2 = str2.substring(1);
                }
                this.mE164PhoneNumber = str2;
                this.mMsisdnPhoneNumber = str2;
                return;
            }
            this.mE164PhoneNumber = null;
            refreshE164PhoneNumber();
        }

        public void refreshE164PhoneNumber() {
            if (TextUtils.isEmpty(this.mE164PhoneNumber)) {
                this.mMsisdnPhoneNumber = PhoneNumberUtils.getE164format((Context) VectorApp.getInstance(), this.mRawPhoneNumber);
                if (TextUtils.isEmpty(this.mMsisdnPhoneNumber)) {
                    this.mMsisdnPhoneNumber = this.mCleanedPhoneNumber;
                }
            }
            String access$000 = Contact.LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## refreshE164PhoneNumber ");
            sb.append(this.mMsisdnPhoneNumber);
            Log.m209d(access$000, sb.toString());
        }

        public boolean startsWith(String str) {
            return this.mRawPhoneNumber.startsWith(str) || (this.mE164PhoneNumber != null && this.mE164PhoneNumber.startsWith(str)) || this.mMsisdnPhoneNumber.startsWith(str) || this.mCleanedPhoneNumber.startsWith(str);
        }
    }

    public Contact(String str) {
        if (str != null) {
            this.mContactId = str;
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("");
        sb.append(System.currentTimeMillis());
        this.mContactId = sb.toString();
    }

    public List<String> getEmails() {
        return this.mEmails;
    }

    public void addEmailAdress(String str) {
        if (this.mEmails.indexOf(str) < 0) {
            this.mEmails.add(str);
            MXID mxid = PIDsRetriever.getInstance().getMXID(str);
            if (mxid != null) {
                this.mMXIDsByElement.put(str, mxid);
            }
        }
    }

    public List<PhoneNumber> getPhonenumbers() {
        return this.mPhoneNumbers;
    }

    public void addPhoneNumber(String str, String str2) {
        if (!TextUtils.isEmpty(str)) {
            PhoneNumber phoneNumber = new PhoneNumber(str, str2);
            this.mPhoneNumbers.add(phoneNumber);
            MXID mxid = PIDsRetriever.getInstance().getMXID(phoneNumber.mMsisdnPhoneNumber);
            if (mxid != null) {
                this.mMXIDsByElement.put(phoneNumber.mMsisdnPhoneNumber, mxid);
            }
        }
    }

    public void onCountryCodeUpdate() {
        if (this.mPhoneNumbers != null) {
            Iterator it = this.mPhoneNumbers.iterator();
            while (it.hasNext()) {
                ((PhoneNumber) it.next()).refreshE164PhoneNumber();
            }
        }
    }

    public String getThumbnailUri() {
        return this.mThumbnailUri;
    }

    public void setThumbnailUri(String str) {
        this.mThumbnailUri = str;
    }

    public void refreshMatridIds() {
        this.mMXIDsByElement.clear();
        PIDsRetriever instance = PIDsRetriever.getInstance();
        for (String str : getEmails()) {
            MXID mxid = instance.getMXID(str);
            if (mxid != null) {
                put(str, mxid);
            }
        }
        for (PhoneNumber phoneNumber : getPhonenumbers()) {
            MXID mxid2 = instance.getMXID(phoneNumber.mMsisdnPhoneNumber);
            if (mxid2 != null) {
                put(phoneNumber.mMsisdnPhoneNumber, mxid2);
            }
        }
    }

    public void put(String str, MXID mxid) {
        if (str != null && mxid != null && !TextUtils.isEmpty(mxid.mMatrixId)) {
            this.mMXIDsByElement.put(str, mxid);
        }
    }

    public boolean contains(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        boolean contains = !TextUtils.isEmpty(this.mDisplayName) ? this.mDisplayName.toLowerCase(VectorApp.getApplicationLocale()).contains(str) : false;
        if (!contains) {
            Iterator it = this.mEmails.iterator();
            while (it.hasNext()) {
                contains |= ((String) it.next()).toLowerCase(VectorApp.getApplicationLocale()).contains(str);
            }
        }
        if (!contains) {
            Iterator it2 = this.mPhoneNumbers.iterator();
            while (it2.hasNext()) {
                PhoneNumber phoneNumber = (PhoneNumber) it2.next();
                contains |= phoneNumber.mMsisdnPhoneNumber.toLowerCase(VectorApp.getApplicationLocale()).contains(str) || phoneNumber.mRawPhoneNumber.toLowerCase(VectorApp.getApplicationLocale()).contains(str) || (phoneNumber.mE164PhoneNumber != null && phoneNumber.mE164PhoneNumber.toLowerCase(VectorApp.getApplicationLocale()).contains(str));
            }
        }
        return contains;
    }

    public boolean startsWith(String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        ArrayList arrayList = new ArrayList();
        Iterator it = this.mEmails.iterator();
        while (it.hasNext()) {
            String str2 = (String) it.next();
            if (str2.startsWith(str)) {
                return true;
            }
            if (this.mMXIDsByElement != null && this.mMXIDsByElement.containsKey(str2)) {
                arrayList.add(this.mMXIDsByElement.get(str2));
            }
        }
        String replaceAll = str.replaceAll("\\s", "");
        if (replaceAll.startsWith("+")) {
            replaceAll = replaceAll.substring(1);
        }
        Iterator it2 = this.mPhoneNumbers.iterator();
        while (it2.hasNext()) {
            PhoneNumber phoneNumber = (PhoneNumber) it2.next();
            if (phoneNumber.startsWith(replaceAll)) {
                return true;
            }
            if (this.mMXIDsByElement != null && this.mMXIDsByElement.containsKey(phoneNumber.mMsisdnPhoneNumber)) {
                arrayList.add(this.mMXIDsByElement.get(phoneNumber.mMsisdnPhoneNumber));
            }
        }
        Iterator it3 = arrayList.iterator();
        while (it3.hasNext()) {
            MXID mxid = (MXID) it3.next();
            if (mxid.mMatrixId != null) {
                String str3 = mxid.mMatrixId;
                StringBuilder sb = new StringBuilder();
                sb.append("@");
                sb.append(str);
                if (str3.startsWith(sb.toString())) {
                    return true;
                }
            }
        }
        return false;
    }

    public Set<String> getMatrixIdMediums() {
        return this.mMXIDsByElement != null ? this.mMXIDsByElement.keySet() : Collections.emptySet();
    }

    public MXID getMXID(String str) {
        if (!TextUtils.isEmpty(str)) {
            return (MXID) this.mMXIDsByElement.get(str);
        }
        return null;
    }

    public void setDisplayName(String str) {
        this.mDisplayName = str;
    }

    public String getDisplayName() {
        String str = this.mDisplayName;
        if (TextUtils.isEmpty(str)) {
            Iterator it = this.mEmails.iterator();
            while (it.hasNext()) {
                String str2 = (String) it.next();
                if (!TextUtils.isEmpty(str2)) {
                    return str2;
                }
            }
        }
        if (TextUtils.isEmpty(str)) {
            Iterator it2 = this.mPhoneNumbers.iterator();
            if (it2.hasNext()) {
                return ((PhoneNumber) it2.next()).mRawPhoneNumber;
            }
        }
        return str;
    }

    public String getContactId() {
        return this.mContactId;
    }

    public Bitmap getThumbnail(Context context) {
        if (this.mThumbnail == null && this.mThumbnailUri != null) {
            try {
                this.mThumbnail = Media.getBitmap(context.getContentResolver(), Uri.parse(this.mThumbnailUri));
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("getThumbnail ");
                sb.append(e.getLocalizedMessage());
                Log.m211e(str, sb.toString());
            }
        }
        return this.mThumbnail;
    }
}
