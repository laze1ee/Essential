/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Date;
import essential.datetime.Time;
import essential.progresive.Few;
import essential.progresive.Lot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static essential.progresive.Pr.*;


class Decoding {

private static final String SET_CDR = "set-cdr";
private static final String SET_CAR = "set-car";
private static final String SET_FEW = "set-few";

private final byte[] bin;
private int index;
private Few share;
private Lot stack;

Decoding(byte[] bin) {
    this.bin = bin;
    index = 0;
    stack = lot();
}

Object process() {
    if (bin[index] != Binary.BIN_FEW) {
        throw new RuntimeException(String.format(Msg.UNMATCHED_BIN_LABEL, hexOfByte(bin[index]), index));
    }
    index += 1;
    int sz = Binary.sizeOfU32(bin, index);
    int len = Binary.decodeU32(bin, index, index + sz);
    share = makeFew(len, false);
    index += sz;

    for (int i = 0; i < len; i += 1) {
        fewSet(share, i, decodeElem());
    }
    while (!stack.isEmpty()) {
        Few pack = (Few) car(stack);
        String operator = (String) ref0(pack);
        switch (operator) {
        case SET_CDR -> {
            Lot lt1 = (Lot) ref1(pack);
            Lot lt2 = (Lot) fewRef(share, (int) ref2(pack));
            //lt2.toString();   // force refresh lt2 for passing junit 5 test
            setCdr(lt1, lt2);
        }
        case SET_CAR -> {
            Lot lt = (Lot) ref1(pack);
            int idx = (int) ref2(pack);
            setCar(lt, fewRef(share, idx));
        }
        case SET_FEW -> {
            Few fw = (Few) ref1(pack);
            int i = (int) ref2(pack);
            int j = (int) ref3(pack);
            fewSet(fw, i, fewRef(share, j));
        }
        }
        stack = cdr(stack);
    }
    return decodeElem();
}

private Object decodeElem() {
    byte label = bin[index];
    index += 1;
    switch (label) {
    case Binary.BIN_BOOLEAN_FALSE -> {return false;}
    case Binary.BIN_BOOLEAN_TRUE -> {return true;}
    case Binary.BIN_SHORT -> {
        byte[] eef = Binary.extendTo64(bin, index, index + 2);
        index += 2;
        return (short) Binary.binaryToI64(eef);
    }
    case Binary.BIN_INT -> {
        byte[] eef = Binary.extendTo64(bin, index, index + 4);
        index += 4;
        return (int) Binary.binaryToI64(eef);
    }
    case Binary.BIN_LONG -> {
        byte[] eef = Binary.extendTo64(bin, index, index + 8);
        index += 8;
        return Binary.binaryToI64(eef);
    }
    case Binary.BIN_FLOAT -> {
        byte[] eef = Arrays.copyOfRange(bin, index, index + 4);
        int bits = 0;
        for (int i = 0; i < 4; i += 1) {
            bits = (bits << 8) | (eef[i] & 0xFF);
        }
        index += 4;
        return Float.intBitsToFloat(bits);
    }
    case Binary.BIN_DOUBLE -> {
        byte[] eef = Arrays.copyOfRange(bin, index, index + 8);
        long bits = Binary.binaryToI64(eef);
        index += 8;
        return Double.longBitsToDouble(bits);
    }
    case Binary.BIN_BOOLEANS -> {
        int sz = Binary.sizeOfU32(bin, index);
        int len = Binary.decodeU32(bin, index, index + sz);
        int start = index + sz;
        index = start + len;
        return decodeBooleans(bin, start, len);
    }
    case Binary.BIN_SHORTS -> {
        int sz = Binary.sizeOfU32(bin, index);
        int len = Binary.decodeU32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 2;
        return decodeShorts(bin, start, len);
    }
    case Binary.BIN_INTS -> {
        int sz = Binary.sizeOfU32(bin, index);
        int len = Binary.decodeU32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 4;
        return decodeInts(bin, start, len);
    }
    case Binary.BIN_LONGS -> {
        int sz = Binary.sizeOfU32(bin, index);
        int len = Binary.decodeU32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 8;
        return decodeLongs(bin, start, len);
    }
    case Binary.BIN_FLOATS -> {
        int sz = Binary.sizeOfU32(bin, index);
        int len = Binary.decodeU32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 4;
        return decodeFloats(bin, start, len);
    }
    case Binary.BIN_DOUBLES -> {
        int sz = Binary.sizeOfU32(bin, index);
        int len = Binary.decodeU32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 8;
        return decodeDoubles(bin, start, len);
    }
    case Binary.BIN_CHAR -> {
        int sz = Binary.sizeOfBinChar(bin, index);
        int start = index;
        index += sz;
        return Binary.binaryToChar(bin, start, index);
    }
    case Binary.BIN_STRING -> {
        StringBuilder builder = new StringBuilder();
        while (bin[index] != 0) {
            int sz = Binary.sizeOfBinChar(bin, index);
            builder.append(Binary.binaryToChar(bin, index, index + sz));
            index += sz;
        }
        index += 1;
        return builder.toString();
    }
    case Binary.BIN_TIME -> {
        Object time = decodeTime(bin, index);
        index += 12;
        return time;
    }
    case Binary.BIN_DATE -> {
        Object date = decodeDate(bin, index);
        index += 18;
        return date;
    }
    case Binary.BIN_LOT -> {
        Lot lt;
        if (bin[index] == Binary.BIN_LOT_BEGIN) {
            index += 1;
            if (bin[index] == Binary.BIN_LOT_END) {
                index += 1;
                return lot();
            } else {
                lt = lot();
            }
        } else if (bin[index] == Binary.BIN_SHARE_INDEX) {
            index += 1;
            int sz = Binary.sizeOfU32(bin, index);
            int idx = Binary.decodeU32(bin, index, index + sz);
            index += sz;
            Object elem_share = fewRef(share, idx);
            if (elem_share instanceof Boolean) {
                lt = lot(decodeElem());
                stack = cons(few(SET_CDR, lt, idx), stack);
            } else {
                lt = lot(elem_share);
            }
        } else {
            throw new RuntimeException(String.format(Msg.UNMATCHED_BIN_LABEL, hexOfByte(bin[index]), index));
        }

        while (bin[index] != Binary.BIN_LOT_END) {
            if (bin[index] == Binary.BIN_SHARE_INDEX) {
                index += 1;
                int sz = Binary.sizeOfU32(bin, index);
                int idx = Binary.decodeU32(bin, index, index + sz);
                index += sz;
                Object elem_share = fewRef(share, idx);
                if (elem_share instanceof Boolean) {
                    lt = cons(false, lt);
                    stack = cons(few(SET_CAR, lt, idx), stack);
                } else {
                    lt = cons(elem_share, lt);
                }
            } else {
                lt = cons(decodeElem(), lt);
            }
        }
        index += 1;
        return lt;
    }
    case Binary.BIN_FEW -> {
        int sz = Binary.sizeOfU32(bin, index);
        int len = Binary.decodeU32(bin, index, index + sz);
        index += sz;
        Few fw = makeFew(len, false);
        for (int i = 0; i < len; i += 1) {
            if (bin[index] == Binary.BIN_SHARE_INDEX) {
                index += 1;
                sz = Binary.sizeOfU32(bin, index);
                int idx = Binary.decodeU32(bin, index, index + sz);
                index += sz;
                Object datum = fewRef(share, idx);
                if (datum instanceof Boolean) {
                    stack = cons(few(SET_FEW, fw, i, idx), stack);
                } else {
                    fewSet(fw, i, datum);
                }
            } else {
                fewSet(fw, i, decodeElem());
            }
        }
        return fw;
    }
    case Binary.BIN_SHARE_INDEX -> {
        int sz = Binary.sizeOfU32(bin, index);
        int idx = Binary.decodeU32(bin, index, index + sz);
        index += sz;
        return fewRef(share, idx);
    }
    default -> throw new RuntimeException(String.format(Msg.UNMATCHED_BIN_LABEL,
                                                        hexOfByte(label), index - 1));
    }
}

private static boolean @NotNull [] decodeBooleans(byte[] bin, int start, int len) {
    boolean[] bs = new boolean[len];
    for (int i = 0, j = start; i < len; i += 1, j += 1) {
        bs[i] = (bin[j] == Binary.BIN_BOOLEAN_TRUE);
    }
    return bs;
}

private static short @NotNull [] decodeShorts(byte[] bin, int start, int len) {
    short[] ss = new short[len];
    for (int i = 0, j = start; i < len; i += 1, j += 2) {
        byte[] eef = Binary.extendTo64(bin, j, j + 2);
        ss[i] = (short) Binary.binaryToI64(eef);
    }
    return ss;
}

private static int @NotNull [] decodeInts(byte[] bin, int start, int len) {
    int[] ins = new int[len];
    for (int i = 0, j = start; i < len; i += 1, j += 4) {
        byte[] eef = Binary.extendTo64(bin, j, j + 4);
        ins[i] = (int) Binary.binaryToI64(eef);
    }
    return ins;
}

private static long @NotNull [] decodeLongs(byte[] bin, int start, int len) {
    long[] ls = new long[len];
    for (int i = 0, j = start; i < len; i += 1, j += 8) {
        byte[] eef = Binary.extendTo64(bin, j, j + 8);
        ls[i] = Binary.binaryToI64(eef);
    }
    return ls;
}

private static float @NotNull [] decodeFloats(byte[] bin, int start, int len) {
    float[] fs = new float[len];
    for (int i = 0, j = start; i < len; i += 1, j += 4) {
        byte[] eef = Arrays.copyOfRange(bin, j, j + 4);
        int bits = 0;
        for (int k = 0; k < 4; k += 1) {
            bits = (bits << 8) | (eef[k] & 0xFF);
        }
        fs[i] = Float.intBitsToFloat(bits);
    }
    return fs;
}

private static double @NotNull [] decodeDoubles(byte[] bin, int start, int sz) {
    double[] ds = new double[sz];
    for (int i = 0, j = start; i < sz; i += 1, j += 8) {
        byte[] eef = Arrays.copyOfRange(bin, j, j + 8);
        long bits = Binary.binaryToI64(eef);
        ds[i] = Double.longBitsToDouble(bits);
    }
    return ds;
}

@Contract("_, _ -> new")
private static @NotNull Time decodeTime(byte[] bin, int start) {
    byte[] eef = Binary.extendTo64(bin, start, start + 8);
    long second = Binary.binaryToI64(eef);
    eef = Binary.extendTo64(bin, start + 8, start + 12);
    int nanosecond = (int) Binary.binaryToI64(eef);
    return new Time(second, nanosecond);
}

@Contract("_, _ -> new")
private static @NotNull Date decodeDate(byte[] bin, int start) {
    byte[] eef = Binary.extendTo64(bin, start, start + 4);
    int year = (int) Binary.binaryToI64(eef);
    eef = Binary.extendTo64(bin, start + 10, start + 14);
    int nanosecond = (int) Binary.binaryToI64(eef);
    eef = Binary.extendTo64(bin, start + 14, start + 18);
    int offset = (int) Binary.binaryToI64(eef);
    return new Date(year, bin[start + 4], bin[start + 5], bin[start + 7], bin[start + 8],
                    bin[start + 9], nanosecond, offset);
}
}
