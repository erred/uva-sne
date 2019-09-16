package com.facebook.imagepipeline.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import bolts.Task;
import com.facebook.cache.common.CacheKey;
import com.facebook.cache.common.CacheKeyUtil;
import com.facebook.common.logging.FLog;
import com.facebook.imagepipeline.image.EncodedImage;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import com.facebook.imagepipeline.request.MediaVariations;
import com.facebook.imagepipeline.request.MediaVariations.Builder;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

public class MediaVariationsIndexDatabase implements MediaVariationsIndex {
    private static final String[] PROJECTION = {IndexEntry.COLUMN_NAME_CACHE_CHOICE, IndexEntry.COLUMN_NAME_CACHE_KEY, "width", "height"};
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS media_variations_index";
    private static final String TAG = "MediaVariationsIndexDatabase";
    @GuardedBy("MediaVariationsIndexDatabase.class")
    private final LazyIndexDbOpenHelper mDbHelper;
    private final Executor mReadExecutor;
    private final Executor mWriteExecutor;

    private static class IndexDbOpenHelper extends SQLiteOpenHelper {
        public static final String DATABASE_NAME = "FrescoMediaVariationsIndex.db";
        public static final int DATABASE_VERSION = 2;
        private static final String INTEGER_TYPE = " INTEGER";
        private static final String SQL_CREATE_ENTRIES = "CREATE TABLE media_variations_index (_id INTEGER PRIMARY KEY,media_id TEXT,width INTEGER,height INTEGER,cache_choice TEXT,cache_key TEXT,resource_id TEXT UNIQUE )";
        private static final String SQL_CREATE_INDEX = "CREATE INDEX index_media_id ON media_variations_index (media_id)";
        private static final String TEXT_TYPE = " TEXT";

        public IndexDbOpenHelper(Context context) {
            super(context, DATABASE_NAME, null, 2);
        }

        public void onCreate(SQLiteDatabase sQLiteDatabase) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
                sQLiteDatabase.execSQL(SQL_CREATE_INDEX);
                sQLiteDatabase.setTransactionSuccessful();
            } finally {
                sQLiteDatabase.endTransaction();
            }
        }

        /* JADX INFO: finally extract failed */
        public void onUpgrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            sQLiteDatabase.beginTransaction();
            try {
                sQLiteDatabase.execSQL(MediaVariationsIndexDatabase.SQL_DELETE_ENTRIES);
                sQLiteDatabase.setTransactionSuccessful();
                sQLiteDatabase.endTransaction();
                onCreate(sQLiteDatabase);
            } catch (Throwable th) {
                sQLiteDatabase.endTransaction();
                throw th;
            }
        }

        public void onDowngrade(SQLiteDatabase sQLiteDatabase, int i, int i2) {
            onUpgrade(sQLiteDatabase, i, i2);
        }
    }

    private static final class IndexEntry implements BaseColumns {
        public static final String COLUMN_NAME_CACHE_CHOICE = "cache_choice";
        public static final String COLUMN_NAME_CACHE_KEY = "cache_key";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_MEDIA_ID = "media_id";
        public static final String COLUMN_NAME_RESOURCE_ID = "resource_id";
        public static final String COLUMN_NAME_WIDTH = "width";
        public static final String TABLE_NAME = "media_variations_index";

        private IndexEntry() {
        }
    }

    private static class LazyIndexDbOpenHelper {
        private final Context mContext;
        @Nullable
        private IndexDbOpenHelper mIndexDbOpenHelper;

        private LazyIndexDbOpenHelper(Context context) {
            this.mContext = context;
        }

        public synchronized SQLiteDatabase getWritableDatabase() {
            if (this.mIndexDbOpenHelper == null) {
                this.mIndexDbOpenHelper = new IndexDbOpenHelper(this.mContext);
            }
            return this.mIndexDbOpenHelper.getWritableDatabase();
        }
    }

    public MediaVariationsIndexDatabase(Context context, Executor executor, Executor executor2) {
        this.mDbHelper = new LazyIndexDbOpenHelper(context);
        this.mReadExecutor = executor;
        this.mWriteExecutor = executor2;
    }

    public Task<MediaVariations> getCachedVariants(final String str, final Builder builder) {
        try {
            return Task.call((Callable<TResult>) new Callable<MediaVariations>() {
                public MediaVariations call() throws Exception {
                    return MediaVariationsIndexDatabase.this.getCachedVariantsSync(str, builder);
                }
            }, this.mReadExecutor);
        } catch (Exception e) {
            FLog.m108w(TAG, (Throwable) e, "Failed to schedule query task for %s", str);
            return Task.forError(e);
        }
    }

    /* JADX INFO: finally extract failed */
    /* access modifiers changed from: protected */
    /* JADX WARNING: Code restructure failed: missing block: B:16:0x002e, code lost:
        return r6;
     */
    /* JADX WARNING: Code restructure failed: missing block: B:32:0x0082, code lost:
        return r6;
     */
    /* JADX WARNING: Removed duplicated region for block: B:47:0x00a2 A[SYNTHETIC, Splitter:B:47:0x00a2] */
    @com.facebook.common.internal.VisibleForTesting
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public com.facebook.imagepipeline.request.MediaVariations getCachedVariantsSync(java.lang.String r19, com.facebook.imagepipeline.request.MediaVariations.Builder r20) {
        /*
            r18 = this;
            java.lang.Class<com.facebook.imagepipeline.cache.MediaVariationsIndexDatabase> r2 = com.facebook.imagepipeline.cache.MediaVariationsIndexDatabase.class
            monitor-enter(r2)
            r3 = r18
            com.facebook.imagepipeline.cache.MediaVariationsIndexDatabase$LazyIndexDbOpenHelper r4 = r3.mDbHelper     // Catch:{ all -> 0x00a6 }
            android.database.sqlite.SQLiteDatabase r5 = r4.getWritableDatabase()     // Catch:{ all -> 0x00a6 }
            r4 = 0
            r13 = 1
            java.lang.String r8 = "media_id = ?"
            java.lang.String[] r9 = new java.lang.String[r13]     // Catch:{ SQLException -> 0x008e, all -> 0x008a }
            r9[r4] = r19     // Catch:{ SQLException -> 0x008e, all -> 0x008a }
            java.lang.String r6 = "media_variations_index"
            java.lang.String[] r7 = PROJECTION     // Catch:{ SQLException -> 0x008e, all -> 0x008a }
            r10 = 0
            r11 = 0
            r12 = 0
            android.database.Cursor r5 = r5.query(r6, r7, r8, r9, r10, r11, r12)     // Catch:{ SQLException -> 0x008e, all -> 0x008a }
            int r6 = r5.getCount()     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            if (r6 != 0) goto L_0x002f
            com.facebook.imagepipeline.request.MediaVariations r6 = r20.build()     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            if (r5 == 0) goto L_0x002d
            r5.close()     // Catch:{ all -> 0x00a6 }
        L_0x002d:
            monitor-exit(r2)     // Catch:{ all -> 0x00a6 }
            return r6
        L_0x002f:
            java.lang.String r6 = "cache_key"
            int r6 = r5.getColumnIndexOrThrow(r6)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            java.lang.String r7 = "width"
            int r7 = r5.getColumnIndexOrThrow(r7)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            java.lang.String r8 = "height"
            int r8 = r5.getColumnIndexOrThrow(r8)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            java.lang.String r9 = "cache_choice"
            int r9 = r5.getColumnIndexOrThrow(r9)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
        L_0x0047:
            boolean r10 = r5.moveToNext()     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            if (r10 == 0) goto L_0x0076
            java.lang.String r10 = r5.getString(r9)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            java.lang.String r11 = r5.getString(r6)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            android.net.Uri r11 = android.net.Uri.parse(r11)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            int r12 = r5.getInt(r7)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            int r15 = r5.getInt(r8)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            boolean r16 = android.text.TextUtils.isEmpty(r10)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            if (r16 == 0) goto L_0x006b
            r10 = r20
            r14 = 0
            goto L_0x0072
        L_0x006b:
            com.facebook.imagepipeline.request.ImageRequest$CacheChoice r10 = com.facebook.imagepipeline.request.ImageRequest.CacheChoice.valueOf(r10)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            r14 = r10
            r10 = r20
        L_0x0072:
            r10.addVariant(r11, r12, r15, r14)     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            goto L_0x0047
        L_0x0076:
            r10 = r20
            com.facebook.imagepipeline.request.MediaVariations r6 = r20.build()     // Catch:{ SQLException -> 0x0086, all -> 0x0083 }
            if (r5 == 0) goto L_0x0081
            r5.close()     // Catch:{ all -> 0x00a6 }
        L_0x0081:
            monitor-exit(r2)     // Catch:{ all -> 0x00a6 }
            return r6
        L_0x0083:
            r0 = move-exception
            r1 = r0
            goto L_0x00a0
        L_0x0086:
            r0 = move-exception
            r14 = r5
            r5 = r0
            goto L_0x0091
        L_0x008a:
            r0 = move-exception
            r1 = r0
            r5 = 0
            goto L_0x00a0
        L_0x008e:
            r0 = move-exception
            r5 = r0
            r14 = 0
        L_0x0091:
            java.lang.String r6 = TAG     // Catch:{ all -> 0x009d }
            java.lang.String r7 = "Error reading for %s"
            java.lang.Object[] r8 = new java.lang.Object[r13]     // Catch:{ all -> 0x009d }
            r8[r4] = r19     // Catch:{ all -> 0x009d }
            com.facebook.common.logging.FLog.m68e(r6, r5, r7, r8)     // Catch:{ all -> 0x009d }
            throw r5     // Catch:{ all -> 0x009d }
        L_0x009d:
            r0 = move-exception
            r1 = r0
            r5 = r14
        L_0x00a0:
            if (r5 == 0) goto L_0x00a5
            r5.close()     // Catch:{ all -> 0x00a6 }
        L_0x00a5:
            throw r1     // Catch:{ all -> 0x00a6 }
        L_0x00a6:
            r0 = move-exception
            r1 = r0
            monitor-exit(r2)     // Catch:{ all -> 0x00a6 }
            throw r1
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.imagepipeline.cache.MediaVariationsIndexDatabase.getCachedVariantsSync(java.lang.String, com.facebook.imagepipeline.request.MediaVariations$Builder):com.facebook.imagepipeline.request.MediaVariations");
    }

    public void saveCachedVariant(String str, CacheChoice cacheChoice, CacheKey cacheKey, EncodedImage encodedImage) {
        Executor executor = this.mWriteExecutor;
        final String str2 = str;
        final CacheChoice cacheChoice2 = cacheChoice;
        final CacheKey cacheKey2 = cacheKey;
        final EncodedImage encodedImage2 = encodedImage;
        C06552 r1 = new Runnable() {
            public void run() {
                MediaVariationsIndexDatabase.this.saveCachedVariantSync(str2, cacheChoice2, cacheKey2, encodedImage2);
            }
        };
        executor.execute(r1);
    }

    /* access modifiers changed from: protected */
    public void saveCachedVariantSync(String str, CacheChoice cacheChoice, CacheKey cacheKey, EncodedImage encodedImage) {
        synchronized (MediaVariationsIndexDatabase.class) {
            SQLiteDatabase writableDatabase = this.mDbHelper.getWritableDatabase();
            try {
                writableDatabase.beginTransaction();
                ContentValues contentValues = new ContentValues();
                contentValues.put(IndexEntry.COLUMN_NAME_MEDIA_ID, str);
                contentValues.put("width", Integer.valueOf(encodedImage.getWidth()));
                contentValues.put("height", Integer.valueOf(encodedImage.getHeight()));
                contentValues.put(IndexEntry.COLUMN_NAME_CACHE_CHOICE, cacheChoice.name());
                contentValues.put(IndexEntry.COLUMN_NAME_CACHE_KEY, cacheKey.getUriString());
                contentValues.put(IndexEntry.COLUMN_NAME_RESOURCE_ID, CacheKeyUtil.getFirstResourceId(cacheKey));
                writableDatabase.replaceOrThrow(IndexEntry.TABLE_NAME, null, contentValues);
                writableDatabase.setTransactionSuccessful();
            } catch (Exception e) {
                try {
                    FLog.m68e(TAG, (Throwable) e, "Error writing for %s", str);
                } catch (Throwable th) {
                    writableDatabase.endTransaction();
                    throw th;
                }
            }
            writableDatabase.endTransaction();
        }
    }
}
