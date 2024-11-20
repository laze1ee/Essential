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


class EncodingWithSharing {

private Few share;

EncodingWithSharing() {}

byte[] process(Object datum) {
    RBTree identical = Identical.detect(datum);
    share = identical.travel()
                    .map(o -> ((Lot) o).ref(1))
                    .toFew();

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
    col = cons(Binary.encodeVarI32(len), col);
    col = cons(new byte[]{Binary.BIN_FEW}, col);
    return Binary.connectBytes(col);
}

private byte @NotNull [] encodeShareFew(@NotNull Few fw) {
    Lot col = lot();
    int length = fw.length();
    for (int i = length - 1; i != -1; i -= 1) {
        col = cons(encodeElem(fw.ref(i)), col);
    }
    col = cons(Binary.encodeVarI32(length), col);
    col = cons(new byte[]{Binary.BIN_FEW}, col);
    return Binary.connectBytes(col);
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
            return Binary.connectBytes(col);
        }
    }
    col = cons(new byte[]{Binary.BIN_LOT, Binary.BIN_LOT_BEGIN}, col);
    return Binary.connectBytes(col);
}

private byte[] encodeElem(Object elem) {
    if (elem instanceof Boolean b) {
        return Binary.encodeLabelBoolean(b);
    } else if (elem instanceof Short s) {
        return BinaryMate.encodeLabelShort(s);
    } else if (elem instanceof Integer in) {
        return BinaryMate.encodeLabelInt(in);
    } else if (elem instanceof Long l) {
        return BinaryMate.encodeLabelLong(l);
    } else if (elem instanceof Float f) {
        return BinaryMate.encodeLabelFloat(f);
    } else if (elem instanceof Double d) {
        return BinaryMate.encodeLabelDouble(d);
    } else if (elem instanceof boolean[] bs) {
        return BinaryMate.encodeLabelBooleans(bs);
    } else if (elem instanceof short[] ss) {
        return BinaryMate.encodeLabelShorts(ss);
    } else if (elem instanceof int[] ins) {
        return BinaryMate.encodeLabelInts(ins);
    } else if (elem instanceof long[] ls) {
        return BinaryMate.encodeLabelLongs(ls);
    } else if (elem instanceof float[] fs) {
        return BinaryMate.encodeLabelFloats(fs);
    } else if (elem instanceof double[] ds) {
        return BinaryMate.encodeLabelDoubles(ds);
    } else if (elem instanceof Character c) {
        return Binary.encodeLabelChar(c);
    } else if (elem instanceof String str) {
        int found = share.find(Pr::eq, str);
        if (found >= 0) {
            return shareIndex(found);
        } else {
            return Binary.encodeLabelString(str);
        }
    } else if (elem instanceof Time t) {
        return BinaryMate.encodeLabelTime(t);
    } else if (elem instanceof Date d) {
        return BinaryMate.encodeLabelDate(d);
    } else if (elem instanceof Few fw) {
        int found = share.find(Pr::eq, fw);
        if (found >= 0) {
            return shareIndex(found);
        } else {
            return encodeShareFew(fw);
        }
    } else if (elem instanceof Lot lt) {
        int found = share.find(Pr::eq, lt);
        if (found >= 0) {
            return shareIndex(found);
        } else {
            return encodeShareLot(lt);
        }
    } else {
        throw new RuntimeException(String.format(Msg.UNSUPPORTED, elem));
    }
}

private static byte @NotNull [] shareIndex(int index) {
    byte[] ooo = Binary.encodeVarI32(index);
    byte[] xxx = new byte[1 + ooo.length];
    xxx[0] = Binary.BIN_SHARE_INDEX;
    System.arraycopy(ooo, 0, xxx, 1, ooo.length);
    return xxx;
}
}
