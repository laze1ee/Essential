/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import essential.utilities.RBTree;


public class Shared {

public static RBTree detect(Object datum) {
  Shared inst = new Shared(datum);
  inst.route();
  return inst.identical;
}

private final RBTree collector;
final RBTree identical;
private Few cont;
private Object datum;

Shared(Object datum) {
  collector = new RBTree((o1, o2) -> (int) o1 < (int) o2,
                         (o1, o2) -> (int) o1 > (int) o2);
  identical = new RBTree((o1, o2) -> (int) o1 < (int) o2,
                         (o1, o2) -> (int) o1 > (int) o2);
  cont = Few.of(Label.END_CONT);
  this.datum = datum;
}

void route() {
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
  boolean success = collector.insert(key, false);
  if (success) {
    if (datum instanceof Few fw) {
      int length = fw.length();
      if (length == 0) {
        return Label.APPLY_CONT;
      }
      cont = Few.of(Label.ITER_FEW, cont, length, 1, fw);
      datum = fw.ref(0);
      return Label.OF_DATUM;
    }
    else if (datum instanceof Lot lt) {
      if (lt.isEmpty()) {
        return Label.APPLY_CONT;
      }
      cont = Few.of(Label.ITER_LOT, cont, lt.cdr());
      datum = lt.car();
      return Label.OF_DATUM;
    }
    else {
      return Label.APPLY_CONT;
    }
  }

  identical.insert(key, datum);
  return Label.APPLY_CONT;
}

private String applyCont() {
  String token = (String) cont.ref(0);
  switch (token) {
    case Label.END_CONT -> {return Label.EXIT;}
    //noinspection DuplicatedCode
    case Label.ITER_FEW -> {
      int length = (int) cont.ref(2);
      int index = (int) cont.ref(3);
      Few fw = (Few) cont.ref(4);
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
      int key = System.identityHashCode(lt);
      boolean success = collector.insert(key, false);
      if (success) {
        if (lt.isEmpty()) {
          cont = (Few) cont.ref(1);
          return Label.APPLY_CONT;
        }
        else {
          cont.set(2, lt.cdr());
          datum = lt.car();
          return Label.OF_DATUM;
        }
      }
      else {
        identical.insert(key, lt);
        cont = (Few) cont.ref(1);
        return Label.APPLY_CONT;
      }
    }
    default -> throw new RuntimeException("undefined continuation " + token);
  }
}
}
