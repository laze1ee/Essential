/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import essential.utilities.RBTree;
import org.jetbrains.annotations.NotNull;

import static essential.progressive.Pr.*;


class ToString {

static @NotNull String process(Object datum) {
  ToString inst = new ToString(datum);
  inst.route();
  return inst.builder.toString();
}

private final RBTree identical;
private Few cont;
private Object datum;
private final StringBuilder builder;
private int order;

private ToString(Object datum) {
  identical = Shared.detect(datum).map(o -> few(false, -1));
  cont = few(Label.END_CONT);
  this.datum = datum;
  builder = new StringBuilder();
  order = 0;
}

private void route() {
  //noinspection DuplicatedCode
  String next = Label.OF_DATUM;
  while (true) {
    switch (next) {
      case Label.OF_DATUM -> next = ofDatum();
      case Label.APPLY_CONT -> next = applyCont();
      case Label.EXIT -> {return;}
    }
  }
}

private String ofDatum() {
  int key = System.identityHashCode(datum);
  if (identical.isPresent(key)) {
    Few mark = (Few) identical.ref(key);
    if ((boolean) mark.ref(0)) {
      builder.append("#").append(mark.ref(1)).append("#");
      return Label.APPLY_CONT;
    }
    mark.set(0, true);
    mark.set(1, order);
    order += 1;
    builder.append("#").append(mark.ref(1)).append("=");
  }

  if (datum instanceof Few fw) {
    int length = fw.length();
    if (length == 0) {
      builder.append("#()");
      return Label.APPLY_CONT;
    }
    else {
      builder.append("#(");
      cont = few(Label.ITER_FEW, cont, length, 1, fw);
      datum = fw.ref(0);
      return Label.OF_DATUM;
    }
  }
  else if (datum instanceof Lot lt) {
    if (lt.isEmpty()) {
      builder.append("()");
      return Label.APPLY_CONT;
    }
    else {
      builder.append("(");
      cont = few(Label.ITER_LOT, cont, lt.cdr());
      datum = lt.car();
      return Label.OF_DATUM;
    }
  }
  else {
    builder.append(stringOf(datum));
    return Label.APPLY_CONT;
  }
}

private String applyCont() {
  String label = (String) cont.ref(0);
  switch (label) {
    case Label.END_CONT -> {return Label.EXIT;}
    case Label.ITER_FEW -> {
      int length = (int) cont.ref(2);
      int index = (int) cont.ref(3);
      Few fw = (Few) cont.ref(4);
      if (index == length) {
        builder.append(")");
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }
      else {
        builder.append(" ");
        cont.set(3, index + 1);
        datum = fw.ref(index);
        return Label.OF_DATUM;
      }
    }
    case Label.ITER_LOT -> {
      Lot lt = (Lot) cont.ref(2);
      int key = System.identityHashCode(lt);
      if (identical.isPresent(key)) {
        Few mark = (Few) identical.ref(key);
        if ((boolean) mark.ref(0)) {
          builder.append(" . #").append(mark.ref(1)).append("#)");
          cont = (Few) cont.ref(1);
          return Label.APPLY_CONT;
        }
        else {
          mark.set(0, true);
          mark.set(1, order);
          order += 1;
          builder.append(" . #").append(mark.ref(1)).append("=(");
          if (lt.isEmpty()) {
            builder.append("))");
            cont = (Few) cont.ref(1);
            return Label.APPLY_CONT;
          }
          else {
            cont.set(2, lot());
            cont = few(Label.ITER_LOT, cont, lt.cdr());
            datum = lt.car();
            return Label.OF_DATUM;
          }
        }
      }
      else {
        if (lt.isEmpty()) {
          builder.append(")");
          cont = (Few) cont.ref(1);
          return Label.APPLY_CONT;
        }
        else {
          builder.append(" ");
          cont.set(2, lt.cdr());
          datum = lt.car();
          return Label.OF_DATUM;
        }
      }
    }
    default -> throw new RuntimeException("undefined continuation " + label);
  }
}
}
