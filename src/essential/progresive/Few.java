/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import essential.functional.Do1;
import essential.functional.Predicate2;
import essential.utilities.Binary;
import essential.utilities.CheckSum;
import essential.utilities.RBTree;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class Few {

final Object[] data;

Few(Object[] data) {this.data = data;}

@Override
public String toString() {
    RBTree identical = Identical.detect(this);
    if (identical.isEmpty()) {
        return Stringing.fewString(this);
    } else {
        return Stringing.identicalString(this, identical);
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Few fw) {
        RBTree identical1 = Identical.detect(this);
        RBTree identical2 = Identical.detect(fw);
        if (identical1.isEmpty() && identical2.isEmpty()) {
            return Equaling.fewEqual(this, fw);
        } else if (!identical1.isEmpty() && !identical2.isEmpty() &&
                   this.data.length == fw.data.length) {
            return Equaling.identicalEqual(this, identical1, fw, identical2);
        } else {
            return false;
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

public int length() {return data.length;}

public Object[] toRaw() {return data;}

public @NotNull Object ref(int index) {
    if (0 <= index && index < data.length) {
        return data[index];
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, this));
    }
}

public void set(int index, Object datum) {
    if (0 <= index && index < this.data.length) {
        this.data[index] = datum;
    } else {
        throw new RuntimeException(String.format(Msg.INDEX_OUT, index, this));
    }
}

public void fill(@NotNull Object datum) {Arrays.fill(this.data, datum);}

@Contract(pure = true)
public @NotNull Few copy() {
    int length = this.data.length;
    Object[] arr = new Object[length];
    System.arraycopy(this.data, 0, arr, 0, length);
    return new Few(arr);
}

public Lot toLot() {
    int length = this.data.length;
    Lot lt = new LotEnd();
    for (int i = length - 1; 0 <= i; i -= 1) {
        lt = new LotPair(this.data[i], lt);
    }
    return lt;
}

public @NotNull Few map(Do1 fn) {
    int length = this.data.length;
    Object[] arr = new Object[length];
    for (int i = 0; i < length; i += 1) {
        arr[i] = fn.apply(this.data[i]);
    }
    return new Few(arr);
}

/**
 * Find the first index satisfying the given predicate.
 *
 * @param fn   A predicate taking two arguments.
 * @param datum The second argument of the predicate.
 *
 * @return The index if found, -1 otherwise.
 */
public int find(Predicate2 fn, Object datum) {
    int length = this.data.length;
    for (int i = 0; i < length; i += 1) {
        if (fn.apply(this.data[i], datum)) {
            return i;
        }
    }
    return -1;
}
}
