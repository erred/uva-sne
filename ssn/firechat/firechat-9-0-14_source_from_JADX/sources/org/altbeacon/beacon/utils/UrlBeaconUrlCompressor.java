package org.altbeacon.beacon.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UrlBeaconUrlCompressor {
    private static final byte EDDYSTONE_URL_BIZ = 12;
    private static final byte EDDYSTONE_URL_BIZ_SLASH = 5;
    private static final byte EDDYSTONE_URL_COM = 7;
    private static final byte EDDYSTONE_URL_COM_SLASH = 0;
    private static final byte EDDYSTONE_URL_EDU = 9;
    private static final byte EDDYSTONE_URL_EDU_SLASH = 2;
    private static final int EDDYSTONE_URL_FQDN_GROUP = 3;
    private static final byte EDDYSTONE_URL_GOV = 13;
    private static final byte EDDYSTONE_URL_GOV_SLASH = 6;
    private static final byte EDDYSTONE_URL_INFO = 11;
    private static final byte EDDYSTONE_URL_INFO_SLASH = 4;
    private static final byte EDDYSTONE_URL_NET = 10;
    private static final byte EDDYSTONE_URL_NET_SLASH = 3;
    private static final byte EDDYSTONE_URL_ORG = 8;
    private static final byte EDDYSTONE_URL_ORG_SLASH = 1;
    private static final int EDDYSTONE_URL_PATH_GROUP = 5;
    private static final int EDDYSTONE_URL_PROTOCOL_GROUP = 1;
    private static final byte EDDYSTONE_URL_PROTOCOL_HTTP = 2;
    private static final byte EDDYSTONE_URL_PROTOCOL_HTTPS = 3;
    private static final byte EDDYSTONE_URL_PROTOCOL_HTTPS_WWW = 1;
    private static final byte EDDYSTONE_URL_PROTOCOL_HTTP_WWW = 0;
    private static final String EDDYSTONE_URL_REGEX = "^((?i)http|https):\\/\\/((?i)www\\.)?((?:[0-9a-zA-Z_-]+\\.?)+)(/?)([./0-9a-zA-Z_-]*)";
    private static final int EDDYSTONE_URL_SLASH_GROUP = 4;
    private static final int EDDYSTONE_URL_WWW_GROUP = 2;
    private static final byte TLD_NOT_ENCODABLE = -1;
    private static final String URL_HOST_WWW = "www.";
    private static final String URL_PROTOCOL_HTTP = "http";
    private static final String URL_PROTOCOL_HTTPS_COLON_SLASH_SLASH = "https://";
    private static final String URL_PROTOCOL_HTTPS_WWW_DOT = "https://www.";
    private static final String URL_PROTOCOL_HTTP_COLON_SLASH_SLASH = "http://";
    private static final String URL_PROTOCOL_HTTP_WWW_DOT = "http://www.";
    private static final String URL_TLD_DOT_BIZ = ".biz";
    private static final String URL_TLD_DOT_BIZ_SLASH = ".biz/";
    private static final String URL_TLD_DOT_COM = ".com";
    private static final String URL_TLD_DOT_COM_SLASH = ".com/";
    private static final String URL_TLD_DOT_EDU = ".edu";
    private static final String URL_TLD_DOT_EDU_SLASH = ".edu/";
    private static final String URL_TLD_DOT_GOV = ".gov";
    private static final String URL_TLD_DOT_GOV_SLASH = ".gov/";
    private static final String URL_TLD_DOT_INFO = ".info";
    private static final String URL_TLD_DOT_INFO_SLASH = ".info/";
    private static final String URL_TLD_DOT_NET = ".net";
    private static final String URL_TLD_DOT_NET_SLASH = ".net/";
    private static final String URL_TLD_DOT_ORG = ".org";
    private static final String URL_TLD_DOT_ORG_SLASH = ".org/";
    private static List<TLDMapEntry> tldMap = new ArrayList();

    private static class TLDMapEntry {
        public final byte encodedByte;
        public final String tld;

        public TLDMapEntry(String str, byte b) {
            this.tld = str;
            this.encodedByte = b;
        }
    }

    static {
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_COM_SLASH, 0));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_ORG_SLASH, 1));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_EDU_SLASH, 2));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_NET_SLASH, 3));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_INFO_SLASH, EDDYSTONE_URL_INFO_SLASH));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_BIZ_SLASH, EDDYSTONE_URL_BIZ_SLASH));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_GOV_SLASH, EDDYSTONE_URL_GOV_SLASH));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_COM, EDDYSTONE_URL_COM));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_ORG, 8));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_EDU, 9));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_NET, 10));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_INFO, EDDYSTONE_URL_INFO));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_BIZ, EDDYSTONE_URL_BIZ));
        tldMap.add(new TLDMapEntry(URL_TLD_DOT_GOV, EDDYSTONE_URL_GOV));
    }

    private static byte encodedByteForTopLevelDomain(String str) {
        Iterator it = tldMap.iterator();
        byte b = -1;
        boolean z = false;
        while (!z && it.hasNext()) {
            TLDMapEntry tLDMapEntry = (TLDMapEntry) it.next();
            boolean equalsIgnoreCase = tLDMapEntry.tld.equalsIgnoreCase(str);
            if (equalsIgnoreCase) {
                b = tLDMapEntry.encodedByte;
            }
            z = equalsIgnoreCase;
        }
        return b;
    }

    private static String topLevelDomainForByte(Byte b) {
        Iterator it = tldMap.iterator();
        String str = null;
        boolean z = false;
        while (!z && it.hasNext()) {
            TLDMapEntry tLDMapEntry = (TLDMapEntry) it.next();
            boolean z2 = tLDMapEntry.encodedByte == b.byteValue();
            if (z2) {
                str = tLDMapEntry.tld;
            }
            z = z2;
        }
        return str;
    }

    /* JADX WARNING: Code restructure failed: missing block: B:36:0x00c4, code lost:
        if (r5 != null) goto L_0x00d6;
     */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static byte[] compress(java.lang.String r11) throws java.net.MalformedURLException {
        /*
            if (r11 == 0) goto L_0x010a
            int r0 = r11.length()
            byte[] r0 = new byte[r0]
            r1 = 0
            java.util.Arrays.fill(r0, r1)
            java.lang.String r2 = "^((?i)http|https):\\/\\/((?i)www\\.)?((?:[0-9a-zA-Z_-]+\\.?)+)(/?)([./0-9a-zA-Z_-]*)"
            java.util.regex.Pattern r2 = java.util.regex.Pattern.compile(r2)
            java.util.regex.Matcher r11 = r2.matcher(r11)
            boolean r2 = r11.matches()
            if (r2 == 0) goto L_0x0104
            r2 = 2
            java.lang.String r3 = r11.group(r2)
            r4 = 1
            if (r3 == 0) goto L_0x0026
            r3 = 1
            goto L_0x0027
        L_0x0026:
            r3 = 0
        L_0x0027:
            java.lang.String r5 = r11.group(r4)
            java.lang.String r5 = r5.toLowerCase()
            java.lang.String r6 = "http"
            boolean r5 = r5.equalsIgnoreCase(r6)
            r6 = 3
            if (r5 == 0) goto L_0x003e
            if (r3 == 0) goto L_0x003b
            r2 = 0
        L_0x003b:
            r0[r1] = r2
            goto L_0x0045
        L_0x003e:
            if (r3 == 0) goto L_0x0042
            r2 = 1
            goto L_0x0043
        L_0x0042:
            r2 = 3
        L_0x0043:
            r0[r1] = r2
        L_0x0045:
            java.lang.String r2 = r11.group(r6)
            byte[] r2 = r2.getBytes()
            java.lang.String r3 = new java.lang.String
            r3.<init>(r2)
            java.lang.String r2 = r3.toLowerCase()
            java.lang.String r3 = "."
            java.lang.String r3 = java.util.regex.Pattern.quote(r3)
            java.lang.String[] r2 = r2.split(r3)
            r3 = 4
            if (r2 == 0) goto L_0x00d4
            byte[] r5 = new byte[r4]
            r6 = 46
            r5[r1] = r6
            int r6 = r2.length
            if (r6 != r4) goto L_0x006e
            r6 = 1
            goto L_0x0070
        L_0x006e:
            int r6 = r2.length
            int r6 = r6 - r4
        L_0x0070:
            r7 = 0
            r8 = 1
        L_0x0072:
            if (r7 >= r6) goto L_0x008a
            if (r7 <= 0) goto L_0x007c
            int r9 = r5.length
            java.lang.System.arraycopy(r5, r1, r0, r8, r9)
            int r9 = r5.length
            int r8 = r8 + r9
        L_0x007c:
            r9 = r2[r7]
            byte[] r9 = r9.getBytes()
            int r10 = r9.length
            java.lang.System.arraycopy(r9, r1, r0, r8, r10)
            int r8 = r8 + r10
            int r7 = r7 + 1
            goto L_0x0072
        L_0x008a:
            int r5 = r2.length
            if (r5 <= r4) goto L_0x00d2
            java.lang.StringBuilder r5 = new java.lang.StringBuilder
            r5.<init>()
            java.lang.String r6 = "."
            r5.append(r6)
            int r6 = r2.length
            int r6 = r6 - r4
            r2 = r2[r6]
            r5.append(r2)
            java.lang.String r2 = r5.toString()
            java.lang.String r5 = r11.group(r3)
            if (r5 != 0) goto L_0x00aa
            r6 = r2
            goto L_0x00b9
        L_0x00aa:
            java.lang.StringBuilder r6 = new java.lang.StringBuilder
            r6.<init>()
            r6.append(r2)
            r6.append(r5)
            java.lang.String r6 = r6.toString()
        L_0x00b9:
            byte r6 = encodedByteForTopLevelDomain(r6)
            r7 = -1
            if (r6 == r7) goto L_0x00c7
            int r2 = r8 + 1
            r0[r8] = r6
            if (r5 == 0) goto L_0x00d5
            goto L_0x00d6
        L_0x00c7:
            byte[] r2 = r2.getBytes()
            int r4 = r2.length
            java.lang.System.arraycopy(r2, r1, r0, r8, r4)
            int r4 = r4 + r8
            r2 = r4
            goto L_0x00d5
        L_0x00d2:
            r2 = r8
            goto L_0x00d5
        L_0x00d4:
            r2 = 1
        L_0x00d5:
            r4 = 0
        L_0x00d6:
            if (r4 != 0) goto L_0x00ea
            java.lang.String r3 = r11.group(r3)
            if (r3 == 0) goto L_0x00ea
            int r4 = r3.length()
            byte[] r3 = r3.getBytes()
            java.lang.System.arraycopy(r3, r1, r0, r2, r4)
            int r2 = r2 + r4
        L_0x00ea:
            r3 = 5
            java.lang.String r11 = r11.group(r3)
            if (r11 == 0) goto L_0x00fd
            int r3 = r11.length()
            byte[] r11 = r11.getBytes()
            java.lang.System.arraycopy(r11, r1, r0, r2, r3)
            int r2 = r2 + r3
        L_0x00fd:
            byte[] r11 = new byte[r2]
            int r2 = r11.length
            java.lang.System.arraycopy(r0, r1, r11, r1, r2)
            return r11
        L_0x0104:
            java.net.MalformedURLException r11 = new java.net.MalformedURLException
            r11.<init>()
            throw r11
        L_0x010a:
            java.net.MalformedURLException r11 = new java.net.MalformedURLException
            r11.<init>()
            throw r11
        */
        throw new UnsupportedOperationException("Method not decompiled: org.altbeacon.beacon.utils.UrlBeaconUrlCompressor.compress(java.lang.String):byte[]");
    }

    public static String uncompress(byte[] bArr) {
        StringBuffer stringBuffer = new StringBuffer();
        switch (bArr[0] & 15) {
            case 0:
                stringBuffer.append(URL_PROTOCOL_HTTP_WWW_DOT);
                break;
            case 1:
                stringBuffer.append(URL_PROTOCOL_HTTPS_WWW_DOT);
                break;
            case 2:
                stringBuffer.append(URL_PROTOCOL_HTTP_COLON_SLASH_SLASH);
                break;
            case 3:
                stringBuffer.append(URL_PROTOCOL_HTTPS_COLON_SLASH_SLASH);
                break;
        }
        byte b = -1;
        int i = 1;
        while (i < bArr.length) {
            byte b2 = bArr[i];
            if (b == 0 && b2 == 0) {
                return stringBuffer.toString();
            }
            String str = topLevelDomainForByte(Byte.valueOf(b2));
            if (str != null) {
                stringBuffer.append(str);
            } else {
                stringBuffer.append((char) b2);
            }
            i++;
            b = b2;
        }
        return stringBuffer.toString();
    }
}
