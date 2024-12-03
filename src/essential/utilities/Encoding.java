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

import static essential.progresive.Pr.cons;
import static essential.progresive.Pr.lot;


class Encoding {

private Few share;

Encoding() {}

byte[] process(Object datum) {
    RBTree identical = Identical.detect(datum);
    share = identical.travel()
                     .map(o -> ((Lot) o).ref(1))
                     .toFew();

    Lot col = lot();
    int length = share.length();
    for (int i = 0; i < length; i += 1) {
        Object elem = share.ref(i);
        if (elem instanceof Lot lt) {
            col = cons(encodeShareLot(lt), col);
        } else if (elem instanceof Few fw) {
            col = cons(encodeShareFew(fw), col);
        } else {
            col = cons(encodeNonContainer(elem), col);
        }
    }
    col = cons(encodeElem(datum), col);
    col = col.reverse();
    col = cons(Binary.encodeVarI32(length), col);
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
    int found = share.find(Pr::eq, elem);
    if (found >= 0) {
        return shareIndex(found);
    } else if (elem instanceof Few fw) {
        return encodeShareFew(fw);
    } else if (elem instanceof Lot lt) {
        return encodeShareLot(lt);
    } else {
        return encodeNonContainer(elem);
    }
}

private static byte @NotNull [] shareIndex(int index) {
    byte[] ooo = Binary.encodeVarI32(index);
    byte[] xxx = new byte[1 + ooo.length];
    xxx[0] = Binary.BIN_SHARE_INDEX;
    System.arraycopy(ooo, 0, xxx, 1, ooo.length);
    return xxx;
}

private static byte[] encodeNonContainer(Object datum) {
    if (datum instanceof Boolean b) {
        return Binary.encodeBoolean(b);
    } else if (datum instanceof Short s) {
        return BinaryMate.encodeShort(s);
    } else if (datum instanceof Integer in) {
        return BinaryMate.encodeInt(in);
    } else if (datum instanceof Long l) {
        return BinaryMate.encodeLong(l);
    } else if (datum instanceof Float f) {
        return BinaryMate.encodeFloat(f);
    } else if (datum instanceof Double d) {
        return BinaryMate.encodeDouble(d);
    } else if (datum instanceof boolean[] bs) {
        return BinaryMate.encodeBooleans(bs);
    } else if (datum instanceof short[] ss) {
        return BinaryMate.encodeShorts(ss);
    } else if (datum instanceof int[] ins) {
        return BinaryMate.encodeInts(ins);
    } else if (datum instanceof long[] ls) {
        return BinaryMate.encodeLongs(ls);
    } else if (datum instanceof float[] fs) {
        return BinaryMate.encodeFloats(fs);
    } else if (datum instanceof double[] ds) {
        return BinaryMate.encodeDoubles(ds);
    } else if (datum instanceof Character c) {
        return Binary.encodeChar(c);
    } else if (datum instanceof String str) {
        return Binary.encodeString(str);
    } else if (datum instanceof Time t) {
        return BinaryMate.encodeTime(t);
    } else if (datum instanceof Date d) {
        return BinaryMate.encodeDate(d);
    } else {
        throw new RuntimeException(String.format(Msg.UNSUPPORTED, datum));
    }
}
}
