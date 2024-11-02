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

private Stringing(RBTree identical) {
    this.identical = RBTree.map(Mate::attach, identical);
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
    if (RBTree.isPresent(identical, key)) {
        Few self = (Few) RBTree.ref(identical, key);
        if ((boolean) ref1(self)) {
            return String.format("#%s#", ref2(self));
        } else {
            set1(self, true);
            set2(self, count);
            count += 1;
            return String.format("#%s=#(%s)", ref2(self), connectArray(fw.data));
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
    if (RBTree.isPresent(identical, key)) {
        Few self = (Few) RBTree.ref(identical, key);
        if ((boolean) ref1(self)) {
            return String.format("#%s#", ref2(self));
        } else {
            set1(self, true);
            set2(self, count);
            count += 1;
            String first = process(car(lt));
            String other = connectLot(cdr(lt));
            return String.format("#%s=(%s %s)", ref2(self), first, other);
        }
    } else {
        String str = connectLot(lt);
        return String.format("(%s)", str);
    }
}

private @NotNull String connectLot(Lot lt) {
    StringBuilder builder = new StringBuilder();
    boolean cycle = false;
    while (true) {
        int key = System.identityHashCode(lt);
        if (RBTree.isPresent(identical, key)) {
            Few self = (Few) RBTree.ref(identical, key);
            if ((boolean) ref1(self)) {
                builder.append(String.format(". #%s#", ref2(self)));
                if (cycle) {
                    builder.append(')');
                }
                return builder.toString();
            } else {
                set1(self, true);
                set2(self, count);
                count += 1;
                builder.append(String.format(". #%s=(%s ", ref2(self), process(car(lt))));
                cycle = true;
                lt = cdr(lt);
            }
        } else if (cdr(lt).isEmpty()) {
            builder.append(process(car(lt)));
            return builder.toString();
        } else {
            builder.append(process(car(lt)));
            builder.append(' ');
            lt = cdr(lt);
        }
    }
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
        while (!cdr(lt).isEmpty()) {
            builder.append(stringOf(car(lt)));
            builder.append(' ');
            lt = cdr(lt);
        }
        builder.append(stringOf(car(lt)));
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
