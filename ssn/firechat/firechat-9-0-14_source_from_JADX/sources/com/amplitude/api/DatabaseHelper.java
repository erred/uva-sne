package com.amplitude.api;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

class DatabaseHelper extends SQLiteOpenHelper {
    private static final String CREATE_EVENTS_TABLE = "CREATE TABLE IF NOT EXISTS events (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);";
    private static final String CREATE_IDENTIFYS_TABLE = "CREATE TABLE IF NOT EXISTS identifys (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);";
    private static final String CREATE_LONG_STORE_TABLE = "CREATE TABLE IF NOT EXISTS long_store (key TEXT PRIMARY KEY NOT NULL, value INTEGER);";
    private static final String CREATE_STORE_TABLE = "CREATE TABLE IF NOT EXISTS store (key TEXT PRIMARY KEY NOT NULL, value TEXT);";
    private static final String EVENT_FIELD = "event";
    protected static final String EVENT_TABLE_NAME = "events";
    protected static final String IDENTIFY_TABLE_NAME = "identifys";
    private static final String ID_FIELD = "id";
    private static final String KEY_FIELD = "key";
    protected static final String LONG_STORE_TABLE_NAME = "long_store";
    protected static final String STORE_TABLE_NAME = "store";
    private static final String TAG = "com.amplitude.api.DatabaseHelper";
    private static final String VALUE_FIELD = "value";
    static final Map<String, DatabaseHelper> instances = new HashMap();
    private static final AmplitudeLog logger = AmplitudeLog.getLogger();
    private File file;
    private String instanceName;

    @Deprecated
    static DatabaseHelper getDatabaseHelper(Context context) {
        return getDatabaseHelper(context, null);
    }

    static synchronized DatabaseHelper getDatabaseHelper(Context context, String str) {
        DatabaseHelper databaseHelper;
        synchronized (DatabaseHelper.class) {
            String normalizeInstanceName = C0532Utils.normalizeInstanceName(str);
            databaseHelper = (DatabaseHelper) instances.get(normalizeInstanceName);
            if (databaseHelper == null) {
                databaseHelper = new DatabaseHelper(context.getApplicationContext(), normalizeInstanceName);
                instances.put(normalizeInstanceName, databaseHelper);
            }
        }
        return databaseHelper;
    }

    private static String getDatabaseName(String str) {
        if (C0532Utils.isEmptyString(str) || str.equals(Constants.DEFAULT_INSTANCE)) {
            return "com.amplitude.api";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("com.amplitude.api_");
        sb.append(str);
        return sb.toString();
    }

    protected DatabaseHelper(Context context) {
        this(context, null);
    }

    protected DatabaseHelper(Context context, String str) {
        super(context, getDatabaseName(str), null, 3);
        this.file = context.getDatabasePath(getDatabaseName(str));
        this.instanceName = C0532Utils.normalizeInstanceName(str);
    }

    public void onCreate(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL(CREATE_STORE_TABLE);
        sQLiteDatabase.execSQL(CREATE_LONG_STORE_TABLE);
        sQLiteDatabase.execSQL(CREATE_EVENTS_TABLE);
        sQLiteDatabase.execSQL(CREATE_IDENTIFYS_TABLE);
    }

    /* JADX WARNING: Code restructure failed: missing block: B:9:0x0038, code lost:
        if (r6 <= 2) goto L_0x0046;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void onUpgrade(android.database.sqlite.SQLiteDatabase r4, int r5, int r6) {
        /*
            r3 = this;
            if (r5 <= r6) goto L_0x000f
            com.amplitude.api.AmplitudeLog r5 = logger
            java.lang.String r6 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r0 = "onUpgrade() with invalid oldVersion and newVersion"
            r5.mo9078e(r6, r0)
            r3.resetDatabase(r4)
            return
        L_0x000f:
            r0 = 1
            if (r6 > r0) goto L_0x0013
            return
        L_0x0013:
            switch(r5) {
                case 1: goto L_0x0032;
                case 2: goto L_0x003b;
                case 3: goto L_0x0046;
                default: goto L_0x0016;
            }
        L_0x0016:
            com.amplitude.api.AmplitudeLog r6 = logger
            java.lang.String r0 = "com.amplitude.api.DatabaseHelper"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "onUpgrade() with unknown oldVersion "
            r1.append(r2)
            r1.append(r5)
            java.lang.String r5 = r1.toString()
            r6.mo9078e(r0, r5)
            r3.resetDatabase(r4)
            goto L_0x0046
        L_0x0032:
            java.lang.String r5 = "CREATE TABLE IF NOT EXISTS store (key TEXT PRIMARY KEY NOT NULL, value TEXT);"
            r4.execSQL(r5)
            r5 = 2
            if (r6 > r5) goto L_0x003b
            goto L_0x0046
        L_0x003b:
            java.lang.String r5 = "CREATE TABLE IF NOT EXISTS identifys (id INTEGER PRIMARY KEY AUTOINCREMENT, event TEXT);"
            r4.execSQL(r5)
            java.lang.String r5 = "CREATE TABLE IF NOT EXISTS long_store (key TEXT PRIMARY KEY NOT NULL, value INTEGER);"
            r4.execSQL(r5)
            r4 = 3
        L_0x0046:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.api.DatabaseHelper.onUpgrade(android.database.sqlite.SQLiteDatabase, int, int):void");
    }

    private void resetDatabase(SQLiteDatabase sQLiteDatabase) {
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS store");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS long_store");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS events");
        sQLiteDatabase.execSQL("DROP TABLE IF EXISTS identifys");
        onCreate(sQLiteDatabase);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long insertOrReplaceKeyValue(String str, String str2) {
        long j;
        if (str2 == null) {
            try {
                j = deleteKeyFromTable(STORE_TABLE_NAME, str);
            } catch (Throwable th) {
                throw th;
            }
        } else {
            j = insertOrReplaceKeyValueToTable(STORE_TABLE_NAME, str, str2);
        }
        return j;
    }

    /* access modifiers changed from: 0000 */
    public synchronized long insertOrReplaceKeyLongValue(String str, Long l) {
        long j;
        if (l == null) {
            try {
                j = deleteKeyFromTable(LONG_STORE_TABLE_NAME, str);
            } catch (Throwable th) {
                throw th;
            }
        } else {
            j = insertOrReplaceKeyValueToTable(LONG_STORE_TABLE_NAME, str, l);
        }
        return j;
    }

    /* access modifiers changed from: 0000 */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:23:0x0060=Splitter:B:23:0x0060, B:16:0x0044=Splitter:B:16:0x0044} */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:26:0x0065=Splitter:B:26:0x0065, B:21:0x004c=Splitter:B:21:0x004c} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized long insertOrReplaceKeyValueToTable(java.lang.String r10, java.lang.String r11, java.lang.Object r12) {
        /*
            r9 = this;
            monitor-enter(r9)
            r0 = 0
            r1 = 1
            r2 = -1
            android.database.sqlite.SQLiteDatabase r4 = r9.getWritableDatabase()     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            android.content.ContentValues r5 = new android.content.ContentValues     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            r5.<init>()     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            java.lang.String r6 = "key"
            r5.put(r6, r11)     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            boolean r11 = r12 instanceof java.lang.Long     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            if (r11 == 0) goto L_0x001f
            java.lang.String r11 = "value"
            java.lang.Long r12 = (java.lang.Long) r12     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            r5.put(r11, r12)     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            goto L_0x0026
        L_0x001f:
            java.lang.String r11 = "value"
            java.lang.String r12 = (java.lang.String) r12     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            r5.put(r11, r12)     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
        L_0x0026:
            r11 = 0
            r12 = 5
            long r11 = r4.insertWithOnConflict(r10, r11, r5, r12)     // Catch:{ SQLiteException -> 0x0064, StackOverflowError -> 0x004b }
            int r4 = (r11 > r2 ? 1 : (r11 == r2 ? 0 : -1))
            if (r4 != 0) goto L_0x0044
            com.amplitude.api.AmplitudeLog r2 = logger     // Catch:{ SQLiteException -> 0x003f, StackOverflowError -> 0x003a }
            java.lang.String r3 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r4 = "Insert failed"
            r2.mo9089w(r3, r4)     // Catch:{ SQLiteException -> 0x003f, StackOverflowError -> 0x003a }
            goto L_0x0044
        L_0x003a:
            r2 = move-exception
            r7 = r11
            r11 = r2
            r2 = r7
            goto L_0x004c
        L_0x003f:
            r2 = move-exception
            r7 = r11
            r11 = r2
            r2 = r7
            goto L_0x0065
        L_0x0044:
            r9.close()     // Catch:{ all -> 0x0080 }
            r2 = r11
            goto L_0x007a
        L_0x0049:
            r10 = move-exception
            goto L_0x007c
        L_0x004b:
            r11 = move-exception
        L_0x004c:
            com.amplitude.api.AmplitudeLog r12 = logger     // Catch:{ all -> 0x0049 }
            java.lang.String r4 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r5 = "insertOrReplaceKeyValue in %s failed"
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x0049 }
            r1[r0] = r10     // Catch:{ all -> 0x0049 }
            java.lang.String r10 = java.lang.String.format(r5, r1)     // Catch:{ all -> 0x0049 }
            r12.mo9079e(r4, r10, r11)     // Catch:{ all -> 0x0049 }
            r9.delete()     // Catch:{ all -> 0x0049 }
        L_0x0060:
            r9.close()     // Catch:{ all -> 0x0080 }
            goto L_0x007a
        L_0x0064:
            r11 = move-exception
        L_0x0065:
            com.amplitude.api.AmplitudeLog r12 = logger     // Catch:{ all -> 0x0049 }
            java.lang.String r4 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r5 = "insertOrReplaceKeyValue in %s failed"
            java.lang.Object[] r1 = new java.lang.Object[r1]     // Catch:{ all -> 0x0049 }
            r1[r0] = r10     // Catch:{ all -> 0x0049 }
            java.lang.String r10 = java.lang.String.format(r5, r1)     // Catch:{ all -> 0x0049 }
            r12.mo9079e(r4, r10, r11)     // Catch:{ all -> 0x0049 }
            r9.delete()     // Catch:{ all -> 0x0049 }
            goto L_0x0060
        L_0x007a:
            monitor-exit(r9)
            return r2
        L_0x007c:
            r9.close()     // Catch:{ all -> 0x0080 }
            throw r10     // Catch:{ all -> 0x0080 }
        L_0x0080:
            r10 = move-exception
            monitor-exit(r9)
            throw r10
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.api.DatabaseHelper.insertOrReplaceKeyValueToTable(java.lang.String, java.lang.String, java.lang.Object):long");
    }

    /* access modifiers changed from: 0000 */
    public synchronized long deleteKeyFromTable(String str, String str2) {
        long j;
        j = -1;
        try {
            j = (long) getWritableDatabase().delete(str, "key=?", new String[]{str2});
            close();
        } catch (SQLiteException e) {
            logger.mo9079e(TAG, String.format("deleteKey from %s failed", new Object[]{str}), e);
            delete();
            close();
            return j;
        } catch (StackOverflowError e2) {
            try {
                logger.mo9079e(TAG, String.format("deleteKey from %s failed", new Object[]{str}), e2);
                delete();
                close();
                return j;
            } catch (Throwable th) {
                close();
                throw th;
            }
        }
        return j;
    }

    /* access modifiers changed from: 0000 */
    public synchronized long addEvent(String str) {
        return addEventToTable(EVENT_TABLE_NAME, str);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long addIdentify(String str) {
        return addEventToTable(IDENTIFY_TABLE_NAME, str);
    }

    private synchronized long addEventToTable(String str, String str2) {
        long j;
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("event", str2);
            j = writableDatabase.insert(str, null, contentValues);
            if (j == -1) {
                try {
                    logger.mo9089w(TAG, String.format("Insert into %s failed", new Object[]{str}));
                } catch (SQLiteException e) {
                    e = e;
                } catch (StackOverflowError e2) {
                    e = e2;
                    try {
                        logger.mo9079e(TAG, String.format("addEvent to %s failed", new Object[]{str}), e);
                        delete();
                        close();
                        return j;
                    } catch (Throwable th) {
                        close();
                        throw th;
                    }
                }
            }
        } catch (SQLiteException e3) {
            e = e3;
            j = -1;
            logger.mo9079e(TAG, String.format("addEvent to %s failed", new Object[]{str}), e);
            delete();
            close();
            return j;
        } catch (StackOverflowError e4) {
            e = e4;
            j = -1;
            logger.mo9079e(TAG, String.format("addEvent to %s failed", new Object[]{str}), e);
            delete();
            close();
            return j;
        }
        close();
        return j;
    }

    /* access modifiers changed from: 0000 */
    public synchronized String getValue(String str) {
        return (String) getValueFromTable(STORE_TABLE_NAME, str);
    }

    /* access modifiers changed from: 0000 */
    public synchronized Long getLongValue(String str) {
        return (Long) getValueFromTable(LONG_STORE_TABLE_NAME, str);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Removed duplicated region for block: B:26:0x0059 A[SYNTHETIC, Splitter:B:26:0x0059] */
    /* JADX WARNING: Removed duplicated region for block: B:33:0x0075 A[SYNTHETIC, Splitter:B:33:0x0075] */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x0091 A[SYNTHETIC, Splitter:B:40:0x0091] */
    /* JADX WARNING: Removed duplicated region for block: B:46:0x009a A[SYNTHETIC, Splitter:B:46:0x009a] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:23:0x0054=Splitter:B:23:0x0054, B:37:0x007b=Splitter:B:37:0x007b, B:30:0x005f=Splitter:B:30:0x005f} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.lang.Object getValueFromTable(java.lang.String r14, java.lang.String r15) {
        /*
            r13 = this;
            monitor-enter(r13)
            r0 = 0
            r1 = 0
            r2 = 1
            android.database.sqlite.SQLiteDatabase r4 = r13.getReadableDatabase()     // Catch:{ SQLiteException -> 0x0079, StackOverflowError -> 0x005d, RuntimeException -> 0x0052, all -> 0x004f }
            r3 = 2
            java.lang.String[] r6 = new java.lang.String[r3]     // Catch:{ SQLiteException -> 0x0079, StackOverflowError -> 0x005d, RuntimeException -> 0x0052, all -> 0x004f }
            java.lang.String r3 = "key"
            r6[r1] = r3     // Catch:{ SQLiteException -> 0x0079, StackOverflowError -> 0x005d, RuntimeException -> 0x0052, all -> 0x004f }
            java.lang.String r3 = "value"
            r6[r2] = r3     // Catch:{ SQLiteException -> 0x0079, StackOverflowError -> 0x005d, RuntimeException -> 0x0052, all -> 0x004f }
            java.lang.String r7 = "key = ?"
            java.lang.String[] r8 = new java.lang.String[r2]     // Catch:{ SQLiteException -> 0x0079, StackOverflowError -> 0x005d, RuntimeException -> 0x0052, all -> 0x004f }
            r8[r1] = r15     // Catch:{ SQLiteException -> 0x0079, StackOverflowError -> 0x005d, RuntimeException -> 0x0052, all -> 0x004f }
            r9 = 0
            r10 = 0
            r11 = 0
            r12 = 0
            r3 = r13
            r5 = r14
            android.database.Cursor r15 = r3.queryDb(r4, r5, r6, r7, r8, r9, r10, r11, r12)     // Catch:{ SQLiteException -> 0x0079, StackOverflowError -> 0x005d, RuntimeException -> 0x0052, all -> 0x004f }
            boolean r3 = r15.moveToFirst()     // Catch:{ SQLiteException -> 0x004d, StackOverflowError -> 0x004b, RuntimeException -> 0x0049 }
            if (r3 == 0) goto L_0x0040
            java.lang.String r3 = "store"
            boolean r3 = r14.equals(r3)     // Catch:{ SQLiteException -> 0x004d, StackOverflowError -> 0x004b, RuntimeException -> 0x0049 }
            if (r3 == 0) goto L_0x0037
            java.lang.String r3 = r15.getString(r2)     // Catch:{ SQLiteException -> 0x004d, StackOverflowError -> 0x004b, RuntimeException -> 0x0049 }
        L_0x0035:
            r0 = r3
            goto L_0x0040
        L_0x0037:
            long r3 = r15.getLong(r2)     // Catch:{ SQLiteException -> 0x004d, StackOverflowError -> 0x004b, RuntimeException -> 0x0049 }
            java.lang.Long r3 = java.lang.Long.valueOf(r3)     // Catch:{ SQLiteException -> 0x004d, StackOverflowError -> 0x004b, RuntimeException -> 0x0049 }
            goto L_0x0035
        L_0x0040:
            if (r15 == 0) goto L_0x0045
            r15.close()     // Catch:{ all -> 0x009e }
        L_0x0045:
            r13.close()     // Catch:{ all -> 0x009e }
            goto L_0x0095
        L_0x0049:
            r14 = move-exception
            goto L_0x0054
        L_0x004b:
            r3 = move-exception
            goto L_0x005f
        L_0x004d:
            r3 = move-exception
            goto L_0x007b
        L_0x004f:
            r14 = move-exception
            r15 = r0
            goto L_0x0098
        L_0x0052:
            r14 = move-exception
            r15 = r0
        L_0x0054:
            convertIfCursorWindowException(r14)     // Catch:{ all -> 0x0097 }
            if (r15 == 0) goto L_0x0045
            r15.close()     // Catch:{ all -> 0x009e }
            goto L_0x0045
        L_0x005d:
            r3 = move-exception
            r15 = r0
        L_0x005f:
            com.amplitude.api.AmplitudeLog r4 = logger     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r6 = "getValue from %s failed"
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0097 }
            r2[r1] = r14     // Catch:{ all -> 0x0097 }
            java.lang.String r14 = java.lang.String.format(r6, r2)     // Catch:{ all -> 0x0097 }
            r4.mo9079e(r5, r14, r3)     // Catch:{ all -> 0x0097 }
            r13.delete()     // Catch:{ all -> 0x0097 }
            if (r15 == 0) goto L_0x0045
            r15.close()     // Catch:{ all -> 0x009e }
            goto L_0x0045
        L_0x0079:
            r3 = move-exception
            r15 = r0
        L_0x007b:
            com.amplitude.api.AmplitudeLog r4 = logger     // Catch:{ all -> 0x0097 }
            java.lang.String r5 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r6 = "getValue from %s failed"
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x0097 }
            r2[r1] = r14     // Catch:{ all -> 0x0097 }
            java.lang.String r14 = java.lang.String.format(r6, r2)     // Catch:{ all -> 0x0097 }
            r4.mo9079e(r5, r14, r3)     // Catch:{ all -> 0x0097 }
            r13.delete()     // Catch:{ all -> 0x0097 }
            if (r15 == 0) goto L_0x0045
            r15.close()     // Catch:{ all -> 0x009e }
            goto L_0x0045
        L_0x0095:
            monitor-exit(r13)
            return r0
        L_0x0097:
            r14 = move-exception
        L_0x0098:
            if (r15 == 0) goto L_0x00a0
            r15.close()     // Catch:{ all -> 0x009e }
            goto L_0x00a0
        L_0x009e:
            r14 = move-exception
            goto L_0x00a4
        L_0x00a0:
            r13.close()     // Catch:{ all -> 0x009e }
            throw r14     // Catch:{ all -> 0x009e }
        L_0x00a4:
            monitor-exit(r13)
            throw r14
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.api.DatabaseHelper.getValueFromTable(java.lang.String, java.lang.String):java.lang.Object");
    }

    /* access modifiers changed from: 0000 */
    public synchronized List<JSONObject> getEvents(long j, long j2) throws JSONException {
        return getEventsFromTable(EVENT_TABLE_NAME, j, j2);
    }

    /* access modifiers changed from: 0000 */
    public synchronized List<JSONObject> getIdentifys(long j, long j2) throws JSONException {
        return getEventsFromTable(IDENTIFY_TABLE_NAME, j, j2);
    }

    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:12:0x0034, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:13:0x0035, code lost:
        r1 = r0;
        r2 = null;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:14:0x003a, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:15:0x003b, code lost:
        r1 = r0;
        r2 = null;
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:47:0x00b4, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:48:0x00b5, code lost:
        r1 = r0;
        r2 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:49:0x00b8, code lost:
        r0 = move-exception;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:50:0x00b9, code lost:
        r1 = r0;
        r12 = null;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:55:?, code lost:
        r12.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:58:0x00c8, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:59:0x00c9, code lost:
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:65:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:66:0x00e6, code lost:
        r0 = e;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:67:0x00e7, code lost:
        r12 = 0;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:73:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Code restructure failed: missing block: B:80:?, code lost:
        r2.close();
     */
    /* JADX WARNING: Failed to process nested try/catch */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00b4 A[ExcHandler: all (r0v9 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:5:0x000c] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00b8 A[ExcHandler: RuntimeException (r0v8 'e' java.lang.RuntimeException A[CUSTOM_DECLARE]), Splitter:B:5:0x000c] */
    /* JADX WARNING: Removed duplicated region for block: B:54:0x00c0 A[SYNTHETIC, Splitter:B:54:0x00c0] */
    /* JADX WARNING: Removed duplicated region for block: B:64:0x00e2 A[SYNTHETIC, Splitter:B:64:0x00e2] */
    /* JADX WARNING: Removed duplicated region for block: B:72:0x0100 A[SYNTHETIC, Splitter:B:72:0x0100] */
    /* JADX WARNING: Removed duplicated region for block: B:79:0x010a A[SYNTHETIC, Splitter:B:79:0x010a] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:69:0x00ea=Splitter:B:69:0x00ea, B:61:0x00cc=Splitter:B:61:0x00cc} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public synchronized java.util.List<org.json.JSONObject> getEventsFromTable(java.lang.String r20, long r21, long r23) throws org.json.JSONException {
        /*
            r19 = this;
            r1 = r21
            r3 = r23
            monitor-enter(r19)
            java.util.LinkedList r11 = new java.util.LinkedList     // Catch:{ all -> 0x0111 }
            r11.<init>()     // Catch:{ all -> 0x0111 }
            r13 = 1
            r10 = 0
            android.database.sqlite.SQLiteDatabase r5 = r19.getReadableDatabase()     // Catch:{ SQLiteException -> 0x00e6, StackOverflowError -> 0x00c8, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r6 = 2
            java.lang.String[] r6 = new java.lang.String[r6]     // Catch:{ SQLiteException -> 0x00e6, StackOverflowError -> 0x00c8, RuntimeException -> 0x00b8, all -> 0x00b4 }
            java.lang.String r7 = "id"
            r6[r10] = r7     // Catch:{ SQLiteException -> 0x00e6, StackOverflowError -> 0x00c8, RuntimeException -> 0x00b8, all -> 0x00b4 }
            java.lang.String r7 = "event"
            r6[r13] = r7     // Catch:{ SQLiteException -> 0x00e6, StackOverflowError -> 0x00c8, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r7 = 0
            int r9 = (r1 > r7 ? 1 : (r1 == r7 ? 0 : -1))
            if (r9 < 0) goto L_0x0040
            java.lang.StringBuilder r9 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r9.<init>()     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            java.lang.String r10 = "id <= "
            r9.append(r10)     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r9.append(r1)     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            java.lang.String r1 = r9.toString()     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r9 = r1
            goto L_0x0041
        L_0x0034:
            r0 = move-exception
            r1 = r0
            r2 = 0
            r12 = 0
            goto L_0x00cc
        L_0x003a:
            r0 = move-exception
            r1 = r0
            r2 = 0
            r12 = 0
            goto L_0x00ea
        L_0x0040:
            r9 = 0
        L_0x0041:
            r10 = 0
            r14 = 0
            r15 = 0
            java.lang.String r16 = "id ASC"
            int r1 = (r3 > r7 ? 1 : (r3 == r7 ? 0 : -1))
            if (r1 < 0) goto L_0x005e
            java.lang.StringBuilder r1 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r1.<init>()     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            java.lang.String r2 = ""
            r1.append(r2)     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r1.append(r3)     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            java.lang.String r1 = r1.toString()     // Catch:{ SQLiteException -> 0x003a, StackOverflowError -> 0x0034, RuntimeException -> 0x00b8, all -> 0x00b4 }
            r17 = r1
            goto L_0x0060
        L_0x005e:
            r17 = 0
        L_0x0060:
            r1 = r19
            r2 = r5
            r3 = r20
            r4 = r6
            r5 = r9
            r6 = r10
            r7 = r14
            r8 = r15
            r9 = r16
            r12 = 0
            r10 = r17
            android.database.Cursor r1 = r1.queryDb(r2, r3, r4, r5, r6, r7, r8, r9, r10)     // Catch:{ SQLiteException -> 0x00b2, StackOverflowError -> 0x00b0, RuntimeException -> 0x00b8, all -> 0x00b4 }
        L_0x0073:
            boolean r2 = r1.moveToNext()     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            if (r2 == 0) goto L_0x0096
            long r2 = r1.getLong(r12)     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            java.lang.String r4 = r1.getString(r13)     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            boolean r5 = com.amplitude.api.C0532Utils.isEmptyString(r4)     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            if (r5 == 0) goto L_0x0088
            goto L_0x0073
        L_0x0088:
            org.json.JSONObject r5 = new org.json.JSONObject     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            r5.<init>(r4)     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            java.lang.String r4 = "event_id"
            r5.put(r4, r2)     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            r11.add(r5)     // Catch:{ SQLiteException -> 0x00ac, StackOverflowError -> 0x00a8, RuntimeException -> 0x00a4, all -> 0x00a0 }
            goto L_0x0073
        L_0x0096:
            if (r1 == 0) goto L_0x009b
            r1.close()     // Catch:{ all -> 0x0111 }
        L_0x009b:
            r19.close()     // Catch:{ all -> 0x0111 }
            goto L_0x0104
        L_0x00a0:
            r0 = move-exception
            r2 = r1
            goto L_0x0107
        L_0x00a4:
            r0 = move-exception
            r12 = r1
            r1 = r0
            goto L_0x00bb
        L_0x00a8:
            r0 = move-exception
            r2 = r1
            r1 = r0
            goto L_0x00cc
        L_0x00ac:
            r0 = move-exception
            r2 = r1
            r1 = r0
            goto L_0x00ea
        L_0x00b0:
            r0 = move-exception
            goto L_0x00ca
        L_0x00b2:
            r0 = move-exception
            goto L_0x00e8
        L_0x00b4:
            r0 = move-exception
            r1 = r0
            r2 = 0
            goto L_0x0108
        L_0x00b8:
            r0 = move-exception
            r1 = r0
            r12 = 0
        L_0x00bb:
            convertIfCursorWindowException(r1)     // Catch:{ all -> 0x00c4 }
            if (r12 == 0) goto L_0x009b
            r12.close()     // Catch:{ all -> 0x0111 }
            goto L_0x009b
        L_0x00c4:
            r0 = move-exception
            r1 = r0
            r2 = r12
            goto L_0x0108
        L_0x00c8:
            r0 = move-exception
            r12 = 0
        L_0x00ca:
            r1 = r0
            r2 = 0
        L_0x00cc:
            com.amplitude.api.AmplitudeLog r3 = logger     // Catch:{ all -> 0x0106 }
            java.lang.String r4 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r5 = "removeEvent from %s failed"
            java.lang.Object[] r6 = new java.lang.Object[r13]     // Catch:{ all -> 0x0106 }
            r6[r12] = r20     // Catch:{ all -> 0x0106 }
            java.lang.String r5 = java.lang.String.format(r5, r6)     // Catch:{ all -> 0x0106 }
            r3.mo9079e(r4, r5, r1)     // Catch:{ all -> 0x0106 }
            r19.delete()     // Catch:{ all -> 0x0106 }
            if (r2 == 0) goto L_0x009b
            r2.close()     // Catch:{ all -> 0x0111 }
            goto L_0x009b
        L_0x00e6:
            r0 = move-exception
            r12 = 0
        L_0x00e8:
            r1 = r0
            r2 = 0
        L_0x00ea:
            com.amplitude.api.AmplitudeLog r3 = logger     // Catch:{ all -> 0x0106 }
            java.lang.String r4 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r5 = "getEvents from %s failed"
            java.lang.Object[] r6 = new java.lang.Object[r13]     // Catch:{ all -> 0x0106 }
            r6[r12] = r20     // Catch:{ all -> 0x0106 }
            java.lang.String r5 = java.lang.String.format(r5, r6)     // Catch:{ all -> 0x0106 }
            r3.mo9079e(r4, r5, r1)     // Catch:{ all -> 0x0106 }
            r19.delete()     // Catch:{ all -> 0x0106 }
            if (r2 == 0) goto L_0x009b
            r2.close()     // Catch:{ all -> 0x0111 }
            goto L_0x009b
        L_0x0104:
            monitor-exit(r19)
            return r11
        L_0x0106:
            r0 = move-exception
        L_0x0107:
            r1 = r0
        L_0x0108:
            if (r2 == 0) goto L_0x010d
            r2.close()     // Catch:{ all -> 0x0111 }
        L_0x010d:
            r19.close()     // Catch:{ all -> 0x0111 }
            throw r1     // Catch:{ all -> 0x0111 }
        L_0x0111:
            r0 = move-exception
            r1 = r0
            monitor-exit(r19)
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.api.DatabaseHelper.getEventsFromTable(java.lang.String, long, long):java.util.List");
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getEventCount() {
        return getEventCountFromTable(EVENT_TABLE_NAME);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getIdentifyCount() {
        return getEventCountFromTable(IDENTIFY_TABLE_NAME);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getTotalEventCount() {
        return getEventCount() + getIdentifyCount();
    }

    /* JADX WARNING: Removed duplicated region for block: B:22:0x0053 A[SYNTHETIC, Splitter:B:22:0x0053] */
    /* JADX WARNING: Removed duplicated region for block: B:29:0x0071 A[SYNTHETIC, Splitter:B:29:0x0071] */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x0079 A[SYNTHETIC, Splitter:B:34:0x0079] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:26:0x005b=Splitter:B:26:0x005b, B:19:0x003d=Splitter:B:19:0x003d} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized long getEventCountFromTable(java.lang.String r11) {
        /*
            r10 = this;
            monitor-enter(r10)
            r0 = 0
            r2 = 0
            r3 = 0
            r4 = 1
            android.database.sqlite.SQLiteDatabase r5 = r10.getReadableDatabase()     // Catch:{ SQLiteException -> 0x005a, StackOverflowError -> 0x003c }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x005a, StackOverflowError -> 0x003c }
            r6.<init>()     // Catch:{ SQLiteException -> 0x005a, StackOverflowError -> 0x003c }
            java.lang.String r7 = "SELECT COUNT(*) FROM "
            r6.append(r7)     // Catch:{ SQLiteException -> 0x005a, StackOverflowError -> 0x003c }
            r6.append(r11)     // Catch:{ SQLiteException -> 0x005a, StackOverflowError -> 0x003c }
            java.lang.String r6 = r6.toString()     // Catch:{ SQLiteException -> 0x005a, StackOverflowError -> 0x003c }
            android.database.sqlite.SQLiteStatement r5 = r5.compileStatement(r6)     // Catch:{ SQLiteException -> 0x005a, StackOverflowError -> 0x003c }
            long r6 = r5.simpleQueryForLong()     // Catch:{ SQLiteException -> 0x0035, StackOverflowError -> 0x0030, all -> 0x002d }
            if (r5 == 0) goto L_0x0028
            r5.close()     // Catch:{ all -> 0x007d }
        L_0x0028:
            r10.close()     // Catch:{ all -> 0x007d }
            r0 = r6
            goto L_0x0075
        L_0x002d:
            r11 = move-exception
            r2 = r5
            goto L_0x0077
        L_0x0030:
            r2 = move-exception
            r9 = r5
            r5 = r2
            r2 = r9
            goto L_0x003d
        L_0x0035:
            r2 = move-exception
            r9 = r5
            r5 = r2
            r2 = r9
            goto L_0x005b
        L_0x003a:
            r11 = move-exception
            goto L_0x0077
        L_0x003c:
            r5 = move-exception
        L_0x003d:
            com.amplitude.api.AmplitudeLog r6 = logger     // Catch:{ all -> 0x003a }
            java.lang.String r7 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r8 = "getNumberRows for %s failed"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x003a }
            r4[r3] = r11     // Catch:{ all -> 0x003a }
            java.lang.String r11 = java.lang.String.format(r8, r4)     // Catch:{ all -> 0x003a }
            r6.mo9079e(r7, r11, r5)     // Catch:{ all -> 0x003a }
            r10.delete()     // Catch:{ all -> 0x003a }
            if (r2 == 0) goto L_0x0056
            r2.close()     // Catch:{ all -> 0x007d }
        L_0x0056:
            r10.close()     // Catch:{ all -> 0x007d }
            goto L_0x0075
        L_0x005a:
            r5 = move-exception
        L_0x005b:
            com.amplitude.api.AmplitudeLog r6 = logger     // Catch:{ all -> 0x003a }
            java.lang.String r7 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r8 = "getNumberRows for %s failed"
            java.lang.Object[] r4 = new java.lang.Object[r4]     // Catch:{ all -> 0x003a }
            r4[r3] = r11     // Catch:{ all -> 0x003a }
            java.lang.String r11 = java.lang.String.format(r8, r4)     // Catch:{ all -> 0x003a }
            r6.mo9079e(r7, r11, r5)     // Catch:{ all -> 0x003a }
            r10.delete()     // Catch:{ all -> 0x003a }
            if (r2 == 0) goto L_0x0056
            r2.close()     // Catch:{ all -> 0x007d }
            goto L_0x0056
        L_0x0075:
            monitor-exit(r10)
            return r0
        L_0x0077:
            if (r2 == 0) goto L_0x007f
            r2.close()     // Catch:{ all -> 0x007d }
            goto L_0x007f
        L_0x007d:
            r11 = move-exception
            goto L_0x0083
        L_0x007f:
            r10.close()     // Catch:{ all -> 0x007d }
            throw r11     // Catch:{ all -> 0x007d }
        L_0x0083:
            monitor-exit(r10)
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.api.DatabaseHelper.getEventCountFromTable(java.lang.String):long");
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getNthEventId(long j) {
        return getNthEventIdFromTable(EVENT_TABLE_NAME, j);
    }

    /* access modifiers changed from: 0000 */
    public synchronized long getNthIdentifyId(long j) {
        return getNthEventIdFromTable(IDENTIFY_TABLE_NAME, j);
    }

    /* JADX WARNING: Removed duplicated region for block: B:25:0x0064 A[SYNTHETIC, Splitter:B:25:0x0064] */
    /* JADX WARNING: Removed duplicated region for block: B:31:0x007f A[SYNTHETIC, Splitter:B:31:0x007f] */
    /* JADX WARNING: Removed duplicated region for block: B:36:0x0087 A[SYNTHETIC, Splitter:B:36:0x0087] */
    /* JADX WARNING: Unknown top exception splitter block from list: {B:28:0x0069=Splitter:B:28:0x0069, B:22:0x004e=Splitter:B:22:0x004e} */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private synchronized long getNthEventIdFromTable(java.lang.String r12, long r13) {
        /*
            r11 = this;
            monitor-enter(r11)
            r0 = 0
            r1 = 0
            r2 = 1
            r3 = -1
            android.database.sqlite.SQLiteDatabase r5 = r11.getReadableDatabase()     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            java.lang.StringBuilder r6 = new java.lang.StringBuilder     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            r6.<init>()     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            java.lang.String r7 = "SELECT id FROM "
            r6.append(r7)     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            r6.append(r12)     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            java.lang.String r7 = " LIMIT 1 OFFSET "
            r6.append(r7)     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            r7 = 1
            long r9 = r13 - r7
            r6.append(r9)     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            java.lang.String r13 = r6.toString()     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            android.database.sqlite.SQLiteStatement r13 = r5.compileStatement(r13)     // Catch:{ SQLiteException -> 0x0068, StackOverflowError -> 0x004d }
            long r5 = r13.simpleQueryForLong()     // Catch:{ SQLiteDoneException -> 0x003a }
            r3 = r5
            goto L_0x0042
        L_0x0031:
            r12 = move-exception
            r0 = r13
            goto L_0x0085
        L_0x0034:
            r14 = move-exception
            r0 = r13
            goto L_0x004e
        L_0x0037:
            r14 = move-exception
            r0 = r13
            goto L_0x0069
        L_0x003a:
            r14 = move-exception
            com.amplitude.api.AmplitudeLog r0 = logger     // Catch:{ SQLiteException -> 0x0037, StackOverflowError -> 0x0034, all -> 0x0031 }
            java.lang.String r5 = "com.amplitude.api.DatabaseHelper"
            r0.mo9091w(r5, r14)     // Catch:{ SQLiteException -> 0x0037, StackOverflowError -> 0x0034, all -> 0x0031 }
        L_0x0042:
            if (r13 == 0) goto L_0x0047
            r13.close()     // Catch:{ all -> 0x008b }
        L_0x0047:
            r11.close()     // Catch:{ all -> 0x008b }
            goto L_0x0083
        L_0x004b:
            r12 = move-exception
            goto L_0x0085
        L_0x004d:
            r14 = move-exception
        L_0x004e:
            com.amplitude.api.AmplitudeLog r13 = logger     // Catch:{ all -> 0x004b }
            java.lang.String r5 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r6 = "getNthEventId from %s failed"
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x004b }
            r2[r1] = r12     // Catch:{ all -> 0x004b }
            java.lang.String r12 = java.lang.String.format(r6, r2)     // Catch:{ all -> 0x004b }
            r13.mo9079e(r5, r12, r14)     // Catch:{ all -> 0x004b }
            r11.delete()     // Catch:{ all -> 0x004b }
            if (r0 == 0) goto L_0x0047
            r0.close()     // Catch:{ all -> 0x008b }
            goto L_0x0047
        L_0x0068:
            r14 = move-exception
        L_0x0069:
            com.amplitude.api.AmplitudeLog r13 = logger     // Catch:{ all -> 0x004b }
            java.lang.String r5 = "com.amplitude.api.DatabaseHelper"
            java.lang.String r6 = "getNthEventId from %s failed"
            java.lang.Object[] r2 = new java.lang.Object[r2]     // Catch:{ all -> 0x004b }
            r2[r1] = r12     // Catch:{ all -> 0x004b }
            java.lang.String r12 = java.lang.String.format(r6, r2)     // Catch:{ all -> 0x004b }
            r13.mo9079e(r5, r12, r14)     // Catch:{ all -> 0x004b }
            r11.delete()     // Catch:{ all -> 0x004b }
            if (r0 == 0) goto L_0x0047
            r0.close()     // Catch:{ all -> 0x008b }
            goto L_0x0047
        L_0x0083:
            monitor-exit(r11)
            return r3
        L_0x0085:
            if (r0 == 0) goto L_0x008d
            r0.close()     // Catch:{ all -> 0x008b }
            goto L_0x008d
        L_0x008b:
            r12 = move-exception
            goto L_0x0091
        L_0x008d:
            r11.close()     // Catch:{ all -> 0x008b }
            throw r12     // Catch:{ all -> 0x008b }
        L_0x0091:
            monitor-exit(r11)
            throw r12
        */
        throw new UnsupportedOperationException("Method not decompiled: com.amplitude.api.DatabaseHelper.getNthEventIdFromTable(java.lang.String, long):long");
    }

    /* access modifiers changed from: 0000 */
    public synchronized void removeEvents(long j) {
        removeEventsFromTable(EVENT_TABLE_NAME, j);
    }

    /* access modifiers changed from: 0000 */
    public synchronized void removeIdentifys(long j) {
        removeEventsFromTable(IDENTIFY_TABLE_NAME, j);
    }

    private synchronized void removeEventsFromTable(String str, long j) {
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append("id <= ");
            sb.append(j);
            writableDatabase.delete(str, sb.toString(), null);
        } catch (SQLiteException e) {
            logger.mo9079e(TAG, String.format("removeEvents from %s failed", new Object[]{str}), e);
            delete();
        } catch (StackOverflowError e2) {
            try {
                logger.mo9079e(TAG, String.format("removeEvents from %s failed", new Object[]{str}), e2);
                delete();
            } catch (Throwable th) {
                close();
                throw th;
            }
        }
        close();
    }

    /* access modifiers changed from: 0000 */
    public synchronized void removeEvent(long j) {
        removeEventFromTable(EVENT_TABLE_NAME, j);
    }

    /* access modifiers changed from: 0000 */
    public synchronized void removeIdentify(long j) {
        removeEventFromTable(IDENTIFY_TABLE_NAME, j);
    }

    private synchronized void removeEventFromTable(String str, long j) {
        try {
            SQLiteDatabase writableDatabase = getWritableDatabase();
            StringBuilder sb = new StringBuilder();
            sb.append("id = ");
            sb.append(j);
            writableDatabase.delete(str, sb.toString(), null);
        } catch (SQLiteException e) {
            logger.mo9079e(TAG, String.format("removeEvent from %s failed", new Object[]{str}), e);
            delete();
        } catch (StackOverflowError e2) {
            try {
                logger.mo9079e(TAG, String.format("removeEvent from %s failed", new Object[]{str}), e2);
                delete();
            } catch (Throwable th) {
                close();
                throw th;
            }
        }
        close();
    }

    private void delete() {
        try {
            close();
            this.file.delete();
        } catch (SecurityException e) {
            logger.mo9079e(TAG, "delete failed", e);
        }
    }

    /* access modifiers changed from: 0000 */
    public boolean dbFileExists() {
        return this.file.exists();
    }

    /* access modifiers changed from: 0000 */
    public Cursor queryDb(SQLiteDatabase sQLiteDatabase, String str, String[] strArr, String str2, String[] strArr2, String str3, String str4, String str5, String str6) {
        return sQLiteDatabase.query(str, strArr, str2, strArr2, str3, str4, str5, str6);
    }

    private static void convertIfCursorWindowException(RuntimeException runtimeException) {
        String message = runtimeException.getMessage();
        if (C0532Utils.isEmptyString(message) || !message.startsWith("Cursor window allocation of")) {
            throw runtimeException;
        }
        throw new CursorWindowAllocationException(message);
    }
}
