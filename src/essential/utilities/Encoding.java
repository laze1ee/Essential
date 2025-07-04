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
import essential.progressive.Shared;
import org.jetbrains.annotations.NotNull;

import static essential.progressive.Pr.cons;


class Encoding {

private final RBTree shared_index;
private final Few    shared;
private       Few    cont;
private       Object datum;
private       Lot    col;

Encoding(Object datum) {
  shared_index = Shared.detect(datum);
  shared = shared_index.travel()
                       .map(o -> ((Lot) o).ref(1))
                       .toFew();
  Lot keys = shared_index.travel()
                         .map(o -> ((Lot) o).ref(0));
  int index = 0;
  while (!keys.isEmpty()) {
    shared_index.set(keys.car(), index);
    index += 1;
    keys = keys.cdr();
  }

  cont = Few.of(Label.END_CONT);
  this.datum = datum;
  col = Lot.of();
}

byte @NotNull [] process() {
  Object datum_bk = datum;
  int    length   = shared.length();
  cont = Few.of(Label.ITER_SHARED, cont, length, 0);
  col = cons(new byte[]{Binary.FEW}, col);
  col = cons(Binary.encodeVarI32(length), col);
  route(Label.APPLY_CONT);

  datum = datum_bk;
  route(Label.OF_DATUM);

  col = col.reverse();
  return Binary.connectBytes(col);
}

private void route(@NotNull String next) {
  while (true) {
    switch (next) {
      case Label.OF_DATUM -> next = ofDatum();
      case Label.OF_SHARED -> next = ofShared();
      case Label.APPLY_CONT -> next = applyCont();
      case Label.EXIT -> { return; }
    }
  }
}

private String ofShared() {
  if (datum instanceof Few fw) {
    int length = fw.length();
    cont = Few.of(Label.ITER_FEW, cont, length, 0, fw);
    col = cons(new byte[]{Binary.FEW}, col);
    col = cons(Binary.encodeVarI32(length), col);
    return Label.APPLY_CONT;
  }
  else if (datum instanceof Lot lt) {
    if (lt.isEmpty()) {
      byte[] bin = new byte[]{Binary.LOT_BEGIN, Binary.LOT_END};
      col = cons(bin, col);
      return Label.APPLY_CONT;
    }
    cont = Few.of(Label.ITER_LOT, cont, lt.cdr());
    col = cons(new byte[]{Binary.LOT_BEGIN}, col);
    datum = lt.car();
    return Label.OF_DATUM;
  }
  else {
    byte[] bin = encodeNonContainer();
    col = cons(bin, col);
    return Label.APPLY_CONT;
  }
}

private String ofDatum() {
  int index = getIndex(datum);
  if (index >= 0) {
    col = cons(shareIndex(index), col);
    return Label.APPLY_CONT;
  }

  return ofShared();
}

private String applyCont() {
  String label = (String) cont.ref(0);

  switch (label) {
    case Label.END_CONT -> { return Label.EXIT; }
    case Label.ITER_SHARED -> {
      int length = (int) cont.ref(2);
      int index  = (int) cont.ref(3);
      if (index == length) {
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }
      else {
        cont.set(3, index + 1);
        datum = shared.ref(index);
        return Label.OF_SHARED;
      }
    }
    //noinspection DuplicatedCode
    case Label.ITER_FEW -> {
      int length = (int) cont.ref(2);
      int index  = (int) cont.ref(3);
      Few fw     = (Few) cont.ref(4);
      if (index == length) {
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }
      else {
        cont.set(3, index + 1);
        datum = fw.ref(index);
        return Label.OF_DATUM;
      }
    }
    case Label.ITER_LOT -> {
      Lot lt = (Lot) cont.ref(2);

      int index = getIndex(lt);
      if (index >= 0) {
        col = cons(new byte[]{Binary.NEXT_LOT}, col);
        col = cons(shareIndex(index), col);
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }

      if (lt.isEmpty()) {
        col = cons(new byte[]{Binary.LOT_END}, col);
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }
      else {
        cont.set(2, lt.cdr());
        datum = lt.car();
        return Label.OF_DATUM;
      }
    }
    default -> throw new RuntimeException("undefined continuation " + label);
  }
}

private byte[] encodeNonContainer() {
  switch (datum) {
    case Boolean b -> { return Binary.encodeBoolean(b); }
    case Short s -> { return BinaryMate.encodeShort(s); }
    case Integer in -> { return BinaryMate.encodeInt(in); }
    case Long l -> { return BinaryMate.encodeLong(l); }
    case Float f -> { return BinaryMate.encodeFloat(f); }
    case Double d -> { return BinaryMate.encodeDouble(d); }
    case boolean[] bs -> { return BinaryMate.encodeBooleans(bs); }
    case short[] ss -> { return BinaryMate.encodeShorts(ss); }
    case int[] ins -> { return BinaryMate.encodeInts(ins); }
    case long[] ls -> { return BinaryMate.encodeLongs(ls); }
    case float[] fs -> { return BinaryMate.encodeFloats(fs); }
    case double[] ds -> { return BinaryMate.encodeDoubles(ds); }
    case Character c -> { return Binary.encodeChar(c); }
    case String str -> { return Binary.encodeString(str); }
    case Time t -> { return BinaryMate.encodeTime(t); }
    case Date d -> { return BinaryMate.encodeDate(d); }
    default -> {
      String msg = String.format(Msg.UNSUPPORTED, datum.getClass().getName());
      throw new RuntimeException(msg);
    }
  }
}

private int getIndex(Object datum) {
  int key = System.identityHashCode(datum);
  if (shared_index.isPresent(key)) {
    return (int) shared_index.ref(key);
  }
  else {
    return -1;
  }
}

private static byte @NotNull [] shareIndex(int index) {
  byte[] lll = Binary.encodeVarI32(index);
  byte[] xxx = new byte[1 + lll.length];
  xxx[0] = Binary.SHARE_INDEX;
  System.arraycopy(lll, 0, xxx, 1, lll.length);
  return xxx;
}
}
