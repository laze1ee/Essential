/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import essential.utilities.Binary;
import essential.utilities.CheckSum;
import essential.utilities.RBTree;


public class Lot {

Lot() {
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

@Override
public int hashCode() {
    byte[] bin = Binary.encode(this);
    return CheckSum.fletcher32(bin);
}

public boolean isEmpty() {
    return this instanceof LotEnd;
}

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
}
