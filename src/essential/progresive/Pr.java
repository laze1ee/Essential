/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import essential.datetime.Time;
import essential.functional.Predicate2;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import essential.functional.Do1;
import essential.functional.Predicate1;

import java.util.Arrays;
import java.util.Random;


public class Pr {

//region Lot
public static @NotNull Lot lot(@NotNull Object @NotNull ... args) {
    int bound = args.length;
    if (bound == 0) {
        return new LotEnd();
    } else {
        Lot lt = new LotEnd();
        for (int i = bound - 1; 0 <= i; i -= 1) {
            lt = new LotPair(args[i], lt);
        }
        return lt;
    }
}

public static @NotNull Object car(@NotNull Lot lt) {
    if (lt.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        return ((LotPair) lt).data;
    }
}

public static @NotNull Lot cdr(@NotNull Lot lt) {
    if (lt.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        return ((LotPair) lt).next;
    }
}

public static @NotNull Object caar(@NotNull Lot lt) {
    return car((Lot) car(lt));
}

public static @NotNull Lot cddr(@NotNull Lot lt) {
    return cdr(cdr(lt));
}

public static @NotNull Lot cdar(@NotNull Lot lt) {
    return cdr((Lot) car(lt));
}

@Contract("_, _ -> new")
public static @NotNull Lot cons(@NotNull Object datum, @NotNull Lot lt) {
    return new LotPair(datum, lt);
}

public static @NotNull Object lotRef(@NotNull Lot lt, int index) {
    int i = index;
    Lot item = lt;
    while (0 <= i && !item.isEmpty()) {
        if (i == 0) {
            return car(item);
        }
        i -= 1;
        item = cdr(item);
    }
    throw new RuntimeException(String.format(Msg.INDEX_OUT, index, lt));
}

public static @NotNull Object car1(@NotNull Lot lt) {
    return lotRef(lt, 1);
}

public static @NotNull Object car2(@NotNull Lot lt) {
    return lotRef(lt, 2);
}

public static @NotNull Object car3(@NotNull Lot lt) {
    return lotRef(lt, 3);
}

public static void setCar(@NotNull Lot lt, @NotNull Object datum) {
    if (lt.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        ((LotPair) lt).data = datum;
    }
}

public static void setCdr(@NotNull Lot lt1, @NotNull Lot lt2) {
    if (lt1.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        ((LotPair) lt1).next = lt2;
    }
}

public static @NotNull Lot reverse(@NotNull Lot lt) {
    if (lt.isEmpty()) {
        return lt;
    } else if (lt.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt));
    } else {
        Lot item = new LotPair(car(lt), new LotEnd());
        lt = cdr(lt);
        while (!lt.isEmpty()) {
            item = new LotPair(car(lt), item);
            lt = cdr(lt);
        }
        return item;
    }
}

public static @NotNull Lot append(@NotNull Lot lt1, @NotNull Lot lt2) {
    if (lt1.isEmpty()) {
        return lt2;
    } else if (lt1.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt1));
    } else {
        Lot ooo = reverse(lt1);
        Lot eee = lt2;
        while (!ooo.isEmpty()) {
            eee = cons(car(ooo), eee);
            ooo = cdr(ooo);
        }
        return eee;
    }
}

public static @NotNull Lot lotHead(@NotNull Lot lt, int index) {
    if (lt.isBreadthCircle() ||
        (0 <= index && index <= Mate.length(lt))) {
        if (index == 0) {
            return new LotEnd();
        } else {
            Lot _head = lot(car(lt));
            Lot ooo = _head;
            Lot eee = cdr(lt);
            for (int i = index - 1; 0 < i; i -= 1) {
                setCdr(ooo, lot(car(eee)));
                ooo = cdr(ooo);
                eee = cdr(eee);
            }
            return _head;
        }
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, lt));
    }
}

public static @NotNull Lot lotTail(@NotNull Lot lt, int index) {
    if (lt.isBreadthCircle() ||
        (0 <= index && index <= Mate.length(lt))) {
        for (int i = index; 0 < i; i -= 1) {
            lt = cdr(lt);
        }
        return lt;
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, lt));
    }
}

public static @NotNull Lot lotCopy(@NotNull Lot lt) {
    if (lt.isEmpty()) {
        return lot();
    } else if (lt.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt));
    } else {
        Lot _head = lot(car(lt));
        Lot ooo = _head;
        Lot eee = cdr(lt);
        while (!eee.isEmpty()) {
            setCdr(ooo, lot(car(eee)));
            ooo = cdr(ooo);
            eee = cdr(eee);
        }
        return _head;
    }
}

public static @NotNull Few lotToFew(@NotNull Lot lt) {
    if (lt.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt));
    }
    int bound = Mate.length(lt);
    Few fw = makeFew(bound, 0);
    for (int i = 0; i < bound; i += 1) {
        fewSet(fw, i, car(lt));
        lt = cdr(lt);
    }
    return fw;
}

public static @NotNull Lot filterLot(Predicate1 fn, @NotNull Lot lt) {
    if (lt.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt));
    }
    Lot col = lot();
    while (!lt.isEmpty()) {
        if (fn.apply(car(lt))) {
            col = cons(car(lt), col);
        }
        lt = cdr(lt);
    }
    return reverse(col);
}

public static @NotNull Lot lotMap(Do1 fn, @NotNull Lot lt) {
    if (lt.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt));
    }
    Lot col = lot();
    while (!lt.isEmpty()) {
        col = cons(fn.apply(car(lt)), col);
        lt = cdr(lt);
    }
    return reverse(col);
}

public static boolean isBelong(Object datum, @NotNull Lot lt) {
    if (lt.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt));
    } else {
        return Mate.isBelong(datum, lt);
    }
}

public static boolean isBelong(Predicate2 fn, Object datum, @NotNull Lot lt) {
    if (lt.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt));
    } else {
        while (!lt.isEmpty()) {
            if (fn.apply(datum, car(lt))) {
                return true;
            }
            lt = cdr(lt);
        }
        return false;
    }
}
//endregion


//region Few
public static @NotNull Few few(@NotNull Object @NotNull ... args) {
    return new Few(args);
}

@Contract("_, _ -> new")
public static @NotNull Few makeFew(int length, @NotNull Object datum) {
    if (0 <= length) {
        Object[] arr = new Object[length];
        Arrays.fill(arr, datum);
        return new Few(arr);
    } else {
        throw new RuntimeException(String.format(Msg.LEN_NON_NATURAL, length));
    }
}

@Contract("_ -> new")
public static @NotNull Few makeFew(int length) {
    return makeFew(length, 0);
}

public static @NotNull Object fewRef(@NotNull Few fw, int index) {
    if (0 <= index && index < fw.data.length) {
        return fw.data[index];
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, fw));
    }
}

public static @NotNull Object ref0(Few fw) {
    return fewRef(fw, 0);
}

public static @NotNull Object ref1(Few fw) {
    return fewRef(fw, 1);
}

public static @NotNull Object ref2(Few fw) {
    return fewRef(fw, 2);
}

public static @NotNull Object ref3(Few fw) {
    return fewRef(fw, 3);
}

public static @NotNull Object ref4(Few fw) {
    return fewRef(fw, 4);
}

public static @NotNull Object ref5(Few fw) {
    return fewRef(fw, 5);
}

public static @NotNull Object ref6(Few fw) {
    return fewRef(fw, 6);
}

public static @NotNull Object ref7(Few fw) {
    return fewRef(fw, 7);
}

public static @NotNull Object ref8(Few fw) {
    return fewRef(fw, 8);
}

public static @NotNull Object ref9(Few fw) {
    return fewRef(fw, 9);
}

public static void fewSet(@NotNull Few fw, int index, Object datum) {
    if (0 <= index && index < fw.data.length) {
        fw.data[index] = datum;
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, fw));
    }
}

public static void set0(Few fw, Object datum) {
    fewSet(fw, 0, datum);
}

public static void set1(Few fw, Object datum) {
    fewSet(fw, 1, datum);
}

public static void set2(Few fw, Object datum) {
    fewSet(fw, 2, datum);
}

public static void set3(Few fw, Object datum) {
    fewSet(fw, 3, datum);
}

public static void set4(Few fw, Object datum) {
    fewSet(fw, 4, datum);
}

public static void set5(Few fw, Object datum) {
    fewSet(fw, 5, datum);
}

public static void fillFew(@NotNull Few fw, @NotNull Object datum) {
    Arrays.fill(fw.data, datum);
}

@Contract("_ -> new")
public static @NotNull Few fewCopy(@NotNull Few fw) {
    int bound = fw.data.length;
    Object[] arr = new Object[bound];
    System.arraycopy(fw.data, 0, arr, 0, bound);
    return new Few(arr);
}

public static Lot fewToLot(@NotNull Few fw) {
    int bound = fw.data.length;
    Lot lt = lot();
    for (int i = bound - 1; 0 <= i; i -= 1) {
        lt = cons(fw.data[i], lt);
    }
    return lt;
}

@Contract("_, _ -> new")
public static @NotNull Few fewMap(Do1 fn, @NotNull Few fw) {
    int bound = fw.data.length;
    Object[] arr = new Object[bound];
    for (int i = 0; i < bound; i += 1) {
        arr[i] = fn.apply(fw.data[i]);
    }
    return new Few(arr);
}

@Contract(pure = true)
public static int fewFind(Predicate2 fn, Object datum, @NotNull Few fw) {
    int bound = fw.data.length;
    for (int i = 0; i < bound; i += 1) {
        if (fn.apply(fw.data[i], datum)) {
            return i;
        }
    }
    return -1;
}
//endregion


//region Comparison
public static boolean eq(Object datum1, Object datum2) {
    if (datum1 == datum2) {
        return true;
    } else if (datum1 instanceof Boolean b1 &&
               datum2 instanceof Boolean b2) {
        return (b1 && b2) || !(b1 || b2);
    } else if (datum1 instanceof Number n1 &&
               datum2 instanceof Number n2) {
        return Mate.numberEq(n1, n2);
    } else if (datum1 instanceof Character c1 &&
               datum2 instanceof Character c2) {
        return ((char) c1) == c2;
    } else {
        return false;
    }
}

public static boolean equal(Object datum1, Object datum2) {
    if (eq(datum1, datum2)) {
        return true;
    } else if (datum1.getClass().isArray() &&
               datum2.getClass().isArray()) {
        if (datum1 instanceof boolean[] bs1 &&
            datum2 instanceof boolean[] bs2) {
            int r = Arrays.compare(bs1, bs2);
            return r == 0;
        } else if (datum1 instanceof byte[] bs1 &&
                   datum2 instanceof byte[] bs2) {
            int r = Arrays.compare(bs1, bs2);
            return r == 0;
        } else if (datum1 instanceof short[] ss1 &&
                   datum2 instanceof short[] ss2) {
            int r = Arrays.compare(ss1, ss2);
            return r == 0;
        } else if (datum1 instanceof int[] ins1 &&
                   datum2 instanceof int[] ins2) {
            int r = Arrays.compare(ins1, ins2);
            return r == 0;
        } else if (datum1 instanceof long[] ls1 &&
                   datum2 instanceof long[] ls2) {
            int r = Arrays.compare(ls1, ls2);
            return r == 0;
        } else if (datum1 instanceof float[] fs1 &&
                   datum2 instanceof float[] fs2) {
            int r = Arrays.compare(fs1, fs2);
            return r == 0;
        } else if (datum1 instanceof double[] ds1 &&
                   datum2 instanceof double[] ds2) {
            int r = Arrays.compare(ds1, ds2);
            return r == 0;
        } else {
            return false;
        }
    } else {
        return datum1.equals(datum2);
    }
}


/**
 * The types supported to compare size:
 * <ul><li>Number</li>
 * <li>String</li>
 * <li>Time</li>
 * <li>boolean[], byte[], int[], long[], double[]</li></ul>
 */
public static boolean less(Object datum1, Object datum2) {
    if (datum1 instanceof Number n1 &&
        datum2 instanceof Number n2) {
        return Mate.numberLess(n1, n2);
    } else if (datum1 instanceof String s1 &&
               datum2 instanceof String s2) {
        int r = s1.compareTo(s2);
        return r < 0;
    } else if (datum1 instanceof Time t1 &&
               datum2 instanceof Time t2) {
        return t1.less(t2);
    } else if (datum1.getClass().isArray() &&
               datum2.getClass().isArray()) {
        if (datum1 instanceof boolean[] bs1 &&
            datum2 instanceof boolean[] bs2) {
            int r = Arrays.compare(bs1, bs2);
            return r < 0;
        } else if (datum1 instanceof byte[] bs1 &&
                   datum2 instanceof byte[] bs2) {
            int r = Arrays.compare(bs1, bs2);
            return r < 0;
        } else if (datum1 instanceof short[] ss1 &&
                   datum2 instanceof short[] ss2) {
            int r = Arrays.compare(ss1, ss2);
            return r < 0;
        } else if (datum1 instanceof int[] ins1 &&
                   datum2 instanceof int[] ins2) {
            int r = Arrays.compare(ins1, ins2);
            return r < 0;
        } else if (datum1 instanceof long[] ls1 &&
                   datum2 instanceof long[] ls2) {
            int r = Arrays.compare(ls1, ls2);
            return r < 0;
        } else if (datum1 instanceof float[] fs1 &&
                   datum2 instanceof float[] fs2) {
            int r = Arrays.compare(fs1, fs2);
            return r < 0;
        } else if (datum1 instanceof double[] ds1 &&
                   datum2 instanceof double[] ds2) {
            int r = Arrays.compare(ds1, ds2);
            return r < 0;
        } else {
            throw new RuntimeException(String.format(Msg.UNDEFINED_ARR_COMPARE,
                                                     stringOf(datum1), stringOf(datum2)));
        }
    } else {
        throw new RuntimeException(String.format(Msg.UNDEFINED_COMPARE, datum1, datum2));
    }
}

public static boolean greater(Object datum1, Object datum2) {
    return less(datum2, datum1);
}
//endregion


//region To String
public static @NotNull String stringOf(Object datum) {
    if (datum == null) {
        return "#<null>";
    } else if (datum instanceof Boolean b) {
        if (b) {
            return "#t";
        } else {
            return "#f";
        }
    } else if (datum instanceof Character c) {
        return Mate.stringOfChar(c);
    } else if (datum instanceof String str) {
        return Mate.dataString(str);
    } else if (datum.getClass().isArray()) {
        return Mate.stringOfArray(datum);
    } else {
        return datum.toString();
    }
}

public static @NotNull String randomString(int length) {
    Random rd = new Random();
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < length; i = i + 1) {
        int index = rd.nextInt(Mate.CHARS_SET.length);
        char c = Mate.CHARS_SET[index];
        builder.append(c);
    }
    return builder.toString();
}

public static @NotNull String hexOfByte(byte b) {
    return new String(new char[]{Mate.HEX_STR[(b >> 4) & 0xF], Mate.HEX_STR[b & 0xF]});
}
//endregion
}
