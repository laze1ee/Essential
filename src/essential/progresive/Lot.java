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

private Object data;
private Lot next;

protected Lot() {
    this.data = null;
    this.next = null;
}

protected Lot(Object data, Lot next) {
    this.data = data;
    this.next = next;
}

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

public boolean isEmpty() {
    return data == null && next == null;
}

public int length() {
    int n = Mate.theHareAndTortoise(this);
    if (n == -1) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, this);
        throw new RuntimeException(msg);
    } else {
        return n;
    }
}

public boolean isBreadthCircle() {
    int n = Mate.theHareAndTortoise(this);
    return n == -1;
}

public @NotNull Object car() {
    if (this.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        return this.data;
    }
}

public @NotNull Lot cdr() {
    if (this.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        return this.next;
    }
}

public @NotNull Object caar() {
    return ((Lot) this.car()).car();
}

public @NotNull Lot cddr() {
    return this.cdr().cdr();
}

/**
 * @return the car of the cdr of this lot.
 */
public Object cadr() {
    return this.cdr().car();
}

/**
 * @return the cdr of the car of this lot.
 */
public @NotNull Lot cdar() {
    return ((Lot) this.car()).cdr();
}

public @NotNull Object ref(int index) {
    Lot lt = this;
    int i = index;
    while (i >= 0) {
        if (lt.isEmpty()) {
            String msg = String.format(Msg.INDEX_OUT, index, this);
            throw new RuntimeException(msg);
        }
        if (i == 0) {
            return lt.car();
        }
        i -= 1;
        lt = lt.cdr();
    }
    String msg = String.format(Msg.INDEX_OUT, index, this);
    throw new RuntimeException(msg);
}

public void setCar(@NotNull Object datum) {
    if (this.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        this.data = datum;
    }
}

public void setCdr(@NotNull Lot lt) {
    if (this.isEmpty()) {
        throw new RuntimeException(Msg.LOT_EMPTY);
    } else {
        this.next = lt;
    }
}

public void set(int index, Object datum) {
    Lot lt = this;
    int i = index;
    while (i >= 0) {
        if (lt.isEmpty()) {
            String msg = String.format(Msg.INDEX_OUT, index, this);
            throw new RuntimeException(msg);
        }
        if (i == 0) {
            lt.data = datum;
            return;
        }
        i -= 1;
        lt = lt.cdr();
    }
    String msg = String.format(Msg.INDEX_OUT, index, this);
    throw new RuntimeException(msg);
}

public @NotNull Lot reverse() {
    if (this.isEmpty()) {
        return this;
    } else if (this.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, this);
        throw new RuntimeException(msg);
    } else {
        Lot head = new Lot(this.car(), new Lot());
        Lot next = this.cdr();
        while (!next.isEmpty()) {
            head = new Lot(next.car(), head);
            next = next.cdr();
        }
        return head;
    }
}

public @NotNull Lot head(int index) {
    if (index == 0) {
        return new Lot();
    } else if (this.isBreadthCircle() || (0 <= index && index <= Mate.length(this))) {
        int i = index - 1;
        Lot head = new Lot(this.car(), new Lot());
        Lot ooo = head;
        Lot xxx = this.cdr();
        while (i > 0) {
            ooo.next = new Lot(xxx.car(), new Lot());
            ooo = ooo.cdr();
            xxx = xxx.cdr();
            i -= 1;
        }
        return head;
    } else {
        String msg = String.format(Msg.INDEX_OUT, index, this);
        throw new RuntimeException(msg);
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
        String msg = String.format(Msg.INDEX_OUT, index, this);
        throw new RuntimeException(msg);
    }
}

public @NotNull Lot copy() {
    if (this.isEmpty()) {
        return Pr.lot();
    } else if (this.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, this);
        throw new RuntimeException(msg);
    } else {
        Lot head = new Lot(this.car(), new Lot());
        Lot ooo = head;
        Lot xxx = this.cdr();
        while (!xxx.isEmpty()) {
            ooo.setCdr(Pr.lot(xxx.car()));
            ooo = ooo.cdr();
            xxx = xxx.cdr();
        }
        return head;
    }
}

public @NotNull Few toFew() {
    if (this.isBreadthCircle()) {
        String msg = String.format(Msg.CIRCULAR_BREADTH, this);
        throw new RuntimeException(msg);
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
        String msg = String.format(Msg.CIRCULAR_BREADTH, this);
        throw new RuntimeException(msg);
    }
    Lot head = new Lot(false, new Lot());
    Lot ooo = head;
    Lot xxx = this;
    while (!xxx.isEmpty()) {
        if (fn.apply(xxx.car())) {
            ooo.next = new Lot(xxx.car(), new Lot());
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
        String msg = String.format(Msg.CIRCULAR_BREADTH, this);
        throw new RuntimeException(msg);
    }
    Lot head = new Lot(fn.apply(this.car()), new Lot());
    Lot ooo = head;
    Lot xxx = this.cdr();
    while (!xxx.isEmpty()) {
        ooo.next = new Lot(fn.apply(xxx.car()), new Lot());
        ooo = ooo.cdr();
        xxx = xxx.cdr();
    }
    return head;
}
}
