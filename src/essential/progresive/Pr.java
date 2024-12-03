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

import java.util.Arrays;
import java.util.Random;


public class Pr {

//region Lot

/**
 * Constructs a Lot from the given arguments.
 *
 * @param args the elements to be included in the Lot.
 *             If no arguments are provided, an empty Lot (LotEnd) is returned.
 * @return a Lot containing the provided elements.
 */
public static @NotNull Lot lot(@NotNull Object @NotNull ... args) {
    int length = args.length;
    if (length == 0) {
        return new LotEnd();
    } else {
        Lot lt = new LotEnd();
        for (int i = length - 1; 0 <= i; i -= 1) {
            lt = new LotPair(args[i], lt);
        }
        return lt;
    }
}

/**
 * Constructs a new Lot by prepending the given datum to the existing Lot.
 *
 * @param datum the element to be added at the beginning of the Lot.
 * @param lt    the Lot to which the datum will be prepended.
 * @return a new Lot with the datum as the first element followed by the elements of the original Lot.
 */
@Contract("_, _ -> new")
public static @NotNull Lot cons(@NotNull Object datum, @NotNull Lot lt) {return new LotPair(datum, lt);}

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
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, lt1));
    } else {
        Lot head = new LotPair(lt1.car(), new LotEnd());
        Lot ooo = head;
        Lot xxx = lt1.cdr();
        while (!xxx.isEmpty()) {
            ((LotPair) ooo).next = new LotPair(xxx.car(), new LotEnd());
            ooo = ooo.cdr();
            xxx = xxx.cdr();
        }
        ((LotPair) ooo).next = lt2;
        return head;
    }
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
public static @NotNull Few few(@NotNull Object @NotNull ... args) {return new Few(args);}

/**
 * Creates a new Few object with a specified length, initializing all elements to the provided datum.
 *
 * @param length the length of the Few to be created. Must be a non-negative integer.
 * @param datum  the object to fill the Few with.
 * @return a Few containing the specified number of elements, all set to the given datum.
 * @throws RuntimeException if the length is negative.
 */
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

/**
 * Creates a new Few object with a specified length, initializing all elements to 0.
 *
 * @param length the length of the Few to be created. Must be a non-negative integer.
 * @return a Few containing the specified number of elements, all set to 0.
 * @throws RuntimeException if the length is negative.
 */
@Contract("_ -> new")
public static @NotNull Few makeFew(int length) {return makeFew(length, 0);}
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
        throw new RuntimeException(String.format(Msg.UNDEFINED_COMPARE,
                                                 datum1.getClass().getName(), datum2.getClass().getName()));
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
