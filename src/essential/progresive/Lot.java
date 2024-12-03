/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import essential.functional.Do1;
import essential.functional.Predicate1;
import essential.utilities.RBTree;
import org.jetbrains.annotations.NotNull;


public class Lot {

Lot() {}

@Override
public String toString() {
    RBTree identical = Identical.detect(this);
    if (identical.isEmpty()) {
        return Stringing.lotString(this);
    } else {
        return Stringing.identicalString(this, identical);
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Lot lt) {
        RBTree identical1 = Identical.detect(this);
        RBTree identical2 = Identical.detect(datum);
        if (identical1.isEmpty() && identical2.isEmpty()) {
            return Equaling.lotEqual(this, lt);
        } else {
            return Equaling.identicalEqual(this, identical1, datum, identical2);
        }
    } else {
        return false;
    }
}

public boolean isEmpty() {return this instanceof LotEnd;}

public int length() {
    int n = Mate.theHareAndTortoise(this);
    if (n == -1) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, this));
    } else {
        return n;
    }
}

public boolean isBreadthCircle() {
    int n = Mate.theHareAndTortoise(this);
    return n == -1;
}

public @NotNull Object car() {
    if (this instanceof LotEnd) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        return ((LotPair) this).data;
    }
}

public @NotNull Lot cdr() {
    if (this instanceof LotEnd) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        return ((LotPair) this).next;
    }
}

public @NotNull Object caar() {return ((Lot) this.car()).car();}

public @NotNull Lot cddr() {return this.cdr().cdr();}

/**
 * @return the cdr of the car of this lot.
 */
public @NotNull Lot cdar() {return ((Lot) this.car()).cdr();}

public @NotNull Object ref(int index) {
    Lot lt = this;
    int i = index;
    while (i >= 0) {
        if (lt.isEmpty()) {
            throw new RuntimeException(String.format(Msg.INDEX_OUT, index, this));
        }
        if (i == 0) {
            return lt.car();
        }
        i -= 1;
        lt = lt.cdr();
    }
    throw new RuntimeException(String.format(Msg.INDEX_OUT, index, this));
}

public @NotNull Lot reverse() {
    if (this.isEmpty()) {
        return this;
    } else if (this.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, this));
    } else {
        Lot head = new LotPair(this.car(), new LotEnd());
        Lot next = this.cdr();
        while (!next.isEmpty()) {
            head = new LotPair(next.car(), head);
            next = next.cdr();
        }
        return head;
    }
}

public @NotNull Lot head(int index) {
    if (index == 0) {
        return new LotEnd();
    } else if (this.isBreadthCircle() || (0 <= index && index <= Mate.length(this))) {
        int i = index - 1;
        Lot head = new LotPair(this.car(), new LotEnd());
        Lot ooo = head;
        Lot xxx = this.cdr();
        while (i > 0) {
            ((LotPair) ooo).next = new LotPair(xxx.car(), new LotEnd());
            ooo = ooo.cdr();
            xxx = xxx.cdr();
            i -= 1;
        }
        return head;
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, this));
    }
}

public @NotNull Lot tail(int index) {
    if (this.isBreadthCircle() || (0 <= index && index <= Mate.length(this))) {
        Lot lt = this;
        int i = index;
        while (i > 0) {
            lt = lt.cdr();
            i -= 1;
        }
        return lt;
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, this));
    }
}

public @NotNull Lot copy() {
    if (this.isEmpty()) {
        return Pr.lot();
    } else if (this.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, this));
    } else {
        Lot head = new LotPair(this.car(), new LotEnd());
        Lot ooo = head;
        Lot xxx = this.cdr();
        while (!xxx.isEmpty()) {
            Pr.setCdr(ooo, Pr.lot(xxx.car()));
            ooo = ooo.cdr();
            xxx = xxx.cdr();
        }
        return head;
    }
}

public @NotNull Few toFew() {
    if (this.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, this));
    }
    int length = Mate.length(this);
    Few fw = Pr.makeFew(length, 0);
    Lot lt = this;
    for (int i = 0; i < length; i += 1) {
        fw.set(i, lt.car());
        lt = lt.cdr();
    }
    return fw;
}

public @NotNull Lot filter(Predicate1 fn) {
    if (this.isEmpty()) {
        return this;
    }
    if (this.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, this));
    }
    Lot head = new LotPair(false, new LotEnd());
    Lot ooo = head;
    Lot xxx = this;
    while (!xxx.isEmpty()) {
        if (fn.apply(xxx.car())) {
            ((LotPair) ooo).next = new LotPair(xxx.car(), new LotEnd());
            ooo = ooo.cdr();
        }
        xxx = xxx.cdr();
    }
    return head.cdr();
}

public @NotNull Lot map(Do1 fn) {
    if (this.isEmpty()) {
        return this;
    }
    if (this.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.CIRCULAR_BREADTH, this));
    }
    Lot head = new LotPair(fn.apply(this.car()), new LotEnd());
    Lot ooo = head;
    Lot xxx = this.cdr();
    while (!xxx.isEmpty()) {
        ((LotPair) ooo).next = new LotPair(fn.apply(xxx.car()), new LotEnd());
        ooo = ooo.cdr();
        xxx = xxx.cdr();
    }
    return head;
}
}
