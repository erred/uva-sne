package org.altbeacon.beacon.distance;

public class DistanceConfigFetcher {
    private static final String TAG = "DistanceConfigFetcher";
    protected Exception mException;
    protected String mResponse;
    private int mResponseCode = -1;
    private String mUrlString;
    private String mUserAgentString;

    public DistanceConfigFetcher(String str, String str2) {
        this.mUrlString = str;
        this.mUserAgentString = str2;
    }

    public int getResponseCode() {
        return this.mResponseCode;
    }

    public String getResponseString() {
        return this.mResponse;
    }

    public Exception getException() {
        return this.mException;
    }

    /* JADX WARNING: Removed duplicated region for block: B:31:0x00b3  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x00c9 A[SYNTHETIC, Splitter:B:39:0x00c9] */
    /* JADX WARNING: Removed duplicated region for block: B:49:0x00c5 A[ADDED_TO_REGION, EDGE_INSN: B:49:0x00c5->B:37:0x00c5 ?: BREAK  , SYNTHETIC] */
    /* JADX WARNING: Removed duplicated region for block: B:55:? A[RETURN, SYNTHETIC] */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public void request() {
        /*
            r12 = this;
            r0 = 0
            r12.mResponse = r0
            java.lang.String r1 = r12.mUrlString
            java.lang.StringBuilder r2 = new java.lang.StringBuilder
            r2.<init>()
            r3 = 0
            r4 = r0
            r5 = r1
            r1 = 0
        L_0x000e:
            r6 = 1
            if (r1 == 0) goto L_0x002d
            java.lang.String r5 = "DistanceConfigFetcher"
            java.lang.String r7 = "Following redirect from %s to %s"
            r8 = 2
            java.lang.Object[] r8 = new java.lang.Object[r8]
            java.lang.String r9 = r12.mUrlString
            r8[r3] = r9
            java.lang.String r9 = "Location"
            java.lang.String r9 = r4.getHeaderField(r9)
            r8[r6] = r9
            org.altbeacon.beacon.logging.LogManager.m260d(r5, r7, r8)
            java.lang.String r5 = "Location"
            java.lang.String r5 = r4.getHeaderField(r5)
        L_0x002d:
            int r1 = r1 + 1
            r7 = -1
            r12.mResponseCode = r7
            java.net.URL r7 = new java.net.URL     // Catch:{ Exception -> 0x0038 }
            r7.<init>(r5)     // Catch:{ Exception -> 0x0038 }
            goto L_0x0049
        L_0x0038:
            r7 = move-exception
            java.lang.String r8 = "DistanceConfigFetcher"
            java.lang.String r9 = "Can't construct URL from: %s"
            java.lang.Object[] r10 = new java.lang.Object[r6]
            java.lang.String r11 = r12.mUrlString
            r10[r3] = r11
            org.altbeacon.beacon.logging.LogManager.m262e(r8, r9, r10)
            r12.mException = r7
            r7 = r0
        L_0x0049:
            if (r7 != 0) goto L_0x0055
            java.lang.String r6 = "DistanceConfigFetcher"
            java.lang.String r7 = "URL is null.  Cannot make request"
            java.lang.Object[] r8 = new java.lang.Object[r3]
            org.altbeacon.beacon.logging.LogManager.m260d(r6, r7, r8)
            goto L_0x00af
        L_0x0055:
            java.net.URLConnection r7 = r7.openConnection()     // Catch:{ SecurityException -> 0x00a0, FileNotFoundException -> 0x0091, IOException -> 0x0082 }
            java.net.HttpURLConnection r7 = (java.net.HttpURLConnection) r7     // Catch:{ SecurityException -> 0x00a0, FileNotFoundException -> 0x0091, IOException -> 0x0082 }
            java.lang.String r4 = "User-Agent"
            java.lang.String r8 = r12.mUserAgentString     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            r7.addRequestProperty(r4, r8)     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            int r4 = r7.getResponseCode()     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            r12.mResponseCode = r4     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            java.lang.String r4 = "DistanceConfigFetcher"
            java.lang.String r8 = "response code is %s"
            java.lang.Object[] r6 = new java.lang.Object[r6]     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            int r9 = r7.getResponseCode()     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            java.lang.Integer r9 = java.lang.Integer.valueOf(r9)     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            r6[r3] = r9     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            org.altbeacon.beacon.logging.LogManager.m260d(r4, r8, r6)     // Catch:{ SecurityException -> 0x0080, FileNotFoundException -> 0x007e, IOException -> 0x007c }
            goto L_0x00ae
        L_0x007c:
            r4 = move-exception
            goto L_0x0085
        L_0x007e:
            r4 = move-exception
            goto L_0x0094
        L_0x0080:
            r4 = move-exception
            goto L_0x00a3
        L_0x0082:
            r6 = move-exception
            r7 = r4
            r4 = r6
        L_0x0085:
            java.lang.String r6 = "DistanceConfigFetcher"
            java.lang.String r8 = "Can't reach server"
            java.lang.Object[] r9 = new java.lang.Object[r3]
            org.altbeacon.beacon.logging.LogManager.m269w(r4, r6, r8, r9)
            r12.mException = r4
            goto L_0x00ae
        L_0x0091:
            r6 = move-exception
            r7 = r4
            r4 = r6
        L_0x0094:
            java.lang.String r6 = "DistanceConfigFetcher"
            java.lang.String r8 = "No data exists at \"+urlString"
            java.lang.Object[] r9 = new java.lang.Object[r3]
            org.altbeacon.beacon.logging.LogManager.m269w(r4, r6, r8, r9)
            r12.mException = r4
            goto L_0x00ae
        L_0x00a0:
            r6 = move-exception
            r7 = r4
            r4 = r6
        L_0x00a3:
            java.lang.String r6 = "DistanceConfigFetcher"
            java.lang.String r8 = "Can't reach sever.  Have you added android.permission.INTERNET to your manifest?"
            java.lang.Object[] r9 = new java.lang.Object[r3]
            org.altbeacon.beacon.logging.LogManager.m269w(r4, r6, r8, r9)
            r12.mException = r4
        L_0x00ae:
            r4 = r7
        L_0x00af:
            r6 = 10
            if (r1 >= r6) goto L_0x00c5
            int r6 = r12.mResponseCode
            r7 = 302(0x12e, float:4.23E-43)
            if (r6 == r7) goto L_0x000e
            int r6 = r12.mResponseCode
            r7 = 301(0x12d, float:4.22E-43)
            if (r6 == r7) goto L_0x000e
            int r6 = r12.mResponseCode
            r7 = 303(0x12f, float:4.25E-43)
            if (r6 == r7) goto L_0x000e
        L_0x00c5:
            java.lang.Exception r0 = r12.mException
            if (r0 != 0) goto L_0x00f7
            java.io.BufferedReader r0 = new java.io.BufferedReader     // Catch:{ Exception -> 0x00eb }
            java.io.InputStreamReader r1 = new java.io.InputStreamReader     // Catch:{ Exception -> 0x00eb }
            java.io.InputStream r4 = r4.getInputStream()     // Catch:{ Exception -> 0x00eb }
            r1.<init>(r4)     // Catch:{ Exception -> 0x00eb }
            r0.<init>(r1)     // Catch:{ Exception -> 0x00eb }
        L_0x00d7:
            java.lang.String r1 = r0.readLine()     // Catch:{ Exception -> 0x00eb }
            if (r1 == 0) goto L_0x00e1
            r2.append(r1)     // Catch:{ Exception -> 0x00eb }
            goto L_0x00d7
        L_0x00e1:
            r0.close()     // Catch:{ Exception -> 0x00eb }
            java.lang.String r0 = r2.toString()     // Catch:{ Exception -> 0x00eb }
            r12.mResponse = r0     // Catch:{ Exception -> 0x00eb }
            goto L_0x00f7
        L_0x00eb:
            r0 = move-exception
            r12.mException = r0
            java.lang.String r1 = "DistanceConfigFetcher"
            java.lang.String r2 = "error reading beacon data"
            java.lang.Object[] r3 = new java.lang.Object[r3]
            org.altbeacon.beacon.logging.LogManager.m269w(r0, r1, r2, r3)
        L_0x00f7:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.distance.DistanceConfigFetcher.request():void");
    }
}
