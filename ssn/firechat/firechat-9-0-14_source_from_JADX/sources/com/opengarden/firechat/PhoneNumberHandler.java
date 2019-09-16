package com.opengarden.firechat;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;
import com.opengarden.firechat.activity.CountryPickerActivity;
import com.opengarden.firechat.util.PhoneNumberUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class PhoneNumberHandler implements TextWatcher, OnFocusChangeListener {
    private static final int DISPLAY_COUNTRY_FULL_NAME = 0;
    public static final int DISPLAY_COUNTRY_ISO_CODE = 1;
    private String mCountryCode;
    private EditText mCountryCodeInput;
    private PhoneNumber mCurrentPhoneNumber;
    private String mCurrentPhonePrefix;
    private final int mDisplayMode;
    private EditText mPhoneNumberInput;

    @Retention(RetentionPolicy.SOURCE)
    @interface DisplayMode {
    }

    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    public PhoneNumberHandler(@NonNull final Activity activity, @NonNull EditText editText, @NonNull EditText editText2, int i, final int i2) {
        this.mPhoneNumberInput = editText;
        this.mCountryCodeInput = editText2;
        this.mDisplayMode = i;
        this.mPhoneNumberInput.addTextChangedListener(this);
        this.mPhoneNumberInput.setOnFocusChangeListener(this);
        this.mCountryCodeInput.setVisibility(8);
        this.mCountryCodeInput.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (!activity.isFinishing()) {
                    activity.startActivityForResult(CountryPickerActivity.getIntent(activity, true), i2);
                }
            }
        });
    }

    public void release() {
        this.mPhoneNumberInput.removeTextChangedListener(this);
        this.mPhoneNumberInput = null;
        this.mCountryCodeInput = null;
    }

    public void reset() {
        this.mCurrentPhoneNumber = null;
        this.mPhoneNumberInput.setText("");
        this.mCountryCodeInput.setVisibility(8);
    }

    public void setCountryCode(String str) {
        if (TextUtils.isEmpty(this.mPhoneNumberInput.getText())) {
            updateCountryCode(str);
            return;
        }
        String obj = this.mPhoneNumberInput.getText().toString();
        if (this.mCurrentPhonePrefix != null && obj.startsWith(this.mCurrentPhonePrefix)) {
            obj = obj.substring(this.mCurrentPhonePrefix.length());
        }
        updateCountryCode(str);
        if (!TextUtils.isEmpty(obj)) {
            formatPhoneNumber(obj);
        } else if (this.mCountryCodeInput.getVisibility() == 0) {
            initPhoneWithPrefix();
        }
    }

    public boolean isPhoneNumberValidForCountry() {
        return this.mCurrentPhoneNumber != null && PhoneNumberUtil.getInstance().isValidNumberForRegion(this.mCurrentPhoneNumber, this.mCountryCode);
    }

    public PhoneNumber getPhoneNumber() {
        return this.mCurrentPhoneNumber;
    }

    public String getE164PhoneNumber() {
        if (this.mCurrentPhoneNumber == null) {
            return null;
        }
        return PhoneNumberUtils.getE164format(this.mCurrentPhoneNumber);
    }

    public String getCountryCode() {
        if (this.mCurrentPhoneNumber == null) {
            return null;
        }
        return PhoneNumberUtil.getInstance().getRegionCodeForCountryCode(this.mCurrentPhoneNumber.getCountryCode());
    }

    private void updateCountryCode(String str) {
        if (!TextUtils.isEmpty(str) && !str.equals(this.mCountryCode)) {
            this.mCountryCode = str;
            switch (this.mDisplayMode) {
                case 0:
                    this.mCountryCodeInput.setText(PhoneNumberUtils.getHumanCountryCode(this.mCountryCode));
                    break;
                case 1:
                    this.mCountryCodeInput.setText(this.mCountryCode);
                    break;
            }
            int countryCodeForRegion = PhoneNumberUtil.getInstance().getCountryCodeForRegion(this.mCountryCode);
            if (countryCodeForRegion > 0) {
                StringBuilder sb = new StringBuilder();
                sb.append("+");
                sb.append(countryCodeForRegion);
                this.mCurrentPhonePrefix = sb.toString();
            }
        }
    }

    private void initPhoneWithPrefix() {
        if (!TextUtils.isEmpty(this.mCurrentPhonePrefix)) {
            this.mPhoneNumberInput.setText(this.mCurrentPhonePrefix);
            this.mPhoneNumberInput.setSelection(this.mPhoneNumberInput.getText().length());
        }
    }

    private void formatPhoneNumber(String str) {
        if (!TextUtils.isEmpty(this.mCountryCode)) {
            try {
                this.mCurrentPhoneNumber = PhoneNumberUtil.getInstance().parse(str.trim(), this.mCountryCode);
                String format = PhoneNumberUtil.getInstance().format(this.mCurrentPhoneNumber, PhoneNumberFormat.INTERNATIONAL);
                if (!TextUtils.equals(format, this.mPhoneNumberInput.getText())) {
                    this.mPhoneNumberInput.setText(format);
                    this.mPhoneNumberInput.setSelection(this.mPhoneNumberInput.getText().length());
                }
            } catch (NumberParseException unused) {
                this.mCurrentPhoneNumber = null;
            }
        }
    }

    public void afterTextChanged(Editable editable) {
        formatPhoneNumber(editable.toString());
    }

    public void onFocusChange(View view, boolean z) {
        if (z) {
            this.mCountryCodeInput.setVisibility(0);
            if (TextUtils.isEmpty(this.mPhoneNumberInput.getText()) && !TextUtils.isEmpty(this.mCurrentPhonePrefix)) {
                initPhoneWithPrefix();
            }
        } else if (TextUtils.isEmpty(this.mPhoneNumberInput.getText()) || TextUtils.equals(this.mPhoneNumberInput.getText(), this.mCurrentPhonePrefix)) {
            reset();
        }
    }
}
