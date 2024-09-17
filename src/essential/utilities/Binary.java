package essential.utilities;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import essential.datetime.Date;
import essential.datetime.Time;
import essential.progresive.Few;
import essential.progresive.Lot;
import essential.progresive.Symbol;
import essential.Share;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static essential.progresive.Pr.*;


public class Binary {

public static final byte LABEL_BOOLEAN_FALSE = (byte) 0x80;
public static final byte LABEL_BOOLEAN_TRUE = (byte) 0x81;
public static final byte LABEL_INT = (byte) 0x82;
public static final byte LABEL_LONG = (byte) 0x83;
public static final byte LABEL_DOUBLE = (byte) 0x84;
public static final byte LABEL_CHAR = (byte) 0x85;
public static final byte LABEL_STRING = (byte) 0x86;

public static final byte LABEL_INTS = (byte) 0x8A;
public static final byte LABEL_LONGS = (byte) 0x8B;
public static final byte LABEL_DOUBLES = (byte) 0x8C;

public static final byte LABEL_SYMBOL = (byte) 0x90;
public static final byte LABEL_LOT_BEGIN = (byte) 0x91;
public static final byte LABEL_LOT_CONS = (byte) 0x92;
public static final byte LABEL_LOT_END = (byte) 0x93;
public static final byte LABEL_FEW = (byte) 0x94;

public static final byte LABEL_TIME = (byte) 0xA0;
public static final byte LABEL_DATE = (byte) 0xA1;


public static byte @NotNull [] integerToBinary(long n, int length, boolean little_endian) {
    byte[] bin = new byte[length];
    if (little_endian) {
        for (int i = 0; i < length; i = i + 1) {
            bin[i] = (byte) n;
            n = n >>> 8;
        }
    } else {
        for (int i = length - 1; i >= 0; i = i - 1) {
            bin[i] = (byte) n;
            n = n >>> 8;
        }
    }
    return bin;
}

public static long binaryToInteger(byte[] bin, int start, int bound, boolean little_endian) {
    long n = 0;
    if (little_endian) {
        for (int i = bound - 1; i >= start; i = i - 1) {
            n = n << 8;
            n = n | (bin[i] & 0xFF);
        }
    } else {
        for (int i = start; i < bound; i = i + 1) {
            n = n << 8;
            n = n | (bin[i] & 0xFF);
        }
    }
    return n;
}


//region Encoding
public static final String UNSUPPORTED = "unsupported data type %s for coding";

public static byte[] encode(Object datum) {
    if (datum instanceof Boolean b) {
        return encodeBoolean(b);
    } else if (datum instanceof Integer in) {
        return encodeInt(in);
    } else if (datum instanceof Long l) {
        return encodeLong(l);
    } else if (datum instanceof Double d) {
        return encodeDouble(d);
    } else if (datum instanceof Character c) {
        return encodeChar(c);
    } else if (datum instanceof String str) {
        return encodeString(str);

    } else if (datum instanceof int[] ins) {
        return encodeInts(ins);
    } else if (datum instanceof long[] ls) {
        return encodeLongs(ls);
    } else if (datum instanceof double[] ds) {
        return encodeDoubles(ds);

    } else if (datum instanceof Symbol sym) {
        return encodeSymbol(sym);
    } else if (datum instanceof Lot lt) {
        return encodeLot(lt);
    } else if (datum instanceof Few fw) {
        return encodeFew(fw);

    } else if (datum instanceof Time t) {
        return encodeTime(t);
    } else if (datum instanceof Date d) {
        return encodeDate(d);
    } else {
        throw new RuntimeException(String.format(UNSUPPORTED, datum));
    }
}

public static byte @NotNull [] encodeSize(long size) {
    byte[] bs1 = integerToBinary(size, 8, false);
    bs1 = Share.trim(bs1);
    byte[] bs2 = new byte[bs1.length + 1];
    bs2[0] = (byte) bs1.length;
    System.arraycopy(bs1, 0, bs2, 1, bs1.length);
    return bs2;
}

public static byte @NotNull [] encodeBoolean(boolean b) {
    if (b) {
        return new byte[]{LABEL_BOOLEAN_TRUE};
    } else {
        return new byte[]{LABEL_BOOLEAN_FALSE};
    }
}

public static byte @NotNull [] encodeInt(int n) {
    byte[] ooo = integerToBinary(n, 4, false);
    byte[] bin = new byte[5];
    bin[0] = LABEL_INT;
    System.arraycopy(ooo, 0, bin, 1, 4);
    return bin;
}

public static byte @NotNull [] encodeLong(long n) {
    byte[] ooo = integerToBinary(n, 8, false);
    byte[] bin = new byte[9];
    bin[0] = LABEL_LONG;
    System.arraycopy(ooo, 0, bin, 1, 8);
    return bin;
}

public static byte @NotNull [] encodeDouble(double n) {
    long bits = Double.doubleToLongBits(n);
    byte[] ooo = integerToBinary(bits, 8, false);
    byte[] bin = new byte[9];
    bin[0] = LABEL_DOUBLE;
    System.arraycopy(ooo, 0, bin, 1, 8);
    return bin;
}

public static byte @NotNull [] encodeChar(char c) {
    byte[] ooo = integerToBinary(c, 3, false);
    byte[] bin = new byte[4];
    bin[0] = LABEL_CHAR;
    System.arraycopy(ooo, 0, bin, 1, 3);
    return bin;
}

public static byte @NotNull [] encodeString(@NotNull String str) {
    byte[] b_str = str.getBytes(StandardCharsets.UTF_8);
    byte[] bin = new byte[b_str.length + 2];
    bin[0] = LABEL_STRING;
    System.arraycopy(b_str, 0, bin, 1, b_str.length);
    return bin;
}

public static byte @NotNull [] encodeInts(int @NotNull [] ins) {
    byte[] size = encodeSize(ins.length);
    byte[] bin = new byte[1 + size.length + 4 * ins.length];
    bin[0] = LABEL_INTS;
    System.arraycopy(size, 0, bin, 1, size.length);
    for (int i = 0; i < ins.length; i += 1) {
        byte[] ooo = integerToBinary(ins[i], 4, false);
        System.arraycopy(ooo, 0, bin, 1 + size.length + 4 * i, 4);
    }
    return bin;
}

public static byte @NotNull [] encodeLongs(long @NotNull [] ls) {
    byte[] size = encodeSize(ls.length);
    byte[] bin = new byte[1 + size.length + 8 * ls.length];
    bin[0] = LABEL_LONGS;
    System.arraycopy(size, 0, bin, 1, size.length);
    for (int i = 0; i < ls.length; i += 1) {
        byte[] ooo = integerToBinary(ls[i], 8, false);
        System.arraycopy(ooo, 0, bin, 1 + size.length + 8 * i, 8);
    }
    return bin;
}

public static byte @NotNull [] encodeDoubles(double @NotNull [] ds) {
    byte[] size = encodeSize(ds.length);
    byte[] bin = new byte[1 + size.length + 8 * ds.length];
    bin[0] = LABEL_DOUBLES;
    System.arraycopy(size, 0, bin, 1, size.length);
    for (int i = 0; i < ds.length; i += 1) {
        long bits = Double.doubleToLongBits(ds[i]);
        byte[] ooo = integerToBinary(bits, 8, false);
        System.arraycopy(ooo, 0, bin, 1 + size.length + 8 * i, 8);
    }
    return bin;
}

public static byte @NotNull [] encodeSymbol(@NotNull Symbol sym) {
    byte[] b_str = sym.toString().getBytes(StandardCharsets.UTF_8);
    byte[] bin = new byte[b_str.length + 2];
    bin[0] = LABEL_SYMBOL;
    System.arraycopy(b_str, 0, bin, 1, b_str.length);
    return bin;
}

public static byte @NotNull [] encodeLot(@NotNull Lot lt) {
    if (lt.isEmpty()) {
        return new byte[]{LABEL_LOT_BEGIN, LABEL_LOT_END};
    } else {
        Lot col = lot();
        while (!lt.isEmpty()) {
            col = cons(encode(car(lt)), col);
            lt = cdr(lt);
        }
        col = reverse(col);
        return connectLot(col);
    }
}

public static byte @NotNull [] connectLot(@NotNull Lot lt) {
    int sz = sizeOf(lt) + lt.length() + 1;
    byte[] bin = new byte[sz];
    bin[0] = LABEL_LOT_BEGIN;
    bin[sz - 1] = LABEL_LOT_END;

    int i = 1;
    while (!cdr(lt).isEmpty()) {
        byte[] bs = (byte[]) car(lt);
        System.arraycopy(bs, 0, bin, i, bs.length);
        i += bs.length;
        bin[i] = LABEL_LOT_CONS;
        i += 1;
        lt = cdr(lt);
    }
    byte[] bs = (byte[]) car(lt);
    System.arraycopy(bs, 0, bin, i, bs.length);
    return bin;
}

private static int sizeOf(@NotNull Lot bins) {
    int n = 0;
    while (!bins.isEmpty()) {
        byte[] bs = (byte[]) car(bins);
        n += bs.length;
        bins = cdr(bins);
    }
    return n;
}

public static byte @NotNull [] encodeFew(@NotNull Few fw) {
    if (fw.length() == 0) {
        byte[] size = encodeSize(0);
        byte[] bin = new byte[1 + size.length];
        bin[0] = LABEL_FEW;
        System.arraycopy(size, 0, bin, 1, size.length);
        return bin;
    } else {
        Lot col = lot();
        int n = fw.length();
        for (int i = 0; i < n; i = i + 1) {
            col = cons(encode(fewRef(fw, i)), col);
        }
        col = reverse(col);
        return connectFew(col);
    }
}

public static byte @NotNull [] connectFew(@NotNull Lot lt) {
    byte[] size = encodeSize(lt.length());
    byte[] bin = new byte[1 + size.length + sizeOf(lt)];
    bin[0] = LABEL_FEW;
    System.arraycopy(size, 0, bin, 1, size.length);

    int i = 1 + size.length;
    while (!lt.isEmpty()) {
        byte[] bs = (byte[]) car(lt);
        System.arraycopy(bs, 0, bin, i, bs.length);
        i = i + bs.length;
        lt = cdr(lt);
    }
    return bin;
}

public static byte @NotNull [] encodeTime(@NotNull Time t) {
    byte[] bin = new byte[13];
    bin[0] = LABEL_TIME;
    byte[] b_sec = integerToBinary(t.second(), 8, false);
    System.arraycopy(b_sec, 0, bin, 1, 8);
    byte[] b_nano = integerToBinary(t.nanosecond(), 4, false);
    System.arraycopy(b_nano, 0, bin, 9, 4);
    return bin;
}

public static byte @NotNull [] encodeDate(@NotNull Date d) {
    byte[] bin = new byte[19];
    bin[0] = LABEL_DATE;
    byte[] ooo = integerToBinary(d.year(), 4, false);
    System.arraycopy(ooo, 0, bin, 1, 4);
    bin[5] = (byte) d.month();
    bin[6] = (byte) d.dayOfMonth();
    bin[7] = (byte) d.dayOfWeek();
    bin[8] = (byte) d.hour();
    bin[9] = (byte) d.minute();
    bin[10] = (byte) d.second();
    ooo = integerToBinary(d.nanosecond(), 4, false);
    System.arraycopy(ooo, 0, bin, 11, 4);
    ooo = integerToBinary(d.offset(), 4, false);
    System.arraycopy(ooo, 0, bin, 15, 4);
    return bin;
}
//endregion


//region Decoding
@Contract(pure = true)
public static int @NotNull [] decodeInts(byte[] bin, int start, int size) {
    int[] ins = new int[size];
    for (int i = 0, j = start; i < size; i += 1, j += 4) {
        ins[i] = (int) binaryToInteger(bin, j, j + 4, false);
    }
    return ins;
}

@Contract(pure = true)
public static long @NotNull [] decodeLongs(byte[] bin, int start, int sz) {
    long[] ls = new long[sz];
    for (int i = 0, j = start; i < sz; i += 1, j += 8) {
        ls[i] = binaryToInteger(bin, j, j + 8, false);
    }
    return ls;
}

public static double @NotNull [] decodeDoubles(byte[] bin, int start, int sz) {
    double[] ds = new double[sz];
    for (int i = 0, j = start; i < sz; i += 1, j += 8) {
        long bits = binaryToInteger(bin, j, j + 8, false);
        ds[i] = Double.longBitsToDouble(bits);
    }
    return ds;
}

@Contract("_, _ -> new")
public static @NotNull Time decodeTime(byte[] bin, int start) {
    long second = binaryToInteger(bin, start, start + 8, false);
    int nanosecond = (int) binaryToInteger(bin, start + 8, start + 12, false);
    return new Time(second, nanosecond);
}

@Contract("_, _ -> new")
public static @NotNull Date decodeDate(byte[] bin, int start) {
    int year = (int) binaryToInteger(bin, start, start + 4, false);
    int nanosecond = (int) binaryToInteger(bin, start + 10, start + 14, false);
    int offset = (int) binaryToInteger(bin, start + 14, start + 18, false);
    return new Date(year, bin[start + 4], bin[start + 5], bin[start + 7], bin[start + 8],
                    bin[start + 9], nanosecond, offset);
}

@Contract(pure = true)
public static long decodeSize(byte @NotNull [] bin, int start) {
    long sz = 0;
    int bound = start + 1 + (bin[start] & 0xFF);
    for (int i = start + 1; i < bound; i += 1) {
        sz = (sz << 4) + (bin[i] & 0xFF);
    }
    return sz;
}

public static final String UNMATCHED_LABEL = "unmatched binary label %s for decoding";

@SuppressWarnings("DuplicatedCode")
private static class Decoding {

    final byte[] bin;

    int pos;

    public Decoding(byte[] bin) {
        this.bin = bin;
        pos = 0;
    }

    Object process() {
        Object datum;
        int label = bin[pos];
        pos += 1;
        switch (label) {
        case LABEL_BOOLEAN_FALSE -> datum = false;
        case LABEL_BOOLEAN_TRUE -> datum = true;
        case LABEL_INT -> {
            datum = (int) binaryToInteger(bin, pos, pos + 4, false);
            pos = pos + 4;
        }
        case LABEL_LONG -> {
            datum = binaryToInteger(bin, pos, pos + 8, false);
            pos = pos + 8;
        }
        case LABEL_DOUBLE -> {
            long bits = binaryToInteger(bin, pos, pos + 8, false);
            datum = Double.longBitsToDouble(bits);
            pos = pos + 8;
        }
        case LABEL_CHAR -> {
            datum = (char) binaryToInteger(bin, pos, pos + 3, false);
            pos = pos + 3;
        }
        case LABEL_STRING -> {
            int start = pos;
            while (bin[pos] != 0) {
                pos = pos + 1;
            }
            byte[] bs_str = Arrays.copyOfRange(bin, start, pos);
            datum = new String(bs_str, StandardCharsets.UTF_8);
            pos = pos + 1;
        }
        case LABEL_INTS -> {
            int size = (int) decodeSize(bin, pos);
            pos = pos + 1 + (bin[pos] & 0xFF);
            datum = decodeInts(bin, pos, size);
            pos = pos + 4 * size;
        }
        case LABEL_LONGS -> {
            int size = (int) decodeSize(bin, pos);
            pos = pos + 1 + (bin[pos] & 0xFF);
            datum = decodeLongs(bin, pos, size);
            pos = pos + 8 * size;
        }
        case LABEL_DOUBLES -> {
            int size = (int) decodeSize(bin, pos);
            pos = pos + 1 + (bin[pos] & 0xFF);
            datum = decodeDoubles(bin, pos, size);
            pos = pos + 8 * size;
        }
        case LABEL_SYMBOL -> {
            int start = pos;
            while (bin[pos] != 0) {
                pos = pos + 1;
            }
            byte[] bs_str = Arrays.copyOfRange(bin, start, pos);
            datum = symbol(new String(bs_str, StandardCharsets.UTF_8));
            pos = pos + 1;
        }
        case LABEL_LOT_BEGIN -> {
            if (bin[pos] == LABEL_LOT_END) {
                datum = lot();
                pos = pos + 1;
            } else {
                Object ooo = process();
                datum = cons(ooo, (Lot) process());
            }
        }
        case LABEL_LOT_CONS -> {
            Object ooo = process();
            datum = cons(ooo, (Lot) process());
        }
        case LABEL_LOT_END -> datum = lot();
        case LABEL_FEW -> {
            int size = (int) decodeSize(bin, pos);
            pos = pos + 1 + (bin[pos] & 0xFF);
            datum = makeFew(size, 0);
            for (int i = 0; i < size; i = i + 1) {
                Object ooo = process();
                fewSet((Few) datum, i, ooo);
            }
        }
        case LABEL_TIME -> {
            datum = decodeTime(bin, pos);
            pos = pos + 12;
        }
        case LABEL_DATE -> {
            datum = decodeDate(bin, pos);
            pos = pos + 18;
        }
        default -> throw new RuntimeException(String.format(UNMATCHED_LABEL, bin[pos]));
        }
        return datum;
    }
}

public static Object decode(byte[] bin) {
    Decoding de = new Decoding(bin);
    return de.process();
}
//endregion
}
