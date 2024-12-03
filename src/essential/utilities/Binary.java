/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.progresive.Few;
import org.jetbrains.annotations.NotNull;
import essential.progresive.Lot;

import java.util.Arrays;

import static essential.progresive.Pr.*;


public class Binary {

public static final byte BIN_BOOLEAN_TRUE = (byte) 0x80;
public static final byte BIN_BOOLEAN_FALSE = (byte) 0x8F;

static final byte BIN_SHORT = (byte) 0x90;
static final byte BIN_INT = (byte) 0x91;
static final byte BIN_LONG = (byte) 0x92;
static final byte BIN_FLOAT = (byte) 0x93;
static final byte BIN_DOUBLE = (byte) 0x94;

static final byte BIN_BOOLEANS = (byte) 0x95;
static final byte BIN_SHORTS = (byte) 0x96;
static final byte BIN_INTS = (byte) 0x97;
static final byte BIN_LONGS = (byte) 0x98;
static final byte BIN_FLOATS = (byte) 0x99;
static final byte BIN_DOUBLES = (byte) 0x9A;

public static final byte BIN_CHAR = (byte) 0xA0;
public static final byte BIN_STRING = (byte) 0xA1;

static final byte BIN_TIME = (byte) 0xB0;
static final byte BIN_DATE = (byte) 0xB1;

public static final byte BIN_SHARE_INDEX = (byte) 0xF0;
public static final byte BIN_FEW = (byte) 0xF1;
public static final byte BIN_LOT = (byte) 0xF2;
public static final byte BIN_LOT_BEGIN = (byte) 0xF3;
public static final byte BIN_LOT_END = (byte) 0xF4;


//region Common Functions

/**
 * Convert a long to a byte array in big-endian order.
 *
 * @param n the number to convert
 * @return the byte array
 */
public static byte @NotNull [] encodeI64(long n) {
    byte[] bin = new byte[8];
    for (int i = 7; i >= 0; i -= 1) {
        bin[i] = (byte) n;
        n = n >>> 8;
    }
    return bin;
}

public static byte @NotNull [] extendTo64Bits(byte[] bin, int start, int bound) {
    int len = bound - start;
    if (len <= 0 || len > 8) {
        throw new RuntimeException(String.format(Msg.INVALID_RANGE, start, bound));
    } else {
        byte[] uuf = new byte[8];
        for (int i = bound - 1, j = 7; i >= start; i -= 1, j -= 1) {
            uuf[j] = bin[i];
        }
        if (bin[start] < 0 && len < 8) {
            for (int i = 0; i < 8 - len; i += 1) {
                uuf[i] = -1;
            }
        }
        return uuf;
    }
}

/**
 * Convert a big-endian byte array to a long.
 *
 * @param bin the 8-bytes array
 * @return the long
 */
public static long decodeI64(byte[] bin) {
    long n = 0;
    for (int i = 0; i < 8; i += 1) {
        n = n << 8;
        n = n | (bin[i] & 0xFF);
    }
    return n;
}

public static byte @NotNull [] encodeBoolean(boolean b) {
    if (b) {
        return new byte[]{BIN_BOOLEAN_TRUE};
    } else {
        return new byte[]{BIN_BOOLEAN_FALSE};
    }
}

public static byte @NotNull [] encodeChar(char c) {
    byte[] ooo = BinaryMate.encodePureChar(c);
    byte[] xxx = new byte[ooo.length + 1];
    xxx[0] = BIN_CHAR;
    System.arraycopy(ooo, 0, xxx, 1, ooo.length);
    return xxx;
}

public static @NotNull Few decodeChar(byte @NotNull [] bin, int start) {
    int sz = BinaryMate.sizeofChar(bin, start);
    char c = BinaryMate.decodePureChar(bin, start, start + sz);
    return few(start + sz, c);
}

public static byte @NotNull [] encodeString(@NotNull String str) {
    byte[] ooo = BinaryMate.encodePureString(str);
    byte[] xxx = new byte[ooo.length + 2];
    xxx[0] = BIN_STRING;
    System.arraycopy(ooo, 0, xxx, 1, ooo.length);
    return xxx;
}

public static @NotNull Few decodeString(byte @NotNull [] bin, int start) {
    StringBuilder builder = new StringBuilder();
    int i = start;
    while (bin[i] != 0) {
        int sz = BinaryMate.sizeofChar(bin, i);
        char c = BinaryMate.decodePureChar(bin, i, i + sz);
        builder.append(c);
        i += sz;
    }
    return few(i + 1, builder.toString());
}

public static int sizeofBytes(@NotNull Lot bins) {
    int bytes = 0;
    while (!bins.isEmpty()) {
        byte[] bs = (byte[]) bins.car();
        bytes += bs.length;
        bins = bins.cdr();
    }
    return bytes;
}

public static byte @NotNull [] connectBytes(@NotNull Lot lt) {
    int bytes = sizeofBytes(lt);
    byte[] bin = new byte[bytes];
    int i = 0;
    while (!lt.isEmpty()) {
        byte[] elem = (byte[]) lt.car();
        System.arraycopy(elem, 0, bin, i, elem.length);
        i += elem.length;
        lt = lt.cdr();
    }
    return bin;
}
//endregion


//region Variable Length i32 Integer

public static byte @NotNull [] encodeVarI32(int n) {
    if (-0x40 <= n && n < 0x40) {                   // 7 bits
        return new byte[]{(byte) (n & 0x7F)};
    } else if (-0x2000 <= n && n < 0x2000) {        // 14 bits
        byte[] bin = new byte[2];
        bin[1] = (byte) n;
        n = n >>> 8;
        bin[0] = (byte) ((n & 0x3F) | 0x80);
        return bin;
    } else if (-0x100000 <= n && n < 0x100000) {    // 21 bits
        byte[] bin = new byte[3];
        bin[2] = (byte) n;
        n = n >>> 8;
        bin[1] = (byte) n;
        n = n >>> 8;
        bin[0] = (byte) ((n & 0x1F) | 0xC0);
        return bin;
    } else if (-0x8000000 <= n && n < 0x8000000) {   // 28 bits
        byte[] bin = new byte[4];
        bin[3] = (byte) n;
        n = n >>> 8;
        bin[2] = (byte) n;
        n = n >>> 8;
        bin[1] = (byte) n;
        n = n >>> 8;
        bin[0] = (byte) ((n & 0x0F) | 0xE0);
        return bin;
    } else {
        byte[] bin = new byte[5];
        bin[4] = (byte) n;
        n = n >>> 8;
        bin[3] = (byte) n;
        n = n >>> 8;
        bin[2] = (byte) n;
        n = n >>> 8;
        bin[1] = (byte) n;
        bin[0] = (byte) 0xF0;
        if (bin[1] < 0) {
            bin[0] = (byte) (bin[0] | 0x07);
        }
        return bin;
    }
}

public static int sizeofVarI32(byte @NotNull [] bin, int start) {
    int bytes = bin[start] & 0xF8;
    if (bytes < 0x80) {
        return 1;
    } else if (bytes < 0xC0) {
        return 2;
    } else if (bytes < 0xE0) {
        return 3;
    } else if (bytes < 0xF0) {
        return 4;
    } else {
        return 5;
    }
}

public static int decodeVarI32(byte @NotNull [] bin, int start, int bound) {
    int bytes = bound - start;
    switch (bytes) {
    case 1 -> {
        if ((bin[start] & 0x40) == 0) {
            return bin[start];
        } else {
            return bin[start] | -0x80;
        }
    }
    case 2 -> {
        byte[] ooo = Arrays.copyOfRange(bin, start, bound);
        ooo[0] = (byte) (ooo[0] & 0x3F);
        if ((ooo[0] & 0x20) != 0) {
            ooo[0] = (byte) (ooo[0] | 0xC0);
        }
        byte[] xxx = extendTo64Bits(ooo, 0, 2);
        return (int) decodeI64(xxx);
    }
    case 3 -> {
        byte[] ooo = Arrays.copyOfRange(bin, start, bound);
        ooo[0] = (byte) (ooo[0] & 0x1F);
        if ((ooo[0] & 0x10) != 0) {
            ooo[0] = (byte) (ooo[0] | 0xE0);
        }
        byte[] xxx = extendTo64Bits(ooo, 0, 3);
        return (int) decodeI64(xxx);
    }
    case 4 -> {
        byte[] ooo = Arrays.copyOfRange(bin, start, bound);
        ooo[0] = (byte) (ooo[0] & 0x0F);
        if ((ooo[0] & 0x08) != 0) {
            ooo[0] = (byte) (ooo[0] | 0xF0);
        }
        byte[] xxx = extendTo64Bits(ooo, 0, 4);
        return (int) decodeI64(xxx);
    }
    default -> {
        byte[] ooo = Arrays.copyOfRange(bin, start, bound);
        ooo[0] = (byte) (ooo[0] & 0x07);
        if ((ooo[0] & 0x04) != 0) {
            ooo[0] = (byte) (ooo[0] | 0xF8);
        }
        byte[] xxx = extendTo64Bits(ooo, 0, 5);
        return (int) decodeI64(xxx);
    }
    }
}
//endregion


public static byte[] encode(Object datum) {
    Encoding inst = new Encoding();
    return inst.process(datum);
}

public static Object decode(byte[] bin) {
    Decoding inst = new Decoding(bin);
    return inst.process();
}

public static Object decode(byte[] bin, int start) {
    Decoding inst = new Decoding(bin, start);
    return inst.process();
}

public static int hashcode(Object datum) {
    byte[] bin = encode(datum);
    return CheckSum.fletcher32(bin);
}
}
