/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import essential.functional.Do1;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

import static essential.progresive.Pr.*;


class Mate {

static final char[] HEX_STR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                         'A', 'B', 'C', 'D', 'E', 'F'};
@SuppressWarnings("SpellCheckingInspection")
static final char[] CHARS_SET =
"_-ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();


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
    } else if (lt.cdr().isEmpty()) {
        return 1;
    } else {
        Lot hare = lt.cddr();
        Lot tortoise = lt;
        int count = 1;
        while (true) {
            if (hare.isEmpty()) {
                return count * 2;
            } else if (hare.cdr().isEmpty()) {
                return count * 2 + 1;
            } else if (hare == tortoise) {
                return -1;
            } else {
                hare = hare.cddr();
                tortoise = tortoise.cdr();
                count += 1;
            }
        }
    }
}
//endregion


//region Stringing

static @NotNull String stringOfChar(char c) {
    switch (c) {
    case 8 -> {return "#\\backspace";}
    case 9 -> {return "#\\tab";}
    case 0xA -> {return "#\\newline";}
    case 0xD -> {return "#\\return";}
    case 0x20 -> {return "#\\space";}
    default -> {
        if (Character.isISOControl(c)) {
            return String.format("#\\u%X", (int) c);
        } else {
            return String.format("#\\%c", c);
        }
    }
    }
}

static @NotNull String dataString(@NotNull String str) {
    int bound = str.length();
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

static @NotNull String stringOfArray(@NotNull Object arr) {
    if (arr instanceof boolean[] bs) {
        return String.format("#1(%s)", serializeArray(Pr::stringOf, bs, bs.length));
    } else if (arr instanceof byte[] bs) {
        return String.format("#u8(%s)", serializeArray(o -> hexOfByte((byte) o), bs, bs.length));
    } else if (arr instanceof short[] ss) {
        return String.format("#i16(%s)", serializeArray(Object::toString, ss, ss.length));
    } else if (arr instanceof int[] ins) {
        return String.format("#i32(%s)", serializeArray(Object::toString, ins, ins.length));
    } else if (arr instanceof long[] ls) {
        return String.format("#i64(%s)", serializeArray(Object::toString, ls, ls.length));
    } else if (arr instanceof float[] fs) {
        return String.format("#r32(%s)", serializeArray(Object::toString, fs, fs.length));
    } else if (arr instanceof double[] ds) {
        return String.format("#r64(%s)", serializeArray(Object::toString, ds, ds.length));
    } else {
        throw new RuntimeException(String.format("unsupported array type %s for printing", arr));
    }
}

static @NotNull String serializeArray(Do1 fn, Object arr, int bound) {
    if (bound == 0) {
        return "";
    } else {
        StringBuilder builder = new StringBuilder();
        bound = bound - 1;
        for (int i = 0; i < bound; i = i + 1) {
            builder.append(fn.apply(Array.get(arr, i)));
            builder.append(" ");
        }
        builder.append(fn.apply(Array.get(arr, bound)));
        return builder.toString();
    }
}
//endregion


//region Comparison

static boolean numberEq(Number n1, Number n2) {
    if (n1 instanceof Byte b1 &&
        n2 instanceof Byte b2) {
        return (byte) b1 == b2;
    } else if (n1 instanceof Short s1 &&
               n2 instanceof Short s2) {
        return (short) s1 == s2;
    } else if (n1 instanceof Integer i1 &&
               n2 instanceof Integer i2) {
        return (int) i1 == i2;
    } else if (n1 instanceof Long l1 &&
               n2 instanceof Long l2) {
        return (long) l1 == l2;
    } else if (n1 instanceof Float f1 &&
               n2 instanceof Float f2) {
        return (float) f1 == f2;
    } else if (n1 instanceof Double d1 &&
               n2 instanceof Double d2) {
        return (double) d1 == d2;
    } else {
        return false;
    }
}

static boolean numberLess(Number n1, Number n2) {
    if (n1 instanceof Byte b1 &&
        n2 instanceof Byte b2) {
        return (byte) b1 < b2;
    } else if (n1 instanceof Short s1 &&
               n2 instanceof Short s2) {
        return (short) s1 < s2;
    } else if (n1 instanceof Integer i1 &&
               n2 instanceof Integer i2) {
        return (int) i1 < i2;
    } else if (n1 instanceof Long l1 &&
               n2 instanceof Long l2) {
        return (long) l1 < l2;
    } else if (n1 instanceof Float f1 &&
               n2 instanceof Float f2) {
        return (float) f1 < f2;
    } else if (n1 instanceof Double d1 &&
               n2 instanceof Double d2) {
        return (double) d1 < d2;
    } else {
        return false;
    }
}
//endregion
}
