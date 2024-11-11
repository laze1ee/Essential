/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progresive;

import essential.utilities.RBTree;
import org.jetbrains.annotations.NotNull;

import static essential.progresive.Pr.*;


class Equaling {

private final RBTree identical1;
private final RBTree identical2;
private int count;

private Equaling(@NotNull RBTree identical1, @NotNull RBTree identical2) {
    this.identical1 = identical1.map(Mate::attach);
    this.identical2 = identical2.map(Mate::attach);
    count = 0;
}

private boolean process(Object datum1, Object datum2) {
    if (datum1 instanceof Few fw1 &&
        datum2 instanceof Few fw2) {
        return jobFew(fw1, fw2);
    } else if (datum1 instanceof Lot lt1 &&
               datum2 instanceof Lot lt2) {
        if (lt1.isEmpty() && lt2.isEmpty()) {
            return true;
        } else {
            return jobLot(lt1, lt2);
        }
    } else {
        return equal(datum1, datum2);
    }
}

private boolean jobFew(@NotNull Few fw1, @NotNull Few fw2) {
    if (fw1.data.length != fw2.data.length) {
        return false;
    } else {
        int key1 = System.identityHashCode(fw1);
        int key2 = System.identityHashCode(fw2);
        if (identical1.isPresent(key1) &&
            identical2.isPresent(key2)) {
            Few self1 = (Few) identical1.ref(key1);
            Few self2 = (Few) identical2.ref(key2);
            if ((boolean) self1.ref(1) &&
                (boolean) self2.ref(1)) {
                return self1.ref(2) == self2.ref(2);
            } else {
                self1.set(1, true);
                self2.set(1, true);
                self1.set(2, count);
                self2.set(2, count);
                count += 1;
                return equalArray(fw1.data, fw2.data);
            }
        } else {
            return equalArray(fw1.data, fw2.data);
        }
    }
}

private boolean equalArray(Object @NotNull [] arr1, Object @NotNull [] arr2) {
    if (arr1.length == arr2.length) {
        int i = 0;
        while (i < arr1.length &&
               process(arr1[i], arr2[i])) {
            i = i + 1;
        }
        return i == arr1.length;
    } else {
        return false;
    }
}

private boolean jobLot(@NotNull Lot lt1, Lot lt2) {
    while (true) {
        if (lt1.isEmpty() && lt2.isEmpty()) {
            return true;
        } else {
            int key1 = System.identityHashCode(lt1);
            int key2 = System.identityHashCode(lt2);
            if (identical1.isPresent(key1) &&
                identical2.isPresent(key2)) {
                Few self1 = (Few) identical1.ref(key1);
                Few self2 = (Few) identical2.ref(key2);
                if ((boolean) self1.ref(1) &&
                    (boolean) self2.ref(1)) {
                    return self1.ref(2) == self2.ref(2);
                } else {
                    self1.set(1, true);
                    self2.set(1, true);
                    self1.set(2, count);
                    self2.set(2, count);
                    count += 1;
                    if (process(lt1.car(), lt2.car())) {
                        lt1 = lt1.cdr();
                        lt2 = lt2.cdr();
                    } else {
                        return false;
                    }
                }
            } else if (process(lt1.car(), lt2.car())) {
                lt1 = lt1.cdr();
                lt2 = lt2.cdr();
            } else {
                return false;
            }
        }
    }
}

static boolean identicalEqual(Object datum1, RBTree identical1, Object datum2, RBTree identical2) {
    Equaling inst = new Equaling(identical1, identical2);
    return inst.process(datum1, datum2);
}

static boolean lotEqual(@NotNull Lot lt1, Lot lt2) {
    while (true) {
        if (lt1.isEmpty() && lt2.isEmpty()) {
            return true;
        } else if (equal(lt1.car(), lt2.car())) {
            lt1 = lt1.cdr();
            lt2 = lt2.cdr();
        } else {
            return false;
        }
    }
}

static boolean fewEqual(@NotNull Few fw1, @NotNull Few fw2) {
    if (fw1.data.length == fw2.data.length) {
        int i = 0;
        while (i < fw1.data.length &&
               equal(fw1.data[i], fw2.data[i])) {
            i = i + 1;
        }
        return i == fw1.data.length;
    } else {
        return false;
    }
}
}