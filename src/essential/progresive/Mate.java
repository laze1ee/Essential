package essential.progresive;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

import static essential.progresive.Pr.*;


class Mate {

static int length(@NotNull Lot lt) {
    int n = 0;
    while (!lt.isEmpty()) {
        n += 1;
        lt = Pr.cdr(lt);
    }
    return n;
}

static boolean isBelong(Object datum, @NotNull Lot lt) {
    while (!lt.isEmpty()) {
        if (eq(datum, car(lt))) {
            return true;
        }
        lt = cdr(lt);
    }
    return false;
}

static int theHareAndTortoise(@NotNull Lot lt) {
    if (lt.isEmpty()) {
        return 0;
    } else if (cdr(lt).isEmpty()) {
        return 1;
    } else {
        Lot hare = cddr(lt);
        Lot tortoise = lt;
        int index = 0;
        while (true) {
            if (hare.isEmpty()) {
                return (index + 1) * 2;
            } else if (cdr(hare).isEmpty()) {
                return (index + 1) * 2 + 1;
            } else if (hare == tortoise) {
                return -1;
            } else {
                hare = cddr(hare);
                tortoise = cdr(tortoise);
                index += 1;
            }
        }
    }
}
//endregion


//region To String
@Contract("_ -> new")
static @NotNull Object attach(Object datum) {
    return few(datum, false, -1);
}

static @NotNull String stringOfChar(char c) {
    switch (c) {
    case 0 -> {
        return "#\\nul";
    }
    case 7 -> {
        return "#\\alarm";
    }
    case 8 -> {
        return "#\\backspace";
    }
    case 9 -> {
        return "#\\tab";
    }
    case 0xA -> {
        return "#\\newline";
    }
    case 0xD -> {
        return "#\\return";
    }
    case 0x20 -> {
        return "#\\space";
    }
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

static @NotNull String stringOfArray(@NotNull Object array) {
    if (array instanceof boolean[] bs) {
        return String.format("#1(%s)", connectArray(bs, bs.length));
    } else if (array instanceof byte[] bs) {
        return String.format("#i8(%s)", connectArray(bs, bs.length));
    } else if (array instanceof int[] ins) {
        return String.format("#i32(%s)", connectArray(ins, ins.length));
    } else if (array instanceof long[] ls) {
        return String.format("#i64(%s)", connectArray(ls, ls.length));
    } else if (array instanceof double[] ds) {
        return String.format("#r64(%s)", connectArray(ds, ds.length));
    } else {
        throw new RuntimeException(String.format("unsupported array type %s for printing", array));
    }
}

static @NotNull String connectArray(Object arr, int bound) {
    if (bound == 0) {
        return "";
    } else {
        StringBuilder builder = new StringBuilder();
        bound = bound - 1;
        for (int i = 0; i < bound; i = i + 1) {
            builder.append(stringOf(Array.get(arr, i)));
            builder.append(" ");
        }
        builder.append(stringOf(Array.get(arr, bound)));
        return builder.toString();
    }
}

private static final char[] HEX_STR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                                 'A', 'B', 'C', 'D', 'E', 'F'};

static @NotNull String stringOfHex(byte b) {
    return new String(new char[]{HEX_STR[(b >> 4) & 0xF], HEX_STR[b & 0xF]});
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
