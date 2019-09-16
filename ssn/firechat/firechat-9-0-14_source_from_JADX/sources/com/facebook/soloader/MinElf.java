package com.facebook.soloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import org.altbeacon.bluetooth.Pdu;

public final class MinElf {
    public static final int DT_NEEDED = 1;
    public static final int DT_NULL = 0;
    public static final int DT_STRTAB = 5;
    public static final int ELF_MAGIC = 1179403647;
    public static final int PN_XNUM = 65535;
    public static final int PT_DYNAMIC = 2;
    public static final int PT_LOAD = 1;

    private static class ElfError extends RuntimeException {
        ElfError(String str) {
            super(str);
        }
    }

    public static String[] extract_DT_NEEDED(File file) throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        try {
            return extract_DT_NEEDED(fileInputStream.getChannel());
        } finally {
            fileInputStream.close();
        }
    }

    public static String[] extract_DT_NEEDED(FileChannel fileChannel) throws IOException {
        long j;
        long j2;
        boolean z;
        long j3;
        long j4;
        long j5;
        long j6;
        long j7;
        FileChannel fileChannel2 = fileChannel;
        ByteBuffer allocate = ByteBuffer.allocate(8);
        allocate.order(ByteOrder.LITTLE_ENDIAN);
        if (getu32(fileChannel2, allocate, 0) != 1179403647) {
            throw new ElfError("file is not ELF");
        }
        boolean z2 = true;
        if (getu8(fileChannel2, allocate, 4) != 1) {
            z2 = false;
        }
        if (getu8(fileChannel2, allocate, 5) == 2) {
            allocate.order(ByteOrder.BIG_ENDIAN);
        }
        long r15 = z2 ? getu32(fileChannel2, allocate, 28) : get64(fileChannel2, allocate, 32);
        long j8 = z2 ? (long) getu16(fileChannel2, allocate, 44) : (long) getu16(fileChannel2, allocate, 56);
        if (z2) {
            j = 42;
        } else {
            j = 54;
        }
        int i = getu16(fileChannel2, allocate, j);
        if (j8 == 65535) {
            long r3 = z2 ? getu32(fileChannel2, allocate, 32) : get64(fileChannel2, allocate, 40);
            if (z2) {
                j7 = getu32(fileChannel2, allocate, r3 + 28);
            } else {
                j7 = getu32(fileChannel2, allocate, r3 + 44);
            }
            j8 = j7;
        }
        long j9 = r15;
        long j10 = 0;
        while (true) {
            if (j10 >= j8) {
                j2 = 0;
                break;
            }
            if (getu32(fileChannel2, allocate, j9 + (z2 ? 0 : 0)) == 2) {
                j2 = z2 ? getu32(fileChannel2, allocate, j9 + 4) : get64(fileChannel2, allocate, j9 + 8);
            } else {
                j10++;
                j9 += (long) i;
            }
        }
        long j11 = 0;
        if (j2 == 0) {
            throw new ElfError("ELF file does not contain dynamic linking information");
        }
        long j12 = j2;
        long j13 = 0;
        int i2 = 0;
        while (true) {
            if (z2) {
                z = z2;
                j3 = getu32(fileChannel2, allocate, j12 + j11);
            } else {
                z = z2;
                j3 = get64(fileChannel2, allocate, j12 + j11);
            }
            long j14 = j2;
            if (j3 == 1) {
                if (i2 == Integer.MAX_VALUE) {
                    throw new ElfError("malformed DT_NEEDED section");
                }
                i2++;
            } else if (j3 == 5) {
                j13 = z ? getu32(fileChannel2, allocate, j12 + 4) : get64(fileChannel2, allocate, j12 + 8);
            }
            long j15 = j12 + (z ? 8 : 16);
            long j16 = 0;
            if (j3 != 0) {
                j11 = 0;
                z2 = z;
                j2 = j14;
                j12 = j15;
            } else if (j13 == 0) {
                throw new ElfError("Dynamic section string-table not found");
            } else {
                int i3 = 0;
                while (true) {
                    if (((long) i3) >= j8) {
                        j4 = 0;
                        j5 = 0;
                        break;
                    }
                    if (!z) {
                    }
                    if (getu32(fileChannel2, allocate, r15 + j16) == 1) {
                        long r9 = z ? getu32(fileChannel2, allocate, r15 + 8) : get64(fileChannel2, allocate, r15 + 16);
                        long r11 = z ? getu32(fileChannel2, allocate, r15 + 20) : get64(fileChannel2, allocate, r15 + 40);
                        if (r9 <= j13 && j13 < r9 + r11) {
                            if (z) {
                                j6 = getu32(fileChannel2, allocate, r15 + 4);
                            } else {
                                j6 = get64(fileChannel2, allocate, r15 + 8);
                            }
                            j5 = j6 + (j13 - r9);
                            j4 = 0;
                        }
                    }
                    i3++;
                    r15 += (long) i;
                    j16 = 0;
                }
                if (j5 == j4) {
                    throw new ElfError("did not find file offset of DT_STRTAB table");
                }
                String[] strArr = new String[i2];
                int i4 = 0;
                while (true) {
                    long r7 = z ? getu32(fileChannel2, allocate, j14 + j4) : get64(fileChannel2, allocate, j14 + j4);
                    if (r7 == 1) {
                        strArr[i4] = getSz(fileChannel2, allocate, j5 + (z ? getu32(fileChannel2, allocate, j14 + 4) : get64(fileChannel2, allocate, j14 + 8)));
                        if (i4 == Integer.MAX_VALUE) {
                            throw new ElfError("malformed DT_NEEDED section");
                        }
                        i4++;
                    }
                    long j17 = j14 + (z ? 8 : 16);
                    if (r7 != 0) {
                        j14 = j17;
                        j4 = 0;
                    } else if (i4 == strArr.length) {
                        return strArr;
                    } else {
                        throw new ElfError("malformed DT_NEEDED section");
                    }
                }
            }
        }
    }

    private static String getSz(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        StringBuilder sb = new StringBuilder();
        while (true) {
            long j2 = j + 1;
            short u8Var = getu8(fileChannel, byteBuffer, j);
            if (u8Var == 0) {
                return sb.toString();
            }
            sb.append((char) u8Var);
            j = j2;
        }
    }

    private static void read(FileChannel fileChannel, ByteBuffer byteBuffer, int i, long j) throws IOException {
        byteBuffer.position(0);
        byteBuffer.limit(i);
        while (byteBuffer.remaining() > 0) {
            int read = fileChannel.read(byteBuffer, j);
            if (read == -1) {
                break;
            }
            j += (long) read;
        }
        if (byteBuffer.remaining() > 0) {
            throw new ElfError("ELF file truncated");
        }
        byteBuffer.position(0);
    }

    private static long get64(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 8, j);
        return byteBuffer.getLong();
    }

    private static long getu32(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 4, j);
        return ((long) byteBuffer.getInt()) & 4294967295L;
    }

    private static int getu16(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 2, j);
        return byteBuffer.getShort() & 65535;
    }

    private static short getu8(FileChannel fileChannel, ByteBuffer byteBuffer, long j) throws IOException {
        read(fileChannel, byteBuffer, 1, j);
        return (short) (byteBuffer.get() & Pdu.MANUFACTURER_DATA_PDU_TYPE);
    }
}
