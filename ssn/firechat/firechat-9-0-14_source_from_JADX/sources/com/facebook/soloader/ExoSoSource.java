package com.facebook.soloader;

import android.content.Context;
import com.facebook.soloader.UnpackingSoSource.Dso;
import com.facebook.soloader.UnpackingSoSource.DsoManifest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class ExoSoSource extends UnpackingSoSource {

    private final class ExoUnpacker extends Unpacker {
        /* access modifiers changed from: private */
        public final FileDso[] mDsos;
        final /* synthetic */ ExoSoSource this$0;

        private final class FileBackedInputDsoIterator extends InputDsoIterator {
            private int mCurrentDso;

            private FileBackedInputDsoIterator() {
            }

            public boolean hasNext() {
                return this.mCurrentDso < ExoUnpacker.this.mDsos.length;
            }

            public InputDso next() throws IOException {
                FileDso[] access$100 = ExoUnpacker.this.mDsos;
                int i = this.mCurrentDso;
                this.mCurrentDso = i + 1;
                FileDso fileDso = access$100[i];
                FileInputStream fileInputStream = new FileInputStream(fileDso.backingFile);
                try {
                    return new InputDso(fileDso, fileInputStream);
                } catch (Throwable th) {
                    if (fileInputStream != null) {
                        fileInputStream.close();
                    }
                    throw th;
                }
            }
        }

        /* JADX WARNING: Code restructure failed: missing block: B:29:0x00db, code lost:
            if (r11 == null) goto L_0x00e0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:31:?, code lost:
            r11.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:32:0x00e0, code lost:
            if (r10 == null) goto L_0x00e5;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:33:0x00e2, code lost:
            r10.close();
         */
        /* JADX WARNING: Code restructure failed: missing block: B:52:0x0104, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:53:0x0105, code lost:
            r2 = r0;
            r8 = null;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:54:0x0108, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:55:0x0109, code lost:
            r8 = r0;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:57:?, code lost:
            throw r8;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:58:0x010b, code lost:
            r0 = move-exception;
         */
        /* JADX WARNING: Code restructure failed: missing block: B:59:0x010c, code lost:
            r2 = r0;
         */
        /* JADX WARNING: Failed to process nested try/catch */
        /* JADX WARNING: Removed duplicated region for block: B:52:0x0104 A[ExcHandler: all (r0v3 'th' java.lang.Throwable A[CUSTOM_DECLARE]), Splitter:B:7:0x005b] */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        ExoUnpacker(com.facebook.soloader.ExoSoSource r19) throws java.io.IOException {
            /*
                r18 = this;
                r1 = r18
                r2 = r19
                r1.this$0 = r2
                r18.<init>()
                android.content.Context r2 = r2.mContext
                java.io.File r3 = new java.io.File
                java.lang.StringBuilder r4 = new java.lang.StringBuilder
                r4.<init>()
                java.lang.String r5 = "/data/local/tmp/exopackage/"
                r4.append(r5)
                java.lang.String r2 = r2.getPackageName()
                r4.append(r2)
                java.lang.String r2 = "/native-libs/"
                r4.append(r2)
                java.lang.String r2 = r4.toString()
                r3.<init>(r2)
                java.util.ArrayList r2 = new java.util.ArrayList
                r2.<init>()
                java.lang.String[] r4 = com.facebook.soloader.SysUtil.getSupportedAbis()
                int r5 = r4.length
                r6 = 0
                r7 = 0
            L_0x0036:
                if (r7 >= r5) goto L_0x011e
                r8 = r4[r7]
                java.io.File r9 = new java.io.File
                r9.<init>(r3, r8)
                boolean r8 = r9.isDirectory()
                if (r8 != 0) goto L_0x0047
                goto L_0x00e5
            L_0x0047:
                java.io.File r8 = new java.io.File
                java.lang.String r10 = "metadata.txt"
                r8.<init>(r9, r10)
                boolean r10 = r8.isFile()
                if (r10 != 0) goto L_0x0056
                goto L_0x00e5
            L_0x0056:
                java.io.FileReader r10 = new java.io.FileReader
                r10.<init>(r8)
                java.io.BufferedReader r11 = new java.io.BufferedReader     // Catch:{ Throwable -> 0x0108, all -> 0x0104 }
                r11.<init>(r10)     // Catch:{ Throwable -> 0x0108, all -> 0x0104 }
            L_0x0060:
                java.lang.String r12 = r11.readLine()     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                if (r12 == 0) goto L_0x00db
                int r13 = r12.length()     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                if (r13 != 0) goto L_0x006d
                goto L_0x0060
            L_0x006d:
                r13 = 32
                int r13 = r12.indexOf(r13)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r14 = -1
                if (r13 != r14) goto L_0x0092
                java.lang.RuntimeException r2 = new java.lang.RuntimeException     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.StringBuilder r3 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r3.<init>()     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.String r4 = "illegal line in exopackage metadata: ["
                r3.append(r4)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r3.append(r12)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.String r4 = "]"
                r3.append(r4)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.String r3 = r3.toString()     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r2.<init>(r3)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                throw r2     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
            L_0x0092:
                java.lang.StringBuilder r14 = new java.lang.StringBuilder     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r14.<init>()     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.String r15 = r12.substring(r6, r13)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r14.append(r15)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.String r15 = ".so"
                r14.append(r15)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.String r14 = r14.toString()     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                int r15 = r2.size()     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
            L_0x00ab:
                if (r6 >= r15) goto L_0x00c2
                java.lang.Object r16 = r2.get(r6)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r8 = r16
                com.facebook.soloader.ExoSoSource$FileDso r8 = (com.facebook.soloader.ExoSoSource.FileDso) r8     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.lang.String r8 = r8.name     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                boolean r8 = r8.equals(r14)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                if (r8 == 0) goto L_0x00bf
                r6 = 1
                goto L_0x00c3
            L_0x00bf:
                int r6 = r6 + 1
                goto L_0x00ab
            L_0x00c2:
                r6 = 0
            L_0x00c3:
                if (r6 == 0) goto L_0x00c7
            L_0x00c5:
                r6 = 0
                goto L_0x0060
            L_0x00c7:
                int r13 = r13 + 1
                java.lang.String r6 = r12.substring(r13)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                com.facebook.soloader.ExoSoSource$FileDso r8 = new com.facebook.soloader.ExoSoSource$FileDso     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                java.io.File r12 = new java.io.File     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r12.<init>(r9, r6)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r8.<init>(r14, r6, r12)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                r2.add(r8)     // Catch:{ Throwable -> 0x00ee, all -> 0x00ea }
                goto L_0x00c5
            L_0x00db:
                if (r11 == 0) goto L_0x00e0
                r11.close()     // Catch:{ Throwable -> 0x0108, all -> 0x0104 }
            L_0x00e0:
                if (r10 == 0) goto L_0x00e5
                r10.close()
            L_0x00e5:
                int r7 = r7 + 1
                r6 = 0
                goto L_0x0036
            L_0x00ea:
                r0 = move-exception
                r2 = r0
                r8 = 0
                goto L_0x00f3
            L_0x00ee:
                r0 = move-exception
                r8 = r0
                throw r8     // Catch:{ all -> 0x00f1 }
            L_0x00f1:
                r0 = move-exception
                r2 = r0
            L_0x00f3:
                if (r11 == 0) goto L_0x0103
                if (r8 == 0) goto L_0x0100
                r11.close()     // Catch:{ Throwable -> 0x00fb, all -> 0x0104 }
                goto L_0x0103
            L_0x00fb:
                r0 = move-exception
                r8.addSuppressed(r0)     // Catch:{ Throwable -> 0x0108, all -> 0x0104 }
                goto L_0x0103
            L_0x0100:
                r11.close()     // Catch:{ Throwable -> 0x0108, all -> 0x0104 }
            L_0x0103:
                throw r2     // Catch:{ Throwable -> 0x0108, all -> 0x0104 }
            L_0x0104:
                r0 = move-exception
                r2 = r0
                r8 = 0
                goto L_0x010d
            L_0x0108:
                r0 = move-exception
                r8 = r0
                throw r8     // Catch:{ all -> 0x010b }
            L_0x010b:
                r0 = move-exception
                r2 = r0
            L_0x010d:
                if (r10 == 0) goto L_0x011d
                if (r8 == 0) goto L_0x011a
                r10.close()     // Catch:{ Throwable -> 0x0115 }
                goto L_0x011d
            L_0x0115:
                r0 = move-exception
                r8.addSuppressed(r0)
                goto L_0x011d
            L_0x011a:
                r10.close()
            L_0x011d:
                throw r2
            L_0x011e:
                int r3 = r2.size()
                com.facebook.soloader.ExoSoSource$FileDso[] r3 = new com.facebook.soloader.ExoSoSource.FileDso[r3]
                java.lang.Object[] r2 = r2.toArray(r3)
                com.facebook.soloader.ExoSoSource$FileDso[] r2 = (com.facebook.soloader.ExoSoSource.FileDso[]) r2
                r1.mDsos = r2
                return
            */
            throw new UnsupportedOperationException("Method not decompiled: com.facebook.soloader.ExoSoSource.ExoUnpacker.<init>(com.facebook.soloader.ExoSoSource):void");
        }

        /* access modifiers changed from: protected */
        public DsoManifest getDsoManifest() throws IOException {
            return new DsoManifest(this.mDsos);
        }

        /* access modifiers changed from: protected */
        public InputDsoIterator openDsoIterator() throws IOException {
            return new FileBackedInputDsoIterator();
        }
    }

    private static final class FileDso extends Dso {
        final File backingFile;

        FileDso(String str, String str2, File file) {
            super(str, str2);
            this.backingFile = file;
        }
    }

    public ExoSoSource(Context context, String str) {
        super(context, str);
    }

    /* access modifiers changed from: protected */
    public Unpacker makeUnpacker() throws IOException {
        return new ExoUnpacker(this);
    }
}
