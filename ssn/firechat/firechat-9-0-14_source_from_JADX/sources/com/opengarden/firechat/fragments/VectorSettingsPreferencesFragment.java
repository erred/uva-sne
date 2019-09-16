package com.opengarden.firechat.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.design.widget.TextInputEditText;
import android.support.p000v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.activity.CountryPickerActivity;
import com.opengarden.firechat.activity.NotificationPrivacyActivity;
import com.opengarden.firechat.contacts.ContactsManager;
import com.opengarden.firechat.gcm.GcmRegistrationManager;
import com.opengarden.firechat.matrixsdk.HomeServerConnectionConfig;
import com.opengarden.firechat.matrixsdk.MXDataHandler;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.crypto.MXCrypto;
import com.opengarden.firechat.matrixsdk.data.Pusher;
import com.opengarden.firechat.matrixsdk.data.RoomMediaMessage;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.listeners.IMXNetworkEventListener;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.PushRuleSet;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThirdPartyIdentifier;
import com.opengarden.firechat.matrixsdk.rest.model.pid.ThreePid;
import com.opengarden.firechat.matrixsdk.rest.model.sync.DeviceInfo;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils;
import com.opengarden.firechat.matrixsdk.util.ResourceUtils.Resource;
import com.opengarden.firechat.preference.BingRulePreference;
import com.opengarden.firechat.preference.ProgressBarPreference;
import com.opengarden.firechat.preference.UserAvatarPreference;
import com.opengarden.firechat.preference.VectorCustomActionEditTextPreference;
import com.opengarden.firechat.preference.VectorSwitchPreference;
import com.opengarden.firechat.settings.FontScale;
import com.opengarden.firechat.util.PhoneNumberUtils;
import com.opengarden.firechat.util.PreferencesManager;
import com.opengarden.firechat.util.ThemeUtils;
import com.opengarden.firechat.util.VectorUtils;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import kotlin.Lazy;
import kotlin.LazyKt;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.PropertyReference1Impl;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KProperty;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000ë\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\b\u000f\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010#\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u0002\n\u0002\b\u0006\n\u0002\u0010\"\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\r\n\u0002\b\t\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001H\u0018\u0000 Û\u00012\u00020\u00012\u00020\u0002:\u0002Û\u0001B\u0005¢\u0006\u0002\u0010\u0003J\n\u0010\u0001\u001a\u00030\u0001H\u0002J\u0015\u0010\u0001\u001a\u00030\u00012\t\u0010\u0001\u001a\u0004\u0018\u00010%H\u0002J\u0019\u0010\u0001\u001a\u00030\u00012\r\u0010\u0001\u001a\b\u0012\u0004\u0012\u00020<0;H\u0002J\u001a\u0010\u0001\u001a\u00030\u00012\u000e\u0010\u0001\u001a\t\u0012\u0004\u0012\u00020%0\u0001H\u0002J\u0013\u0010\u0001\u001a\u00030\u00012\u0007\u0010\u0001\u001a\u00020%H\u0002J\u001e\u0010\u0001\u001a\u00030\u00012\b\u0010\u0001\u001a\u00030\u00012\b\u0010\u0001\u001a\u00030\u0001H\u0002J\u0015\u0010\u0001\u001a\u00030\u00012\t\u0010\u0001\u001a\u0004\u0018\u00010<H\u0002J\u0015\u0010\u0001\u001a\u00030\u00012\t\u0010\u0001\u001a\u0004\u0018\u00010<H\u0002J\u0013\u0010\u0001\u001a\u00030\u00012\u0007\u0010\u0001\u001a\u00020<H\u0002J\n\u0010\u0001\u001a\u00030\u0001H\u0002J\u0014\u0010\u0001\u001a\u00030\u00012\b\u0010 \u0001\u001a\u00030¡\u0001H\u0002J\n\u0010¢\u0001\u001a\u00030\u0001H\u0002J\n\u0010£\u0001\u001a\u00030\u0001H\u0002J\u0014\u0010£\u0001\u001a\u00030\u00012\b\u0010¤\u0001\u001a\u00030¥\u0001H\u0002J\n\u0010¦\u0001\u001a\u00030\u0001H\u0003J\u0016\u0010¦\u0001\u001a\u00030\u00012\n\u0010§\u0001\u001a\u0005\u0018\u00010¨\u0001H\u0002J*\u0010©\u0001\u001a\u00030\u00012\b\u0010ª\u0001\u001a\u00030«\u00012\b\u0010¬\u0001\u001a\u00030«\u00012\n\u0010­\u0001\u001a\u0005\u0018\u00010¨\u0001H\u0016J\u0015\u0010®\u0001\u001a\u00030\u00012\t\u0010¯\u0001\u001a\u0004\u0018\u00010%H\u0002J\u0016\u0010°\u0001\u001a\u00030\u00012\n\u0010±\u0001\u001a\u0005\u0018\u00010²\u0001H\u0016J-\u0010³\u0001\u001a\u0004\u0018\u00010W2\b\u0010´\u0001\u001a\u00030µ\u00012\n\u0010¶\u0001\u001a\u0005\u0018\u00010·\u00012\n\u0010±\u0001\u001a\u0005\u0018\u00010²\u0001H\u0016J\u0015\u0010¸\u0001\u001a\u00030\u00012\t\u0010¹\u0001\u001a\u0004\u0018\u00010%H\u0002J\n\u0010º\u0001\u001a\u00030\u0001H\u0002J\n\u0010»\u0001\u001a\u00030\u0001H\u0016J\u0016\u0010¼\u0001\u001a\u00030\u00012\n\u0010­\u0001\u001a\u0005\u0018\u00010¨\u0001H\u0002J\u001d\u0010½\u0001\u001a\u00030\u00012\u0007\u0010¾\u0001\u001a\u00020%2\b\u0010¿\u0001\u001a\u00030¥\u0001H\u0002J\n\u0010À\u0001\u001a\u00030\u0001H\u0016J\u001d\u0010Á\u0001\u001a\u00030\u00012\b\u0010Â\u0001\u001a\u00030Ã\u00012\u0007\u0010Ä\u0001\u001a\u00020%H\u0016J\n\u0010Å\u0001\u001a\u00030\u0001H\u0002J\n\u0010Æ\u0001\u001a\u00030\u0001H\u0002J\u0015\u0010Ç\u0001\u001a\u00030\u00012\t\u0010È\u0001\u001a\u0004\u0018\u00010<H\u0002J\n\u0010É\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ê\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ë\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ì\u0001\u001a\u00030\u0001H\u0002J\n\u0010Í\u0001\u001a\u00030\u0001H\u0002J\n\u0010Î\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ï\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ð\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ñ\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ò\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ó\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ô\u0001\u001a\u00030\u0001H\u0002J\u0013\u0010Õ\u0001\u001a\u00020%2\b\u0010Ö\u0001\u001a\u00030«\u0001H\u0002J\n\u0010×\u0001\u001a\u00030\u0001H\u0002J\n\u0010Ø\u0001\u001a\u00030\u0001H\u0002J\u0014\u0010Ù\u0001\u001a\u00030\u00012\b\u0010\u0001\u001a\u00030Ú\u0001H\u0002R#\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u00058BX\u0002¢\u0006\f\n\u0004\b\t\u0010\n\u001a\u0004\b\u0007\u0010\bR#\u0010\u000b\u001a\n \u0006*\u0004\u0018\u00010\u00050\u00058BX\u0002¢\u0006\f\n\u0004\b\r\u0010\n\u001a\u0004\b\f\u0010\bR\u001b\u0010\u000e\u001a\u00020\u000f8BX\u0002¢\u0006\f\n\u0004\b\u0012\u0010\n\u001a\u0004\b\u0010\u0010\u0011R\u001b\u0010\u0013\u001a\u00020\u00148BX\u0002¢\u0006\f\n\u0004\b\u0017\u0010\n\u001a\u0004\b\u0015\u0010\u0016R\u001b\u0010\u0018\u001a\u00020\u00148BX\u0002¢\u0006\f\n\u0004\b\u001a\u0010\n\u001a\u0004\b\u0019\u0010\u0016R\u001b\u0010\u001b\u001a\u00020\u00148BX\u0002¢\u0006\f\n\u0004\b\u001d\u0010\n\u001a\u0004\b\u001c\u0010\u0016R\u001b\u0010\u001e\u001a\u00020\u00148BX\u0002¢\u0006\f\n\u0004\b \u0010\n\u001a\u0004\b\u001f\u0010\u0016R\u001b\u0010!\u001a\u00020\u00148BX\u0002¢\u0006\f\n\u0004\b#\u0010\n\u001a\u0004\b\"\u0010\u0016R\u0010\u0010$\u001a\u0004\u0018\u00010%X\u000e¢\u0006\u0002\n\u0000R\u001b\u0010&\u001a\u00020\u00148BX\u0002¢\u0006\f\n\u0004\b(\u0010\n\u001a\u0004\b'\u0010\u0016R\u001b\u0010)\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\b-\u0010\n\u001a\u0004\b+\u0010,R\u001b\u0010.\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\b0\u0010\n\u001a\u0004\b/\u0010,R\u001b\u00101\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\b3\u0010\n\u001a\u0004\b2\u0010,R\u001b\u00104\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\b6\u0010\n\u001a\u0004\b5\u0010,R\u001b\u00107\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\b9\u0010\n\u001a\u0004\b8\u0010,R\u0014\u0010:\u001a\b\u0012\u0004\u0012\u00020<0;X\u000e¢\u0006\u0002\n\u0000R\u001b\u0010=\u001a\u00020>8BX\u0002¢\u0006\f\n\u0004\bA\u0010\n\u001a\u0004\b?\u0010@R\u0014\u0010B\u001a\b\u0012\u0004\u0012\u00020%0CX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010D\u001a\b\u0012\u0004\u0012\u00020%0CX\u000e¢\u0006\u0002\n\u0000R\u0014\u0010E\u001a\b\u0012\u0004\u0012\u00020F0CX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010G\u001a\u00020HX\u0004¢\u0006\u0004\n\u0002\u0010IR\u001b\u0010J\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\bL\u0010\n\u001a\u0004\bK\u0010,R\u001b\u0010M\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\bO\u0010\n\u001a\u0004\bN\u0010,R\u001b\u0010P\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\bR\u0010\n\u001a\u0004\bQ\u0010,R\u001b\u0010S\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\bU\u0010\n\u001a\u0004\bT\u0010,R\u0010\u0010V\u001a\u0004\u0018\u00010WX\u000e¢\u0006\u0002\n\u0000R\u0010\u0010X\u001a\u0004\u0018\u00010<X\u000e¢\u0006\u0002\n\u0000R\u000e\u0010Y\u001a\u00020ZX\u0004¢\u0006\u0002\n\u0000R#\u0010[\u001a\n \u0006*\u0004\u0018\u00010\u00050\u00058BX\u0002¢\u0006\f\n\u0004\b]\u0010\n\u001a\u0004\b\\\u0010\bR#\u0010^\u001a\n \u0006*\u0004\u0018\u00010\u00050\u00058BX\u0002¢\u0006\f\n\u0004\b`\u0010\n\u001a\u0004\b_\u0010\bR\u0016\u0010a\u001a\n\u0012\u0004\u0012\u00020%\u0018\u00010bX\u000e¢\u0006\u0002\n\u0000R\u001b\u0010c\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\be\u0010\n\u001a\u0004\bd\u0010,R\u001b\u0010f\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\bh\u0010\n\u001a\u0004\bg\u0010,R#\u0010i\u001a\n \u0006*\u0004\u0018\u00010\u00050\u00058BX\u0002¢\u0006\f\n\u0004\bk\u0010\n\u001a\u0004\bj\u0010\bR\u000e\u0010l\u001a\u00020mX.¢\u0006\u0002\n\u0000R\u001d\u0010n\u001a\u0004\u0018\u00010>8BX\u0002¢\u0006\f\n\u0004\bp\u0010\n\u001a\u0004\bo\u0010@R\u001d\u0010q\u001a\u0004\u0018\u00010>8BX\u0002¢\u0006\f\n\u0004\bs\u0010\n\u001a\u0004\br\u0010@R\u001b\u0010t\u001a\u00020u8BX\u0002¢\u0006\f\n\u0004\bx\u0010\n\u001a\u0004\bv\u0010wR\u001b\u0010y\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\b{\u0010\n\u001a\u0004\bz\u0010,R\u001b\u0010|\u001a\u00020*8BX\u0002¢\u0006\f\n\u0004\b~\u0010\n\u001a\u0004\b}\u0010,R\u001d\u0010\u001a\u00020\u00148BX\u0002¢\u0006\u000e\n\u0005\b\u0001\u0010\n\u001a\u0005\b\u0001\u0010\u0016R\u001e\u0010\u0001\u001a\u00020\u000f8BX\u0002¢\u0006\u000e\n\u0005\b\u0001\u0010\n\u001a\u0005\b\u0001\u0010\u0011R\u001e\u0010\u0001\u001a\u00020\u00148BX\u0002¢\u0006\u000e\n\u0005\b\u0001\u0010\n\u001a\u0005\b\u0001\u0010\u0016¨\u0006Ü\u0001"}, mo21251d2 = {"Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;", "Landroid/preference/PreferenceFragment;", "Landroid/content/SharedPreferences$OnSharedPreferenceChangeListener;", "()V", "backgroundSyncCategory", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "getBackgroundSyncCategory", "()Landroid/preference/Preference;", "backgroundSyncCategory$delegate", "Lkotlin/Lazy;", "backgroundSyncDivider", "getBackgroundSyncDivider", "backgroundSyncDivider$delegate", "backgroundSyncPreference", "Landroid/preference/CheckBoxPreference;", "getBackgroundSyncPreference", "()Landroid/preference/CheckBoxPreference;", "backgroundSyncPreference$delegate", "cryptoInfoDeviceIdPreference", "Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;", "getCryptoInfoDeviceIdPreference", "()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;", "cryptoInfoDeviceIdPreference$delegate", "cryptoInfoDeviceNamePreference", "getCryptoInfoDeviceNamePreference", "cryptoInfoDeviceNamePreference$delegate", "cryptoInfoTextPreference", "getCryptoInfoTextPreference", "cryptoInfoTextPreference$delegate", "exportPref", "getExportPref", "exportPref$delegate", "importPref", "getImportPref", "importPref$delegate", "mAccountPassword", "", "mContactPhonebookCountryPreference", "getMContactPhonebookCountryPreference", "mContactPhonebookCountryPreference$delegate", "mContactSettingsCategory", "Landroid/preference/PreferenceCategory;", "getMContactSettingsCategory", "()Landroid/preference/PreferenceCategory;", "mContactSettingsCategory$delegate", "mCryptographyCategory", "getMCryptographyCategory", "mCryptographyCategory$delegate", "mCryptographyCategoryDivider", "getMCryptographyCategoryDivider", "mCryptographyCategoryDivider$delegate", "mDevicesListSettingsCategory", "getMDevicesListSettingsCategory", "mDevicesListSettingsCategory$delegate", "mDevicesListSettingsCategoryDivider", "getMDevicesListSettingsCategoryDivider", "mDevicesListSettingsCategoryDivider$delegate", "mDevicesNameList", "", "Lcom/opengarden/firechat/matrixsdk/rest/model/sync/DeviceInfo;", "mDisplayNamePreference", "Landroid/preference/EditTextPreference;", "getMDisplayNamePreference", "()Landroid/preference/EditTextPreference;", "mDisplayNamePreference$delegate", "mDisplayedEmails", "Ljava/util/ArrayList;", "mDisplayedPhoneNumber", "mDisplayedPushers", "Lcom/opengarden/firechat/matrixsdk/data/Pusher;", "mEventsListener", "com/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$mEventsListener$1", "Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$mEventsListener$1;", "mGroupsFlairCategory", "getMGroupsFlairCategory", "mGroupsFlairCategory$delegate", "mIgnoredUserSettingsCategory", "getMIgnoredUserSettingsCategory", "mIgnoredUserSettingsCategory$delegate", "mIgnoredUserSettingsCategoryDivider", "getMIgnoredUserSettingsCategoryDivider", "mIgnoredUserSettingsCategoryDivider$delegate", "mLabsCategory", "getMLabsCategory", "mLabsCategory$delegate", "mLoadingView", "Landroid/view/View;", "mMyDeviceInfo", "mNetworkListener", "Lcom/opengarden/firechat/matrixsdk/listeners/IMXNetworkEventListener;", "mNotificationPrivacyPreference", "getMNotificationPrivacyPreference", "mNotificationPrivacyPreference$delegate", "mPasswordPreference", "getMPasswordPreference", "mPasswordPreference$delegate", "mPublicisedGroups", "", "mPushersSettingsCategory", "getMPushersSettingsCategory", "mPushersSettingsCategory$delegate", "mPushersSettingsDivider", "getMPushersSettingsDivider", "mPushersSettingsDivider$delegate", "mRingtonePreference", "getMRingtonePreference", "mRingtonePreference$delegate", "mSession", "Lcom/opengarden/firechat/matrixsdk/MXSession;", "mSyncRequestDelayPreference", "getMSyncRequestDelayPreference", "mSyncRequestDelayPreference$delegate", "mSyncRequestTimeoutPreference", "getMSyncRequestTimeoutPreference", "mSyncRequestTimeoutPreference$delegate", "mUserAvatarPreference", "Lcom/opengarden/firechat/preference/UserAvatarPreference;", "getMUserAvatarPreference", "()Lcom/opengarden/firechat/preference/UserAvatarPreference;", "mUserAvatarPreference$delegate", "mUserSettingsCategory", "getMUserSettingsCategory", "mUserSettingsCategory$delegate", "notificationsSettingsCategory", "getNotificationsSettingsCategory", "notificationsSettingsCategory$delegate", "selectedLanguagePreference", "getSelectedLanguagePreference", "selectedLanguagePreference$delegate", "sendToUnverifiedDevicesPref", "getSendToUnverifiedDevicesPref", "sendToUnverifiedDevicesPref$delegate", "textSizePreference", "getTextSizePreference", "textSizePreference$delegate", "addButtons", "", "addEmail", "email", "buildDevicesSettings", "aDeviceInfoList", "buildGroupsList", "publicisedGroups", "", "deleteDevice", "deviceId", "displayDelete3PIDConfirmationDialog", "pid", "Lcom/opengarden/firechat/matrixsdk/rest/model/pid/ThirdPartyIdentifier;", "preferenceSummary", "", "displayDeviceDeletionDialog", "aDeviceInfoToDelete", "displayDeviceDetailsDialog", "aDeviceInfo", "displayDeviceRenameDialog", "aDeviceInfoToRename", "displayLoadingView", "displayTextSizeSelection", "activity", "Landroid/app/Activity;", "exportKeys", "hideLoadingView", "refresh", "", "importKeys", "intent", "Landroid/content/Intent;", "onActivityResult", "requestCode", "", "resultCode", "data", "onCommonDone", "errorMessage", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "onCreateView", "inflater", "Landroid/view/LayoutInflater;", "container", "Landroid/view/ViewGroup;", "onDisplayNameClick", "value", "onPasswordUpdateClick", "onPause", "onPhonebookCountryUpdate", "onPushRuleClick", "fResourceText", "newValue", "onResume", "onSharedPreferenceChanged", "sharedPreferences", "Landroid/content/SharedPreferences;", "key", "onUpdateAvatarClick", "refreshBackgroundSyncPrefs", "refreshCryptographyPreference", "aMyDeviceInfo", "refreshDevicesList", "refreshDisplay", "refreshEmailsList", "refreshGroupFlairsList", "refreshIgnoredUsersList", "refreshNotificationPrivacy", "refreshNotificationRingTone", "refreshPhoneNumbersList", "refreshPreferences", "refreshPushersList", "removeCryptographyPreference", "removeDevicesPreference", "secondsToText", "seconds", "setContactsPreferences", "setUserInterfacePreferences", "showEmailValidationDialog", "Lcom/opengarden/firechat/matrixsdk/rest/model/pid/ThreePid;", "Companion", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
public final class VectorSettingsPreferencesFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener {
    static final /* synthetic */ KProperty[] $$delegatedProperties = {Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mUserSettingsCategory", "getMUserSettingsCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mUserAvatarPreference", "getMUserAvatarPreference()Lcom/opengarden/firechat/preference/UserAvatarPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mDisplayNamePreference", "getMDisplayNamePreference()Landroid/preference/EditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mPasswordPreference", "getMPasswordPreference()Landroid/preference/Preference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mContactSettingsCategory", "getMContactSettingsCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mContactPhonebookCountryPreference", "getMContactPhonebookCountryPreference()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mGroupsFlairCategory", "getMGroupsFlairCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mCryptographyCategory", "getMCryptographyCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mCryptographyCategoryDivider", "getMCryptographyCategoryDivider()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mPushersSettingsDivider", "getMPushersSettingsDivider()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mPushersSettingsCategory", "getMPushersSettingsCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mDevicesListSettingsCategory", "getMDevicesListSettingsCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mDevicesListSettingsCategoryDivider", "getMDevicesListSettingsCategoryDivider()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mIgnoredUserSettingsCategoryDivider", "getMIgnoredUserSettingsCategoryDivider()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mIgnoredUserSettingsCategory", "getMIgnoredUserSettingsCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mSyncRequestTimeoutPreference", "getMSyncRequestTimeoutPreference()Landroid/preference/EditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mSyncRequestDelayPreference", "getMSyncRequestDelayPreference()Landroid/preference/EditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mLabsCategory", "getMLabsCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "backgroundSyncCategory", "getBackgroundSyncCategory()Landroid/preference/Preference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "backgroundSyncDivider", "getBackgroundSyncDivider()Landroid/preference/Preference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "backgroundSyncPreference", "getBackgroundSyncPreference()Landroid/preference/CheckBoxPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mRingtonePreference", "getMRingtonePreference()Landroid/preference/Preference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "notificationsSettingsCategory", "getNotificationsSettingsCategory()Landroid/preference/PreferenceCategory;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "mNotificationPrivacyPreference", "getMNotificationPrivacyPreference()Landroid/preference/Preference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "selectedLanguagePreference", "getSelectedLanguagePreference()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "textSizePreference", "getTextSizePreference()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "cryptoInfoDeviceNamePreference", "getCryptoInfoDeviceNamePreference()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "cryptoInfoDeviceIdPreference", "getCryptoInfoDeviceIdPreference()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "exportPref", "getExportPref()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "importPref", "getImportPref()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "cryptoInfoTextPreference", "getCryptoInfoTextPreference()Lcom/opengarden/firechat/preference/VectorCustomActionEditTextPreference;")), Reflection.property1(new PropertyReference1Impl(Reflection.getOrCreateKotlinClass(VectorSettingsPreferencesFragment.class), "sendToUnverifiedDevicesPref", "getSendToUnverifiedDevicesPref()Landroid/preference/CheckBoxPreference;"))};
    private static final String ADD_EMAIL_PREFERENCE_KEY = "ADD_EMAIL_PREFERENCE_KEY";
    private static final String ADD_PHONE_NUMBER_PREFERENCE_KEY = "ADD_PHONE_NUMBER_PREFERENCE_KEY";
    private static final String APP_INFO_LINK_PREFERENCE_KEY = "application_info_link";
    private static final String ARG_MATRIX_ID = "VectorSettingsPreferencesFragment.ARG_MATRIX_ID";
    public static final Companion Companion = new Companion(null);
    private static final String DEVICES_PREFERENCE_KEY_BASE = "DEVICES_PREFERENCE_KEY_BASE";
    private static final String DUMMY_RULE = "DUMMY_RULE";
    private static final String EMAIL_PREFERENCE_KEY_BASE = "EMAIL_PREFERENCE_KEY_BASE";
    private static final String IGNORED_USER_KEY_BASE = "IGNORED_USER_KEY_BASE";
    private static final String LABEL_UNAVAILABLE_DATA = "none";
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorSettingsPreferencesFragment";
    private static final String PHONE_NUMBER_PREFERENCE_KEY_BASE = "PHONE_NUMBER_PREFERENCE_KEY_BASE";
    private static final String PUSHER_PREFERENCE_KEY_BASE = "PUSHER_PREFERENCE_KEY_BASE";
    private static final int REQUEST_E2E_FILE_REQUEST_CODE = 123;
    private static final int REQUEST_LOCALE = 777;
    private static final int REQUEST_NEW_PHONE_NUMBER = 456;
    private static final int REQUEST_NOTIFICATION_RINGTONE = 888;
    private static final int REQUEST_PHONEBOOK_COUNTRY = 789;
    /* access modifiers changed from: private */
    public static Map<String, String> mPushesRuleByResourceId = MapsKt.mapOf(TuplesKt.m228to(PreferencesManager.SETTINGS_ENABLE_ALL_NOTIF_PREFERENCE_KEY, BingRule.RULE_ID_DISABLE_ALL), TuplesKt.m228to(PreferencesManager.SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY, DUMMY_RULE), TuplesKt.m228to(PreferencesManager.SETTINGS_TURN_SCREEN_ON_PREFERENCE_KEY, DUMMY_RULE), TuplesKt.m228to(PreferencesManager.SETTINGS_CONTAINING_MY_DISPLAY_NAME_PREFERENCE_KEY, BingRule.RULE_ID_CONTAIN_DISPLAY_NAME), TuplesKt.m228to(PreferencesManager.SETTINGS_CONTAINING_MY_USER_NAME_PREFERENCE_KEY, BingRule.RULE_ID_CONTAIN_USER_NAME), TuplesKt.m228to(PreferencesManager.SETTINGS_MESSAGES_IN_ONE_TO_ONE_PREFERENCE_KEY, BingRule.RULE_ID_ONE_TO_ONE_ROOM), TuplesKt.m228to(PreferencesManager.SETTINGS_MESSAGES_IN_GROUP_CHAT_PREFERENCE_KEY, BingRule.RULE_ID_ALL_OTHER_MESSAGES_ROOMS), TuplesKt.m228to(PreferencesManager.SETTINGS_INVITED_TO_ROOM_PREFERENCE_KEY, BingRule.RULE_ID_INVITE_ME), TuplesKt.m228to(PreferencesManager.SETTINGS_CALL_INVITATIONS_PREFERENCE_KEY, BingRule.RULE_ID_CALL), TuplesKt.m228to(PreferencesManager.SETTINGS_MESSAGES_SENT_BY_BOT_PREFERENCE_KEY, BingRule.RULE_ID_SUPPRESS_BOTS_NOTIFICATIONS));
    private final Lazy backgroundSyncCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$backgroundSyncCategory$2(this));
    private final Lazy backgroundSyncDivider$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$backgroundSyncDivider$2(this));
    private final Lazy backgroundSyncPreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$backgroundSyncPreference$2(this));
    private final Lazy cryptoInfoDeviceIdPreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$cryptoInfoDeviceIdPreference$2(this));
    private final Lazy cryptoInfoDeviceNamePreference$delegate = LazyKt.lazy(new C2271xb63f62bb(this));
    private final Lazy cryptoInfoTextPreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$cryptoInfoTextPreference$2(this));
    private final Lazy exportPref$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$exportPref$2(this));
    private final Lazy importPref$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$importPref$2(this));
    /* access modifiers changed from: private */
    public String mAccountPassword;
    private final Lazy mContactPhonebookCountryPreference$delegate = LazyKt.lazy(new C2278x8b54d99d(this));
    private final Lazy mContactSettingsCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mContactSettingsCategory$2(this));
    private final Lazy mCryptographyCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mCryptographyCategory$2(this));
    private final Lazy mCryptographyCategoryDivider$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mCryptographyCategoryDivider$2(this));
    private final Lazy mDevicesListSettingsCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mDevicesListSettingsCategory$2(this));
    private final Lazy mDevicesListSettingsCategoryDivider$delegate = LazyKt.lazy(new C2279x5cd0d916(this));
    private List<? extends DeviceInfo> mDevicesNameList = new ArrayList();
    private final Lazy mDisplayNamePreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mDisplayNamePreference$2(this));
    private ArrayList<String> mDisplayedEmails = new ArrayList<>();
    private ArrayList<String> mDisplayedPhoneNumber = new ArrayList<>();
    private ArrayList<Pusher> mDisplayedPushers = new ArrayList<>();
    private final VectorSettingsPreferencesFragment$mEventsListener$1 mEventsListener = new VectorSettingsPreferencesFragment$mEventsListener$1(this);
    private final Lazy mGroupsFlairCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mGroupsFlairCategory$2(this));
    private final Lazy mIgnoredUserSettingsCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mIgnoredUserSettingsCategory$2(this));
    private final Lazy mIgnoredUserSettingsCategoryDivider$delegate = LazyKt.lazy(new C2280x205c2ab4(this));
    private final Lazy mLabsCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mLabsCategory$2(this));
    private View mLoadingView;
    private DeviceInfo mMyDeviceInfo;
    private final IMXNetworkEventListener mNetworkListener = new VectorSettingsPreferencesFragment$mNetworkListener$1(this);
    private final Lazy mNotificationPrivacyPreference$delegate = LazyKt.lazy(new C2281xdf454cbb(this));
    private final Lazy mPasswordPreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mPasswordPreference$2(this));
    /* access modifiers changed from: private */
    public Set<String> mPublicisedGroups;
    private final Lazy mPushersSettingsCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mPushersSettingsCategory$2(this));
    private final Lazy mPushersSettingsDivider$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mPushersSettingsDivider$2(this));
    private final Lazy mRingtonePreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mRingtonePreference$2(this));
    /* access modifiers changed from: private */
    public MXSession mSession;
    private final Lazy mSyncRequestDelayPreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mSyncRequestDelayPreference$2(this));
    private final Lazy mSyncRequestTimeoutPreference$delegate = LazyKt.lazy(new C2282xac335ea1(this));
    private final Lazy mUserAvatarPreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mUserAvatarPreference$2(this));
    private final Lazy mUserSettingsCategory$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$mUserSettingsCategory$2(this));
    private final Lazy notificationsSettingsCategory$delegate = LazyKt.lazy(new C2283x30fab9d5(this));
    private final Lazy selectedLanguagePreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$selectedLanguagePreference$2(this));
    private final Lazy sendToUnverifiedDevicesPref$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$sendToUnverifiedDevicesPref$2(this));
    private final Lazy textSizePreference$delegate = LazyKt.lazy(new VectorSettingsPreferencesFragment$textSizePreference$2(this));

    @Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010$\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0002J\u000e\u0010\u001f\u001a\u00020 2\u0006\u0010!\u001a\u00020\u0004R\u000e\u0010\u0003\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0005\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u001c\u0010\r\u001a\n \u000e*\u0004\u0018\u00010\u00040\u0004X\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0011\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0004XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0014XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0016\u001a\u00020\u0014XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0014XT¢\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0014XT¢\u0006\u0002\n\u0000R&\u0010\u0019\u001a\u000e\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00040\u001aX\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001b\u0010\u001c\"\u0004\b\u001d\u0010\u001e¨\u0006\""}, mo21251d2 = {"Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment$Companion;", "", "()V", "ADD_EMAIL_PREFERENCE_KEY", "", "ADD_PHONE_NUMBER_PREFERENCE_KEY", "APP_INFO_LINK_PREFERENCE_KEY", "ARG_MATRIX_ID", "DEVICES_PREFERENCE_KEY_BASE", "DUMMY_RULE", "EMAIL_PREFERENCE_KEY_BASE", "IGNORED_USER_KEY_BASE", "LABEL_UNAVAILABLE_DATA", "LOG_TAG", "kotlin.jvm.PlatformType", "getLOG_TAG", "()Ljava/lang/String;", "PHONE_NUMBER_PREFERENCE_KEY_BASE", "PUSHER_PREFERENCE_KEY_BASE", "REQUEST_E2E_FILE_REQUEST_CODE", "", "REQUEST_LOCALE", "REQUEST_NEW_PHONE_NUMBER", "REQUEST_NOTIFICATION_RINGTONE", "REQUEST_PHONEBOOK_COUNTRY", "mPushesRuleByResourceId", "", "getMPushesRuleByResourceId", "()Ljava/util/Map;", "setMPushesRuleByResourceId", "(Ljava/util/Map;)V", "newInstance", "Lcom/opengarden/firechat/fragments/VectorSettingsPreferencesFragment;", "matrixId", "vector_appfirechatRelease"}, mo21252k = 1, mo21253mv = {1, 1, 9})
    /* compiled from: VectorSettingsPreferencesFragment.kt */
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        /* access modifiers changed from: private */
        public final String getLOG_TAG() {
            return VectorSettingsPreferencesFragment.LOG_TAG;
        }

        /* access modifiers changed from: private */
        public final Map<String, String> getMPushesRuleByResourceId() {
            return VectorSettingsPreferencesFragment.mPushesRuleByResourceId;
        }

        /* access modifiers changed from: private */
        public final void setMPushesRuleByResourceId(Map<String, String> map) {
            VectorSettingsPreferencesFragment.mPushesRuleByResourceId = map;
        }

        @NotNull
        public final VectorSettingsPreferencesFragment newInstance(@NotNull String str) {
            Intrinsics.checkParameterIsNotNull(str, "matrixId");
            VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment = new VectorSettingsPreferencesFragment();
            Bundle bundle = new Bundle();
            bundle.putString(VectorSettingsPreferencesFragment.ARG_MATRIX_ID, str);
            vectorSettingsPreferencesFragment.setArguments(bundle);
            return vectorSettingsPreferencesFragment;
        }
    }

    private final Preference getBackgroundSyncCategory() {
        Lazy lazy = this.backgroundSyncCategory$delegate;
        KProperty kProperty = $$delegatedProperties[18];
        return (Preference) lazy.getValue();
    }

    private final Preference getBackgroundSyncDivider() {
        Lazy lazy = this.backgroundSyncDivider$delegate;
        KProperty kProperty = $$delegatedProperties[19];
        return (Preference) lazy.getValue();
    }

    private final CheckBoxPreference getBackgroundSyncPreference() {
        Lazy lazy = this.backgroundSyncPreference$delegate;
        KProperty kProperty = $$delegatedProperties[20];
        return (CheckBoxPreference) lazy.getValue();
    }

    private final VectorCustomActionEditTextPreference getCryptoInfoDeviceIdPreference() {
        Lazy lazy = this.cryptoInfoDeviceIdPreference$delegate;
        KProperty kProperty = $$delegatedProperties[27];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    private final VectorCustomActionEditTextPreference getCryptoInfoDeviceNamePreference() {
        Lazy lazy = this.cryptoInfoDeviceNamePreference$delegate;
        KProperty kProperty = $$delegatedProperties[26];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    /* access modifiers changed from: private */
    public final VectorCustomActionEditTextPreference getCryptoInfoTextPreference() {
        Lazy lazy = this.cryptoInfoTextPreference$delegate;
        KProperty kProperty = $$delegatedProperties[30];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    private final VectorCustomActionEditTextPreference getExportPref() {
        Lazy lazy = this.exportPref$delegate;
        KProperty kProperty = $$delegatedProperties[28];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    private final VectorCustomActionEditTextPreference getImportPref() {
        Lazy lazy = this.importPref$delegate;
        KProperty kProperty = $$delegatedProperties[29];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    private final VectorCustomActionEditTextPreference getMContactPhonebookCountryPreference() {
        Lazy lazy = this.mContactPhonebookCountryPreference$delegate;
        KProperty kProperty = $$delegatedProperties[5];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    private final PreferenceCategory getMContactSettingsCategory() {
        Lazy lazy = this.mContactSettingsCategory$delegate;
        KProperty kProperty = $$delegatedProperties[4];
        return (PreferenceCategory) lazy.getValue();
    }

    private final PreferenceCategory getMCryptographyCategory() {
        Lazy lazy = this.mCryptographyCategory$delegate;
        KProperty kProperty = $$delegatedProperties[7];
        return (PreferenceCategory) lazy.getValue();
    }

    private final PreferenceCategory getMCryptographyCategoryDivider() {
        Lazy lazy = this.mCryptographyCategoryDivider$delegate;
        KProperty kProperty = $$delegatedProperties[8];
        return (PreferenceCategory) lazy.getValue();
    }

    /* access modifiers changed from: private */
    public final PreferenceCategory getMDevicesListSettingsCategory() {
        Lazy lazy = this.mDevicesListSettingsCategory$delegate;
        KProperty kProperty = $$delegatedProperties[11];
        return (PreferenceCategory) lazy.getValue();
    }

    private final PreferenceCategory getMDevicesListSettingsCategoryDivider() {
        Lazy lazy = this.mDevicesListSettingsCategoryDivider$delegate;
        KProperty kProperty = $$delegatedProperties[12];
        return (PreferenceCategory) lazy.getValue();
    }

    private final EditTextPreference getMDisplayNamePreference() {
        Lazy lazy = this.mDisplayNamePreference$delegate;
        KProperty kProperty = $$delegatedProperties[2];
        return (EditTextPreference) lazy.getValue();
    }

    /* access modifiers changed from: private */
    public final PreferenceCategory getMGroupsFlairCategory() {
        Lazy lazy = this.mGroupsFlairCategory$delegate;
        KProperty kProperty = $$delegatedProperties[6];
        return (PreferenceCategory) lazy.getValue();
    }

    private final PreferenceCategory getMIgnoredUserSettingsCategory() {
        Lazy lazy = this.mIgnoredUserSettingsCategory$delegate;
        KProperty kProperty = $$delegatedProperties[14];
        return (PreferenceCategory) lazy.getValue();
    }

    private final PreferenceCategory getMIgnoredUserSettingsCategoryDivider() {
        Lazy lazy = this.mIgnoredUserSettingsCategoryDivider$delegate;
        KProperty kProperty = $$delegatedProperties[13];
        return (PreferenceCategory) lazy.getValue();
    }

    /* access modifiers changed from: private */
    public final PreferenceCategory getMLabsCategory() {
        Lazy lazy = this.mLabsCategory$delegate;
        KProperty kProperty = $$delegatedProperties[17];
        return (PreferenceCategory) lazy.getValue();
    }

    private final Preference getMNotificationPrivacyPreference() {
        Lazy lazy = this.mNotificationPrivacyPreference$delegate;
        KProperty kProperty = $$delegatedProperties[23];
        return (Preference) lazy.getValue();
    }

    private final Preference getMPasswordPreference() {
        Lazy lazy = this.mPasswordPreference$delegate;
        KProperty kProperty = $$delegatedProperties[3];
        return (Preference) lazy.getValue();
    }

    private final PreferenceCategory getMPushersSettingsCategory() {
        Lazy lazy = this.mPushersSettingsCategory$delegate;
        KProperty kProperty = $$delegatedProperties[10];
        return (PreferenceCategory) lazy.getValue();
    }

    private final PreferenceCategory getMPushersSettingsDivider() {
        Lazy lazy = this.mPushersSettingsDivider$delegate;
        KProperty kProperty = $$delegatedProperties[9];
        return (PreferenceCategory) lazy.getValue();
    }

    private final Preference getMRingtonePreference() {
        Lazy lazy = this.mRingtonePreference$delegate;
        KProperty kProperty = $$delegatedProperties[21];
        return (Preference) lazy.getValue();
    }

    private final EditTextPreference getMSyncRequestDelayPreference() {
        Lazy lazy = this.mSyncRequestDelayPreference$delegate;
        KProperty kProperty = $$delegatedProperties[16];
        return (EditTextPreference) lazy.getValue();
    }

    private final EditTextPreference getMSyncRequestTimeoutPreference() {
        Lazy lazy = this.mSyncRequestTimeoutPreference$delegate;
        KProperty kProperty = $$delegatedProperties[15];
        return (EditTextPreference) lazy.getValue();
    }

    private final UserAvatarPreference getMUserAvatarPreference() {
        Lazy lazy = this.mUserAvatarPreference$delegate;
        KProperty kProperty = $$delegatedProperties[1];
        return (UserAvatarPreference) lazy.getValue();
    }

    private final PreferenceCategory getMUserSettingsCategory() {
        Lazy lazy = this.mUserSettingsCategory$delegate;
        KProperty kProperty = $$delegatedProperties[0];
        return (PreferenceCategory) lazy.getValue();
    }

    private final PreferenceCategory getNotificationsSettingsCategory() {
        Lazy lazy = this.notificationsSettingsCategory$delegate;
        KProperty kProperty = $$delegatedProperties[22];
        return (PreferenceCategory) lazy.getValue();
    }

    private final VectorCustomActionEditTextPreference getSelectedLanguagePreference() {
        Lazy lazy = this.selectedLanguagePreference$delegate;
        KProperty kProperty = $$delegatedProperties[24];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    /* access modifiers changed from: private */
    public final CheckBoxPreference getSendToUnverifiedDevicesPref() {
        Lazy lazy = this.sendToUnverifiedDevicesPref$delegate;
        KProperty kProperty = $$delegatedProperties[31];
        return (CheckBoxPreference) lazy.getValue();
    }

    private final VectorCustomActionEditTextPreference getTextSizePreference() {
        Lazy lazy = this.textSizePreference$delegate;
        KProperty kProperty = $$delegatedProperties[25];
        return (VectorCustomActionEditTextPreference) lazy.getValue();
    }

    @NotNull
    public static final /* synthetic */ MXSession access$getMSession$p(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        MXSession mXSession = vectorSettingsPreferencesFragment.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        return mXSession;
    }

    public void onCreate(@Nullable Bundle bundle) {
        super.onCreate(bundle);
        Activity activity = getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        Context applicationContext = activity.getApplicationContext();
        MXSession session = Matrix.getInstance(applicationContext).getSession(getArguments().getString(ARG_MATRIX_ID));
        if (session == null || !session.isAlive()) {
            getActivity().finish();
            return;
        }
        this.mSession = session;
        addPreferencesFromResource(C1299R.xml.vector_settings_preferences);
        UserAvatarPreference mUserAvatarPreference = getMUserAvatarPreference();
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        mUserAvatarPreference.setSession(mXSession);
        mUserAvatarPreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$1(this));
        EditTextPreference mDisplayNamePreference = getMDisplayNamePreference();
        MXSession mXSession2 = this.mSession;
        if (mXSession2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        mDisplayNamePreference.setSummary(mXSession2.getMyUser().displayname);
        mDisplayNamePreference.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$2(this));
        Preference mPasswordPreference = getMPasswordPreference();
        Intrinsics.checkExpressionValueIsNotNull(mPasswordPreference, "mPasswordPreference");
        mPasswordPreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$3(this));
        addButtons();
        refreshEmailsList();
        refreshPhoneNumbersList();
        setContactsPreferences();
        setUserInterfacePreferences();
        Preference findPreference = findPreference(PreferencesManager.SETTINGS_SHOW_URL_PREVIEW_KEY);
        if (findPreference == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.preference.VectorSwitchPreference");
        }
        VectorSwitchPreference vectorSwitchPreference = (VectorSwitchPreference) findPreference;
        MXSession mXSession3 = this.mSession;
        if (mXSession3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        vectorSwitchPreference.setChecked(mXSession3.isURLPreviewEnabled());
        vectorSwitchPreference.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$3(vectorSwitchPreference, this));
        Preference findPreference2 = findPreference(PreferencesManager.SETTINGS_ENABLE_OFFLINE_MESSAGING);
        if (findPreference2 == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.preference.VectorSwitchPreference");
        }
        VectorSwitchPreference vectorSwitchPreference2 = (VectorSwitchPreference) findPreference2;
        vectorSwitchPreference2.setChecked(PreferencesManager.getOfflinePreference(VectorApp.getInstance().getApplicationContext()));
        vectorSwitchPreference2.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$4(vectorSwitchPreference2, this));
        Preference findPreference3 = findPreference(ThemeUtils.APPLICATION_THEME_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference3, "findPreference(ThemeUtils.APPLICATION_THEME_KEY)");
        findPreference3.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$6(this));
        refreshGroupFlairsList();
        Preference mNotificationPrivacyPreference = getMNotificationPrivacyPreference();
        Intrinsics.checkExpressionValueIsNotNull(mNotificationPrivacyPreference, "mNotificationPrivacyPreference");
        mNotificationPrivacyPreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$7(this));
        refreshNotificationPrivacy();
        Preference mRingtonePreference = getMRingtonePreference();
        Intrinsics.checkExpressionValueIsNotNull(mRingtonePreference, "mRingtonePreference");
        mRingtonePreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$8(this));
        refreshNotificationRingTone();
        for (String findPreference4 : Companion.getMPushesRuleByResourceId().keySet()) {
            Preference findPreference5 = findPreference(findPreference4);
            if (findPreference5 != null) {
                if (findPreference5 instanceof CheckBoxPreference) {
                    ((CheckBoxPreference) findPreference5).setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$9(this));
                } else if (findPreference5 instanceof BingRulePreference) {
                    ((BingRulePreference) findPreference5).setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$10(this, findPreference5));
                }
            }
        }
        Matrix instance = Matrix.getInstance(applicationContext);
        if (instance == null) {
            Intrinsics.throwNpe();
        }
        GcmRegistrationManager sharedGCMRegistrationManager = instance.getSharedGCMRegistrationManager();
        if (!sharedGCMRegistrationManager.useGCM() || !sharedGCMRegistrationManager.hasRegistrationToken()) {
            CheckBoxPreference backgroundSyncPreference = getBackgroundSyncPreference();
            Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "gcmMgr");
            backgroundSyncPreference.setChecked(sharedGCMRegistrationManager.isBackgroundSyncAllowed());
            backgroundSyncPreference.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$5(this, sharedGCMRegistrationManager));
        } else {
            getPreferenceScreen().removePreference(getBackgroundSyncDivider());
            getPreferenceScreen().removePreference(getBackgroundSyncCategory());
        }
        refreshPushersList();
        refreshIgnoredUsersList();
        Preference findPreference6 = findPreference(PreferencesManager.SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_PREFERENCE_KEY);
        if (findPreference6 == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.preference.CheckBoxPreference");
        }
        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference6;
        Preference findPreference7 = findPreference(PreferencesManager.SETTINGS_ROOM_SETTINGS_LABS_END_TO_END_IS_ACTIVE_PREFERENCE_KEY);
        MXSession mXSession4 = this.mSession;
        if (mXSession4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        if (mXSession4.isCryptoEnabled()) {
            getMLabsCategory().removePreference(checkBoxPreference);
            Intrinsics.checkExpressionValueIsNotNull(findPreference7, "cryptoIsEnabledPref");
            findPreference7.setEnabled(false);
        } else {
            getMLabsCategory().removePreference(findPreference7);
            checkBoxPreference.setChecked(false);
            checkBoxPreference.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$12(this, checkBoxPreference, findPreference7));
        }
        Preference findPreference8 = findPreference(PreferencesManager.SETTINGS_DATA_SAVE_MODE_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference8, "findPreference(Preferenc…SAVE_MODE_PREFERENCE_KEY)");
        findPreference8.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$13(this));
        refreshDevicesList();
        Preference findPreference9 = findPreference(PreferencesManager.SETTINGS_LOGGED_IN_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference9, "findPreference(Preferenc…LOGGED_IN_PREFERENCE_KEY)");
        MXSession mXSession5 = this.mSession;
        if (mXSession5 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        findPreference9.setSummary(mXSession5.getMyUserId());
        Preference findPreference10 = findPreference(PreferencesManager.SETTINGS_HOME_SERVER_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference10, "findPreference(Preferenc…ME_SERVER_PREFERENCE_KEY)");
        MXSession mXSession6 = this.mSession;
        if (mXSession6 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        HomeServerConnectionConfig homeServerConfig = mXSession6.getHomeServerConfig();
        Intrinsics.checkExpressionValueIsNotNull(homeServerConfig, "mSession.homeServerConfig");
        findPreference10.setSummary(homeServerConfig.getHomeserverUri().toString());
        Preference findPreference11 = findPreference(PreferencesManager.SETTINGS_IDENTITY_SERVER_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference11, "findPreference(Preferenc…TY_SERVER_PREFERENCE_KEY)");
        MXSession mXSession7 = this.mSession;
        if (mXSession7 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        HomeServerConnectionConfig homeServerConfig2 = mXSession7.getHomeServerConfig();
        Intrinsics.checkExpressionValueIsNotNull(homeServerConfig2, "mSession.homeServerConfig");
        findPreference11.setSummary(homeServerConfig2.getIdentityServerUri().toString());
        Preference findPreference12 = findPreference(PreferencesManager.SETTINGS_SEND_MESSAGE_ENTER_KEY);
        if (findPreference12 == null) {
            throw new TypeCastException("null cannot be cast to non-null type android.preference.CheckBoxPreference");
        }
        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference12;
        checkBoxPreference2.setChecked(PreferencesManager.useEnterKeyToSendMessage(applicationContext));
        checkBoxPreference2.setOnPreferenceChangeListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$6(applicationContext));
        Preference findPreference13 = findPreference(APP_INFO_LINK_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference13, "findPreference(APP_INFO_LINK_PREFERENCE_KEY)");
        findPreference13.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$15(this, applicationContext));
        Preference findPreference14 = findPreference(PreferencesManager.SETTINGS_VERSION_PREFERENCE_KEY);
        if (findPreference14 == null) {
            throw new TypeCastException("null cannot be cast to non-null type com.opengarden.firechat.preference.VectorCustomActionEditTextPreference");
        }
        VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = (VectorCustomActionEditTextPreference) findPreference14;
        vectorCustomActionEditTextPreference.setSummary(VectorUtils.getApplicationVersion(applicationContext));
        vectorCustomActionEditTextPreference.setOnPreferenceLongClickListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$7(applicationContext));
        Preference findPreference15 = findPreference(PreferencesManager.SETTINGS_OLM_VERSION_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference15, "findPreference(Preferenc…M_VERSION_PREFERENCE_KEY)");
        MXSession mXSession8 = this.mSession;
        if (mXSession8 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        findPreference15.setSummary(mXSession8.getCryptoVersion(applicationContext, false));
        Preference findPreference16 = findPreference(PreferencesManager.SETTINGS_APP_TERM_CONDITIONS_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference16, "findPreference(Preferenc…ONDITIONS_PREFERENCE_KEY)");
        findPreference16.setOnPreferenceClickListener(VectorSettingsPreferencesFragment$onCreate$17.INSTANCE);
        Preference findPreference17 = findPreference(PreferencesManager.SETTINGS_THIRD_PARTY_NOTICES_PREFERENCE_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference17, "findPreference(Preferenc…Y_NOTICES_PREFERENCE_KEY)");
        findPreference17.setOnPreferenceClickListener(VectorSettingsPreferencesFragment$onCreate$18.INSTANCE);
        Preference findPreference18 = findPreference(PreferencesManager.SETTINGS_MEDIA_SAVING_PERIOD_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference18, "it");
        findPreference18.setSummary(PreferencesManager.getSelectedMediasSavingPeriodString(getActivity()));
        findPreference18.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$8(this));
        Preference findPreference19 = findPreference(PreferencesManager.SETTINGS_CLEAR_MEDIA_CACHE_PREFERENCE_KEY);
        MXMediasCache.getCachesSize(getActivity(), new VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$9(findPreference19, this));
        Intrinsics.checkExpressionValueIsNotNull(findPreference19, "it");
        findPreference19.setOnPreferenceClickListener(new C2258x45ea6a7d(this));
        Preference findPreference20 = findPreference(PreferencesManager.SETTINGS_CLEAR_CACHE_PREFERENCE_KEY);
        MXSession.getApplicationSizeCaches(getActivity(), new C2261x45ea6a7e(findPreference20, this, applicationContext));
        Intrinsics.checkExpressionValueIsNotNull(findPreference20, "it");
        findPreference20.setOnPreferenceClickListener(new C2262x45ea6a7f(this, applicationContext));
        Preference findPreference21 = findPreference(PreferencesManager.SETTINGS_DEACTIVATE_ACCOUNT_KEY);
        Intrinsics.checkExpressionValueIsNotNull(findPreference21, "findPreference(Preferenc…S_DEACTIVATE_ACCOUNT_KEY)");
        findPreference21.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$onCreate$22(this));
    }

    @Nullable
    public View onCreateView(@NotNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        Intrinsics.checkParameterIsNotNull(layoutInflater, "inflater");
        View onCreateView = super.onCreateView(layoutInflater, viewGroup, bundle);
        if (onCreateView != null) {
            View findViewById = onCreateView.findViewById(16908298);
            if (findViewById != null) {
                findViewById.setPadding(0, 0, 0, 0);
            }
        }
        return onCreateView;
    }

    public void onSharedPreferenceChanged(@NotNull SharedPreferences sharedPreferences, @NotNull String str) {
        Intrinsics.checkParameterIsNotNull(sharedPreferences, "sharedPreferences");
        Intrinsics.checkParameterIsNotNull(str, "key");
        if (TextUtils.equals(str, ContactsManager.CONTACTS_BOOK_ACCESS_KEY)) {
            ContactsManager.getInstance().clearSnapshot();
        }
    }

    public void onResume() {
        super.onResume();
        this.mLoadingView = getView().findViewById(C1299R.C1301id.vector_settings_spinner_views);
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        if (mXSession.isAlive()) {
            Activity activity = getActivity();
            Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
            Context applicationContext = activity.getApplicationContext();
            MXSession mXSession2 = this.mSession;
            if (mXSession2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            mXSession2.getDataHandler().addListener(this.mEventsListener);
            Matrix instance = Matrix.getInstance(applicationContext);
            if (instance == null) {
                Intrinsics.throwNpe();
            }
            instance.addNetworkEventListener(this.mNetworkListener);
            MXSession mXSession3 = this.mSession;
            if (mXSession3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            mXSession3.getMyUser().refreshThirdPartyIdentifiers(new VectorSettingsPreferencesFragment$onResume$1(this));
            Matrix instance2 = Matrix.getInstance(applicationContext);
            if (instance2 == null) {
                Intrinsics.throwNpe();
            }
            GcmRegistrationManager sharedGCMRegistrationManager = instance2.getSharedGCMRegistrationManager();
            Matrix instance3 = Matrix.getInstance(applicationContext);
            if (instance3 == null) {
                Intrinsics.throwNpe();
            }
            sharedGCMRegistrationManager.refreshPushersList(instance3.getSessions(), new VectorSettingsPreferencesFragment$onResume$2(this));
            PreferenceManager.getDefaultSharedPreferences(applicationContext).registerOnSharedPreferenceChangeListener(this);
            refreshPreferences();
            refreshNotificationPrivacy();
            refreshDisplay();
            refreshBackgroundSyncPrefs();
        }
    }

    public void onPause() {
        super.onPause();
        Activity activity = getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        Context applicationContext = activity.getApplicationContext();
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        if (mXSession.isAlive()) {
            MXSession mXSession2 = this.mSession;
            if (mXSession2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            mXSession2.getDataHandler().removeListener(this.mEventsListener);
            Matrix instance = Matrix.getInstance(applicationContext);
            if (instance == null) {
                Intrinsics.throwNpe();
            }
            instance.removeNetworkEventListener(this.mNetworkListener);
        }
        PreferenceManager.getDefaultSharedPreferences(applicationContext).unregisterOnSharedPreferenceChangeListener(this);
    }

    /* access modifiers changed from: private */
    public final void displayLoadingView() {
        if (this.mLoadingView == null) {
            View view = getView();
            while (view != null && this.mLoadingView == null) {
                this.mLoadingView = view.findViewById(C1299R.C1301id.vector_settings_spinner_views);
                ViewParent parent = view.getParent();
                if (parent == null) {
                    throw new TypeCastException("null cannot be cast to non-null type android.view.View");
                }
                view = (View) parent;
            }
        }
        if (this.mLoadingView != null) {
            View view2 = this.mLoadingView;
            if (view2 == null) {
                Intrinsics.throwNpe();
            }
            view2.setVisibility(0);
        }
    }

    /* access modifiers changed from: private */
    public final void hideLoadingView() {
        if (this.mLoadingView != null) {
            View view = this.mLoadingView;
            if (view == null) {
                Intrinsics.throwNpe();
            }
            view.setVisibility(8);
        }
    }

    /* access modifiers changed from: private */
    public final void hideLoadingView(boolean z) {
        View view = this.mLoadingView;
        if (view == null) {
            Intrinsics.throwNpe();
        }
        view.setVisibility(8);
        if (z) {
            refreshDisplay();
        }
    }

    /* access modifiers changed from: private */
    public final void refreshDisplay() {
        boolean z;
        Matrix instance = Matrix.getInstance(getActivity());
        if (instance == null) {
            Intrinsics.throwNpe();
        }
        boolean isConnected = instance.isConnected();
        Activity activity = getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        Context applicationContext = activity.getApplicationContext();
        PreferenceManager preferenceManager = getPreferenceManager();
        getMUserAvatarPreference().refreshAvatar();
        getMUserAvatarPreference().setEnabled(isConnected);
        EditTextPreference mDisplayNamePreference = getMDisplayNamePreference();
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        mDisplayNamePreference.setSummary(mXSession.getMyUser().displayname);
        EditTextPreference mDisplayNamePreference2 = getMDisplayNamePreference();
        MXSession mXSession2 = this.mSession;
        if (mXSession2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        mDisplayNamePreference2.setText(mXSession2.getMyUser().displayname);
        getMDisplayNamePreference().setEnabled(isConnected);
        Preference mPasswordPreference = getMPasswordPreference();
        Intrinsics.checkExpressionValueIsNotNull(mPasswordPreference, "mPasswordPreference");
        mPasswordPreference.setEnabled(isConnected);
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext);
        MXSession mXSession3 = this.mSession;
        if (mXSession3 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        PushRuleSet pushRules = mXSession3.getDataHandler().pushRules();
        Matrix instance2 = Matrix.getInstance(applicationContext);
        if (instance2 == null) {
            Intrinsics.throwNpe();
        }
        GcmRegistrationManager sharedGCMRegistrationManager = instance2.getSharedGCMRegistrationManager();
        Iterator it = Companion.getMPushesRuleByResourceId().keySet().iterator();
        while (true) {
            z = true;
            if (!it.hasNext()) {
                break;
            }
            String str = (String) it.next();
            Preference findPreference = preferenceManager.findPreference(str);
            if (findPreference != null) {
                if (findPreference instanceof BingRulePreference) {
                    BingRulePreference bingRulePreference = (BingRulePreference) findPreference;
                    if (pushRules == null || !isConnected || !sharedGCMRegistrationManager.areDeviceNotificationsAllowed()) {
                        z = false;
                    }
                    bingRulePreference.setEnabled(z);
                    MXSession mXSession4 = this.mSession;
                    if (mXSession4 == null) {
                        Intrinsics.throwUninitializedPropertyAccessException("mSession");
                    }
                    bingRulePreference.setBingRule(mXSession4.getDataHandler().pushRules().findDefaultRule((String) Companion.getMPushesRuleByResourceId().get(str)));
                } else if (findPreference instanceof CheckBoxPreference) {
                    if (Intrinsics.areEqual((Object) str, (Object) PreferencesManager.SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY)) {
                        ((CheckBoxPreference) findPreference).setChecked(sharedGCMRegistrationManager.areDeviceNotificationsAllowed());
                    } else if (Intrinsics.areEqual((Object) str, (Object) PreferencesManager.SETTINGS_TURN_SCREEN_ON_PREFERENCE_KEY)) {
                        CheckBoxPreference checkBoxPreference = (CheckBoxPreference) findPreference;
                        Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "gcmMgr");
                        checkBoxPreference.setChecked(sharedGCMRegistrationManager.isScreenTurnedOn());
                        checkBoxPreference.setEnabled(sharedGCMRegistrationManager.areDeviceNotificationsAllowed());
                    } else {
                        CheckBoxPreference checkBoxPreference2 = (CheckBoxPreference) findPreference;
                        if (pushRules == null || !isConnected) {
                            z = false;
                        }
                        checkBoxPreference2.setEnabled(z);
                        checkBoxPreference2.setChecked(defaultSharedPreferences.getBoolean(str, false));
                    }
                }
            }
        }
        boolean z2 = (pushRules == null || pushRules.findDefaultRule(BingRule.RULE_ID_DISABLE_ALL) == null || !pushRules.findDefaultRule(BingRule.RULE_ID_DISABLE_ALL).isEnabled) ? false : true;
        Preference mRingtonePreference = getMRingtonePreference();
        Intrinsics.checkExpressionValueIsNotNull(mRingtonePreference, "mRingtonePreference");
        mRingtonePreference.setEnabled(!z2 && sharedGCMRegistrationManager.areDeviceNotificationsAllowed());
        Preference mNotificationPrivacyPreference = getMNotificationPrivacyPreference();
        Intrinsics.checkExpressionValueIsNotNull(mNotificationPrivacyPreference, "mNotificationPrivacyPreference");
        if (z2 || !sharedGCMRegistrationManager.areDeviceNotificationsAllowed() || !sharedGCMRegistrationManager.useGCM()) {
            z = false;
        }
        mNotificationPrivacyPreference.setEnabled(z);
    }

    private final void addButtons() {
        PreferenceCategory mUserSettingsCategory = getMUserSettingsCategory();
        EditTextPreference editTextPreference = new EditTextPreference(getActivity());
        editTextPreference.setTitle(C1299R.string.settings_add_email_address);
        editTextPreference.setDialogTitle(C1299R.string.settings_add_email_address);
        editTextPreference.setKey(ADD_EMAIL_PREFERENCE_KEY);
        ThemeUtils themeUtils = ThemeUtils.INSTANCE;
        Activity activity = getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        Context context = activity;
        Drawable drawable = ContextCompat.getDrawable(getActivity(), C1299R.C1300drawable.ic_add_black);
        if (drawable == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(drawable, "ContextCompat.getDrawabl….drawable.ic_add_black)!!");
        editTextPreference.setIcon(themeUtils.tintDrawable(context, drawable, C1299R.attr.settings_icon_tint_color));
        editTextPreference.setOrder(100);
        EditText editText = editTextPreference.getEditText();
        Intrinsics.checkExpressionValueIsNotNull(editText, "editText");
        editText.setInputType(33);
        editTextPreference.setOnPreferenceChangeListener(new C2256x10323f05(this));
        mUserSettingsCategory.addPreference(editTextPreference);
        PreferenceCategory mUserSettingsCategory2 = getMUserSettingsCategory();
        Preference preference = new Preference(getActivity());
        preference.setTitle(C1299R.string.settings_add_phone_number);
        preference.setKey(ADD_PHONE_NUMBER_PREFERENCE_KEY);
        ThemeUtils themeUtils2 = ThemeUtils.INSTANCE;
        Activity activity2 = getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity2, "activity");
        Context context2 = activity2;
        Drawable drawable2 = ContextCompat.getDrawable(getActivity(), C1299R.C1300drawable.ic_add_black);
        if (drawable2 == null) {
            Intrinsics.throwNpe();
        }
        Intrinsics.checkExpressionValueIsNotNull(drawable2, "ContextCompat.getDrawabl….drawable.ic_add_black)!!");
        preference.setIcon(themeUtils2.tintDrawable(context2, drawable2, C1299R.attr.settings_icon_tint_color));
        preference.setOrder(200);
        preference.setOnPreferenceClickListener(new C2257x10323f06(this));
        mUserSettingsCategory2.addPreference(preference);
    }

    /* access modifiers changed from: private */
    public final void onPasswordUpdateClick() {
        getActivity().runOnUiThread(new VectorSettingsPreferencesFragment$onPasswordUpdateClick$1(this));
    }

    /* access modifiers changed from: private */
    public final void onPushRuleClick(String str, boolean z) {
        Matrix instance = Matrix.getInstance(getActivity());
        if (instance == null) {
            Intrinsics.throwNpe();
        }
        GcmRegistrationManager sharedGCMRegistrationManager = instance.getSharedGCMRegistrationManager();
        String access$getLOG_TAG$p = Companion.getLOG_TAG();
        StringBuilder sb = new StringBuilder();
        sb.append("onPushRuleClick ");
        sb.append(str);
        sb.append(" : set to ");
        sb.append(z);
        Log.m209d(access$getLOG_TAG$p, sb.toString());
        if (Intrinsics.areEqual((Object) str, (Object) PreferencesManager.SETTINGS_TURN_SCREEN_ON_PREFERENCE_KEY)) {
            Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "gcmMgr");
            if (sharedGCMRegistrationManager.isScreenTurnedOn() != z) {
                sharedGCMRegistrationManager.setScreenTurnedOn(z);
            }
        } else if (Intrinsics.areEqual((Object) str, (Object) PreferencesManager.SETTINGS_ENABLE_THIS_DEVICE_PREFERENCE_KEY)) {
            Matrix instance2 = Matrix.getInstance(getActivity());
            if (instance2 == null) {
                Intrinsics.throwNpe();
            }
            boolean isConnected = instance2.isConnected();
            boolean areDeviceNotificationsAllowed = sharedGCMRegistrationManager.areDeviceNotificationsAllowed();
            if (areDeviceNotificationsAllowed != z) {
                sharedGCMRegistrationManager.setDeviceNotificationsAllowed(!areDeviceNotificationsAllowed);
                if (isConnected && sharedGCMRegistrationManager.useGCM()) {
                    Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "gcmMgr");
                    if (sharedGCMRegistrationManager.isServerRegistred() || sharedGCMRegistrationManager.isServerUnRegistred()) {
                        VectorSettingsPreferencesFragment$onPushRuleClick$listener$1 vectorSettingsPreferencesFragment$onPushRuleClick$listener$1 = new VectorSettingsPreferencesFragment$onPushRuleClick$listener$1(this, sharedGCMRegistrationManager, areDeviceNotificationsAllowed);
                        displayLoadingView();
                        if (sharedGCMRegistrationManager.isServerRegistred()) {
                            sharedGCMRegistrationManager.unregister(vectorSettingsPreferencesFragment$onPushRuleClick$listener$1);
                        } else {
                            sharedGCMRegistrationManager.register(vectorSettingsPreferencesFragment$onPushRuleClick$listener$1);
                        }
                    }
                }
            }
        } else {
            String str2 = (String) Companion.getMPushesRuleByResourceId().get(str);
            MXSession mXSession = this.mSession;
            if (mXSession == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            BingRule findDefaultRule = mXSession.getDataHandler().pushRules().findDefaultRule(str2);
            boolean z2 = findDefaultRule != null && findDefaultRule.isEnabled;
            CharSequence charSequence = str2;
            if (TextUtils.equals(charSequence, BingRule.RULE_ID_DISABLE_ALL) || TextUtils.equals(charSequence, BingRule.RULE_ID_SUPPRESS_BOTS_NOTIFICATIONS)) {
                z2 = !z2;
            }
            if (!(z == z2 || findDefaultRule == null)) {
                displayLoadingView();
                MXSession mXSession2 = this.mSession;
                if (mXSession2 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mSession");
                }
                MXDataHandler dataHandler = mXSession2.getDataHandler();
                Intrinsics.checkExpressionValueIsNotNull(dataHandler, "mSession.dataHandler");
                dataHandler.getBingRulesManager().updateEnableRuleStatus(findDefaultRule, !findDefaultRule.isEnabled, new VectorSettingsPreferencesFragment$onPushRuleClick$1(this));
            }
        }
    }

    /* access modifiers changed from: private */
    public final void onDisplayNameClick(String str) {
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        if (!TextUtils.equals(mXSession.getMyUser().displayname, str)) {
            displayLoadingView();
            MXSession mXSession2 = this.mSession;
            if (mXSession2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            mXSession2.getMyUser().updateDisplayName(str, new VectorSettingsPreferencesFragment$onDisplayNameClick$1(this, str));
        }
    }

    /* access modifiers changed from: private */
    public final void onUpdateAvatarClick() {
        getActivity().runOnUiThread(new VectorSettingsPreferencesFragment$onUpdateAvatarClick$1(this));
    }

    private final void refreshNotificationRingTone() {
        Preference mRingtonePreference = getMRingtonePreference();
        Intrinsics.checkExpressionValueIsNotNull(mRingtonePreference, "mRingtonePreference");
        mRingtonePreference.setSummary(PreferencesManager.getNotificationRingToneName(getActivity()));
    }

    private final void refreshNotificationPrivacy() {
        Matrix instance = Matrix.getInstance(getActivity());
        if (instance == null) {
            Intrinsics.throwNpe();
        }
        GcmRegistrationManager sharedGCMRegistrationManager = instance.getSharedGCMRegistrationManager();
        if (sharedGCMRegistrationManager.useGCM()) {
            Activity activity = getActivity();
            Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
            Context applicationContext = activity.getApplicationContext();
            Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "gcmRegistrationManager");
            String notificationPrivacyString = NotificationPrivacyActivity.getNotificationPrivacyString(applicationContext, sharedGCMRegistrationManager.getNotificationPrivacy());
            Preference mNotificationPrivacyPreference = getMNotificationPrivacyPreference();
            Intrinsics.checkExpressionValueIsNotNull(mNotificationPrivacyPreference, "mNotificationPrivacyPreference");
            mNotificationPrivacyPreference.setSummary(notificationPrivacyString);
            return;
        }
        getNotificationsSettingsCategory().removePreference(getMNotificationPrivacyPreference());
    }

    public void onActivityResult(int i, int i2, @Nullable Intent intent) {
        super.onActivityResult(i, i2, intent);
        if (i2 == -1) {
            Parcelable parcelable = null;
            if (i == 1) {
                Context activity = getActivity();
                MXSession mXSession = this.mSession;
                if (mXSession == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mSession");
                }
                Uri thumbnailUriFromIntent = VectorUtils.getThumbnailUriFromIntent(activity, intent, mXSession.getMediasCache());
                if (thumbnailUriFromIntent != null) {
                    displayLoadingView();
                    Resource openResource = ResourceUtils.openResource(getActivity(), thumbnailUriFromIntent, null);
                    if (openResource != null) {
                        MXSession mXSession2 = this.mSession;
                        if (mXSession2 == null) {
                            Intrinsics.throwUninitializedPropertyAccessException("mSession");
                        }
                        mXSession2.getMediasCache().uploadContent(openResource.mContentStream, null, openResource.mMimeType, null, new VectorSettingsPreferencesFragment$onActivityResult$1(this));
                    }
                }
            } else if (i == REQUEST_E2E_FILE_REQUEST_CODE) {
                importKeys(intent);
            } else if (i == REQUEST_NEW_PHONE_NUMBER) {
                refreshPhoneNumbersList();
            } else if (i == REQUEST_LOCALE) {
                Activity activity2 = getActivity();
                Intrinsics.checkExpressionValueIsNotNull(activity2, "activity");
                startActivity(activity2.getIntent());
                getActivity().finish();
            } else if (i == REQUEST_PHONEBOOK_COUNTRY) {
                onPhonebookCountryUpdate(intent);
            } else if (i == REQUEST_NOTIFICATION_RINGTONE) {
                Context activity3 = getActivity();
                if (intent != null) {
                    parcelable = intent.getParcelableExtra("android.intent.extra.ringtone.PICKED_URI");
                }
                if (parcelable == null) {
                    throw new TypeCastException("null cannot be cast to non-null type android.net.Uri");
                }
                PreferencesManager.setNotificationRingTone(activity3, (Uri) parcelable);
                if (PreferencesManager.getNotificationRingToneName(getActivity()) == null) {
                    PreferencesManager.setNotificationRingTone(getActivity(), PreferencesManager.getNotificationRingTone(getActivity()));
                }
                refreshNotificationRingTone();
            }
        }
    }

    /* access modifiers changed from: private */
    public final void refreshPreferences() {
        SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…aredPreferences(activity)");
        Editor edit = defaultSharedPreferences.edit();
        Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
        String str = PreferencesManager.SETTINGS_DISPLAY_NAME_PREFERENCE_KEY;
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        edit.putString(str, mXSession.getMyUser().displayname);
        edit.putString(PreferencesManager.SETTINGS_VERSION_PREFERENCE_KEY, VectorUtils.getApplicationVersion(getActivity()));
        MXSession mXSession2 = this.mSession;
        if (mXSession2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        PushRuleSet pushRules = mXSession2.getDataHandler().pushRules();
        if (pushRules != null) {
            for (String str2 : Companion.getMPushesRuleByResourceId().keySet()) {
                Preference findPreference = findPreference(str2);
                if (findPreference != null && (findPreference instanceof CheckBoxPreference)) {
                    String str3 = (String) Companion.getMPushesRuleByResourceId().get(str2);
                    BingRule findDefaultRule = pushRules.findDefaultRule(str3);
                    boolean z = false;
                    boolean z2 = findDefaultRule != null && findDefaultRule.isEnabled;
                    CharSequence charSequence = str3;
                    if (TextUtils.equals(charSequence, BingRule.RULE_ID_DISABLE_ALL) || TextUtils.equals(charSequence, BingRule.RULE_ID_SUPPRESS_BOTS_NOTIFICATIONS)) {
                        z = !z2;
                    } else {
                        if (z2) {
                            if (findDefaultRule == null) {
                                Intrinsics.throwNpe();
                            }
                            List<Object> list = findDefaultRule.actions;
                            if (list != null && !list.isEmpty()) {
                                if (1 == list.size()) {
                                    try {
                                        Object obj = list.get(0);
                                        if (obj == null) {
                                            throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
                                        }
                                        z = !TextUtils.equals((String) obj, BingRule.ACTION_DONT_NOTIFY);
                                    } catch (Exception e) {
                                        String access$getLOG_TAG$p = Companion.getLOG_TAG();
                                        StringBuilder sb = new StringBuilder();
                                        sb.append("## refreshPreferences failed ");
                                        sb.append(e.getMessage());
                                        Log.m211e(access$getLOG_TAG$p, sb.toString());
                                    }
                                }
                            }
                        }
                        z = z2;
                    }
                    edit.putBoolean(str2, z);
                }
            }
        }
        edit.apply();
    }

    /* access modifiers changed from: private */
    public final void displayDelete3PIDConfirmationDialog(ThirdPartyIdentifier thirdPartyIdentifier, CharSequence charSequence) {
        String mediumFriendlyName = ThreePid.getMediumFriendlyName(thirdPartyIdentifier.medium, getActivity());
        Intrinsics.checkExpressionValueIsNotNull(mediumFriendlyName, "ThreePid.getMediumFriend…ame(pid.medium, activity)");
        Locale applicationLocale = VectorApp.getApplicationLocale();
        Intrinsics.checkExpressionValueIsNotNull(applicationLocale, "VectorApp.getApplicationLocale()");
        if (mediumFriendlyName == null) {
            throw new TypeCastException("null cannot be cast to non-null type java.lang.String");
        }
        String lowerCase = mediumFriendlyName.toLowerCase(applicationLocale);
        Intrinsics.checkExpressionValueIsNotNull(lowerCase, "(this as java.lang.String).toLowerCase(locale)");
        new Builder(getActivity()).setTitle(C1299R.string.dialog_title_confirmation).setMessage(getString(C1299R.string.settings_delete_threepid_confirmation, new Object[]{lowerCase, charSequence})).setPositiveButton(C1299R.string.remove, new C2272xbdbb6d(this, thirdPartyIdentifier)).setNegativeButton(C1299R.string.cancel, C2274xbdbb6e.INSTANCE).create().show();
    }

    private final void refreshIgnoredUsersList() {
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        MXDataHandler dataHandler = mXSession.getDataHandler();
        Intrinsics.checkExpressionValueIsNotNull(dataHandler, "mSession.dataHandler");
        List<String> ignoredUserIds = dataHandler.getIgnoredUserIds();
        Intrinsics.checkExpressionValueIsNotNull(ignoredUserIds, "ignoredUsersList");
        CollectionsKt.sortWith(ignoredUserIds, VectorSettingsPreferencesFragment$refreshIgnoredUsersList$1.INSTANCE);
        PreferenceScreen preferenceScreen = getPreferenceScreen();
        preferenceScreen.removePreference(getMIgnoredUserSettingsCategory());
        preferenceScreen.removePreference(getMIgnoredUserSettingsCategoryDivider());
        getMIgnoredUserSettingsCategory().removeAll();
        if (ignoredUserIds.size() > 0) {
            preferenceScreen.addPreference(getMIgnoredUserSettingsCategoryDivider());
            preferenceScreen.addPreference(getMIgnoredUserSettingsCategory());
            for (String str : ignoredUserIds) {
                VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference(getActivity());
                vectorCustomActionEditTextPreference.setTitle(str);
                StringBuilder sb = new StringBuilder();
                sb.append(IGNORED_USER_KEY_BASE);
                sb.append(str);
                vectorCustomActionEditTextPreference.setKey(sb.toString());
                vectorCustomActionEditTextPreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$refreshIgnoredUsersList$2(this, str));
                getMIgnoredUserSettingsCategory().addPreference(vectorCustomActionEditTextPreference);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void refreshPushersList() {
        Matrix instance = Matrix.getInstance(getActivity());
        if (instance == null) {
            Intrinsics.throwNpe();
        }
        GcmRegistrationManager sharedGCMRegistrationManager = instance.getSharedGCMRegistrationManager();
        ArrayList<Pusher> arrayList = new ArrayList<>(sharedGCMRegistrationManager.mPushersList);
        if (arrayList.isEmpty()) {
            getPreferenceScreen().removePreference(getMPushersSettingsCategory());
            getPreferenceScreen().removePreference(getMPushersSettingsDivider());
            return;
        }
        boolean z = true;
        if (arrayList.size() == this.mDisplayedPushers.size()) {
            z = true ^ this.mDisplayedPushers.containsAll(arrayList);
        }
        if (z) {
            getMPushersSettingsCategory().removeAll();
            this.mDisplayedPushers = arrayList;
            int i = 0;
            Iterator it = this.mDisplayedPushers.iterator();
            while (it.hasNext()) {
                Pusher pusher = (Pusher) it.next();
                if (pusher.lang != null) {
                    Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "gcmRegistrationManager");
                    boolean equals = TextUtils.equals(sharedGCMRegistrationManager.getCurrentRegistrationToken(), pusher.pushkey);
                    VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference((Context) getActivity(), equals ? 1 : 0);
                    vectorCustomActionEditTextPreference.setTitle(pusher.deviceDisplayName);
                    vectorCustomActionEditTextPreference.setSummary(pusher.appDisplayName);
                    StringBuilder sb = new StringBuilder();
                    sb.append(PUSHER_PREFERENCE_KEY_BASE);
                    sb.append(i);
                    vectorCustomActionEditTextPreference.setKey(sb.toString());
                    i++;
                    getMPushersSettingsCategory().addPreference(vectorCustomActionEditTextPreference);
                    if (!equals) {
                        vectorCustomActionEditTextPreference.setOnPreferenceLongClickListener(new VectorSettingsPreferencesFragment$refreshPushersList$1(this, sharedGCMRegistrationManager, pusher));
                    }
                }
            }
        }
    }

    /* access modifiers changed from: private */
    public final void refreshEmailsList() {
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        ArrayList arrayList = new ArrayList(mXSession.getMyUser().getlinkedEmails());
        ArrayList<String> arrayList2 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(((ThirdPartyIdentifier) it.next()).address);
        }
        if (arrayList2.size() == this.mDisplayedEmails.size() ? !this.mDisplayedEmails.containsAll(arrayList2) : true) {
            VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment = this;
            int i = 0;
            int i2 = 0;
            while (true) {
                PreferenceCategory mUserSettingsCategory = vectorSettingsPreferencesFragment.getMUserSettingsCategory();
                StringBuilder sb = new StringBuilder();
                sb.append(EMAIL_PREFERENCE_KEY_BASE);
                sb.append(i2);
                Preference findPreference = mUserSettingsCategory.findPreference(sb.toString());
                if (findPreference == null) {
                    break;
                }
                vectorSettingsPreferencesFragment.getMUserSettingsCategory().removePreference(findPreference);
                i2++;
            }
            this.mDisplayedEmails = arrayList2;
            Preference findPreference2 = getMUserSettingsCategory().findPreference(ADD_EMAIL_PREFERENCE_KEY);
            if (findPreference2 != null) {
                int order = findPreference2.getOrder();
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    ThirdPartyIdentifier thirdPartyIdentifier = (ThirdPartyIdentifier) it2.next();
                    VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference(getActivity());
                    vectorCustomActionEditTextPreference.setTitle(getString(C1299R.string.settings_email_address));
                    vectorCustomActionEditTextPreference.setSummary(thirdPartyIdentifier.address);
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append(EMAIL_PREFERENCE_KEY_BASE);
                    sb2.append(i);
                    vectorCustomActionEditTextPreference.setKey(sb2.toString());
                    vectorCustomActionEditTextPreference.setOrder(order);
                    vectorCustomActionEditTextPreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$refreshEmailsList$2(this, thirdPartyIdentifier));
                    vectorCustomActionEditTextPreference.setOnPreferenceLongClickListener(new VectorSettingsPreferencesFragment$refreshEmailsList$3(this, thirdPartyIdentifier));
                    getMUserSettingsCategory().addPreference(vectorCustomActionEditTextPreference);
                    i++;
                    order++;
                }
                findPreference2.setOrder(order);
            }
        }
    }

    /* access modifiers changed from: private */
    public final void onCommonDone(String str) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(new VectorSettingsPreferencesFragment$onCommonDone$1(this, str));
        }
    }

    /* access modifiers changed from: private */
    public final void addEmail(String str) {
        CharSequence charSequence = str;
        if (!TextUtils.isEmpty(charSequence)) {
            Pattern pattern = Patterns.EMAIL_ADDRESS;
            if (str == null) {
                Intrinsics.throwNpe();
            }
            if (pattern.matcher(charSequence).matches()) {
                if (this.mDisplayedEmails.indexOf(str) >= 0) {
                    Toast.makeText(getActivity(), getString(C1299R.string.auth_email_already_defined), 0).show();
                    return;
                }
                ThreePid threePid = new ThreePid(str, "email");
                displayLoadingView();
                MXSession mXSession = this.mSession;
                if (mXSession == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mSession");
                }
                mXSession.getMyUser().requestEmailValidationToken(threePid, new VectorSettingsPreferencesFragment$addEmail$1(this, threePid));
                return;
            }
        }
        Toast.makeText(getActivity(), getString(C1299R.string.auth_invalid_email), 0).show();
    }

    /* access modifiers changed from: private */
    public final void showEmailValidationDialog(ThreePid threePid) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(C1299R.string.account_email_validation_title);
        builder.setMessage(C1299R.string.account_email_validation_message);
        builder.setPositiveButton(C1299R.string._continue, new VectorSettingsPreferencesFragment$showEmailValidationDialog$1(this, threePid));
        builder.setNegativeButton(C1299R.string.cancel, new VectorSettingsPreferencesFragment$showEmailValidationDialog$2(this));
        builder.create().show();
    }

    /* access modifiers changed from: private */
    public final void refreshPhoneNumbersList() {
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        ArrayList arrayList = new ArrayList(mXSession.getMyUser().getlinkedPhoneNumbers());
        ArrayList<String> arrayList2 = new ArrayList<>();
        Iterator it = arrayList.iterator();
        while (it.hasNext()) {
            arrayList2.add(((ThirdPartyIdentifier) it.next()).address);
        }
        if (arrayList2.size() == this.mDisplayedPhoneNumber.size() ? !this.mDisplayedPhoneNumber.containsAll(arrayList2) : true) {
            VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment = this;
            int i = 0;
            int i2 = 0;
            while (true) {
                PreferenceCategory mUserSettingsCategory = vectorSettingsPreferencesFragment.getMUserSettingsCategory();
                StringBuilder sb = new StringBuilder();
                sb.append(PHONE_NUMBER_PREFERENCE_KEY_BASE);
                sb.append(i2);
                Preference findPreference = mUserSettingsCategory.findPreference(sb.toString());
                if (findPreference == null) {
                    break;
                }
                vectorSettingsPreferencesFragment.getMUserSettingsCategory().removePreference(findPreference);
                i2++;
            }
            this.mDisplayedPhoneNumber = arrayList2;
            Preference findPreference2 = getMUserSettingsCategory().findPreference(ADD_PHONE_NUMBER_PREFERENCE_KEY);
            if (findPreference2 != null) {
                int order = findPreference2.getOrder();
                Iterator it2 = arrayList.iterator();
                while (it2.hasNext()) {
                    ThirdPartyIdentifier thirdPartyIdentifier = (ThirdPartyIdentifier) it2.next();
                    VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference(getActivity());
                    vectorCustomActionEditTextPreference.setTitle(getString(C1299R.string.settings_phone_number));
                    String str = thirdPartyIdentifier.address;
                    try {
                        PhoneNumberUtil instance = PhoneNumberUtil.getInstance();
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append('+');
                        sb2.append(str);
                        str = PhoneNumberUtil.getInstance().format(instance.parse(sb2.toString(), null), PhoneNumberFormat.INTERNATIONAL);
                    } catch (NumberParseException unused) {
                    }
                    vectorCustomActionEditTextPreference.setSummary(str);
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append(PHONE_NUMBER_PREFERENCE_KEY_BASE);
                    sb3.append(i);
                    vectorCustomActionEditTextPreference.setKey(sb3.toString());
                    vectorCustomActionEditTextPreference.setOrder(order);
                    vectorCustomActionEditTextPreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$refreshPhoneNumbersList$2(this, thirdPartyIdentifier));
                    vectorCustomActionEditTextPreference.setOnPreferenceLongClickListener(new VectorSettingsPreferencesFragment$refreshPhoneNumbersList$3(this, thirdPartyIdentifier));
                    i++;
                    order++;
                    getMUserSettingsCategory().addPreference(vectorCustomActionEditTextPreference);
                }
                findPreference2.setOrder(order);
            }
        }
    }

    private final void setContactsPreferences() {
        if (VERSION.SDK_INT >= 23) {
            getMContactSettingsCategory().removePreference(findPreference(ContactsManager.CONTACTS_BOOK_ACCESS_KEY));
        }
        getMContactPhonebookCountryPreference().setSummary(PhoneNumberUtils.getHumanCountryCode(PhoneNumberUtils.getCountryCode(getActivity())));
        getMContactPhonebookCountryPreference().setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$setContactsPreferences$1(this));
    }

    private final void onPhonebookCountryUpdate(Intent intent) {
        if (intent != null && intent.hasExtra(CountryPickerActivity.EXTRA_OUT_COUNTRY_NAME) && intent.hasExtra(CountryPickerActivity.EXTRA_OUT_COUNTRY_CODE)) {
            String stringExtra = intent.getStringExtra(CountryPickerActivity.EXTRA_OUT_COUNTRY_CODE);
            if (!TextUtils.equals(stringExtra, PhoneNumberUtils.getCountryCode(getActivity()))) {
                PhoneNumberUtils.setCountryCode(getActivity(), stringExtra);
                getMContactPhonebookCountryPreference().setSummary(intent.getStringExtra(CountryPickerActivity.EXTRA_OUT_COUNTRY_NAME));
            }
        }
    }

    private final void setUserInterfacePreferences() {
        getSelectedLanguagePreference().setSummary(VectorApp.localeToLocalisedString(VectorApp.getApplicationLocale()));
        getSelectedLanguagePreference().setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$setUserInterfacePreferences$1(this));
        getTextSizePreference().setSummary(FontScale.INSTANCE.getFontScaleDescription());
        getTextSizePreference().setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$setUserInterfacePreferences$2(this));
    }

    /* access modifiers changed from: private */
    public final void displayTextSizeSelection(Activity activity) {
        Builder builder = new Builder(activity);
        View inflate = activity.getLayoutInflater().inflate(C1299R.layout.text_size_selection, null);
        builder.setTitle(C1299R.string.font_size);
        builder.setView(inflate);
        builder.setPositiveButton(C1299R.string.f115ok, VectorSettingsPreferencesFragment$displayTextSizeSelection$1.INSTANCE);
        builder.setNegativeButton(C1299R.string.cancel, VectorSettingsPreferencesFragment$displayTextSizeSelection$2.INSTANCE);
        AlertDialog create = builder.create();
        create.show();
        LinearLayout linearLayout = (LinearLayout) inflate.findViewById(C1299R.C1301id.text_selection_group_view);
        Intrinsics.checkExpressionValueIsNotNull(linearLayout, "linearLayout");
        int childCount = linearLayout.getChildCount();
        String fontScalePrefValue = FontScale.INSTANCE.getFontScalePrefValue();
        for (int i = 0; i < childCount; i++) {
            View childAt = linearLayout.getChildAt(i);
            if (childAt instanceof CheckedTextView) {
                CheckedTextView checkedTextView = (CheckedTextView) childAt;
                checkedTextView.setChecked(TextUtils.equals(checkedTextView.getText(), fontScalePrefValue));
                childAt.setOnClickListener(new VectorSettingsPreferencesFragment$displayTextSizeSelection$3(create, childAt, activity));
            }
        }
    }

    private final String secondsToText(int i) {
        if (i > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(i));
            sb.append(StringUtils.SPACE);
            sb.append(getString(C1299R.string.settings_seconds));
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append(String.valueOf(i));
        sb2.append(StringUtils.SPACE);
        sb2.append(getString(C1299R.string.settings_second));
        return sb2.toString();
    }

    /* access modifiers changed from: private */
    public final void refreshBackgroundSyncPrefs() {
        if (getActivity() != null) {
            Matrix instance = Matrix.getInstance(getActivity());
            if (instance == null) {
                Intrinsics.throwNpe();
            }
            GcmRegistrationManager sharedGCMRegistrationManager = instance.getSharedGCMRegistrationManager();
            Intrinsics.checkExpressionValueIsNotNull(sharedGCMRegistrationManager, "gcmmgr");
            int backgroundSyncTimeOut = sharedGCMRegistrationManager.getBackgroundSyncTimeOut() / 1000;
            int backgroundSyncDelay = sharedGCMRegistrationManager.getBackgroundSyncDelay() / 1000;
            SharedPreferences defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            Intrinsics.checkExpressionValueIsNotNull(defaultSharedPreferences, "PreferenceManager.getDef…aredPreferences(activity)");
            Editor edit = defaultSharedPreferences.edit();
            Intrinsics.checkExpressionValueIsNotNull(edit, "editor");
            String str = PreferencesManager.SETTINGS_SET_SYNC_TIMEOUT_PREFERENCE_KEY;
            StringBuilder sb = new StringBuilder();
            sb.append(String.valueOf(backgroundSyncTimeOut));
            sb.append("");
            edit.putString(str, sb.toString());
            String str2 = PreferencesManager.SETTINGS_SET_SYNC_DELAY_PREFERENCE_KEY;
            StringBuilder sb2 = new StringBuilder();
            sb2.append(String.valueOf(backgroundSyncDelay));
            sb2.append("");
            edit.putString(str2, sb2.toString());
            edit.apply();
            EditTextPreference mSyncRequestTimeoutPreference = getMSyncRequestTimeoutPreference();
            if (mSyncRequestTimeoutPreference != null) {
                mSyncRequestTimeoutPreference.setSummary(secondsToText(backgroundSyncTimeOut));
                StringBuilder sb3 = new StringBuilder();
                sb3.append(String.valueOf(backgroundSyncTimeOut));
                sb3.append("");
                mSyncRequestTimeoutPreference.setText(sb3.toString());
                mSyncRequestTimeoutPreference.setOnPreferenceChangeListener(new C2266x24e1f184(this, backgroundSyncTimeOut, sharedGCMRegistrationManager));
            }
            EditTextPreference mSyncRequestDelayPreference = getMSyncRequestDelayPreference();
            if (mSyncRequestDelayPreference != null) {
                mSyncRequestDelayPreference.setSummary(secondsToText(backgroundSyncDelay));
                StringBuilder sb4 = new StringBuilder();
                sb4.append(String.valueOf(backgroundSyncDelay));
                sb4.append("");
                mSyncRequestDelayPreference.setText(sb4.toString());
                mSyncRequestDelayPreference.setOnPreferenceChangeListener(new C2268x24e1f185(this, backgroundSyncDelay, sharedGCMRegistrationManager));
            }
        }
    }

    private final void removeCryptographyPreference() {
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().removePreference(getMCryptographyCategory());
            getPreferenceScreen().removePreference(getMCryptographyCategoryDivider());
        }
    }

    private final void refreshCryptographyPreference(DeviceInfo deviceInfo) {
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        String myUserId = mXSession.getMyUserId();
        MXSession mXSession2 = this.mSession;
        if (mXSession2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        String str = mXSession2.getCredentials().deviceId;
        if (deviceInfo != null && !TextUtils.isEmpty(deviceInfo.display_name)) {
            getCryptoInfoDeviceNamePreference().setSummary(deviceInfo.display_name);
            getCryptoInfoDeviceNamePreference().setOnPreferenceClickListener(new C2302x44133ed(this, deviceInfo));
            getCryptoInfoDeviceNamePreference().setOnPreferenceLongClickListener(new C2303x44133ee(this, deviceInfo));
        }
        CharSequence charSequence = str;
        if (!TextUtils.isEmpty(charSequence)) {
            getCryptoInfoDeviceIdPreference().setSummary(charSequence);
            getCryptoInfoDeviceIdPreference().setOnPreferenceLongClickListener(new C2304x44133ef(this, str));
            getExportPref().setOnPreferenceClickListener(new C2305x44133f0(this));
            getImportPref().setOnPreferenceClickListener(new C2306x44133f1(this));
        }
        if (!TextUtils.isEmpty(charSequence) && !TextUtils.isEmpty(myUserId)) {
            MXSession mXSession3 = this.mSession;
            if (mXSession3 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            mXSession3.getCrypto().getDeviceInfo(myUserId, str, new C2307x44133f2(this));
        }
        getSendToUnverifiedDevicesPref().setChecked(false);
        MXSession mXSession4 = this.mSession;
        if (mXSession4 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        mXSession4.getCrypto().getGlobalBlacklistUnverifiedDevices(new C2309x44133f3(this));
        getSendToUnverifiedDevicesPref().setOnPreferenceClickListener(new C2310x44133f4(this));
    }

    /* access modifiers changed from: private */
    public final void removeDevicesPreference() {
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().removePreference(getMDevicesListSettingsCategory());
            getPreferenceScreen().removePreference(getMDevicesListSettingsCategoryDivider());
        }
    }

    /* access modifiers changed from: private */
    public final void refreshDevicesList() {
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        if (mXSession.isCryptoEnabled()) {
            MXSession mXSession2 = this.mSession;
            if (mXSession2 == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            if (!TextUtils.isEmpty(mXSession2.getCredentials().deviceId)) {
                if (getMDevicesListSettingsCategory().getPreferenceCount() == 0) {
                    getMDevicesListSettingsCategory().addPreference(new ProgressBarPreference(getActivity()));
                }
                MXSession mXSession3 = this.mSession;
                if (mXSession3 == null) {
                    Intrinsics.throwUninitializedPropertyAccessException("mSession");
                }
                mXSession3.getDevicesList(new VectorSettingsPreferencesFragment$refreshDevicesList$1(this));
                return;
            }
        }
        removeDevicesPreference();
        removeCryptographyPreference();
    }

    /* access modifiers changed from: private */
    public final void buildDevicesSettings(List<? extends DeviceInfo> list) {
        int i;
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        String str = mXSession.getCredentials().deviceId;
        if (list.size() == this.mDevicesNameList.size() ? !this.mDevicesNameList.containsAll(list) : true) {
            this.mDevicesNameList = list;
            DeviceInfo.sortByLastSeen(this.mDevicesNameList);
            getMDevicesListSettingsCategory().removeAll();
            int i2 = 0;
            for (DeviceInfo deviceInfo : this.mDevicesNameList) {
                if (str == null || !Intrinsics.areEqual((Object) str, (Object) deviceInfo.device_id)) {
                    i = 0;
                } else {
                    this.mMyDeviceInfo = deviceInfo;
                    i = 1;
                }
                VectorCustomActionEditTextPreference vectorCustomActionEditTextPreference = new VectorCustomActionEditTextPreference((Context) getActivity(), i);
                if (deviceInfo.device_id != null || deviceInfo.display_name != null) {
                    if (deviceInfo.device_id != null) {
                        vectorCustomActionEditTextPreference.setTitle(deviceInfo.device_id);
                    }
                    if (deviceInfo.display_name != null) {
                        vectorCustomActionEditTextPreference.setSummary(deviceInfo.display_name);
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(DEVICES_PREFERENCE_KEY_BASE);
                    sb.append(i2);
                    vectorCustomActionEditTextPreference.setKey(sb.toString());
                    i2++;
                    vectorCustomActionEditTextPreference.setOnPreferenceClickListener(new VectorSettingsPreferencesFragment$buildDevicesSettings$1(this, deviceInfo));
                    getMDevicesListSettingsCategory().addPreference(vectorCustomActionEditTextPreference);
                }
            }
            refreshCryptographyPreference(this.mMyDeviceInfo);
        }
    }

    /* access modifiers changed from: private */
    public final void displayDeviceDetailsDialog(DeviceInfo deviceInfo) {
        android.support.p003v7.app.AlertDialog.Builder builder = new android.support.p003v7.app.AlertDialog.Builder(getActivity());
        Activity activity = getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        View inflate = activity.getLayoutInflater().inflate(C1299R.layout.devices_details_settings, null);
        if (deviceInfo != null) {
            TextView textView = (TextView) inflate.findViewById(C1299R.C1301id.device_id);
            Intrinsics.checkExpressionValueIsNotNull(textView, "textView");
            textView.setText(deviceInfo.device_id);
            TextView textView2 = (TextView) inflate.findViewById(C1299R.C1301id.device_name);
            String str = TextUtils.isEmpty(deviceInfo.display_name) ? LABEL_UNAVAILABLE_DATA : deviceInfo.display_name;
            Intrinsics.checkExpressionValueIsNotNull(textView2, "textView");
            textView2.setText(str);
            TextView textView3 = (TextView) inflate.findViewById(C1299R.C1301id.device_last_seen);
            if (!TextUtils.isEmpty(deviceInfo.last_seen_ip)) {
                String str2 = deviceInfo.last_seen_ip;
                String str3 = LABEL_UNAVAILABLE_DATA;
                if (getActivity() != null) {
                    String format = new SimpleDateFormat(getString(C1299R.string.devices_details_time_format)).format(new Date(deviceInfo.last_seen_ts));
                    DateFormat dateInstance = DateFormat.getDateInstance(3, Locale.getDefault());
                    StringBuilder sb = new StringBuilder();
                    sb.append(dateInstance.format(new Date(deviceInfo.last_seen_ts)));
                    sb.append(", ");
                    sb.append(format);
                    str3 = sb.toString();
                }
                String string = getString(C1299R.string.devices_details_last_seen_format, new Object[]{str2, str3});
                Intrinsics.checkExpressionValueIsNotNull(textView3, "textView");
                textView3.setText(string);
            } else {
                View findViewById = inflate.findViewById(C1299R.C1301id.device_last_seen_title);
                Intrinsics.checkExpressionValueIsNotNull(findViewById, "layout.findViewById<View…d.device_last_seen_title)");
                findViewById.setVisibility(8);
                Intrinsics.checkExpressionValueIsNotNull(textView3, "textView");
                textView3.setVisibility(8);
            }
            builder.setTitle((int) C1299R.string.devices_details_dialog_title);
            builder.setIcon(17301659);
            builder.setView(inflate);
            builder.setPositiveButton((int) C1299R.string.rename, (OnClickListener) new VectorSettingsPreferencesFragment$displayDeviceDetailsDialog$1(this, deviceInfo));
            MXSession mXSession = this.mSession;
            if (mXSession == null) {
                Intrinsics.throwUninitializedPropertyAccessException("mSession");
            }
            MXCrypto crypto = mXSession.getCrypto();
            Intrinsics.checkExpressionValueIsNotNull(crypto, "mSession.crypto");
            if (!TextUtils.equals(crypto.getMyDevice().deviceId, deviceInfo.device_id)) {
                builder.setNegativeButton((int) C1299R.string.delete, (OnClickListener) new VectorSettingsPreferencesFragment$displayDeviceDetailsDialog$2(this, deviceInfo));
            }
            builder.setNeutralButton((int) C1299R.string.cancel, (OnClickListener) VectorSettingsPreferencesFragment$displayDeviceDetailsDialog$3.INSTANCE);
            builder.setOnKeyListener(VectorSettingsPreferencesFragment$displayDeviceDetailsDialog$4.INSTANCE);
            builder.create().show();
            return;
        }
        Log.m211e(Companion.getLOG_TAG(), "## displayDeviceDetailsDialog(): sanity check failure");
        Activity activity2 = getActivity();
        if (activity2 != null) {
            Context applicationContext = activity2.getApplicationContext();
            if (applicationContext != null) {
                Toast makeText = Toast.makeText(applicationContext, "DeviceDetailsDialog cannot be displayed.\nBad input parameters.", 0);
                makeText.show();
                Intrinsics.checkExpressionValueIsNotNull(makeText, "Toast.makeText(this, tex…uration).apply { show() }");
            }
        }
    }

    /* access modifiers changed from: private */
    public final void displayDeviceRenameDialog(DeviceInfo deviceInfo) {
        EditText editText = new EditText(getActivity());
        editText.setText(deviceInfo.display_name);
        new Builder(getActivity()).setTitle(C1299R.string.devices_details_device_name).setView(editText).setPositiveButton(C1299R.string.f115ok, new VectorSettingsPreferencesFragment$displayDeviceRenameDialog$1(this, deviceInfo, editText)).setNegativeButton(C1299R.string.cancel, VectorSettingsPreferencesFragment$displayDeviceRenameDialog$2.INSTANCE).show();
    }

    /* access modifiers changed from: private */
    public final void deleteDevice(String str) {
        displayLoadingView();
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        mXSession.deleteDevice(str, this.mAccountPassword, new VectorSettingsPreferencesFragment$deleteDevice$1(this));
    }

    /* access modifiers changed from: private */
    public final void displayDeviceDeletionDialog(DeviceInfo deviceInfo) {
        if (deviceInfo == null || deviceInfo.device_id == null) {
            Log.m211e(Companion.getLOG_TAG(), "## displayDeviceDeletionDialog(): sanity check failure");
        } else if (!TextUtils.isEmpty(this.mAccountPassword)) {
            String str = deviceInfo.device_id;
            Intrinsics.checkExpressionValueIsNotNull(str, "aDeviceInfoToDelete.device_id");
            deleteDevice(str);
        } else {
            Activity activity = getActivity();
            Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
            View inflate = activity.getLayoutInflater().inflate(C1299R.layout.devices_settings_delete, null);
            new android.support.p003v7.app.AlertDialog.Builder(getActivity()).setIcon(17301543).setTitle((int) C1299R.string.devices_delete_dialog_title).setView(inflate).setPositiveButton((int) C1299R.string.devices_delete_submit_button_label, (OnClickListener) new VectorSettingsPreferencesFragment$displayDeviceDeletionDialog$1(this, (EditText) inflate.findViewById(C1299R.C1301id.delete_password), deviceInfo)).setNegativeButton((int) C1299R.string.cancel, (OnClickListener) new VectorSettingsPreferencesFragment$displayDeviceDeletionDialog$2(this)).setOnKeyListener(new VectorSettingsPreferencesFragment$displayDeviceDeletionDialog$3(this)).show();
        }
    }

    /* access modifiers changed from: private */
    public final void exportKeys() {
        Activity activity = getActivity();
        Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
        View inflate = activity.getLayoutInflater().inflate(C1299R.layout.dialog_export_e2e_keys, null);
        Builder builder = new Builder(getActivity());
        builder.setTitle(C1299R.string.encryption_export_room_keys);
        builder.setView(inflate);
        TextInputEditText textInputEditText = (TextInputEditText) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_passphrase_edit_text);
        TextInputEditText textInputEditText2 = (TextInputEditText) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_confirm_passphrase_edit_text);
        Button button = (Button) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_export_button);
        TextWatcher vectorSettingsPreferencesFragment$exportKeys$textWatcher$1 = new VectorSettingsPreferencesFragment$exportKeys$textWatcher$1(button, textInputEditText, textInputEditText2);
        textInputEditText.addTextChangedListener(vectorSettingsPreferencesFragment$exportKeys$textWatcher$1);
        textInputEditText2.addTextChangedListener(vectorSettingsPreferencesFragment$exportKeys$textWatcher$1);
        Intrinsics.checkExpressionValueIsNotNull(button, "exportButton");
        button.setEnabled(false);
        button.setOnClickListener(new VectorSettingsPreferencesFragment$exportKeys$1(this, textInputEditText, builder.show()));
    }

    /* access modifiers changed from: private */
    @SuppressLint({"NewApi"})
    public final void importKeys() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        if (VERSION.SDK_INT >= 18) {
            intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", false);
        }
        intent.setType(ResourceUtils.MIME_TYPE_ALL_CONTENT);
        startActivityForResult(intent, REQUEST_E2E_FILE_REQUEST_CODE);
    }

    private final void importKeys(Intent intent) {
        if (intent != null) {
            ArrayList arrayList = new ArrayList(RoomMediaMessage.listRoomMediaMessages(intent));
            if (arrayList.size() > 0) {
                RoomMediaMessage roomMediaMessage = (RoomMediaMessage) arrayList.get(0);
                Activity activity = getActivity();
                Intrinsics.checkExpressionValueIsNotNull(activity, "activity");
                View inflate = activity.getLayoutInflater().inflate(C1299R.layout.dialog_import_e2e_keys, null);
                Builder builder = new Builder(getActivity());
                builder.setTitle(C1299R.string.encryption_import_room_keys);
                builder.setView(inflate);
                TextInputEditText textInputEditText = (TextInputEditText) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_passphrase_edit_text);
                Button button = (Button) inflate.findViewById(C1299R.C1301id.dialog_e2e_keys_import_button);
                textInputEditText.addTextChangedListener(new VectorSettingsPreferencesFragment$importKeys$1(button, textInputEditText));
                Intrinsics.checkExpressionValueIsNotNull(button, "importButton");
                button.setEnabled(false);
                AlertDialog show = builder.show();
                Activity activity2 = getActivity();
                Intrinsics.checkExpressionValueIsNotNull(activity2, "activity");
                VectorSettingsPreferencesFragment$importKeys$2 vectorSettingsPreferencesFragment$importKeys$2 = new VectorSettingsPreferencesFragment$importKeys$2(this, textInputEditText, activity2.getApplicationContext(), roomMediaMessage, show);
                button.setOnClickListener(vectorSettingsPreferencesFragment$importKeys$2);
            }
        }
    }

    private final void refreshGroupFlairsList() {
        MXSession mXSession = this.mSession;
        if (mXSession == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        GroupsManager groupsManager = mXSession.getGroupsManager();
        MXSession mXSession2 = this.mSession;
        if (mXSession2 == null) {
            Intrinsics.throwUninitializedPropertyAccessException("mSession");
        }
        groupsManager.getUserPublicisedGroups(mXSession2.getMyUserId(), true, new VectorSettingsPreferencesFragment$refreshGroupFlairsList$1(this));
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:13:0x0029  */
    /* JADX WARNING: Removed duplicated region for block: B:28:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public final void buildGroupsList(java.util.Set<java.lang.String> r8) {
        /*
            r7 = this;
            java.util.Set<java.lang.String> r0 = r7.mPublicisedGroups
            r1 = 1
            if (r0 == 0) goto L_0x0026
            java.util.Set<java.lang.String> r0 = r7.mPublicisedGroups
            if (r0 != 0) goto L_0x000c
            kotlin.jvm.internal.Intrinsics.throwNpe()
        L_0x000c:
            int r0 = r0.size()
            int r2 = r8.size()
            if (r0 != r2) goto L_0x0026
            java.util.Set<java.lang.String> r0 = r7.mPublicisedGroups
            if (r0 != 0) goto L_0x001d
            kotlin.jvm.internal.Intrinsics.throwNpe()
        L_0x001d:
            r2 = r8
            java.util.Collection r2 = (java.util.Collection) r2
            boolean r0 = r0.containsAll(r2)
            r0 = r0 ^ r1
            goto L_0x0027
        L_0x0026:
            r0 = 1
        L_0x0027:
            if (r0 == 0) goto L_0x00d5
            java.util.ArrayList r0 = new java.util.ArrayList
            com.opengarden.firechat.matrixsdk.MXSession r2 = r7.mSession
            if (r2 != 0) goto L_0x0034
            java.lang.String r3 = "mSession"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r3)
        L_0x0034:
            com.opengarden.firechat.matrixsdk.groups.GroupsManager r2 = r2.getGroupsManager()
            java.lang.String r3 = "mSession.groupsManager"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r2, r3)
            java.util.Collection r2 = r2.getJoinedGroups()
            r0.<init>(r2)
            r2 = r0
            java.util.List r2 = (java.util.List) r2
            java.util.Comparator<com.opengarden.firechat.matrixsdk.rest.model.group.Group> r3 = com.opengarden.firechat.matrixsdk.rest.model.group.Group.mGroupsComparator
            java.util.Collections.sort(r2, r3)
            r2 = 0
            r3 = r8
            java.lang.Iterable r3 = (java.lang.Iterable) r3
            java.util.Set r3 = kotlin.collections.CollectionsKt.toMutableSet(r3)
            r7.mPublicisedGroups = r3
            android.preference.PreferenceCategory r3 = r7.getMGroupsFlairCategory()
            r3.removeAll()
            java.util.Iterator r0 = r0.iterator()
        L_0x0061:
            boolean r3 = r0.hasNext()
            if (r3 == 0) goto L_0x00d0
            java.lang.Object r3 = r0.next()
            com.opengarden.firechat.matrixsdk.rest.model.group.Group r3 = (com.opengarden.firechat.matrixsdk.rest.model.group.Group) r3
            com.opengarden.firechat.preference.VectorGroupPreference r4 = new com.opengarden.firechat.preference.VectorGroupPreference
            android.app.Activity r5 = r7.getActivity()
            android.content.Context r5 = (android.content.Context) r5
            r4.<init>(r5)
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "DEVICES_PREFERENCE_KEY_BASE"
            r5.append(r6)
            r5.append(r2)
            java.lang.String r5 = r5.toString()
            r4.setKey(r5)
            int r2 = r2 + r1
            com.opengarden.firechat.matrixsdk.MXSession r5 = r7.mSession
            if (r5 != 0) goto L_0x0096
            java.lang.String r6 = "mSession"
            kotlin.jvm.internal.Intrinsics.throwUninitializedPropertyAccessException(r6)
        L_0x0096:
            r4.setGroup(r3, r5)
            java.lang.String r5 = "group"
            kotlin.jvm.internal.Intrinsics.checkExpressionValueIsNotNull(r3, r5)
            java.lang.String r5 = r3.getDisplayName()
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            r4.setTitle(r5)
            java.lang.String r5 = r3.getGroupId()
            java.lang.CharSequence r5 = (java.lang.CharSequence) r5
            r4.setSummary(r5)
            java.lang.String r5 = r3.getGroupId()
            boolean r5 = r8.contains(r5)
            r4.setChecked(r5)
            android.preference.PreferenceCategory r5 = r7.getMGroupsFlairCategory()
            r6 = r4
            android.preference.Preference r6 = (android.preference.Preference) r6
            r5.addPreference(r6)
            com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$buildGroupsList$1 r5 = new com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$buildGroupsList$1
            r5.<init>(r7, r3, r4, r8)
            android.preference.Preference$OnPreferenceChangeListener r5 = (android.preference.Preference.OnPreferenceChangeListener) r5
            r4.setOnPreferenceChangeListener(r5)
            goto L_0x0061
        L_0x00d0:
            com.opengarden.firechat.matrixsdk.rest.model.sync.DeviceInfo r8 = r7.mMyDeviceInfo
            r7.refreshCryptographyPreference(r8)
        L_0x00d5:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment.buildGroupsList(java.util.Set):void");
    }
}
