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


public class Few{

final Object[] data;

Few(Object[] data) {
    this.data = data;
}

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

public int length() {
    return data.length;
}

public Object[] toRaw() {
    return data;
}
}
