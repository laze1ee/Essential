/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Date;
import essential.datetime.Time;
import essential.progressive.Lot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static essential.progressive.Pr.cons;


class BinaryMate {

//region Encoding
static byte @NotNull [] encodeShort(short n) {
  byte[] lll = Binary.encodeI64(n);
  byte[] xxx = new byte[3];
  xxx[0] = Binary.SHORT;
  System.arraycopy(lll, 6, xxx, 1, 2);
  return xxx;
}

static byte @NotNull [] encodeInt(int n) {
  byte[] lll = Binary.encodeI64(n);
  byte[] xxx = new byte[5];
  xxx[0] = Binary.INT;
  System.arraycopy(lll, 4, xxx, 1, 4);
  return xxx;
}

static byte @NotNull [] encodeLong(long n) {
  byte[] lll = Binary.encodeI64(n);
  byte[] xxx = new byte[9];
  xxx[0] = Binary.LONG;
  System.arraycopy(lll, 0, xxx, 1, 8);
  return xxx;
}

static byte @NotNull [] encodeFloat(float n) {
  int bits = Float.floatToIntBits(n);
  byte[] lll = Binary.encodeI64(bits);
  byte[] xxx = new byte[5];
  xxx[0] = Binary.FLOAT;
  System.arraycopy(lll, 4, xxx, 1, 4);
  return xxx;
}

static byte @NotNull [] encodeDouble(double n) {
  long bits = Double.doubleToLongBits(n);
  byte[] lll = Binary.encodeI64(bits);
  byte[] xxx = new byte[9];
  xxx[0] = Binary.DOUBLE;
  System.arraycopy(lll, 0, xxx, 1, 8);
  return xxx;
}

static byte @NotNull [] encodeBooleans(boolean @NotNull [] bs) {
  byte[] len = Binary.encodeVarI32(bs.length);
  byte[] bin = new byte[1 + len.length + bs.length];
  bin[0] = Binary.BOOLEANS;
  System.arraycopy(len, 0, bin, 1, len.length);
  for (int i = 0, j = 1 + len.length; i < bs.length; i += 1, j += 1) {
    if (bs[i]) {
      bin[j] = Binary.BOOLEAN_TRUE;
    }
    else {
      bin[j] = Binary.BOOLEAN_FALSE;
    }
  }
  return bin;
}

static byte @NotNull [] encodeShorts(short @NotNull [] ss) {
  byte[] len = Binary.encodeVarI32(ss.length);
  byte[] bin = new byte[1 + len.length + 2 * ss.length];
  bin[0] = Binary.SHORTS;
  System.arraycopy(len, 0, bin, 1, len.length);
  for (int i = 0; i < ss.length; i += 1) {
    byte[] lll = Binary.encodeI64(ss[i]);
    System.arraycopy(lll, 6, bin, 1 + len.length + 2 * i, 2);
  }
  return bin;
}

static byte @NotNull [] encodeInts(int @NotNull [] ins) {
  byte[] len = Binary.encodeVarI32(ins.length);
  byte[] bin = new byte[1 + len.length + 4 * ins.length];
  bin[0] = Binary.INTS;
  System.arraycopy(len, 0, bin, 1, len.length);
  for (int i = 0; i < ins.length; i += 1) {
    byte[] lll = Binary.encodeI64(ins[i]);
    System.arraycopy(lll, 4, bin, 1 + len.length + 4 * i, 4);
  }
  return bin;
}

static byte @NotNull [] encodeLongs(long @NotNull [] ls) {
  byte[] len = Binary.encodeVarI32(ls.length);
  byte[] bin = new byte[1 + len.length + 8 * ls.length];
  bin[0] = Binary.LONGS;
  System.arraycopy(len, 0, bin, 1, len.length);
  for (int i = 0; i < ls.length; i += 1) {
    byte[] lll = Binary.encodeI64(ls[i]);
    System.arraycopy(lll, 0, bin, 1 + len.length + 8 * i, 8);
  }
  return bin;
}

static byte @NotNull [] encodeFloats(float @NotNull [] fs) {
  byte[] len = Binary.encodeVarI32(fs.length);
  byte[] bin = new byte[1 + len.length + 4 * fs.length];
  bin[0] = Binary.FLOATS;
  System.arraycopy(len, 0, bin, 1, len.length);
  for (int i = 0; i < fs.length; i += 1) {
    long bits = Float.floatToRawIntBits(fs[i]);
    byte[] lll = Binary.encodeI64(bits);
    System.arraycopy(lll, 4, bin, 1 + len.length + 4 * i, 4);
  }
  return bin;
}

static byte @NotNull [] encodeDoubles(double @NotNull [] ds) {
  byte[] len = Binary.encodeVarI32(ds.length);
  byte[] bin = new byte[1 + len.length + 8 * ds.length];
  bin[0] = Binary.DOUBLES;
  System.arraycopy(len, 0, bin, 1, len.length);
  for (int i = 0; i < ds.length; i += 1) {
    long bits = Double.doubleToLongBits(ds[i]);
    byte[] lll = Binary.encodeI64(bits);
    System.arraycopy(lll, 0, bin, 1 + len.length + 8 * i, 8);
  }
  return bin;
}

static byte @NotNull [] encodePureChar(int c) {
  if (c < 0x80) {
    return new byte[]{(byte) c};
  }
  else if (c < 0x800) {
    byte[] bin = new byte[2];
    bin[1] = (byte) (0x80 | c & 0x3F);
    c = c >> 6;
    bin[0] = (byte) (0xC0 | c & 0x1F);
    return bin;
  }
  else if (c < 0x10000) {
    byte[] bin = new byte[3];
    bin[2] = (byte) (0x80 | c & 0x3F);
    c = c >> 6;
    bin[1] = (byte) (0x80 | c & 0x3F);
    c = c >> 6;
    bin[0] = (byte) (0xE0 | c & 0x0F);
    return bin;
  }
  else {
    byte[] bin = new byte[4];
    bin[3] = (byte) (0x80 | c & 0x3F);
    c = c >> 6;
    bin[2] = (byte) (0x80 | c & 0x3F);
    c = c >> 6;
    bin[1] = (byte) (0x80 | c & 0x3F);
    c = c >> 6;
    bin[0] = (byte) (0xF0 | c & 0x07);
    return bin;
  }
}

static byte @NotNull [] encodePureString(@NotNull String str) {
  Lot col = Lot.of();
  for (int i = str.length() - 1; i != -1; i -= 1) {
    byte[] bin = encodePureChar(str.charAt(i));
    col = cons(bin, col);
  }
  return Binary.connectBytes(col);
}

static byte @NotNull [] encodeTime(@NotNull Time t) {
  byte[] bin = new byte[13];
  bin[0] = Binary.TIME;
  byte[] bin_sec = Binary.encodeI64(t.second());
  System.arraycopy(bin_sec, 0, bin, 1, 8);
  byte[] b_nano = Binary.encodeI64(t.nanosecond());
  System.arraycopy(b_nano, 4, bin, 9, 4);
  return bin;
}

static byte @NotNull [] encodeDate(@NotNull Date d) {
  byte[] bin = new byte[19];
  bin[0] = Binary.DATE;
  byte[] uuf = Binary.encodeI64(d.year());
  System.arraycopy(uuf, 4, bin, 1, 4);
  bin[5] = (byte) d.month();
  bin[6] = (byte) d.dayOfMonth();
  bin[7] = (byte) d.dayOfWeek();
  bin[8] = (byte) d.hour();
  bin[9] = (byte) d.minute();
  bin[10] = (byte) d.second();
  uuf = Binary.encodeI64(d.nanosecond());
  System.arraycopy(uuf, 4, bin, 11, 4);
  uuf = Binary.encodeI64(d.offset());
  System.arraycopy(uuf, 4, bin, 15, 4);
  return bin;
}
//endregion


//region Decoding
static boolean @NotNull [] decodeBooleans(byte[] bin, int start, int len) {
  boolean[] bs = new boolean[len];
  for (int i = 0, j = start; i < len; i += 1, j += 1) {
    bs[i] = (bin[j] == Binary.BOOLEAN_TRUE);
  }
  return bs;
}

static short @NotNull [] decodeShorts(byte[] bin, int start, int len) {
  short[] ss = new short[len];
  for (int i = 0, j = start; i < len; i += 1, j += 2) {
    byte[] lll = Binary.extendTo64Bits(bin, j, j + 2);
    ss[i] = (short) Binary.decodeI64(lll);
  }
  return ss;
}

static int @NotNull [] decodeInts(byte[] bin, int start, int len) {
  int[] ins = new int[len];
  for (int i = 0, j = start; i < len; i += 1, j += 4) {
    byte[] lll = Binary.extendTo64Bits(bin, j, j + 4);
    ins[i] = (int) Binary.decodeI64(lll);
  }
  return ins;
}

static long @NotNull [] decodeLongs(byte[] bin, int start, int len) {
  long[] ls = new long[len];
  for (int i = 0, j = start; i < len; i += 1, j += 8) {
    byte[] lll = Binary.extendTo64Bits(bin, j, j + 8);
    ls[i] = Binary.decodeI64(lll);
  }
  return ls;
}

static float @NotNull [] decodeFloats(byte[] bin, int start, int len) {
  float[] fs = new float[len];
  for (int i = 0, j = start; i < len; i += 1, j += 4) {
    byte[] lll = Arrays.copyOfRange(bin, j, j + 4);
    int bits = 0;
    for (int k = 0; k < 4; k += 1) {
      bits = (bits << 8) | (lll[k] & 0xFF);
    }
    fs[i] = Float.intBitsToFloat(bits);
  }
  return fs;
}

static double @NotNull [] decodeDoubles(byte[] bin, int start, int sz) {
  double[] ds = new double[sz];
  for (int i = 0, j = start; i < sz; i += 1, j += 8) {
    byte[] lll = Arrays.copyOfRange(bin, j, j + 8);
    long bits = 0;
    for (int k = 0; k < 8; k += 1) {
      bits = (bits << 8) | (lll[k] & 0xFF);
    }
    ds[i] = Double.longBitsToDouble(bits);
  }
  return ds;
}

static @NotNull Time decodeTime(byte[] bin, int start) {
  byte[] lll = Binary.extendTo64Bits(bin, start, start + 8);
  long second = Binary.decodeI64(lll);
  lll = Binary.extendTo64Bits(bin, start + 8, start + 12);
  int nanosecond = (int) Binary.decodeI64(lll);
  return new Time(second, nanosecond);
}

static @NotNull Date decodeDate(byte[] bin, int start) {
  byte[] lll = Binary.extendTo64Bits(bin, start, start + 4);
  int year = (int) Binary.decodeI64(lll);
  lll = Binary.extendTo64Bits(bin, start + 10, start + 14);
  int nanosecond = (int) Binary.decodeI64(lll);
  lll = Binary.extendTo64Bits(bin, start + 14, start + 18);
  int offset = (int) Binary.decodeI64(lll);
  return new Date(year, bin[start + 4], bin[start + 5], bin[start + 7], bin[start + 8],
                  bin[start + 9], nanosecond, offset);
}

static int sizeofChar(byte @NotNull [] bin, int start) {
  int bytes = bin[start] & 0xF8;
  if (bytes < 0x80) {
    return 1;
  }
  else if (bytes < 0xE0) {
    return 2;
  }
  else if (bytes < 0xF0) {
    return 3;
  }
  else {
    return 4;
  }
}

static char decodePureChar(byte[] bin, int start, int bound) {
  int bytes = bound - start;
  switch (bytes) {
    case 1 -> {
      return (char) bin[start];
    }
    case 2 -> {
      int c = (bin[start] & 0x1F) << 6;
      c = c | (bin[start + 1] & 0x3F);
      return (char) c;
    }
    case 3 -> {
      int c = (bin[start] & 0x0F) << 12;
      c = c | ((bin[start + 1] & 0x3F) << 6);
      c = c | (bin[start + 2] & 0x3F);
      return (char) c;
    }
    default -> {
      int c = (bin[start] & 0x07) << 18;
      c = c | ((bin[start + 1] & 0x3F) << 12);
      c = c | ((bin[start + 2] & 0x3F) << 6);
      c = c | (bin[start + 3] & 0x3F);
      return (char) c;
    }
  }
}
//endregion
}
