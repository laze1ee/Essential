/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.progresive.Few;
import essential.progresive.Lot;

import java.util.Arrays;

import static essential.progresive.Pr.*;
import static essential.progresive.Pr.hexOfByte;


class Decoding {

private final byte[] bin;
private int index;

Decoding(byte[] bin) {
    this.bin = bin;
    index = 0;
}

Decoding(byte[] bin, int index) {
    this.bin = bin;
    this.index = index;
}

Object process() {
    byte label = bin[index];
    index += 1;
    switch (label) {
    case Binary.BIN_BOOLEAN_FALSE -> {return false;}
    case Binary.BIN_BOOLEAN_TRUE -> {return true;}
    case Binary.BIN_SHORT -> {
        byte[] eef = Binary.to64Bits(bin, index, index + 2);
        index += 2;
        return (short) Binary.decodeI64(eef);
    }
    case Binary.BIN_INT -> {
        byte[] eef = Binary.to64Bits(bin, index, index + 4);
        index += 4;
        return (int) Binary.decodeI64(eef);
    }
    case Binary.BIN_LONG -> {
        byte[] eef = Binary.to64Bits(bin, index, index + 8);
        index += 8;
        return Binary.decodeI64(eef);
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
        long bits = Binary.decodeI64(eef);
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
        int sz = Binary.sizeofChar(bin, index);
        int start = index;
        index += sz;
        return Binary.decodeChar(bin, start, index);
    }
    case Binary.BIN_STRING -> {
        StringBuilder builder = new StringBuilder();
        while (bin[index] != 0) {
            int sz = Binary.sizeofChar(bin, index);
            builder.append(Binary.decodeChar(bin, index, index + sz));
            index += sz;
        }
        index += 1;
        return builder.toString();
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
        Few fw = makeFew(length, false);
        for (int i = 0; i < length; i += 1) {
            fw.set(i, process());
        }
        return fw;
    }
    case Binary.BIN_LOT -> {
        Lot lt = lot();
        if (bin[index] != Binary.BIN_LOT_BEGIN) {
            throw new RuntimeException(String.format(Msg.UNMATCHED_BIN_LABEL, hexOfByte(bin[index]), index));
        }
        index += 1;

        while (bin[index] != Binary.BIN_LOT_END) {
            lt = cons(process(), lt);
        }
        index += 1;
        return lt;
    }
    default -> throw new RuntimeException(String.format(Msg.UNMATCHED_BIN_LABEL,
                                                        hexOfByte(label), index - 1));
    }
}
}
