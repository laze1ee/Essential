/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import essential.utilities.RBTree;

import static essential.progressive.Pr.equal;
import static essential.progressive.Pr.few;


class Equality {

static boolean process(Object datum1, Object datum2) {
    Equality inst = new Equality(datum1, datum2);
    inst.route();
    return inst.r0;
}

private final RBTree identical1;
private final RBTree identical2;
private Few cont;
private Object datum1;
private Object datum2;
private int order;
private boolean r0;

private Equality(Object datum1, Object datum2) {
    identical1 = Sharing.detect(datum1).map(o -> few(false, -1));
    identical2 = Sharing.detect(datum2).map(o -> few(false, -1));
    cont = few(Label.END_CONT);
    this.datum1 = datum1;
    this.datum2 = datum2;
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
    if (datum1 == datum2) {
        r0 = true;
        return Label.APPLY_CONT;
    } else if (datum1 instanceof Few && datum2 instanceof Few) {
        return ofFew();
    } else if (datum1 instanceof Lot && datum2 instanceof Lot) {
        cont = few(Label.ITER_LOT, cont, datum1, datum2);
        r0 = true;
        return Label.APPLY_CONT;
    } else {
        r0 = equal(datum1, datum2);
        return Label.APPLY_CONT;
    }
}

private String ofFew() {
    Few fw1 = (Few) datum1;
    Few fw2 = (Few) datum2;

    int length = fw1.length();
    if (length != fw2.length()) {
        r0 = false;
    } else {
        int key1 = System.identityHashCode(fw1);
        int key2 = System.identityHashCode(fw2);
        if (identical1.isPresent(key1) &&
            identical2.isPresent(key2)) {
            Few mark1 = (Few) identical1.ref(key1);
            Few mark2 = (Few) identical2.ref(key2);
            if ((boolean) mark1.ref(0) &&
                (boolean) mark2.ref(0)) {
                r0 = mark1.ref(1) == mark2.ref(1);
            } else if ((boolean) mark1.ref(0) ||
                       (boolean) mark2.ref(0)) {
                r0 = false;
            } else {
                mark1.set(0, true);
                mark2.set(0, true);
                mark1.set(1, order);
                mark2.set(1, order);
                order += 1;
                cont = few(Label.ITER_FEW, cont, length, 0, fw1, fw2);
                r0 = true;
            }
        } else if (identical1.isPresent(key1) ||
                   identical2.isPresent(key2)) {
            r0 = false;
        } else {
            cont = few(Label.ITER_FEW, cont, length, 0, fw1, fw2);
            r0 = true;
        }
    }

    return Label.APPLY_CONT;
}

private String applyCont() {
    String label = (String) cont.ref(0);

    switch (label) {
    case Label.END_CONT -> {return Label.EXIT;}
    case Label.ITER_FEW -> {
        int length = (int) cont.ref(2);
        int index = (int) cont.ref(3);
        Few fw1 = (Few) cont.ref(4);
        Few fw2 = (Few) cont.ref(5);
        if (!r0 || index == length) {
            cont = (Few) cont.ref(1);
            return Label.APPLY_CONT;
        } else {
            cont.set(3, index + 1);
            datum1 = fw1.ref(index);
            datum2 = fw2.ref(index);
            return Label.OF_DATUM;
        }
    }
    case Label.ITER_LOT -> {
        Lot lt1 = (Lot) cont.ref(2);
        Lot lt2 = (Lot) cont.ref(3);
        if (lt1.isEmpty() && lt2.isEmpty()) {
            cont = (Few) cont.ref(1);
            return Label.APPLY_CONT;
        } else if (!r0 || lt1.isEmpty() || lt2.isEmpty()) {
            r0 = false;
            cont = (Few) cont.ref(1);
            return Label.APPLY_CONT;
        } else {
            int key1 = System.identityHashCode(lt1);
            int key2 = System.identityHashCode(lt2);
            if (identical1.isPresent(key1) &&
                identical2.isPresent(key2)) {
                Few mark1 = (Few) identical1.ref(key1);
                Few mark2 = (Few) identical2.ref(key2);
                if ((boolean) mark1.ref(0) &&
                    (boolean) mark2.ref(0)) {
                    r0 = mark1.ref(1) == mark2.ref(1);
                    cont = (Few) cont.ref(1);
                    return Label.APPLY_CONT;
                } else if ((boolean) mark1.ref(0) ||
                           (boolean) mark2.ref(0)) {
                    r0 = false;
                    cont = (Few) cont.ref(1);
                    return Label.APPLY_CONT;
                } else {
                    mark1.set(0, true);
                    mark2.set(0, true);
                    mark1.set(1, order);
                    mark2.set(1, order);
                    order += 1;
                    cont.set(2, lt1.cdr());
                    cont.set(3, lt2.cdr());
                    datum1 = lt1.car();
                    datum2 = lt2.car();
                    return Label.OF_DATUM;
                }
            } else {
                cont.set(2, lt1.cdr());
                cont.set(3, lt2.cdr());
                datum1 = lt1.car();
                datum2 = lt2.car();
                return Label.OF_DATUM;
            }
        }
    }
    default -> throw new RuntimeException("undefined continuation " + label);
    }
}
}
