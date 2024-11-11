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
import essential.progresive.Identical;
import essential.progresive.Lot;
import essential.progresive.Pr;
import org.jetbrains.annotations.NotNull;

import static essential.progresive.Pr.*;


class Encoding {

private Few share;

Encoding() {}

byte[] process(Object datum) {
    RBTree identical = Identical.detect(datum);
    share = identical.travel().map(o -> ((Lot) o).ref(1)).toFew();

    Lot col = lot();
    int len = share.length();
    for (int i = 0; i < len; i += 1) {
        Object elem = share.ref(i);
        if (elem instanceof Lot lt) {
            col = cons(encodeShareLot(lt), col);
        } else {
            col = cons(encodeShareFew((Few) elem), col);
        }
    }
    col = cons(encodeElem(datum), col);
    col = col.reverse();
    col = cons(Binary.encodeI32(len), col);
    col = cons(new byte[]{Binary.BIN_FEW}, col);
    return Binary.serializeBinaries(col);
}

private byte @NotNull [] encodeShareLot(@NotNull Lot lt) {
    Lot col = lot();
    col = cons(new byte[]{Binary.BIN_LOT_END}, col);
    while (!lt.isEmpty()) {
        col = cons(encodeElem(lt.car()), col);
        lt = lt.cdr();
        int found = share.find(Pr::eq, lt);
        if (found >= 0) {
            col = cons(shareIndex(found), col);
            col = cons(new byte[]{Binary.BIN_LOT}, col);
            return Binary.serializeBinaries(col);
        }
    }
    col = cons(new byte[]{Binary.BIN_LOT, Binary.BIN_LOT_BEGIN}, col);
    return Binary.serializeBinaries(col);
}

private byte @NotNull [] encodeShareFew(@NotNull Few fw) {
    Lot col = lot();
    int len = fw.length();
    for (int i = 0; i < len; i += 1) {
        col = cons(encodeElem(fw.ref(i)), col);
    }
    col = col.reverse();
    col = cons(Binary.encodeI32(len), col);
    col = cons(new byte[]{Binary.BIN_FEW}, col);
    return Binary.serializeBinaries(col);
}

private byte[] encodeElem(Object elem) {
    if (elem instanceof Boolean b) {
        return Binary.encodeBoolean(b);
    } else if (elem instanceof Short s) {
        return encodeShort(s);
    } else if (elem instanceof Integer in) {
        return encodeInt(in);
    } else if (elem instanceof Long l) {
        return encodeLong(l);
    } else if (elem instanceof Float f) {
        return encodeFloat(f);
    } else if (elem instanceof Double d) {
        return encodeDouble(d);
    } else if (elem instanceof boolean[] bs) {
        return encodeBooleans(bs);
    } else if (elem instanceof short[] ss) {
        return encodeShorts(ss);
    } else if (elem instanceof int[] ins) {
        return encodeInts(ins);
    } else if (elem instanceof long[] ls) {
        return encodeLongs(ls);
    } else if (elem instanceof float[] fs) {
        return encodeFloats(fs);
    } else if (elem instanceof double[] ds) {
        return encodeDoubles(ds);
    } else if (elem instanceof Character c) {
        return Binary.encodeChar(c);
    } else if (elem instanceof String str) {
        return Binary.encodeString(str);
    } else if (elem instanceof Time t) {
        return encodeTime(t);
    } else if (elem instanceof Date d) {
        return encodeDate(d);
    } else if (elem instanceof Lot lt) {
        int found = share.find(Pr::eq, lt);
        if (found >= 0) {
            return shareIndex(found);
        } else {
            return encodeShareLot(lt);
        }
    } else if (elem instanceof Few fw) {
        int found = share.find(Pr::eq, fw);
        if (found >= 0) {
            return shareIndex(found);
        } else {
            return encodeShareFew(fw);
        }
    } else {
        throw new RuntimeException(String.format(Msg.UNSUPPORTED, elem));
    }
}

private static byte @NotNull [] shareIndex(int index) {
    byte[] ooo = Binary.encodeI32(index);
    byte[] xxx = new byte[1 + ooo.length];
    xxx[0] = Binary.BIN_SHARE_INDEX;
    System.arraycopy(ooo, 0, xxx, 1, ooo.length);
    return xxx;
}

private static byte @NotNull [] encodeShort(short n) {
    byte[] ooo = Binary.i64ToBinary(n);
    byte[] xxx = new byte[3];
    xxx[0] = Binary.BIN_SHORT;
    System.arraycopy(ooo, 6, xxx, 1, 2);
    return xxx;
}

private static byte @NotNull [] encodeInt(int n) {
    byte[] ooo = Binary.i64ToBinary(n);
    byte[] xxx = new byte[5];
    xxx[0] = Binary.BIN_INT;
    System.arraycopy(ooo, 4, xxx, 1, 4);
    return xxx;
}

private static byte @NotNull [] encodeLong(long n) {
    byte[] ooo = Binary.i64ToBinary(n);
    byte[] xxx = new byte[9];
    xxx[0] = Binary.BIN_LONG;
    System.arraycopy(ooo, 0, xxx, 1, 8);
    return xxx;
}

private static byte @NotNull [] encodeFloat(float n) {
    int bits = Float.floatToIntBits(n);
    byte[] ooo = Binary.i64ToBinary(bits);
    byte[] xxx = new byte[5];
    xxx[0] = Binary.BIN_FLOAT;
    System.arraycopy(ooo, 4, xxx, 1, 4);
    return xxx;
}

private static byte @NotNull [] encodeDouble(double n) {
    long bits = Double.doubleToLongBits(n);
    byte[] ooo = Binary.i64ToBinary(bits);
    byte[] xxx = new byte[9];
    xxx[0] = Binary.BIN_DOUBLE;
    System.arraycopy(ooo, 0, xxx, 1, 8);
    return xxx;
}

private static byte @NotNull [] encodeBooleans(boolean @NotNull [] bs) {
    byte[] len = Binary.encodeI32(bs.length);
    byte[] bin = new byte[1 + len.length + bs.length];
    bin[0] = Binary.BIN_BOOLEANS;
    System.arraycopy(len, 0, bin, 1, len.length);
    for (int i = 0, j = 1 + len.length; i < bs.length; i += 1, j += 1) {
        if (bs[i]) {
            bin[j] = Binary.BIN_BOOLEAN_TRUE;
        } else {
            bin[j] = Binary.BIN_BOOLEAN_FALSE;
        }
    }
    return bin;
}

private static byte @NotNull [] encodeShorts(short @NotNull [] ss) {
    byte[] len = Binary.encodeI32(ss.length);
    byte[] bin = new byte[1 + len.length + 2 * ss.length];
    bin[0] = Binary.BIN_SHORTS;
    System.arraycopy(len, 0, bin, 1, len.length);
    for (int i = 0; i < ss.length; i += 1) {
        byte[] ooo = Binary.i64ToBinary(ss[i]);
        System.arraycopy(ooo, 6, bin, 1 + len.length + 2 * i, 2);
    }
    return bin;
}

private static byte @NotNull [] encodeInts(int @NotNull [] ins) {
    byte[] len = Binary.encodeI32(ins.length);
    byte[] bin = new byte[1 + len.length + 4 * ins.length];
    bin[0] = Binary.BIN_INTS;
    System.arraycopy(len, 0, bin, 1, len.length);
    for (int i = 0; i < ins.length; i += 1) {
        byte[] ooo = Binary.i64ToBinary(ins[i]);
        System.arraycopy(ooo, 4, bin, 1 + len.length + 4 * i, 4);
    }
    return bin;
}

private static byte @NotNull [] encodeLongs(long @NotNull [] ls) {
    byte[] len = Binary.encodeI32(ls.length);
    byte[] bin = new byte[1 + len.length + 8 * ls.length];
    bin[0] = Binary.BIN_LONGS;
    System.arraycopy(len, 0, bin, 1, len.length);
    for (int i = 0; i < ls.length; i += 1) {
        byte[] ooo = Binary.i64ToBinary(ls[i]);
        System.arraycopy(ooo, 0, bin, 1 + len.length + 8 * i, 8);
    }
    return bin;
}

private static byte @NotNull [] encodeFloats(float @NotNull [] fs) {
    byte[] len = Binary.encodeI32(fs.length);
    byte[] bin = new byte[1 + len.length + 4 * fs.length];
    bin[0] = Binary.BIN_FLOATS;
    System.arraycopy(len, 0, bin, 1, len.length);
    for (int i = 0; i < fs.length; i += 1) {
        long bits = Float.floatToRawIntBits(fs[i]);
        byte[] ooo = Binary.i64ToBinary(bits);
        System.arraycopy(ooo, 4, bin, 1 + len.length + 4 * i, 4);
    }
    return bin;
}

private static byte @NotNull [] encodeDoubles(double @NotNull [] ds) {
    byte[] len = Binary.encodeI32(ds.length);
    byte[] bin = new byte[1 + len.length + 8 * ds.length];
    bin[0] = Binary.BIN_DOUBLES;
    System.arraycopy(len, 0, bin, 1, len.length);
    for (int i = 0; i < ds.length; i += 1) {
        long bits = Double.doubleToLongBits(ds[i]);
        byte[] ooo = Binary.i64ToBinary(bits);
        System.arraycopy(ooo, 0, bin, 1 + len.length + 8 * i, 8);
    }
    return bin;
}

private static byte @NotNull [] encodeTime(@NotNull Time t) {
    byte[] bin = new byte[13];
    bin[0] = Binary.BIN_TIME;
    byte[] bin_sec = Binary.i64ToBinary(t.second());
    System.arraycopy(bin_sec, 0, bin, 1, 8);
    byte[] b_nano = Binary.i64ToBinary(t.nanosecond());
    System.arraycopy(b_nano, 4, bin, 9, 4);
    return bin;
}

public static byte @NotNull [] encodeDate(@NotNull Date d) {
    byte[] bin = new byte[19];
    bin[0] = Binary.BIN_DATE;
    byte[] ooo = Binary.i64ToBinary(d.year());
    System.arraycopy(ooo, 4, bin, 1, 4);
    bin[5] = (byte) d.month();
    bin[6] = (byte) d.dayOfMonth();
    bin[7] = (byte) d.dayOfWeek();
    bin[8] = (byte) d.hour();
    bin[9] = (byte) d.minute();
    bin[10] = (byte) d.second();
    ooo = Binary.i64ToBinary(d.nanosecond());
    System.arraycopy(ooo, 4, bin, 11, 4);
    ooo = Binary.i64ToBinary(d.offset());
    System.arraycopy(ooo, 4, bin, 15, 4);
    return bin;
}
}
