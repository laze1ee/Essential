package essential.progresive;

import essential.datetime.Time;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Array;

import static essential.progresive.Pr.*;


class Mate {

//region Lot
static @NotNull Lot toPairLink(Lot lt) {
    LotPairLink _head = new LotPairLinkHead(car(lt), new LotEnd());
    LotPairLink item = _head;
    lt = cdr(lt);
    while (!lt.isEmpty()) {
        item.next = new LotPairLink(car(lt), new LotEnd());
        item = (LotPairLink) item.next;
        lt = cdr(lt);
    }
    return _head;
}

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
//endregion


//region To String
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
        return String.format("#1(%s)", consArray(bs, bs.length));
    } else if (array instanceof byte[] bs) {
        return String.format("#i8(%s)", consArray(bs, bs.length));
    } else if (array instanceof int[] ins) {
        return String.format("#i32(%s)", consArray(ins, ins.length));
    } else if (array instanceof long[] ls) {
        return String.format("#i64(%s)", consArray(ls, ls.length));
    } else if (array instanceof double[] ds) {
        return String.format("#r64(%s)", consArray(ds, ds.length));
    } else {
        throw new RuntimeException(String.format("unsupported array type %s for printing", array));
    }
}

static @NotNull String consArray(Object arr, int bound) {
    if (bound == 0) {
        return "";
    } else {
        StringBuilder str = new StringBuilder();
        bound = bound - 1;
        for (int i = 0; i < bound; i = i + 1) {
            str.append(stringOf(Array.get(arr, i)));
            str.append(" ");
        }
        str.append(stringOf(Array.get(arr, bound)));
        return str.toString();
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

static boolean objectArrayEqual(Object @NotNull [] arr1, Object @NotNull [] arr2) {
    if (arr1.length == arr2.length) {
        int i = 0;
        while (i < arr1.length &&
               equal(arr1[i], arr2[i])) {
            i = i + 1;
        }
        return i == arr1.length;
    } else {
        return false;
    }
}

static boolean timeLess(@NotNull Time t1, @NotNull Time t2) {
    if (t1.second() < t2.second()) {
        return true;
    } else if (t1.second() == t2.second()) {
        return t1.nanosecond() < t2.nanosecond();
    } else {
        return false;
    }
}
//endregion
}
