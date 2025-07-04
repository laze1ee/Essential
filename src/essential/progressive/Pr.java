/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import essential.datetime.Time;
import essential.functional.Predicate2;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class Pr {

//region Lot

public static void setCar(@NotNull Lot lt, @NotNull Object datum) {
  if (lt.isEmpty()) {
    throw new RuntimeException(Msg.LOT_EMPTY);
  }
  else {
    lt.setData(datum);
  }
}

public static void setCdr(@NotNull Lot lt1, @NotNull Lot lt2) {
  if (lt1.isEmpty()) {
    throw new RuntimeException(Msg.LOT_EMPTY);
  }
  else {
    lt1.setNext(lt2);
  }
}

public static @NotNull Lot cons(@NotNull Object datum, @NotNull Lot lt) {
  return new Lot(datum, lt);
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
  }
  else if (lt1.isBreadthCircle()) {
    String msg = String.format(Msg.CIRCULAR_BREADTH, lt1);
    throw new RuntimeException(msg);
  }
  else {
    Lot head = new Lot(lt1.car(), new Lot());
    Lot lll  = head;
    Lot xxx  = lt1.cdr();
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
  }
  else {
    return Mate.isBelong(datum, lt);
  }
}

public static boolean isBelong(Predicate2 fn, Object datum, @NotNull Lot lt) {
  if (lt.isBreadthCircle()) {
    String msg = String.format(Msg.CIRCULAR_BREADTH, lt);
    throw new RuntimeException(msg);
  }
  else {
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


//region Comparison

public static boolean eq(Object datum1, Object datum2) {
  if (datum1 == datum2) {
    return true;
  }
  else if (datum1 instanceof Boolean b1 &&
           datum2 instanceof Boolean b2) {
    return (b1 && b2) || !(b1 || b2);
  }
  else if (datum1 instanceof Number n1 &&
           datum2 instanceof Number n2) {
    return Mate.numberEq(n1, n2);
  }
  else if (datum1 instanceof Character c1 &&
           datum2 instanceof Character c2) {
    return ((char) c1) == c2;
  }
  else {
    return false;
  }
}

public static boolean equal(Object datum1, Object datum2) {
  if (eq(datum1, datum2)) {
    return true;
  }
  else if (datum1.getClass().isArray() &&
           datum2.getClass().isArray()) {
    switch (datum1) {
      case boolean[] bs1 when datum2 instanceof boolean[] bs2 -> {
        return Arrays.compare(bs1, bs2) == 0;
      }
      case byte[] bs1 when datum2 instanceof byte[] bs2 -> {
        return Arrays.compare(bs1, bs2) == 0;
      }
      case short[] ss1 when datum2 instanceof short[] ss2 -> {
        return Arrays.compare(ss1, ss2) == 0;
      }
      case int[] ins1 when datum2 instanceof int[] ins2 -> {
        return Arrays.compare(ins1, ins2) == 0;
      }
      case long[] ls1 when datum2 instanceof long[] ls2 -> {
        return Arrays.compare(ls1, ls2) == 0;
      }
      case float[] fs1 when datum2 instanceof float[] fs2 -> {
        return Arrays.compare(fs1, fs2) == 0;
      }
      case double[] ds1 when datum2 instanceof double[] ds2 -> {
        return Arrays.compare(ds1, ds2) == 0;
      }
      default -> { return false; }
    }
  }
  else {
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
  }
  else if (datum1 instanceof String s1 &&
           datum2 instanceof String s2) {
    return s1.compareTo(s2) < 0;
  }
  else if (datum1 instanceof Time t1 &&
           datum2 instanceof Time t2) {
    return t1.less(t2);
  }
  else if (datum1.getClass().isArray() &&
           datum2.getClass().isArray()) {
    switch (datum1) {
      case boolean[] bs1 when datum2 instanceof boolean[] bs2 -> {
        return Arrays.compare(bs1, bs2) < 0;
      }
      case byte[] bs1 when datum2 instanceof byte[] bs2 -> {
        return Arrays.compare(bs1, bs2) < 0;
      }
      case short[] ss1 when datum2 instanceof short[] ss2 -> {
        return Arrays.compare(ss1, ss2) < 0;
      }
      case int[] ins1 when datum2 instanceof int[] ins2 -> {
        return Arrays.compare(ins1, ins2) < 0;
      }
      case long[] ls1 when datum2 instanceof long[] ls2 -> {
        return Arrays.compare(ls1, ls2) < 0;
      }
      case float[] fs1 when datum2 instanceof float[] fs2 -> {
        return Arrays.compare(fs1, fs2) < 0;
      }
      case double[] ds1 when datum2 instanceof double[] ds2 -> {
        return Arrays.compare(ds1, ds2) < 0;
      }
      default -> {
        String msg = String.format(Msg.UNDEFINED_ARR_COMPARE, stringOf(datum1), stringOf(datum2));
        throw new RuntimeException(msg);
      }
    }
  }
  else {
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
  if (datum == null) { return "«null»"; }
  else if (datum instanceof Boolean b) {
    if (b) { return "#t"; }
    else { return "#f"; }
  }
  else if (datum instanceof Character c) { return Mate.stringOfChar(c); }
  else if (datum instanceof String str) { return Mate.dataString(str); }
  else if (datum.getClass().isArray()) { return Mate.stringOfArray(datum); }
  else { return datum.toString(); }
}
//endregion
}
