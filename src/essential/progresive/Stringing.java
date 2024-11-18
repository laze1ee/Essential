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


class Stringing {

private final RBTree identical;
private int count;

private Stringing(@NotNull RBTree identical) {
    this.identical = identical.map(o -> few(o, -1));
    count = 0;
}

private String process(Object datum) {
    if (datum instanceof Few fw) {
        return jobFew(fw);
    } else if (datum instanceof Lot lt) {
        if (lt.isEmpty()) {
            return "()";
        } else {
            return jobLot(lt);
        }
    } else {
        return stringOf(datum);
    }
}

private @NotNull String jobFew(Few fw) {
    int key = System.identityHashCode(fw);
    if (identical.isPresent(key)) {
        Few self = (Few) identical.ref(key);
        if ((boolean) self.ref(0)) {
            return String.format("#%s#", self.ref(1));
        } else {
            self.set(0, true);
            self.set(1, count);
            count += 1;
            return String.format("#%s=#(%s)", self.ref(1), connectArray(fw.data));
        }
    } else {
        return String.format("#(%s)", connectArray(fw.data));
    }
}

private @NotNull String connectArray(Object @NotNull [] arr) {
    int bound = arr.length;
    if (bound == 0) {
        return "";
    } else {
        StringBuilder str = new StringBuilder();
        bound = bound - 1;
        for (int i = 0; i < bound; i = i + 1) {
            str.append(process(arr[i]));
            str.append(' ');
        }
        str.append(process(arr[bound]));
        return str.toString();
    }
}

private @NotNull String jobLot(Lot lt) {
    int key = System.identityHashCode(lt);
    if (identical.isPresent(key)) {
        Few self = (Few) identical.ref(key);
        if ((boolean) self.ref(0)) {
            return String.format("#%s#", self.ref(1));
        } else {
            self.set(0, true);
            self.set(1, count);
            count += 1;
            return String.format("#%s=(%s)", self.ref(1), connectLot(lt));
        }
    } else {
        return String.format("(%s)", connectLot(lt));
    }
}

private @NotNull String connectLot(@NotNull Lot lt) {
    StringBuilder builder = new StringBuilder();
    builder.append(process(lt.car()));
    lt = lt.cdr();
    if (lt.isEmpty()) {
        return builder.toString();
    }

    while (!lt.isEmpty()) {
        int key = System.identityHashCode(lt);
        if (identical.isPresent(key)) {
            Few self = (Few) identical.ref(key);
            if ((boolean) self.ref(0)) {
                builder.append(String.format(" . #%s#", self.ref(1)));
            } else {
                self.set(0, true);
                self.set(1, count);
                count += 1;
                builder.append(String.format(" . #%s=(%s)", self.ref(1), connectLot(lt)));
            }
            return builder.toString();
        } else {
            builder.append(' ');
            builder.append(process(lt.car()));
            lt = lt.cdr();
        }
    }
    return builder.toString();
}

static @NotNull String identicalString(Object datum, RBTree identical) {
    Stringing inst = new Stringing(identical);
    return inst.process(datum);
}

static @NotNull String lotString(@NotNull Lot lt) {
    if (lt.isEmpty()) {
        return "()";
    } else {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        while (!lt.cdr().isEmpty()) {
            builder.append(stringOf(lt.car()));
            builder.append(' ');
            lt = lt.cdr();
        }
        builder.append(stringOf(lt.car()));
        builder.append(')');
        return builder.toString();
    }
}

static @NotNull String fewString(@NotNull Few fw) {
    int len = fw.data.length;
    if (len == 0) {
        return "#()";
    } else {
        StringBuilder builder = new StringBuilder();
        builder.append("#(");
        len -= 1;
        for (int i = 0; i < len; i = i + 1) {
            builder.append(stringOf(fw.data[i]));
            builder.append(' ');
        }
        builder.append(stringOf(fw.data[len]));
        builder.append(')');
        return builder.toString();
    }
}
}
