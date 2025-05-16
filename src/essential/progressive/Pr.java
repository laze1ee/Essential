/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import essential.datetime.Time;
import essential.functional.Predicate2;
import essential.utilities.RBTree;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class Pr {

public static RBTree detectShared(Object datum) {
    Shared inst = new Shared(datum);
    inst.route();
    return inst.identical;
}


//region Lot
/**
 * Constructs a Lot from the given arguments.
 *
 * @param args the elements to be included in the Lot.
 *             If no arguments are provided, an empty Lot is returned.
 * @return a Lot containing the provided elements.
 */
public static @NotNull Lot lot(@NotNull Object @NotNull ... args) {
    int length = args.length;
    if (length == 0) {
        return new Lot();
    } else {
        Lot lt = new Lot();
        for (int i = length - 1; 0 <= i; i -= 1) {
            lt = new Lot(args[i], lt);
        }
        return lt;
    }
}

public static void setCar(@NotNull Lot lt, @NotNull Object datum) {
    if (lt.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        lt.setData(datum);
    }
}

public static void setCdr(@NotNull Lot lt1, @NotNull Lot lt2) {
    if (lt1.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        lt1.setNext(lt2);
    }
}

public static @NotNull Lot cons(@NotNull Object datum, @NotNull Lot lt) {
    return new Lot(datum, lt);
}

public static void setCons(Object datum, @NotNull Lot lt) {
    if (lt.isEmpty()) {
        lt.setData(datum);
        lt.setNext(new Lot());
    } else {
        Lot ls = cons(lt.car(), lt.cdr());
        lt.setData(datum);
        lt.setNext(ls);
    }
}

public static @NotNull Lot append(@NotNull Lot lt, Object datum) {
    if (lt.isEmpty()) {
        return new Lot(datum, new Lot());
    } else if (lt.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, lt);
        throw new RuntimeException(msg);
    } else {
        Lot head = new Lot(lt.car(), new Lot());
        Lot lll = head.cdr();
        Lot xxx = lt.cdr();
        while (!xxx.isEmpty()) {
            lll.setData(xxx.car());
            lll.setNext(new Lot());
            lll = lll.cdr();
            xxx = xxx.cdr();
        }
        lll.setData(datum);
        lll.setNext(new Lot());
        return head;
    }
}

public static void setAppend(@NotNull Lot lt, Object datum) {
    if (lt.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, lt);
        throw new RuntimeException(msg);
    } else {
        while (!lt.isEmpty()) {
            lt = lt.cdr();
        }
        lt.setData(datum);
        lt.setNext(new Lot());
    }
}

/**
 * Returns a new Lot which is the result of appending the elements of {@code lt2} to the elements of {@code lt1}.
 *
 * @param lt1 the first Lot.
 * @param lt2 the second Lot.
 * @return a new Lot which is the concatenation of the elements of {@code lt1} and {@code lt2}.
 * @throws RuntimeException if {@code lt1} is a circular breadth Lot.
 */
public static @NotNull Lot append(@NotNull Lot lt1, @NotNull Lot lt2) {
    if (lt1.isEmpty()) {
        return lt2;
    } else if (lt1.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, lt1);
        throw new RuntimeException(msg);
    } else {
        Lot head = new Lot(lt1.car(), new Lot());
        Lot lll = head;
        Lot xxx = lt1.cdr();
        while (!xxx.isEmpty()) {
            lll.setNext(new Lot(xxx.car(), new Lot()));
            lll = lll.cdr();
            xxx = xxx.cdr();
        }
        lll.setNext(lt2);
        return head;
    }
}

public static boolean isBelong(Object datum, @NotNull Lot lt) {
    if (lt.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, lt);
        throw new RuntimeException(msg);
    } else {
        return Mate.isBelong(datum, lt);
    }
}

public static boolean isBelong(Predicate2 fn, Object datum, @NotNull Lot lt) {
    if (lt.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, lt);
        throw new RuntimeException(msg);
    } else {
        while (!lt.isEmpty()) {
            if (fn.apply(datum, lt.car())) {
                return true;
            }
            lt = lt.cdr();
        }
        return false;
    }
}
//endregion


//region Few
/**
 * Constructs a Few from the given arguments.
 *
 * @param args the elements to be included in the Few.
 *             If no arguments are provided, an empty Few is returned.
 * @return a Few containing the provided elements.
 */
public static @NotNull Few few(@NotNull Object @NotNull ... args) {
    return new Few(args);
}

public static @NotNull Few makeFew(int length, @NotNull Object datum) {
    if (0 <= length) {
        Object[] arr = new Object[length];
        Arrays.fill(arr, datum);
        return new Few(arr);
    } else {
        String msg = String.format(Msg.LEN_NON_NATURAL, length);
        throw new RuntimeException(msg);
    }
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
            String msg = String.format(Msg.UNDEFINED_ARR_COMPARE, stringOf(datum1), stringOf(datum2));
            throw new RuntimeException(msg);
        }
    } else {
        String msg = String.format(Msg.UNDEFINED_COMPARE,
                                   datum1.getClass().getName(), datum2.getClass().getName());
        throw new RuntimeException(msg);
    }
}

public static boolean greater(Object datum1, Object datum2) {
    return less(datum2, datum1);
}
//endregion


//region To String
public static @NotNull String stringOf(Object datum) {
    if (datum == null) {
        return "«null»";
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
//endregion
}
