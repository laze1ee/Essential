/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import essential.functional.Do1;
import essential.functional.Predicate2;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

import static essential.progressive.Pr.cons;
import static essential.progressive.Pr.eq;


class Mate {

//region Few

static void quickSort(Predicate2 compare, @NotNull Few fw) {
  Lot stack = Lot.of(0, fw.length());
  while (!stack.isEmpty()) {
    int start = (int) stack.car();
    int bound = (int) stack.ref(1);
    if (bound - start < 15) {
      stack = stack.cddr();
      insertionSort(compare, fw, start, bound);
    }
    else {
      int p = partition(compare, fw, start, bound);
      stack = cons(p + 1, cons(bound, stack.cddr()));
      stack = cons(start, cons(p, stack));
    }
  }
}

private static int partition(@NotNull Predicate2 compare, @NotNull Few fw, int start, int bound) {
  int mid = (start + bound) / 2;
  bound -= 1;
  if (compare.apply(fw.ref(mid), fw.ref(start))) {
    swap(fw, start, mid);
  }
  if (compare.apply(fw.ref(bound), fw.ref(start))) {
    swap(fw, bound, start);
  }
  if (compare.apply(fw.ref(mid), fw.ref(bound))) {
    swap(fw, mid, bound);
  }
  Object pivot = fw.ref(bound);

  int i = start;
  while (start < bound) {
    if (compare.apply(fw.ref(start), pivot)) {
      swap(fw, i, start);
      i += 1;
    }
    start += 1;
  }
  swap(fw, i, bound);
  return i;
}

private static void swap(@NotNull Few fw, int i, int j) {
  Object tmp = fw.ref(i);
  fw.set(i, fw.ref(j));
  fw.set(j, tmp);
}

private static void insertionSort(Predicate2 compare, @NotNull Few fw, int start, int bound) {
  for (int i = start + 1; i < bound; i += 1) {
    Object key = fw.ref(i);
    int    j   = i - 1;
    while (start <= j && compare.apply(key, fw.ref(j))) {
      fw.set(j + 1, fw.ref(j));
      j -= 1;
    }
    fw.set(j + 1, key);
  }
}
//endregion


//region Lot

static int length(@NotNull Lot lt) {
  int n = 0;
  while (!lt.isEmpty()) {
    n += 1;
    lt = lt.cdr();
  }
  return n;
}

static boolean isBelong(Object datum, @NotNull Lot lt) {
  while (!lt.isEmpty()) {
    if (eq(datum, lt.car())) {
      return true;
    }
    lt = lt.cdr();
  }
  return false;
}

static int theHareAndTortoise(@NotNull Lot lt) {
  if (lt.isEmpty()) {
    return 0;
  }
  else if (lt.cdr().isEmpty()) {
    return 1;
  }
  else {
    Lot hare     = lt.cddr();
    Lot tortoise = lt;
    int count    = 1;
    while (true) {
      if (hare.isEmpty()) {
        return count * 2;
      }
      else if (hare.cdr().isEmpty()) {
        return count * 2 + 1;
      }
      else if (hare == tortoise) {
        return -1;
      }
      else {
        hare = hare.cddr();
        tortoise = tortoise.cdr();
        count += 1;
      }
    }
  }
}
//endregion


//region To String

static @NotNull String charToString(char c) {
  switch (c) {
    case 8 -> { return "#\\backspace"; }
    case 9 -> { return "#\\tab"; }
    case 0xA -> { return "#\\newline"; }
    case 0xD -> { return "#\\return"; }
    case 0x20 -> { return "#\\space"; }
    default -> {
      if (Character.isISOControl(c)) {
        return String.format("#\\u%X", (int) c);
      }
      else {
        return String.format("#\\%c", c);
      }
    }
  }
}

static @NotNull String dataString(@NotNull String str) {
  int           bound   = str.length();
  StringBuilder builder = new StringBuilder("\"");
  for (int i = 0; i < bound; i += 1) {
    char c = str.charAt(i);
    switch (c) {
      case '\b' -> builder.append("\\b");
      case '\t' -> builder.append("\\t");
      case '\n' -> builder.append("\\n");
      case '\r' -> builder.append("\\r");
      case '"' -> builder.append("\\\"");
      case '\\' -> builder.append("\\\\");
      default -> builder.append(c);
    }
  }
  builder.append('"');
  return builder.toString();
}

private static final char[] HEX_STR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                                 'A', 'B', 'C', 'D', 'E', 'F'};

private static @NotNull String hexOfByte(byte b) {
  return new String(new char[]{HEX_STR[(b >> 4) & 0xF], HEX_STR[b & 0xF]});
}

static @NotNull String arrayToString(@NotNull Object array) {
  return switch (array) {
    case boolean[] bs -> String.format("#1(%s)", arrayToStringMate(Pr::toString, bs, bs.length));
    case byte[] bs ->
        String.format("#u8(%s)", arrayToStringMate(o -> hexOfByte((byte) o), bs, bs.length));
    case short[] ss -> String.format("#i16(%s)", arrayToStringMate(Object::toString, ss, ss.length));
    case int[] ins -> String.format("#i32(%s)", arrayToStringMate(Object::toString, ins, ins.length));
    case long[] ls -> String.format("#i64(%s)", arrayToStringMate(Object::toString, ls, ls.length));
    case float[] fs -> String.format("#f32(%s)", arrayToStringMate(Object::toString, fs, fs.length));
    case double[] ds -> String.format("#f64(%s)", arrayToStringMate(Object::toString, ds, ds.length));
    default ->
        throw new RuntimeException(String.format("unsupported array type %s for printing", array));
  };
}

static @NotNull String arrayToStringMate(Do1 fn, Object array, int bound) {
  if (bound == 0) {
    return "";
  }
  else {
    StringBuilder builder = new StringBuilder();
    bound = bound - 1;
    for (int i = 0; i < bound; i = i + 1) {
      builder.append(fn.apply(Array.get(array, i)));
      builder.append(" ");
    }
    builder.append(fn.apply(Array.get(array, bound)));
    return builder.toString();
  }
}
//endregion


//region Comparison

static boolean numberEq(Number n1, Number n2) {
  return switch (n1) {
    case Byte b1 when n2 instanceof Byte b2 -> (byte) b1 == b2;
    case Short s1 when n2 instanceof Short s2 -> (short) s1 == s2;
    case Integer i1 when n2 instanceof Integer i2 -> (int) i1 == i2;
    case Long l1 when n2 instanceof Long l2 -> (long) l1 == l2;
    case Float f1 when n2 instanceof Float f2 -> (float) f1 == f2;
    case Double d1 when n2 instanceof Double d2 -> (double) d1 == d2;
    case null, default -> false;
  };
}

static boolean numberLess(@NotNull Number n1, Number n2) {
  return switch (n1) {
    case Byte b1 when n2 instanceof Byte b2 -> (byte) b1 < b2;
    case Short s1 when n2 instanceof Short s2 -> (short) s1 < s2;
    case Integer i1 when n2 instanceof Integer i2 -> (int) i1 < i2;
    case Long l1 when n2 instanceof Long l2 -> (long) l1 < l2;
    case Float f1 when n2 instanceof Float f2 -> (float) f1 < f2;
    case Double d1 when n2 instanceof Double d2 -> (double) d1 < d2;
    default -> false;
  };
}
//endregion
}
