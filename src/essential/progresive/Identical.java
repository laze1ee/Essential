/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import essential.utilities.RBTree;


public class Identical {

private final RBTree collector;
private final RBTree identical;

private Identical() {
    collector = new RBTree(Pr::less, Pr::greater);
    identical = new RBTree(Pr::less, Pr::greater);
}

private RBTree process(Object datum) {
    collect(datum);
    return identical;
}

private void collect(Object datum) {
    if (datum instanceof Few fw) {
        collectFew(fw);
    } else if (datum instanceof Lot lt) {
        collectLot(lt);
    }
}

private void collectFew(Few fw) {
    int key = System.identityHashCode(fw);
    boolean success = collector.insert(key, false);
    if (success) {
        for (int i = 0; i < fw.data.length; i += 1) {
            collect(fw.data[i]);
        }
    } else {
        identical.insert(key, fw);
    }
}

private void collectLot(Lot lt) {
    while (true) {
        if (lt instanceof LotEnd) {
            break;
        } else {
            int key = System.identityHashCode(lt);
            boolean success = collector.insert(key, false);
            if (success) {
                collect(lt.car());
                lt = lt.cdr();
            } else {
                identical.insert(key, lt);
                break;
            }
        }
    }
}

public static RBTree detect(Object datum) {
    Identical inst = new Identical();
    return inst.process(datum);
}
}
