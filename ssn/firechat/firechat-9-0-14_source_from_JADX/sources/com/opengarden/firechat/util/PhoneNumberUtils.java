package com.opengarden.firechat.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.p000v4.util.Pair;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

public class PhoneNumberUtils {
    public static final String COUNTRY_CODE_PREF_KEY = "COUNTRY_CODE_PREF_KEY";
    private static final String LOG_TAG = "PhoneNumberUtils";
    private static String[] mCountryCodes;
    private static List<CountryPhoneData> mCountryIndicatorList;
    private static Map<String, String> mCountryNameByCC;
    private static String[] mCountryNames;
    private static final HashMap<String, String> mE164PhoneNumberByText = new HashMap<>();
    private static final HashMap<String, Object> mPhoneNumberByText = new HashMap<>();

    public static void onLocaleUpdate() {
        mCountryCodes = null;
        mCountryIndicatorList = null;
    }

    private static void buildCountryCodesList() {
        if (mCountryCodes == null) {
            Locale applicationLocale = VectorApp.getApplicationLocale();
            String[] iSOCountries = Locale.getISOCountries();
            ArrayList arrayList = new ArrayList();
            for (String str : iSOCountries) {
                arrayList.add(new Pair(str, new Locale("", str).getDisplayCountry(applicationLocale)));
            }
            Collections.sort(arrayList, new Comparator<Pair<String, String>>() {
                public int compare(Pair<String, String> pair, Pair<String, String> pair2) {
                    return ((String) pair.second).compareTo((String) pair2.second);
                }
            });
            mCountryNameByCC = new HashMap(iSOCountries.length);
            mCountryCodes = new String[iSOCountries.length];
            mCountryNames = new String[iSOCountries.length];
            for (int i = 0; i < iSOCountries.length; i++) {
                Pair pair = (Pair) arrayList.get(i);
                mCountryCodes[i] = (String) pair.first;
                mCountryNames[i] = (String) pair.second;
                mCountryNameByCC.put(pair.first, pair.second);
            }
        }
    }

    public static List<CountryPhoneData> getCountriesWithIndicator() {
        if (mCountryIndicatorList == null) {
            mCountryIndicatorList = new ArrayList();
            buildCountryCodesList();
            for (Entry entry : mCountryNameByCC.entrySet()) {
                int countryCodeForRegion = PhoneNumberUtil.getInstance().getCountryCodeForRegion((String) entry.getKey());
                if (countryCodeForRegion > 0) {
                    mCountryIndicatorList.add(new CountryPhoneData((String) entry.getKey(), (String) entry.getValue(), countryCodeForRegion));
                }
            }
            Collections.sort(mCountryIndicatorList, new Comparator<CountryPhoneData>() {
                public int compare(CountryPhoneData countryPhoneData, CountryPhoneData countryPhoneData2) {
                    return countryPhoneData.getCountryName().compareTo(countryPhoneData2.getCountryName());
                }
            });
        }
        return mCountryIndicatorList;
    }

    public static String getHumanCountryCode(String str) {
        buildCountryCodesList();
        if (!TextUtils.isEmpty(str)) {
            return (String) mCountryNameByCC.get(str);
        }
        return null;
    }

    public static String getCountryCode(Context context) {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (!defaultSharedPreferences.contains(COUNTRY_CODE_PREF_KEY) || TextUtils.isEmpty(defaultSharedPreferences.getString(COUNTRY_CODE_PREF_KEY, ""))) {
            try {
                String upperCase = ((TelephonyManager) context.getSystemService("phone")).getNetworkCountryIso().toUpperCase(VectorApp.getApplicationLocale());
                if (!TextUtils.isEmpty(upperCase) || TextUtils.isEmpty(Locale.getDefault().getCountry()) || PhoneNumberUtil.getInstance().getCountryCodeForRegion(Locale.getDefault().getCountry()) == 0) {
                    setCountryCode(context, upperCase);
                } else {
                    setCountryCode(context, Locale.getDefault().getCountry());
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getCountryCode failed ");
                sb.append(e.getMessage());
                Log.m211e(str, sb.toString());
            }
        }
        return defaultSharedPreferences.getString(COUNTRY_CODE_PREF_KEY, "");
    }

    public static void setCountryCode(Context context, String str) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString(COUNTRY_CODE_PREF_KEY, str).apply();
    }

    private static String getMapKey(String str, String str2) {
        StringBuilder sb = new StringBuilder();
        sb.append("μ");
        sb.append(str2);
        sb.append("μ");
        sb.append(str);
        return sb.toString();
    }

    private static PhoneNumber getPhoneNumber(String str, String str2) {
        String mapKey = getMapKey(str, str2);
        PhoneNumber phoneNumber = null;
        if (mPhoneNumberByText.containsKey(mapKey)) {
            Object obj = mPhoneNumberByText.get(mapKey);
            if (obj instanceof PhoneNumber) {
                return (PhoneNumber) obj;
            }
            return null;
        }
        try {
            phoneNumber = PhoneNumberUtil.getInstance().parse(str, str2);
        } catch (Exception e) {
            String str3 = LOG_TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("## getPhoneNumber() : failed ");
            sb.append(e.getMessage());
            Log.m211e(str3, sb.toString());
        }
        if (phoneNumber != null) {
            mPhoneNumberByText.put(mapKey, phoneNumber);
            return phoneNumber;
        }
        mPhoneNumberByText.put(mapKey, "");
        return phoneNumber;
    }

    public static String getE164format(Context context, String str) {
        return getE164format(str, getCountryCode(context));
    }

    private static String getE164format(String str, String str2) {
        String str3 = null;
        if (TextUtils.isEmpty(str) || TextUtils.isEmpty(str2)) {
            return null;
        }
        String mapKey = getMapKey(str, str2);
        String str4 = (String) mE164PhoneNumberByText.get(mapKey);
        if (str4 == null) {
            str4 = "";
            try {
                PhoneNumber phoneNumber = getPhoneNumber(str, str2);
                if (phoneNumber != null) {
                    str4 = PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberFormat.E164);
                }
            } catch (Exception e) {
                String str5 = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## getE164format() failed ");
                sb.append(e.getMessage());
                Log.m211e(str5, sb.toString());
            }
            if (str4.startsWith("+")) {
                str4 = str4.substring(1);
            }
            mE164PhoneNumberByText.put(mapKey, str4);
        }
        if (!TextUtils.isEmpty(str4)) {
            str3 = str4;
        }
        return str3;
    }

    public static String getE164format(PhoneNumber phoneNumber) {
        if (phoneNumber != null) {
            return PhoneNumberUtil.getInstance().format(phoneNumber, PhoneNumberFormat.E164);
        }
        return null;
    }
}
