/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.progresive.Few;
import essential.progresive.Lot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static essential.progresive.Pr.*;


class Decoding {

private static class Nothing {}

private static final String SET_CDR = "set-cdr";
private static final String SET_CAR = "set-car";
private static final String SET_FEW = "set-few";

private static final char[] HEX_STR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                                 'A', 'B', 'C', 'D', 'E', 'F'};

private static @NotNull String hexOfByte(byte b) {
    return new String(new char[]{HEX_STR[(b >> 4) & 0xF], HEX_STR[b & 0xF]});
}

private final byte[] bin;
private int index;
private Few share;
private Lot stack;

Decoding(byte[] bin) {
    this.bin = bin;
    index = 0;
    stack = lot();
}

Decoding(byte[] bin, int index) {
    this.bin = bin;
    this.index = index;
    stack = lot();
}

Object process() {
    if (bin[index] != Binary.BIN_FEW) {
        String msg = String.format(Msg.UNMATCHED_BIN_LABEL, hexOfByte(bin[index]), index);
        throw new RuntimeException(msg);
    }
    index += 1;
    int sz = Binary.sizeofVarI32(bin, index);
    int len = Binary.decodeVarI32(bin, index, index + sz);
    share = makeFew(len, new Nothing());
    index += sz;

    for (int i = 0; i < len; i += 1) {
        share.set(i, decodeElem());
    }
    while (!stack.isEmpty()) {
        Few pack = (Few) stack.car();
        String operator = (String) pack.ref(0);
        switch (operator) {
        case SET_CDR -> {
            Lot lt1 = (Lot) pack.ref(1);
            Lot lt2 = (Lot) share.ref((int) pack.ref(2));
            setCdr(lt1, lt2);
        }
        case SET_CAR -> {
            Lot lt = (Lot) pack.ref(1);
            int idx = (int) pack.ref(2);
            setCar(lt, share.ref(idx));
        }
        case SET_FEW -> {
            Few fw = (Few) pack.ref(1);
            int i = (int) pack.ref(2);
            int j = (int) pack.ref(3);
            fw.set(i, share.ref(j));
        }
        }
        stack = stack.cdr();
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
        byte[] uuf = Binary.extendTo64Bits(bin, index, index + 2);
        index += 2;
        return (short) Binary.decodeI64(uuf);
    }
    case Binary.BIN_INT -> {
        byte[] uuf = Binary.extendTo64Bits(bin, index, index + 4);
        index += 4;
        return (int) Binary.decodeI64(uuf);
    }
    case Binary.BIN_LONG -> {
        byte[] uuf = Binary.extendTo64Bits(bin, index, index + 8);
        index += 8;
        return Binary.decodeI64(uuf);
    }
    case Binary.BIN_FLOAT -> {
        byte[] uuf = Arrays.copyOfRange(bin, index, index + 4);
        int bits = 0;
        for (int i = 0; i < 4; i += 1) {
            bits = (bits << 8) | (uuf[i] & 0xFF);
        }
        index += 4;
        return Float.intBitsToFloat(bits);
    }
    case Binary.BIN_DOUBLE -> {
        byte[] uuf = Arrays.copyOfRange(bin, index, index + 8);
        long bits = Binary.decodeI64(uuf);
        index += 8;
        return Double.longBitsToDouble(bits);
    }
    case Binary.BIN_BOOLEANS -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int len = Binary.decodeVarI32(bin, index, index + sz);
        int start = index + sz;
        index = start + len;
        return BinaryMate.decodeBooleans(bin, start, len);
    }
    case Binary.BIN_SHORTS -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int len = Binary.decodeVarI32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 2;
        return BinaryMate.decodeShorts(bin, start, len);
    }
    case Binary.BIN_INTS -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int len = Binary.decodeVarI32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 4;
        return BinaryMate.decodeInts(bin, start, len);
    }
    case Binary.BIN_LONGS -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int len = Binary.decodeVarI32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 8;
        return BinaryMate.decodeLongs(bin, start, len);
    }
    case Binary.BIN_FLOATS -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int len = Binary.decodeVarI32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 4;
        return BinaryMate.decodeFloats(bin, start, len);
    }
    case Binary.BIN_DOUBLES -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int len = Binary.decodeVarI32(bin, index, index + sz);
        int start = index + sz;
        index = start + len * 8;
        return BinaryMate.decodeDoubles(bin, start, len);
    }
    case Binary.BIN_CHAR -> {
        Few pack = Binary.decodeChar(bin, index);
        index = (int) pack.ref(0);
        return pack.ref(1);
    }
    case Binary.BIN_STRING -> {
        Few pack = Binary.decodeString(bin, index);
        index = (int) pack.ref(0);
        return pack.ref(1);
    }
    case Binary.BIN_TIME -> {
        Object time = BinaryMate.decodeTime(bin, index);
        index += 12;
        return time;
    }
    case Binary.BIN_DATE -> {
        Object date = BinaryMate.decodeDate(bin, index);
        index += 18;
        return date;
    }
    case Binary.BIN_FEW -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int length = Binary.decodeVarI32(bin, index, index + sz);
        index += sz;
        Few fw = makeFew(length, new Nothing());
        for (int i = 0; i < length; i += 1) {
            if (bin[index] == Binary.BIN_SHARE_INDEX) {
                index += 1;
                sz = Binary.sizeofVarI32(bin, index);
                int idx = Binary.decodeVarI32(bin, index, index + sz);
                index += sz;
                Object datum = share.ref(idx);
                if (datum instanceof Nothing) {
                    stack = cons(few(SET_FEW, fw, i, idx), stack);
                } else {
                    fw.set(i, datum);
                }
            } else {
                fw.set(i, decodeElem());
            }
        }
        return fw;
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
            int sz = Binary.sizeofVarI32(bin, index);
            int idx = Binary.decodeVarI32(bin, index, index + sz);
            index += sz;
            Object elem_share = share.ref(idx);
            if (elem_share instanceof Nothing) {
                lt = lot(decodeElem());
                stack = cons(few(SET_CDR, lt, idx), stack);
            } else {
                lt = lot(elem_share);
            }
        } else {
            String msg = String.format(Msg.UNMATCHED_BIN_LABEL, hexOfByte(bin[index]), index);
            throw new RuntimeException(msg);
        }

        while (bin[index] != Binary.BIN_LOT_END) {
            if (bin[index] == Binary.BIN_SHARE_INDEX) {
                index += 1;
                int sz = Binary.sizeofVarI32(bin, index);
                int idx = Binary.decodeVarI32(bin, index, index + sz);
                index += sz;
                Object elem_share = share.ref(idx);
                if (elem_share instanceof Nothing) {
                    lt = cons(new Nothing(), lt);
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
    case Binary.BIN_SHARE_INDEX -> {
        int sz = Binary.sizeofVarI32(bin, index);
        int idx = Binary.decodeVarI32(bin, index, index + sz);
        index += sz;
        return share.ref(idx);
    }
    default -> {
        String msg = String.format(Msg.UNMATCHED_BIN_LABEL, hexOfByte(label), index - 1);
        throw new RuntimeException(msg);
    }
    }
}
}
