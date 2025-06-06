/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Date;
import essential.datetime.Time;
import essential.progressive.Few;
import essential.progressive.Lot;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static essential.progressive.Pr.*;


class Decoding {

private record Nothing(int index) {
  @Override
  public @NotNull String toString() {
    return "»" + index + "«";
  }
}

private static final char[] HEX_STR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
                                                 'A', 'B', 'C', 'D', 'E', 'F'};

private static @NotNull String hexOfByte(byte b) {
  return new String(new char[]{HEX_STR[(b >> 4) & 0xF], HEX_STR[b & 0xF]});
}

private final byte[] bin;
private int index;
private Few shared;
private Few stack;
private Few cont;
private Object r0;

Decoding(byte[] bin, int index) {
  this.bin = bin;
  this.index = index;
  stack = few(Label.END_CONT);
  cont = few(Label.END_CONT);
}

Object process() {
  index += 1;
  int sz = Binary.sizeofVarI32(bin, index);
  int length = Binary.decodeVarI32(bin, index, index + sz);
  index += sz;

  if (length != 0) {
    shared = makeFew(length, false);
    for (int i = 0; i < length; i += 1) {
      shared.set(i, new Nothing(i));
    }
    cont = few(Label.ITER_FEW, cont, length, 0, shared);
    route();
    link();
  }

  route();
  return r0;
}

private void link() {
  while (true) {
    switch ((String) stack.ref(0)) {
      case Label.END_CONT -> {return;}
      case Label.SET_FEW -> {
        Few fw = (Few) stack.ref(2);
        int i = (int) stack.ref(3);
        int j = (int) stack.ref(4);
        fw.set(i, shared.ref(j));
        stack = (Few) stack.ref(1);
      }
      case Label.SET_CAR -> {
        Lot lt = (Lot) stack.ref(2);
        int j = (int) stack.ref(3);
        setCar(lt, shared.ref(j));
        stack = (Few) stack.ref(1);
      }
      case Label.SET_CDR -> {
        Lot lt = (Lot) stack.ref(2);
        int j = (int) stack.ref(3);
        setCdr(lt, (Lot) shared.ref(j));
        stack = (Few) stack.ref(1);
      }
    }
  }
}

private void route() {
  String next = Label.OF_BYTE;
  while (true) {
    switch (next) {
      case Label.OF_BYTE -> next = ofByte();
      case Label.APPLY_CONT -> next = applyCont();
      case Label.EXIT -> {return;}
    }
  }
}

private String ofByte() {
  byte label = bin[index];
  index += 1;
  switch (label) {
    case Binary.BOOLEAN_TRUE -> {
      r0 = true;
      return Label.APPLY_CONT;
    }
    case Binary.BOOLEAN_FALSE -> {
      r0 = false;
      return Label.APPLY_CONT;
    }
    case Binary.SHORT -> {
      byte[] lll = Binary.extendTo64Bits(bin, index, index + 2);
      index += 2;
      r0 = (short) Binary.decodeI64(lll);
      return Label.APPLY_CONT;
    }
    case Binary.INT -> {
      byte[] lll = Binary.extendTo64Bits(bin, index, index + 4);
      index += 4;
      r0 = (int) Binary.decodeI64(lll);
      return Label.APPLY_CONT;
    }
    case Binary.LONG -> {
      byte[] lll = Binary.extendTo64Bits(bin, index, index + 8);
      index += 8;
      r0 = Binary.decodeI64(lll);
      return Label.APPLY_CONT;
    }
    case Binary.FLOAT -> {
      byte[] lll = Arrays.copyOfRange(bin, index, index + 4);
      index += 4;
      int bits = 0;
      for (int i = 0; i < 4; i += 1) {
        bits = (bits << 8) | (lll[i] & 0xFF);
      }
      r0 = Float.intBitsToFloat(bits);
      return Label.APPLY_CONT;
    }
    case Binary.DOUBLE -> {
      byte[] lll = Arrays.copyOfRange(bin, index, index + 8);
      index += 8;
      long bits = 0;
      for (int i = 0; i < 8; i += 1) {
        bits = (bits << 8) | (lll[i] & 0xFF);
      }
      r0 = Double.longBitsToDouble(bits);
      return Label.APPLY_CONT;
    }
    case Binary.BOOLEANS -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int length = Binary.decodeVarI32(bin, index, index + sz);
      int start = index + sz;
      index = start + length;
      r0 = BinaryMate.decodeBooleans(bin, start, length);
      return Label.APPLY_CONT;
    }
    case Binary.SHORTS -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int length = Binary.decodeVarI32(bin, index, index + sz);
      int start = index + sz;
      index = start + length * 2;
      r0 = BinaryMate.decodeShorts(bin, start, length);
      return Label.APPLY_CONT;
    }
    case Binary.INTS -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int length = Binary.decodeVarI32(bin, index, index + sz);
      int start = index + sz;
      index = start + length * 4;
      r0 = BinaryMate.decodeInts(bin, start, length);
      return Label.APPLY_CONT;
    }
    case Binary.LONGS -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int length = Binary.decodeVarI32(bin, index, index + sz);
      int start = index + sz;
      index = start + length * 8;
      r0 = BinaryMate.decodeLongs(bin, start, length);
      return Label.APPLY_CONT;
    }
    case Binary.FLOATS -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int length = Binary.decodeVarI32(bin, index, index + sz);
      int start = index + sz;
      index = start + length * 4;
      r0 = BinaryMate.decodeFloats(bin, start, length);
      return Label.APPLY_CONT;
    }
    case Binary.DOUBLES -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int length = Binary.decodeVarI32(bin, index, index + sz);
      int start = index + sz;
      index = start + length * 8;
      r0 = BinaryMate.decodeDoubles(bin, start, length);
      return Label.APPLY_CONT;
    }
    case Binary.CHAR -> {
      Few pack = Binary.decodeChar(bin, index);
      index = (int) pack.ref(0);
      r0 = pack.ref(1);
      return Label.APPLY_CONT;
    }
    case Binary.STRING -> {
      Few pack = Binary.decodeString(bin, index);
      index = (int) pack.ref(0);
      r0 = pack.ref(1);
      return Label.APPLY_CONT;
    }
    case Binary.TIME -> {
      Time t = BinaryMate.decodeTime(bin, index);
      index += 12;
      r0 = t;
      return Label.APPLY_CONT;
    }
    case Binary.DATE -> {
      Date d = BinaryMate.decodeDate(bin, index);
      index += 18;
      r0 = d;
      return Label.APPLY_CONT;
    }
    case Binary.SHARE_INDEX -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int idx = Binary.decodeVarI32(bin, index, index + sz);
      index += sz;
      r0 = shared.ref(idx);
      return Label.APPLY_CONT;
    }
    case Binary.FEW -> {
      int sz = Binary.sizeofVarI32(bin, index);
      int length = Binary.decodeVarI32(bin, index, index + sz);
      index += sz;

      if (length == 0) {
        r0 = few();
        return Label.APPLY_CONT;
      }
      r0 = makeFew(length, new Nothing(-1));
      cont = few(Label.ITER_FEW, cont, length, 0, r0);
      return Label.OF_BYTE;
    }
    case Binary.LOT_BEGIN -> {
      if (bin[index] == Binary.LOT_END) {
        index += 1;
        r0 = lot();
        return Label.APPLY_CONT;
      }

      cont = few(Label.ITER_LOT, cont, lot());
      return Label.OF_BYTE;
    }
    case Binary.LOT_END -> {
      Lot lll = (Lot) cont.ref(2);
      Lot xxx = lot();
      return reverseLot(lll, xxx);
    }
    case Binary.NEXT_LOT -> {
      cont.set(0, Label.NEXT_LOT);
      return Label.OF_BYTE;
    }
    default -> {
      String msg = String.format(Msg.INVALID_BIN_BYTE, hexOfByte(label), index - 1);
      throw new RuntimeException(msg);
    }
  }
}

private String applyCont() {
  String label = (String) cont.ref(0);

  switch (label) {
    case Label.END_CONT -> {return Label.EXIT;}
    case Label.ITER_FEW -> {
      int length = (int) cont.ref(2);
      int idx = (int) cont.ref(3);
      Few fw = (Few) cont.ref(4);
      fw.set(idx, r0);

      if (r0 instanceof Nothing nt) {
        stack = few(Label.SET_FEW, stack, fw, idx, nt.index);
      }

      if (idx + 1 == length) {
        r0 = fw;
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }
      else {
        cont.set(3, idx + 1);
        return Label.OF_BYTE;
      }
    }
    case Label.ITER_LOT -> {
      Lot lt = (Lot) cont.ref(2);
      cont.set(2, cons(r0, lt));
      return Label.OF_BYTE;
    }
    case Label.NEXT_LOT -> {
      Lot lll = (Lot) cont.ref(2);
      Lot xxx = lot(lll.car());
      if (lll.car() instanceof Nothing nt) {
        stack = few(Label.SET_CAR, stack, xxx, nt.index);
      }

      if (r0 instanceof Nothing nt) {
        stack = few(Label.SET_CDR, stack, xxx, nt.index);
      }
      else {
        setCdr(xxx, (Lot) r0);
      }

      return reverseLot(lll.cdr(), xxx);
    }
    default -> throw new RuntimeException("undefined continuation " + label);
  }
}

private String reverseLot(@NotNull Lot lll, Lot xxx) {
  while (!lll.isEmpty()) {
    xxx = cons(lll.car(), xxx);
    if (lll.car() instanceof Nothing nt) {
      stack = few(Label.SET_CAR, stack, xxx, nt.index);
    }
    lll = lll.cdr();
  }

  r0 = xxx;
  cont = (Few) cont.ref(1);
  return Label.APPLY_CONT;
}
}
