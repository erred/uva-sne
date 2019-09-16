package org.altbeacon.beacon.distance;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import com.google.android.gms.dynamite.ProviderConstants;
import com.opengarden.firechat.matrixsdk.rest.model.bingrules.BingRule;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;
import org.altbeacon.beacon.logging.LogManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ModelSpecificDistanceCalculator implements DistanceCalculator {
    private static final String CONFIG_FILE = "model-distance-calculations.json";
    private static final String TAG = "ModelSpecificDistanceCalculator";
    private Context mContext;
    private AndroidModel mDefaultModel;
    /* access modifiers changed from: private */
    public DistanceCalculator mDistanceCalculator;
    private final ReentrantLock mLock;
    private AndroidModel mModel;
    Map<AndroidModel, DistanceCalculator> mModelMap;
    /* access modifiers changed from: private */
    public String mRemoteUpdateUrlString;
    /* access modifiers changed from: private */
    public AndroidModel mRequestedModel;

    public ModelSpecificDistanceCalculator(Context context, String str) {
        this(context, str, AndroidModel.forThisDevice());
    }

    public ModelSpecificDistanceCalculator(Context context, String str, AndroidModel androidModel) {
        this.mRemoteUpdateUrlString = null;
        this.mLock = new ReentrantLock();
        this.mRequestedModel = androidModel;
        this.mRemoteUpdateUrlString = str;
        this.mContext = context;
        loadModelMap();
        this.mDistanceCalculator = findCalculatorForModelWithLock(androidModel);
    }

    public AndroidModel getModel() {
        return this.mModel;
    }

    public AndroidModel getRequestedModel() {
        return this.mRequestedModel;
    }

    public double calculateDistance(int i, double d) {
        if (this.mDistanceCalculator != null) {
            return this.mDistanceCalculator.calculateDistance(i, d);
        }
        LogManager.m268w(TAG, "distance calculator has not been set", new Object[0]);
        return -1.0d;
    }

    /* access modifiers changed from: 0000 */
    public DistanceCalculator findCalculatorForModelWithLock(AndroidModel androidModel) {
        this.mLock.lock();
        try {
            return findCalculatorForModel(androidModel);
        } finally {
            this.mLock.unlock();
        }
    }

    private DistanceCalculator findCalculatorForModel(AndroidModel androidModel) {
        LogManager.m260d(TAG, "Finding best distance calculator for %s, %s, %s, %s", androidModel.getVersion(), androidModel.getBuildNumber(), androidModel.getModel(), androidModel.getManufacturer());
        if (this.mModelMap == null) {
            LogManager.m260d(TAG, "Cannot get distance calculator because modelMap was never initialized", new Object[0]);
            return null;
        }
        AndroidModel androidModel2 = null;
        int i = 0;
        for (AndroidModel androidModel3 : this.mModelMap.keySet()) {
            if (androidModel3.matchScore(androidModel) > i) {
                i = androidModel3.matchScore(androidModel);
                androidModel2 = androidModel3;
            }
        }
        if (androidModel2 != null) {
            LogManager.m260d(TAG, "found a match with score %s", Integer.valueOf(i));
            LogManager.m260d(TAG, "Finding best distance calculator for %s, %s, %s, %s", androidModel2.getVersion(), androidModel2.getBuildNumber(), androidModel2.getModel(), androidModel2.getManufacturer());
            this.mModel = androidModel2;
        } else {
            this.mModel = this.mDefaultModel;
            LogManager.m268w(TAG, "Cannot find match for this device.  Using default", new Object[0]);
        }
        return (DistanceCalculator) this.mModelMap.get(this.mModel);
    }

    private void loadModelMap() {
        boolean z;
        if (this.mRemoteUpdateUrlString != null) {
            z = loadModelMapFromFile();
            if (!z) {
                requestModelMapFromWeb();
            }
        } else {
            z = false;
        }
        if (!z) {
            loadDefaultModelMap();
        }
        this.mDistanceCalculator = findCalculatorForModelWithLock(this.mRequestedModel);
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Can't wrap try/catch for region: R(8:3|4|(3:5|6|(1:8)(1:67))|(2:10|11)|(2:14|15)|16|17|18) */
    /* JADX WARNING: Code restructure failed: missing block: B:19:0x0045, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:20:0x0046, code lost:
        org.altbeacon.beacon.logging.LogManager.m263e(r0, TAG, "Cannot update distance models from online database at %s with JSON: %s", r8.mRemoteUpdateUrlString, r1.toString());
     */
    /* JADX WARNING: Code restructure failed: missing block: B:21:0x005a, code lost:
        return false;
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Missing exception handler attribute for start block: B:16:0x003d */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0077 A[SYNTHETIC, Splitter:B:37:0x0077] */
    /* JADX WARNING: Removed duplicated region for block: B:41:0x007c A[SYNTHETIC, Splitter:B:41:0x007c] */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x0083 A[SYNTHETIC, Splitter:B:47:0x0083] */
    /* JADX WARNING: Removed duplicated region for block: B:51:0x0088 A[SYNTHETIC, Splitter:B:51:0x0088] */
    /* JADX WARNING: Removed duplicated region for block: B:59:0x008f A[SYNTHETIC, Splitter:B:59:0x008f] */
    /* JADX WARNING: Removed duplicated region for block: B:63:0x0094 A[SYNTHETIC, Splitter:B:63:0x0094] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean loadModelMapFromFile() {
        /*
            r8 = this;
            java.io.File r0 = new java.io.File
            android.content.Context r1 = r8.mContext
            java.io.File r1 = r1.getFilesDir()
            java.lang.String r2 = "model-distance-calculations.json"
            r0.<init>(r1, r2)
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            r2 = 0
            r3 = 1
            r4 = 0
            java.io.FileInputStream r5 = new java.io.FileInputStream     // Catch:{ FileNotFoundException -> 0x008c, IOException -> 0x0068, all -> 0x0065 }
            r5.<init>(r0)     // Catch:{ FileNotFoundException -> 0x008c, IOException -> 0x0068, all -> 0x0065 }
            java.io.BufferedReader r6 = new java.io.BufferedReader     // Catch:{ FileNotFoundException -> 0x008d, IOException -> 0x0063 }
            java.io.InputStreamReader r7 = new java.io.InputStreamReader     // Catch:{ FileNotFoundException -> 0x008d, IOException -> 0x0063 }
            r7.<init>(r5)     // Catch:{ FileNotFoundException -> 0x008d, IOException -> 0x0063 }
            r6.<init>(r7)     // Catch:{ FileNotFoundException -> 0x008d, IOException -> 0x0063 }
        L_0x0024:
            java.lang.String r2 = r6.readLine()     // Catch:{ FileNotFoundException -> 0x0061, IOException -> 0x005e, all -> 0x005b }
            if (r2 == 0) goto L_0x0033
            r1.append(r2)     // Catch:{ FileNotFoundException -> 0x0061, IOException -> 0x005e, all -> 0x005b }
            java.lang.String r2 = "\n"
            r1.append(r2)     // Catch:{ FileNotFoundException -> 0x0061, IOException -> 0x005e, all -> 0x005b }
            goto L_0x0024
        L_0x0033:
            if (r6 == 0) goto L_0x0038
            r6.close()     // Catch:{ Exception -> 0x0038 }
        L_0x0038:
            if (r5 == 0) goto L_0x003d
            r5.close()     // Catch:{ Exception -> 0x003d }
        L_0x003d:
            java.lang.String r0 = r1.toString()     // Catch:{ JSONException -> 0x0045 }
            r8.buildModelMapWithLock(r0)     // Catch:{ JSONException -> 0x0045 }
            return r3
        L_0x0045:
            r0 = move-exception
            java.lang.String r2 = "ModelSpecificDistanceCalculator"
            java.lang.String r5 = "Cannot update distance models from online database at %s with JSON: %s"
            r6 = 2
            java.lang.Object[] r6 = new java.lang.Object[r6]
            java.lang.String r7 = r8.mRemoteUpdateUrlString
            r6[r4] = r7
            java.lang.String r1 = r1.toString()
            r6[r3] = r1
            org.altbeacon.beacon.logging.LogManager.m263e(r0, r2, r5, r6)
            return r4
        L_0x005b:
            r0 = move-exception
            r2 = r6
            goto L_0x0081
        L_0x005e:
            r1 = move-exception
            r2 = r6
            goto L_0x006a
        L_0x0061:
            r2 = r6
            goto L_0x008d
        L_0x0063:
            r1 = move-exception
            goto L_0x006a
        L_0x0065:
            r0 = move-exception
            r5 = r2
            goto L_0x0081
        L_0x0068:
            r1 = move-exception
            r5 = r2
        L_0x006a:
            java.lang.String r6 = "ModelSpecificDistanceCalculator"
            java.lang.String r7 = "Cannot open distance model file %s"
            java.lang.Object[] r3 = new java.lang.Object[r3]     // Catch:{ all -> 0x0080 }
            r3[r4] = r0     // Catch:{ all -> 0x0080 }
            org.altbeacon.beacon.logging.LogManager.m263e(r1, r6, r7, r3)     // Catch:{ all -> 0x0080 }
            if (r2 == 0) goto L_0x007a
            r2.close()     // Catch:{ Exception -> 0x007a }
        L_0x007a:
            if (r5 == 0) goto L_0x007f
            r5.close()     // Catch:{ Exception -> 0x007f }
        L_0x007f:
            return r4
        L_0x0080:
            r0 = move-exception
        L_0x0081:
            if (r2 == 0) goto L_0x0086
            r2.close()     // Catch:{ Exception -> 0x0086 }
        L_0x0086:
            if (r5 == 0) goto L_0x008b
            r5.close()     // Catch:{ Exception -> 0x008b }
        L_0x008b:
            throw r0
        L_0x008c:
            r5 = r2
        L_0x008d:
            if (r2 == 0) goto L_0x0092
            r2.close()     // Catch:{ Exception -> 0x0092 }
        L_0x0092:
            if (r5 == 0) goto L_0x0097
            r5.close()     // Catch:{ Exception -> 0x0097 }
        L_0x0097:
            return r4
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator.loadModelMapFromFile():boolean");
    }

    /* access modifiers changed from: private */
    /* JADX WARNING: Removed duplicated region for block: B:21:0x0038 A[SYNTHETIC, Splitter:B:21:0x0038] */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x003e A[SYNTHETIC, Splitter:B:26:0x003e] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public boolean saveJson(java.lang.String r6) {
        /*
            r5 = this;
            r0 = 0
            r1 = 0
            android.content.Context r2 = r5.mContext     // Catch:{ Exception -> 0x002c }
            java.lang.String r3 = "model-distance-calculations.json"
            java.io.FileOutputStream r2 = r2.openFileOutput(r3, r0)     // Catch:{ Exception -> 0x002c }
            byte[] r6 = r6.getBytes()     // Catch:{ Exception -> 0x0027, all -> 0x0024 }
            r2.write(r6)     // Catch:{ Exception -> 0x0027, all -> 0x0024 }
            r2.close()     // Catch:{ Exception -> 0x0027, all -> 0x0024 }
            if (r2 == 0) goto L_0x0019
            r2.close()     // Catch:{ Exception -> 0x0019 }
        L_0x0019:
            java.lang.String r6 = "ModelSpecificDistanceCalculator"
            java.lang.String r1 = "Successfully saved new distance model file"
            java.lang.Object[] r0 = new java.lang.Object[r0]
            org.altbeacon.beacon.logging.LogManager.m264i(r6, r1, r0)
            r6 = 1
            return r6
        L_0x0024:
            r6 = move-exception
            r1 = r2
            goto L_0x003c
        L_0x0027:
            r6 = move-exception
            r1 = r2
            goto L_0x002d
        L_0x002a:
            r6 = move-exception
            goto L_0x003c
        L_0x002c:
            r6 = move-exception
        L_0x002d:
            java.lang.String r2 = "ModelSpecificDistanceCalculator"
            java.lang.String r3 = "Cannot write updated distance model to local storage"
            java.lang.Object[] r4 = new java.lang.Object[r0]     // Catch:{ all -> 0x002a }
            org.altbeacon.beacon.logging.LogManager.m269w(r6, r2, r3, r4)     // Catch:{ all -> 0x002a }
            if (r1 == 0) goto L_0x003b
            r1.close()     // Catch:{ Exception -> 0x003b }
        L_0x003b:
            return r0
        L_0x003c:
            if (r1 == 0) goto L_0x0041
            r1.close()     // Catch:{ Exception -> 0x0041 }
        L_0x0041:
            throw r6
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator.saveJson(java.lang.String):boolean");
    }

    @TargetApi(11)
    private void requestModelMapFromWeb() {
        if (this.mContext.checkCallingOrSelfPermission("android.permission.INTERNET") != 0) {
            LogManager.m268w(TAG, "App has no android.permission.INTERNET permission.  Cannot check for distance model updates", new Object[0]);
        } else {
            new ModelSpecificDistanceUpdater(this.mContext, this.mRemoteUpdateUrlString, new CompletionHandler() {
                public void onComplete(String str, Exception exc, int i) {
                    if (exc != null) {
                        LogManager.m268w(ModelSpecificDistanceCalculator.TAG, "Cannot updated distance models from online database at %s", exc, ModelSpecificDistanceCalculator.this.mRemoteUpdateUrlString);
                    } else if (i != 200) {
                        LogManager.m268w(ModelSpecificDistanceCalculator.TAG, "Cannot updated distance models from online database at %s due to HTTP status code %s", ModelSpecificDistanceCalculator.this.mRemoteUpdateUrlString, Integer.valueOf(i));
                    } else {
                        LogManager.m260d(ModelSpecificDistanceCalculator.TAG, "Successfully downloaded distance models from online database", new Object[0]);
                        try {
                            ModelSpecificDistanceCalculator.this.buildModelMapWithLock(str);
                            if (ModelSpecificDistanceCalculator.this.saveJson(str)) {
                                ModelSpecificDistanceCalculator.this.loadModelMapFromFile();
                                ModelSpecificDistanceCalculator.this.mDistanceCalculator = ModelSpecificDistanceCalculator.this.findCalculatorForModelWithLock(ModelSpecificDistanceCalculator.this.mRequestedModel);
                                LogManager.m264i(ModelSpecificDistanceCalculator.TAG, "Successfully updated distance model with latest from online database", new Object[0]);
                            }
                        } catch (JSONException e) {
                            LogManager.m269w(e, ModelSpecificDistanceCalculator.TAG, "Cannot parse json from downloaded distance model", new Object[0]);
                        }
                    }
                }
            }).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        }
    }

    /* access modifiers changed from: 0000 */
    public void buildModelMapWithLock(String str) throws JSONException {
        this.mLock.lock();
        try {
            buildModelMap(str);
        } finally {
            this.mLock.unlock();
        }
    }

    private void buildModelMap(String str) throws JSONException {
        this.mModelMap = new HashMap();
        JSONArray jSONArray = new JSONObject(str).getJSONArray("models");
        for (int i = 0; i < jSONArray.length(); i++) {
            JSONObject jSONObject = jSONArray.getJSONObject(i);
            boolean z = jSONObject.has(BingRule.ACTION_VALUE_DEFAULT) ? jSONObject.getBoolean(BingRule.ACTION_VALUE_DEFAULT) : false;
            Double valueOf = Double.valueOf(jSONObject.getDouble("coefficient1"));
            Double valueOf2 = Double.valueOf(jSONObject.getDouble("coefficient2"));
            Double valueOf3 = Double.valueOf(jSONObject.getDouble("coefficient3"));
            String string = jSONObject.getString(ProviderConstants.API_COLNAME_FEATURE_VERSION);
            String string2 = jSONObject.getString("build_number");
            String string3 = jSONObject.getString("model");
            String string4 = jSONObject.getString("manufacturer");
            double doubleValue = valueOf.doubleValue();
            double doubleValue2 = valueOf2.doubleValue();
            double doubleValue3 = valueOf3.doubleValue();
            CurveFittedDistanceCalculator curveFittedDistanceCalculator = r12;
            CurveFittedDistanceCalculator curveFittedDistanceCalculator2 = new CurveFittedDistanceCalculator(doubleValue, doubleValue2, doubleValue3);
            AndroidModel androidModel = new AndroidModel(string, string2, string3, string4);
            this.mModelMap.put(androidModel, curveFittedDistanceCalculator);
            if (z) {
                this.mDefaultModel = androidModel;
            }
        }
    }

    private void loadDefaultModelMap() {
        this.mModelMap = new HashMap();
        try {
            buildModelMap(stringFromFilePath(CONFIG_FILE));
        } catch (Exception e) {
            LogManager.m263e(e, TAG, "Cannot build model distance calculations", new Object[0]);
        }
    }

    /* JADX WARNING: Removed duplicated region for block: B:27:0x008f  */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0094  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private java.lang.String stringFromFilePath(java.lang.String r7) throws java.io.IOException {
        /*
            r6 = this;
            java.lang.StringBuilder r0 = new java.lang.StringBuilder
            r0.<init>()
            r1 = 0
            java.lang.Class<org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator> r2 = org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator.class
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x008b }
            r3.<init>()     // Catch:{ all -> 0x008b }
            java.lang.String r4 = "/"
            r3.append(r4)     // Catch:{ all -> 0x008b }
            r3.append(r7)     // Catch:{ all -> 0x008b }
            java.lang.String r3 = r3.toString()     // Catch:{ all -> 0x008b }
            java.io.InputStream r2 = r2.getResourceAsStream(r3)     // Catch:{ all -> 0x008b }
            if (r2 != 0) goto L_0x0040
            java.lang.Class r3 = r6.getClass()     // Catch:{ all -> 0x003e }
            java.lang.ClassLoader r3 = r3.getClassLoader()     // Catch:{ all -> 0x003e }
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch:{ all -> 0x003e }
            r4.<init>()     // Catch:{ all -> 0x003e }
            java.lang.String r5 = "/"
            r4.append(r5)     // Catch:{ all -> 0x003e }
            r4.append(r7)     // Catch:{ all -> 0x003e }
            java.lang.String r4 = r4.toString()     // Catch:{ all -> 0x003e }
            java.io.InputStream r3 = r3.getResourceAsStream(r4)     // Catch:{ all -> 0x003e }
            r2 = r3
            goto L_0x0040
        L_0x003e:
            r7 = move-exception
            goto L_0x008d
        L_0x0040:
            if (r2 != 0) goto L_0x0059
            java.lang.RuntimeException r0 = new java.lang.RuntimeException     // Catch:{ all -> 0x003e }
            java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ all -> 0x003e }
            r3.<init>()     // Catch:{ all -> 0x003e }
            java.lang.String r4 = "Cannot load resource at "
            r3.append(r4)     // Catch:{ all -> 0x003e }
            r3.append(r7)     // Catch:{ all -> 0x003e }
            java.lang.String r7 = r3.toString()     // Catch:{ all -> 0x003e }
            r0.<init>(r7)     // Catch:{ all -> 0x003e }
            throw r0     // Catch:{ all -> 0x003e }
        L_0x0059:
            java.io.BufferedReader r7 = new java.io.BufferedReader     // Catch:{ all -> 0x003e }
            java.io.InputStreamReader r3 = new java.io.InputStreamReader     // Catch:{ all -> 0x003e }
            java.lang.String r4 = "UTF-8"
            r3.<init>(r2, r4)     // Catch:{ all -> 0x003e }
            r7.<init>(r3)     // Catch:{ all -> 0x003e }
            java.lang.String r1 = r7.readLine()     // Catch:{ all -> 0x0087 }
        L_0x0069:
            if (r1 == 0) goto L_0x0078
            r0.append(r1)     // Catch:{ all -> 0x0087 }
            r1 = 10
            r0.append(r1)     // Catch:{ all -> 0x0087 }
            java.lang.String r1 = r7.readLine()     // Catch:{ all -> 0x0087 }
            goto L_0x0069
        L_0x0078:
            if (r7 == 0) goto L_0x007d
            r7.close()
        L_0x007d:
            if (r2 == 0) goto L_0x0082
            r2.close()
        L_0x0082:
            java.lang.String r7 = r0.toString()
            return r7
        L_0x0087:
            r0 = move-exception
            r1 = r7
            r7 = r0
            goto L_0x008d
        L_0x008b:
            r7 = move-exception
            r2 = r1
        L_0x008d:
            if (r1 == 0) goto L_0x0092
            r1.close()
        L_0x0092:
            if (r2 == 0) goto L_0x0097
            r2.close()
        L_0x0097:
            throw r7
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.distance.ModelSpecificDistanceCalculator.stringFromFilePath(java.lang.String):java.lang.String");
    }
}
