package org.altbeacon.beacon;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.support.p000v4.view.InputDeviceCompat;
import android.util.Log;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.altbeacon.beacon.logging.LogManager;
import org.altbeacon.bluetooth.BleAdvertisement;
import org.altbeacon.bluetooth.Pdu;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.StringUtils;

public class BeaconParser implements Serializable {
    public static final String ALTBEACON_LAYOUT = "m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25";
    private static final Pattern D_PATTERN = Pattern.compile("d\\:(\\d+)\\-(\\d+)([bl]*)?");
    public static final String EDDYSTONE_TLM_LAYOUT = "x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15";
    public static final String EDDYSTONE_UID_LAYOUT = "s:0-1=feaa,m:2-2=00,p:3-3:-41,i:4-13,i:14-19";
    public static final String EDDYSTONE_URL_LAYOUT = "s:0-1=feaa,m:2-2=10,p:3-3:-41,i:4-21v";
    private static final char[] HEX_ARRAY = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final Pattern I_PATTERN = Pattern.compile("i\\:(\\d+)\\-(\\d+)([blv]*)?");
    private static final String LITTLE_ENDIAN_SUFFIX = "l";
    private static final Pattern M_PATTERN = Pattern.compile("m\\:(\\d+)-(\\d+)\\=([0-9A-Fa-f]+)");
    private static final Pattern P_PATTERN = Pattern.compile("p\\:(\\d+)\\-(\\d+)\\:?([\\-\\d]+)?");
    private static final Pattern S_PATTERN = Pattern.compile("s\\:(\\d+)-(\\d+)\\=([0-9A-Fa-f]+)");
    private static final String TAG = "BeaconParser";
    public static final String URI_BEACON_LAYOUT = "s:0-1=fed8,m:2-2=00,p:3-3:-41,i:4-21v";
    private static final String VARIABLE_LENGTH_SUFFIX = "v";
    private static final Pattern X_PATTERN = Pattern.compile("x");
    protected List<BeaconParser> extraParsers = new ArrayList();
    protected Boolean mAllowPduOverflow = Boolean.valueOf(true);
    protected String mBeaconLayout;
    protected Integer mDBmCorrection;
    protected final List<Integer> mDataEndOffsets = new ArrayList();
    protected final List<Boolean> mDataLittleEndianFlags = new ArrayList();
    protected final List<Integer> mDataStartOffsets = new ArrayList();
    protected Boolean mExtraFrame;
    protected int[] mHardwareAssistManufacturers = {76};
    protected String mIdentifier;
    protected final List<Integer> mIdentifierEndOffsets = new ArrayList();
    protected final List<Boolean> mIdentifierLittleEndianFlags = new ArrayList();
    protected final List<Integer> mIdentifierStartOffsets = new ArrayList();
    protected final List<Boolean> mIdentifierVariableLengthFlags = new ArrayList();
    protected Integer mLayoutSize;
    private Long mMatchingBeaconTypeCode;
    protected Integer mMatchingBeaconTypeCodeEndOffset;
    protected Integer mMatchingBeaconTypeCodeStartOffset;
    protected Integer mPowerEndOffset;
    protected Integer mPowerStartOffset;
    protected Long mServiceUuid;
    protected Integer mServiceUuidEndOffset;
    protected Integer mServiceUuidStartOffset;

    public static class BeaconLayoutException extends RuntimeException {
        public BeaconLayoutException(String str) {
            super(str);
        }
    }

    public BeaconParser() {
    }

    public BeaconParser(String str) {
        this.mIdentifier = str;
    }

    public BeaconParser setBeaconLayout(String str) {
        this.mBeaconLayout = str;
        String str2 = TAG;
        StringBuilder sb = new StringBuilder();
        sb.append("Parsing beacon layout: ");
        sb.append(str);
        Log.d(str2, sb.toString());
        String[] split = str.split(",");
        this.mExtraFrame = Boolean.valueOf(false);
        for (String str3 : split) {
            Matcher matcher = I_PATTERN.matcher(str3);
            boolean z = false;
            while (matcher.find()) {
                try {
                    int parseInt = Integer.parseInt(matcher.group(1));
                    int parseInt2 = Integer.parseInt(matcher.group(2));
                    this.mIdentifierLittleEndianFlags.add(Boolean.valueOf(matcher.group(3).contains(LITTLE_ENDIAN_SUFFIX)));
                    this.mIdentifierVariableLengthFlags.add(Boolean.valueOf(matcher.group(3).contains(VARIABLE_LENGTH_SUFFIX)));
                    this.mIdentifierStartOffsets.add(Integer.valueOf(parseInt));
                    this.mIdentifierEndOffsets.add(Integer.valueOf(parseInt2));
                    z = true;
                } catch (NumberFormatException unused) {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Cannot parse integer byte offset in term: ");
                    sb2.append(str3);
                    throw new BeaconLayoutException(sb2.toString());
                }
            }
            Matcher matcher2 = D_PATTERN.matcher(str3);
            while (matcher2.find()) {
                try {
                    int parseInt3 = Integer.parseInt(matcher2.group(1));
                    int parseInt4 = Integer.parseInt(matcher2.group(2));
                    this.mDataLittleEndianFlags.add(Boolean.valueOf(matcher2.group(3).contains(LITTLE_ENDIAN_SUFFIX)));
                    this.mDataStartOffsets.add(Integer.valueOf(parseInt3));
                    this.mDataEndOffsets.add(Integer.valueOf(parseInt4));
                    z = true;
                } catch (NumberFormatException unused2) {
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Cannot parse integer byte offset in term: ");
                    sb3.append(str3);
                    throw new BeaconLayoutException(sb3.toString());
                }
            }
            Matcher matcher3 = P_PATTERN.matcher(str3);
            while (matcher3.find()) {
                try {
                    int parseInt5 = Integer.parseInt(matcher3.group(1));
                    int parseInt6 = Integer.parseInt(matcher3.group(2));
                    this.mDBmCorrection = Integer.valueOf(matcher3.group(3) != null ? Integer.parseInt(matcher3.group(3)) : 0);
                    this.mPowerStartOffset = Integer.valueOf(parseInt5);
                    this.mPowerEndOffset = Integer.valueOf(parseInt6);
                    z = true;
                } catch (NumberFormatException unused3) {
                    StringBuilder sb4 = new StringBuilder();
                    sb4.append("Cannot parse integer power byte offset in term: ");
                    sb4.append(str3);
                    throw new BeaconLayoutException(sb4.toString());
                }
            }
            Matcher matcher4 = M_PATTERN.matcher(str3);
            while (matcher4.find()) {
                try {
                    int parseInt7 = Integer.parseInt(matcher4.group(1));
                    int parseInt8 = Integer.parseInt(matcher4.group(2));
                    this.mMatchingBeaconTypeCodeStartOffset = Integer.valueOf(parseInt7);
                    this.mMatchingBeaconTypeCodeEndOffset = Integer.valueOf(parseInt8);
                    String group = matcher4.group(3);
                    try {
                        StringBuilder sb5 = new StringBuilder();
                        sb5.append("0x");
                        sb5.append(group);
                        this.mMatchingBeaconTypeCode = Long.decode(sb5.toString());
                        z = true;
                    } catch (NumberFormatException unused4) {
                        StringBuilder sb6 = new StringBuilder();
                        sb6.append("Cannot parse beacon type code: ");
                        sb6.append(group);
                        sb6.append(" in term: ");
                        sb6.append(str3);
                        throw new BeaconLayoutException(sb6.toString());
                    }
                } catch (NumberFormatException unused5) {
                    StringBuilder sb7 = new StringBuilder();
                    sb7.append("Cannot parse integer byte offset in term: ");
                    sb7.append(str3);
                    throw new BeaconLayoutException(sb7.toString());
                }
            }
            Matcher matcher5 = S_PATTERN.matcher(str3);
            while (matcher5.find()) {
                try {
                    int parseInt9 = Integer.parseInt(matcher5.group(1));
                    int parseInt10 = Integer.parseInt(matcher5.group(2));
                    this.mServiceUuidStartOffset = Integer.valueOf(parseInt9);
                    this.mServiceUuidEndOffset = Integer.valueOf(parseInt10);
                    String group2 = matcher5.group(3);
                    try {
                        StringBuilder sb8 = new StringBuilder();
                        sb8.append("0x");
                        sb8.append(group2);
                        this.mServiceUuid = Long.decode(sb8.toString());
                        z = true;
                    } catch (NumberFormatException unused6) {
                        StringBuilder sb9 = new StringBuilder();
                        sb9.append("Cannot parse serviceUuid: ");
                        sb9.append(group2);
                        sb9.append(" in term: ");
                        sb9.append(str3);
                        throw new BeaconLayoutException(sb9.toString());
                    }
                } catch (NumberFormatException unused7) {
                    StringBuilder sb10 = new StringBuilder();
                    sb10.append("Cannot parse integer byte offset in term: ");
                    sb10.append(str3);
                    throw new BeaconLayoutException(sb10.toString());
                }
            }
            Matcher matcher6 = X_PATTERN.matcher(str3);
            while (matcher6.find()) {
                this.mExtraFrame = Boolean.valueOf(true);
                z = true;
            }
            if (!z) {
                LogManager.m260d(TAG, "cannot parse term %s", str3);
                StringBuilder sb11 = new StringBuilder();
                sb11.append("Cannot parse beacon layout term: ");
                sb11.append(str3);
                throw new BeaconLayoutException(sb11.toString());
            }
        }
        if (!this.mExtraFrame.booleanValue()) {
            if (this.mIdentifierStartOffsets.size() == 0 || this.mIdentifierEndOffsets.size() == 0) {
                throw new BeaconLayoutException("You must supply at least one identifier offset with a prefix of 'i'");
            } else if (this.mPowerStartOffset == null || this.mPowerEndOffset == null) {
                throw new BeaconLayoutException("You must supply a power byte offset with a prefix of 'p'");
            }
        }
        if (this.mMatchingBeaconTypeCodeStartOffset == null || this.mMatchingBeaconTypeCodeEndOffset == null) {
            throw new BeaconLayoutException("You must supply a matching beacon type expression with a prefix of 'm'");
        }
        this.mLayoutSize = Integer.valueOf(calculateLayoutSize());
        return this;
    }

    public boolean addExtraDataParser(BeaconParser beaconParser) {
        return beaconParser != null && beaconParser.mExtraFrame.booleanValue() && this.extraParsers.add(beaconParser);
    }

    public List<BeaconParser> getExtraDataParsers() {
        return new ArrayList(this.extraParsers);
    }

    public String getIdentifier() {
        return this.mIdentifier;
    }

    public int[] getHardwareAssistManufacturers() {
        return this.mHardwareAssistManufacturers;
    }

    public void setHardwareAssistManufacturerCodes(int[] iArr) {
        this.mHardwareAssistManufacturers = iArr;
    }

    public void setAllowPduOverflow(Boolean bool) {
        this.mAllowPduOverflow = bool;
    }

    public Long getMatchingBeaconTypeCode() {
        return this.mMatchingBeaconTypeCode;
    }

    public int getMatchingBeaconTypeCodeStartOffset() {
        return this.mMatchingBeaconTypeCodeStartOffset.intValue();
    }

    public int getMatchingBeaconTypeCodeEndOffset() {
        return this.mMatchingBeaconTypeCodeEndOffset.intValue();
    }

    public Long getServiceUuid() {
        return this.mServiceUuid;
    }

    public int getMServiceUuidStartOffset() {
        return this.mServiceUuidStartOffset.intValue();
    }

    public int getServiceUuidEndOffset() {
        return this.mServiceUuidEndOffset.intValue();
    }

    public Beacon fromScanData(byte[] bArr, int i, BluetoothDevice bluetoothDevice) {
        return fromScanData(bArr, i, bluetoothDevice, new Beacon());
    }

    /* access modifiers changed from: protected */
    public Beacon fromScanData(byte[] bArr, int i, BluetoothDevice bluetoothDevice, Beacon beacon) {
        boolean z;
        Pdu pdu;
        int i2;
        Beacon beacon2;
        boolean z2;
        String str;
        String str2;
        boolean z3;
        boolean z4;
        Beacon beacon3;
        byte[] bArr2 = bArr;
        BleAdvertisement bleAdvertisement = new BleAdvertisement(bArr2);
        ArrayList arrayList = new ArrayList();
        ArrayList arrayList2 = new ArrayList();
        Iterator it = bleAdvertisement.getPdus().iterator();
        while (true) {
            z = true;
            if (!it.hasNext()) {
                pdu = null;
                break;
            }
            pdu = (Pdu) it.next();
            if (pdu.getType() != 22 && pdu.getType() != -1) {
                if (LogManager.isVerboseLoggingEnabled()) {
                    LogManager.m260d(TAG, "Ignoring pdu type %02X", Byte.valueOf(pdu.getType()));
                }
            }
        }
        if (LogManager.isVerboseLoggingEnabled()) {
            LogManager.m260d(TAG, "Processing pdu type %02X: %s with startIndex: %d, endIndex: %d", Byte.valueOf(pdu.getType()), bytesToHex(bArr), Integer.valueOf(pdu.getStartIndex()), Integer.valueOf(pdu.getEndIndex()));
        }
        if (pdu == null) {
            if (LogManager.isVerboseLoggingEnabled()) {
                LogManager.m260d(TAG, "No PDUs to process in this packet.", new Object[0]);
            }
            beacon2 = beacon;
            z2 = true;
            i2 = 0;
        } else {
            byte[] longToByteArray = longToByteArray(getMatchingBeaconTypeCode().longValue(), (this.mMatchingBeaconTypeCodeEndOffset.intValue() - this.mMatchingBeaconTypeCodeStartOffset.intValue()) + 1);
            byte[] longToByteArray2 = getServiceUuid() != null ? longToByteArray(getServiceUuid().longValue(), (this.mServiceUuidEndOffset.intValue() - this.mServiceUuidStartOffset.intValue()) + 1, false) : null;
            i2 = pdu.getStartIndex();
            if (getServiceUuid() != null ? !byteArraysMatch(bArr2, this.mServiceUuidStartOffset.intValue() + i2, longToByteArray2) || !byteArraysMatch(bArr2, this.mMatchingBeaconTypeCodeStartOffset.intValue() + i2, longToByteArray) : !byteArraysMatch(bArr2, this.mMatchingBeaconTypeCodeStartOffset.intValue() + i2, longToByteArray)) {
                z3 = false;
            } else {
                z3 = true;
            }
            if (!z3) {
                if (getServiceUuid() == null) {
                    if (LogManager.isVerboseLoggingEnabled()) {
                        LogManager.m260d(TAG, "This is not a matching Beacon advertisement. (Was expecting %s.  The bytes I see are: %s", byteArrayToString(longToByteArray), bytesToHex(bArr));
                    }
                } else if (LogManager.isVerboseLoggingEnabled()) {
                    LogManager.m260d(TAG, "This is not a matching Beacon advertisement. Was expecting %s at offset %d and %s at offset %d.  The bytes I see are: %s", byteArrayToString(longToByteArray2), Integer.valueOf(this.mServiceUuidStartOffset.intValue() + i2), byteArrayToString(longToByteArray), Integer.valueOf(this.mMatchingBeaconTypeCodeStartOffset.intValue() + i2), bytesToHex(bArr));
                }
                beacon3 = null;
                z4 = true;
            } else {
                if (LogManager.isVerboseLoggingEnabled()) {
                    LogManager.m260d(TAG, "This is a recognized beacon advertisement -- %s seen", byteArrayToString(longToByteArray));
                    LogManager.m260d(TAG, "Bytes are: %s", bytesToHex(bArr));
                }
                beacon3 = beacon;
                z4 = false;
            }
            if (z3) {
                if (bArr2.length <= this.mLayoutSize.intValue() + i2 && this.mAllowPduOverflow.booleanValue()) {
                    if (LogManager.isVerboseLoggingEnabled()) {
                        String str3 = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("Expanding buffer because it is too short to parse: ");
                        sb.append(bArr2.length);
                        sb.append(", needed: ");
                        sb.append(this.mLayoutSize.intValue() + i2);
                        LogManager.m260d(str3, sb.toString(), new Object[0]);
                    }
                    bArr2 = ensureMaxSize(bArr2, this.mLayoutSize.intValue() + i2);
                }
                z2 = z4;
                for (int i3 = 0; i3 < this.mIdentifierEndOffsets.size(); i3++) {
                    int intValue = ((Integer) this.mIdentifierEndOffsets.get(i3)).intValue() + i2;
                    if (intValue > pdu.getEndIndex() && ((Boolean) this.mIdentifierVariableLengthFlags.get(i3)).booleanValue()) {
                        if (LogManager.isVerboseLoggingEnabled()) {
                            String str4 = TAG;
                            StringBuilder sb2 = new StringBuilder();
                            sb2.append("Need to truncate identifier by ");
                            sb2.append(intValue - pdu.getEndIndex());
                            LogManager.m260d(str4, sb2.toString(), new Object[0]);
                        }
                        arrayList.add(Identifier.fromBytes(bArr2, ((Integer) this.mIdentifierStartOffsets.get(i3)).intValue() + i2, pdu.getEndIndex() + 1, ((Boolean) this.mIdentifierLittleEndianFlags.get(i3)).booleanValue()));
                    } else if (intValue <= pdu.getEndIndex() || this.mAllowPduOverflow.booleanValue()) {
                        arrayList.add(Identifier.fromBytes(bArr2, ((Integer) this.mIdentifierStartOffsets.get(i3)).intValue() + i2, intValue + 1, ((Boolean) this.mIdentifierLittleEndianFlags.get(i3)).booleanValue()));
                    } else {
                        if (LogManager.isVerboseLoggingEnabled()) {
                            String str5 = TAG;
                            StringBuilder sb3 = new StringBuilder();
                            sb3.append("Cannot parse identifier ");
                            sb3.append(i3);
                            sb3.append(" because PDU is too short.  endIndex: ");
                            sb3.append(intValue);
                            sb3.append(" PDU endIndex: ");
                            sb3.append(pdu.getEndIndex());
                            LogManager.m260d(str5, sb3.toString(), new Object[0]);
                        }
                        z2 = true;
                    }
                }
                for (int i4 = 0; i4 < this.mDataEndOffsets.size(); i4++) {
                    int intValue2 = ((Integer) this.mDataEndOffsets.get(i4)).intValue() + i2;
                    if (intValue2 <= pdu.getEndIndex() || this.mAllowPduOverflow.booleanValue()) {
                        arrayList2.add(Long.decode(byteArrayToFormattedString(bArr2, ((Integer) this.mDataStartOffsets.get(i4)).intValue() + i2, intValue2, ((Boolean) this.mDataLittleEndianFlags.get(i4)).booleanValue())));
                    } else {
                        if (LogManager.isVerboseLoggingEnabled()) {
                            String str6 = TAG;
                            StringBuilder sb4 = new StringBuilder();
                            sb4.append("Cannot parse data field ");
                            sb4.append(i4);
                            sb4.append(" because PDU is too short.  endIndex: ");
                            sb4.append(intValue2);
                            sb4.append(" PDU endIndex: ");
                            sb4.append(pdu.getEndIndex());
                            sb4.append(".  Setting value to 0");
                            LogManager.m260d(str6, sb4.toString(), new Object[0]);
                        }
                        arrayList2.add(new Long(0));
                    }
                }
                if (this.mPowerStartOffset != null) {
                    int intValue3 = this.mPowerEndOffset.intValue() + i2;
                    try {
                        if (intValue3 <= pdu.getEndIndex() || this.mAllowPduOverflow.booleanValue()) {
                            int parseInt = Integer.parseInt(byteArrayToFormattedString(bArr2, this.mPowerStartOffset.intValue() + i2, this.mPowerEndOffset.intValue() + i2, false)) + this.mDBmCorrection.intValue();
                            if (parseInt > 127) {
                                parseInt += InputDeviceCompat.SOURCE_ANY;
                            }
                            beacon3.mTxPower = parseInt;
                        } else {
                            try {
                                if (LogManager.isVerboseLoggingEnabled()) {
                                    String str7 = TAG;
                                    StringBuilder sb5 = new StringBuilder();
                                    sb5.append("Cannot parse power field because PDU is too short.  endIndex: ");
                                    sb5.append(intValue3);
                                    sb5.append(" PDU endIndex: ");
                                    sb5.append(pdu.getEndIndex());
                                    LogManager.m260d(str7, sb5.toString(), new Object[0]);
                                }
                            } catch (NullPointerException | NumberFormatException unused) {
                            }
                            z2 = true;
                        }
                    } catch (NullPointerException | NumberFormatException unused2) {
                    }
                }
                beacon2 = beacon3;
            } else {
                beacon2 = beacon3;
                z2 = z4;
            }
        }
        if (z2) {
            return null;
        }
        int parseInt2 = Integer.parseInt(byteArrayToFormattedString(bArr2, this.mMatchingBeaconTypeCodeStartOffset.intValue() + i2, this.mMatchingBeaconTypeCodeEndOffset.intValue() + i2, false));
        int parseInt3 = Integer.parseInt(byteArrayToFormattedString(bArr2, i2, i2 + 1, true));
        if (bluetoothDevice != null) {
            str = bluetoothDevice.getAddress();
            str2 = bluetoothDevice.getName();
        } else {
            str2 = null;
            str = null;
        }
        beacon2.mIdentifiers = arrayList;
        beacon2.mDataFields = arrayList2;
        beacon2.mRssi = i;
        beacon2.mBeaconTypeCode = parseInt2;
        if (this.mServiceUuid != null) {
            beacon2.mServiceUuid = (int) this.mServiceUuid.longValue();
        } else {
            beacon2.mServiceUuid = -1;
        }
        beacon2.mBluetoothAddress = str;
        beacon2.mBluetoothName = str2;
        beacon2.mManufacturer = parseInt3;
        beacon2.mParserIdentifier = this.mIdentifier;
        if (this.extraParsers.size() <= 0 && !this.mExtraFrame.booleanValue()) {
            z = false;
        }
        beacon2.mMultiFrameBeacon = z;
        return beacon2;
    }

    @TargetApi(9)
    public byte[] getBeaconAdvertisementData(Beacon beacon) {
        Beacon beacon2 = beacon;
        if (beacon.getIdentifiers().size() != getIdentifierCount()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Beacon has ");
            sb.append(beacon.getIdentifiers().size());
            sb.append(" identifiers but format requires ");
            sb.append(getIdentifierCount());
            throw new IllegalArgumentException(sb.toString());
        }
        int i = -1;
        if (this.mMatchingBeaconTypeCodeEndOffset != null && this.mMatchingBeaconTypeCodeEndOffset.intValue() > -1) {
            i = this.mMatchingBeaconTypeCodeEndOffset.intValue();
        }
        if (this.mPowerEndOffset != null && this.mPowerEndOffset.intValue() > i) {
            i = this.mPowerEndOffset.intValue();
        }
        int i2 = i;
        for (int i3 = 0; i3 < this.mIdentifierEndOffsets.size(); i3++) {
            if (this.mIdentifierEndOffsets.get(i3) != null && ((Integer) this.mIdentifierEndOffsets.get(i3)).intValue() > i2) {
                i2 = ((Integer) this.mIdentifierEndOffsets.get(i3)).intValue();
            }
        }
        for (int i4 = 0; i4 < this.mDataEndOffsets.size(); i4++) {
            if (this.mDataEndOffsets.get(i4) != null && ((Integer) this.mDataEndOffsets.get(i4)).intValue() > i2) {
                i2 = ((Integer) this.mDataEndOffsets.get(i4)).intValue();
            }
        }
        int i5 = 0;
        for (int i6 = 0; i6 < this.mIdentifierStartOffsets.size(); i6++) {
            if (((Boolean) this.mIdentifierVariableLengthFlags.get(i6)).booleanValue()) {
                i5 = (i5 + beacon2.getIdentifier(i6).getByteCount()) - ((((Integer) this.mIdentifierEndOffsets.get(i6)).intValue() - ((Integer) this.mIdentifierStartOffsets.get(i6)).intValue()) + 1);
            }
        }
        byte[] bArr = new byte[(((i2 + i5) + 1) - 2)];
        getMatchingBeaconTypeCode().longValue();
        for (int intValue = this.mMatchingBeaconTypeCodeStartOffset.intValue(); intValue <= this.mMatchingBeaconTypeCodeEndOffset.intValue(); intValue++) {
            bArr[intValue - 2] = (byte) ((int) ((getMatchingBeaconTypeCode().longValue() >> ((this.mMatchingBeaconTypeCodeEndOffset.intValue() - intValue) * 8)) & 255));
        }
        for (int i7 = 0; i7 < this.mIdentifierStartOffsets.size(); i7++) {
            byte[] byteArrayOfSpecifiedEndianness = beacon2.getIdentifier(i7).toByteArrayOfSpecifiedEndianness(!((Boolean) this.mIdentifierLittleEndianFlags.get(i7)).booleanValue());
            if (byteArrayOfSpecifiedEndianness.length < getIdentifierByteCount(i7)) {
                if (!((Boolean) this.mIdentifierVariableLengthFlags.get(i7)).booleanValue()) {
                    if (((Boolean) this.mIdentifierLittleEndianFlags.get(i7)).booleanValue()) {
                        byteArrayOfSpecifiedEndianness = Arrays.copyOf(byteArrayOfSpecifiedEndianness, getIdentifierByteCount(i7));
                    } else {
                        byte[] bArr2 = new byte[getIdentifierByteCount(i7)];
                        System.arraycopy(byteArrayOfSpecifiedEndianness, 0, bArr2, getIdentifierByteCount(i7) - byteArrayOfSpecifiedEndianness.length, byteArrayOfSpecifiedEndianness.length);
                        byteArrayOfSpecifiedEndianness = bArr2;
                    }
                }
                String str = TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Expanded identifier because it is too short.  It is now: ");
                sb2.append(byteArrayToString(byteArrayOfSpecifiedEndianness));
                LogManager.m260d(str, sb2.toString(), new Object[0]);
            } else if (byteArrayOfSpecifiedEndianness.length > getIdentifierByteCount(i7)) {
                if (((Boolean) this.mIdentifierLittleEndianFlags.get(i7)).booleanValue()) {
                    byteArrayOfSpecifiedEndianness = Arrays.copyOfRange(byteArrayOfSpecifiedEndianness, getIdentifierByteCount(i7) - byteArrayOfSpecifiedEndianness.length, getIdentifierByteCount(i7));
                } else {
                    byteArrayOfSpecifiedEndianness = Arrays.copyOf(byteArrayOfSpecifiedEndianness, getIdentifierByteCount(i7));
                }
                String str2 = TAG;
                StringBuilder sb3 = new StringBuilder();
                sb3.append("Truncated identifier because it is too long.  It is now: ");
                sb3.append(byteArrayToString(byteArrayOfSpecifiedEndianness));
                LogManager.m260d(str2, sb3.toString(), new Object[0]);
            } else {
                String str3 = TAG;
                StringBuilder sb4 = new StringBuilder();
                sb4.append("Identifier size is just right: ");
                sb4.append(byteArrayToString(byteArrayOfSpecifiedEndianness));
                LogManager.m260d(str3, sb4.toString(), new Object[0]);
            }
            for (int intValue2 = ((Integer) this.mIdentifierStartOffsets.get(i7)).intValue(); intValue2 <= (((Integer) this.mIdentifierStartOffsets.get(i7)).intValue() + byteArrayOfSpecifiedEndianness.length) - 1; intValue2++) {
                bArr[intValue2 - 2] = byteArrayOfSpecifiedEndianness[intValue2 - ((Integer) this.mIdentifierStartOffsets.get(i7)).intValue()];
            }
        }
        if (!(this.mPowerStartOffset == null || this.mPowerEndOffset == null)) {
            for (int intValue3 = this.mPowerStartOffset.intValue(); intValue3 <= this.mPowerEndOffset.intValue(); intValue3++) {
                bArr[intValue3 - 2] = (byte) ((beacon.getTxPower() >> ((intValue3 - this.mPowerStartOffset.intValue()) * 8)) & 255);
            }
        }
        int i8 = 0;
        while (i8 < this.mDataStartOffsets.size()) {
            long longValue = ((Long) beacon.getDataFields().get(i8)).longValue();
            int intValue4 = ((Integer) this.mDataEndOffsets.get(i8)).intValue() - ((Integer) this.mDataStartOffsets.get(i8)).intValue();
            int i9 = 0;
            while (i9 <= intValue4) {
                int i10 = i8;
                bArr[(((Integer) this.mDataStartOffsets.get(i8)).intValue() - 2) + (!((Boolean) this.mDataLittleEndianFlags.get(i8)).booleanValue() ? intValue4 - i9 : i9)] = (byte) ((int) ((longValue >> (i9 * 8)) & 255));
                i9++;
                i8 = i10;
            }
            i8++;
        }
        return bArr;
    }

    public BeaconParser setMatchingBeaconTypeCode(Long l) {
        this.mMatchingBeaconTypeCode = l;
        return this;
    }

    public int getIdentifierByteCount(int i) {
        return (((Integer) this.mIdentifierEndOffsets.get(i)).intValue() - ((Integer) this.mIdentifierStartOffsets.get(i)).intValue()) + 1;
    }

    public int getIdentifierCount() {
        return this.mIdentifierStartOffsets.size();
    }

    public int getDataFieldCount() {
        return this.mDataStartOffsets.size();
    }

    public String getLayout() {
        return this.mBeaconLayout;
    }

    public int getPowerCorrection() {
        return this.mDBmCorrection.intValue();
    }

    protected static String bytesToHex(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            byte b = bArr[i] & Pdu.MANUFACTURER_DATA_PDU_TYPE;
            int i2 = i * 2;
            cArr[i2] = HEX_ARRAY[b >>> 4];
            cArr[i2 + 1] = HEX_ARRAY[b & 15];
        }
        return new String(cArr);
    }

    public static byte[] longToByteArray(long j, int i) {
        return longToByteArray(j, i, true);
    }

    public static byte[] longToByteArray(long j, int i, boolean z) {
        byte[] bArr = new byte[i];
        for (int i2 = 0; i2 < i; i2++) {
            int i3 = ((i - (z ? i2 : (i - i2) - 1)) - 1) * 8;
            bArr[i2] = (byte) ((int) ((j & (255 << i3)) >> ((int) ((long) i3))));
        }
        return bArr;
    }

    private int calculateLayoutSize() {
        int i = 0;
        if (this.mIdentifierEndOffsets != null) {
            for (Integer intValue : this.mIdentifierEndOffsets) {
                int intValue2 = intValue.intValue();
                if (intValue2 > i) {
                    i = intValue2;
                }
            }
        }
        if (this.mDataEndOffsets != null) {
            for (Integer intValue3 : this.mDataEndOffsets) {
                int intValue4 = intValue3.intValue();
                if (intValue4 > i) {
                    i = intValue4;
                }
            }
        }
        if (this.mPowerEndOffset != null && this.mPowerEndOffset.intValue() > i) {
            i = this.mPowerEndOffset.intValue();
        }
        if (this.mServiceUuidEndOffset != null && this.mServiceUuidEndOffset.intValue() > i) {
            i = this.mServiceUuidEndOffset.intValue();
        }
        return i + 1;
    }

    private boolean byteArraysMatch(byte[] bArr, int i, byte[] bArr2) {
        int length = bArr2.length;
        if (bArr.length - i < length) {
            return false;
        }
        for (int i2 = 0; i2 < length; i2++) {
            if (bArr[i + i2] != bArr2[i2]) {
                return false;
            }
        }
        return true;
    }

    private String byteArrayToString(byte[] bArr) {
        StringBuilder sb = new StringBuilder();
        for (byte valueOf : bArr) {
            sb.append(String.format("%02x", new Object[]{Byte.valueOf(valueOf)}));
            sb.append(StringUtils.SPACE);
        }
        return sb.toString().trim();
    }

    private String byteArrayToFormattedString(byte[] bArr, int i, int i2, boolean z) {
        int i3 = i2 - i;
        int i4 = i3 + 1;
        byte[] bArr2 = new byte[i4];
        int i5 = 0;
        if (z) {
            for (int i6 = 0; i6 <= i3; i6++) {
                bArr2[i6] = bArr[((bArr2.length + i) - 1) - i6];
            }
        } else {
            for (int i7 = 0; i7 <= i3; i7++) {
                bArr2[i7] = bArr[i + i7];
            }
        }
        if (i4 < 5) {
            long j = 0;
            while (i5 < bArr2.length) {
                i5++;
                j += ((long) (bArr2[(bArr2.length - i5) - 1] & Pdu.MANUFACTURER_DATA_PDU_TYPE)) * ((long) Math.pow(256.0d, ((double) i5) * 1.0d));
            }
            return Long.toString(j);
        }
        String bytesToHex = bytesToHex(bArr2);
        if (bArr2.length == 16) {
            StringBuilder sb = new StringBuilder();
            sb.append(bytesToHex.substring(0, 8));
            sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
            sb.append(bytesToHex.substring(8, 12));
            sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
            sb.append(bytesToHex.substring(12, 16));
            sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
            sb.append(bytesToHex.substring(16, 20));
            sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
            sb.append(bytesToHex.substring(20, 32));
            return sb.toString();
        }
        StringBuilder sb2 = new StringBuilder();
        sb2.append("0x");
        sb2.append(bytesToHex);
        return sb2.toString();
    }

    @TargetApi(9)
    private byte[] ensureMaxSize(byte[] bArr, int i) {
        if (bArr.length >= i) {
            return bArr;
        }
        return Arrays.copyOf(bArr, i);
    }

    public int hashCode() {
        return Arrays.hashCode(new Object[]{this.mMatchingBeaconTypeCode, this.mIdentifierStartOffsets, this.mIdentifierEndOffsets, this.mIdentifierLittleEndianFlags, this.mDataStartOffsets, this.mDataEndOffsets, this.mDataLittleEndianFlags, this.mIdentifierVariableLengthFlags, this.mMatchingBeaconTypeCodeStartOffset, this.mMatchingBeaconTypeCodeEndOffset, this.mServiceUuidStartOffset, this.mServiceUuidEndOffset, this.mServiceUuid, this.mExtraFrame, this.mPowerStartOffset, this.mPowerEndOffset, this.mDBmCorrection, this.mLayoutSize, this.mAllowPduOverflow, this.mIdentifier, this.mHardwareAssistManufacturers, this.extraParsers});
    }

    public boolean equals(Object obj) {
        try {
            BeaconParser beaconParser = (BeaconParser) obj;
            if (beaconParser.mBeaconLayout != null && beaconParser.mBeaconLayout.equals(this.mBeaconLayout) && beaconParser.mIdentifier != null && beaconParser.mIdentifier.equals(this.mIdentifier)) {
                return true;
            }
        } catch (ClassCastException unused) {
        }
        return false;
    }
}
