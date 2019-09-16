package org.altbeacon.beacon.service.scanner;

import android.annotation.TargetApi;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanFilter.Builder;
import android.os.ParcelUuid;
import java.util.ArrayList;
import java.util.List;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.logging.LogManager;
import org.apache.commons.lang3.StringUtils;

@TargetApi(21)
public class ScanFilterUtils {
    public static final String TAG = "ScanFilterUtils";

    class ScanFilterData {
        public byte[] filter;
        public int manufacturer;
        public byte[] mask;
        public Long serviceUuid = null;

        ScanFilterData() {
        }
    }

    public List<ScanFilterData> createScanFilterDataForBeaconParser(BeaconParser beaconParser) {
        int[] hardwareAssistManufacturers;
        ArrayList arrayList = new ArrayList();
        for (int i : beaconParser.getHardwareAssistManufacturers()) {
            Long serviceUuid = beaconParser.getServiceUuid();
            long longValue = beaconParser.getMatchingBeaconTypeCode().longValue();
            int matchingBeaconTypeCodeStartOffset = beaconParser.getMatchingBeaconTypeCodeStartOffset();
            int matchingBeaconTypeCodeEndOffset = beaconParser.getMatchingBeaconTypeCodeEndOffset();
            int i2 = (matchingBeaconTypeCodeEndOffset + 1) - 2;
            byte[] bArr = new byte[i2];
            byte[] bArr2 = new byte[i2];
            byte[] longToByteArray = BeaconParser.longToByteArray(longValue, (matchingBeaconTypeCodeEndOffset - matchingBeaconTypeCodeStartOffset) + 1);
            for (int i3 = 2; i3 <= matchingBeaconTypeCodeEndOffset; i3++) {
                int i4 = i3 - 2;
                if (i3 < matchingBeaconTypeCodeStartOffset) {
                    bArr[i4] = 0;
                    bArr2[i4] = 0;
                } else {
                    bArr[i4] = longToByteArray[i3 - matchingBeaconTypeCodeStartOffset];
                    bArr2[i4] = -1;
                }
            }
            ScanFilterData scanFilterData = new ScanFilterData();
            scanFilterData.manufacturer = i;
            scanFilterData.filter = bArr;
            scanFilterData.mask = bArr2;
            scanFilterData.serviceUuid = serviceUuid;
            arrayList.add(scanFilterData);
        }
        return arrayList;
    }

    public List<ScanFilter> createScanFiltersForBeaconParsers(List<BeaconParser> list) {
        ArrayList arrayList = new ArrayList();
        for (BeaconParser createScanFilterDataForBeaconParser : list) {
            for (ScanFilterData scanFilterData : createScanFilterDataForBeaconParser(createScanFilterDataForBeaconParser)) {
                Builder builder = new Builder();
                if (scanFilterData.serviceUuid != null) {
                    String format = String.format("0000%04X-0000-1000-8000-00805f9b34fb", new Object[]{scanFilterData.serviceUuid});
                    String str = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
                    ParcelUuid fromString = ParcelUuid.fromString(format);
                    ParcelUuid fromString2 = ParcelUuid.fromString(str);
                    if (LogManager.isVerboseLoggingEnabled()) {
                        String str2 = TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("making scan filter for service: ");
                        sb.append(format);
                        sb.append(StringUtils.SPACE);
                        sb.append(fromString);
                        LogManager.m260d(str2, sb.toString(), new Object[0]);
                        String str3 = TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("making scan filter with service mask: ");
                        sb2.append(str);
                        sb2.append(StringUtils.SPACE);
                        sb2.append(fromString2);
                        LogManager.m260d(str3, sb2.toString(), new Object[0]);
                    }
                    builder.setServiceUuid(fromString, fromString2);
                } else {
                    builder.setServiceUuid(null);
                    builder.setManufacturerData(scanFilterData.manufacturer, scanFilterData.filter, scanFilterData.mask);
                }
                ScanFilter build = builder.build();
                if (LogManager.isVerboseLoggingEnabled()) {
                    String str4 = TAG;
                    StringBuilder sb3 = new StringBuilder();
                    sb3.append("Set up a scan filter: ");
                    sb3.append(build);
                    LogManager.m260d(str4, sb3.toString(), new Object[0]);
                }
                arrayList.add(build);
            }
        }
        return arrayList;
    }
}
