package essential.progresive;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static essential.progresive.Pr.*;


class Self {

private static class Detecting {

    final Object datum;
    Lot collector;
    Lot identical;

    Detecting(Object datum) {
        this.datum = datum;
        this.collector = lot();
        this.identical = lot();
    }

    Lot process() {
        job(datum);
        return identical;
    }

    void job(Object datum) {
        if (datum instanceof Few fw) {
            collectArray(fw.data);
        } else if (datum instanceof Pair pair) {
            collectPair(pair);
        }
    }

    void collectArray(Object[] arr) {
        if (Mate.isBelong(arr, collector)) {
            if (!Mate.isBelong(arr, identical)) {
                identical = cons(arr, identical);
            }
        } else {
            collector = cons(arr, collector);
            for (Object datum : arr) {
                job(datum);
            }
        }
    }

    void collectPair(Lot pair) {
        if (!(pair instanceof LotEnd)) {
            if (Mate.isBelong(pair, collector)) {
                if (!Mate.isBelong(pair, identical)) {
                    identical = cons(pair, identical);
                }
            } else {
                collector = cons(pair, collector);
                job(car(pair));
                collectPair(cdr(pair));
            }
        }
    }
}

static Lot detect(Object datum) {
    Detecting inst = new Detecting(datum);
    return inst.process();
}


private static class Labeling {

    final Object datum;
    final Lot identical;
    int count;

    Labeling(Object datum, Lot cycle) {
        this.datum = datum;
        this.identical = mapLot(Labeling::attach, cycle);
        this.count = 0;
    }

    Object process() {
        return job(datum);
    }

    Object job(Object datum) {
        if (datum instanceof Few fw) {
            return jobArray(fw.data);
        } else if (datum instanceof Pair pair) {
            return jobPair(pair);
        } else {
            return datum;
        }
    }

    @Contract("_ -> new")
    @NotNull Fev jobArray(Object[] arr) {
        Few cyc = find(arr);
        if (cyc != null &&
            (boolean) ref1(cyc)) {
            return new FewMark((int) ref2(cyc));
        } else if (cyc != null) {
            set1(cyc, true);
            set2(cyc, count);
            count = count + 1;
            Object[] ooo = batchArray(arr);
            return new FewSelf(ooo, (int) ref2(cyc));
        } else {
            Object[] ooo = batchArray(arr);
            return new Few(ooo);
        }
    }

    Object @NotNull [] batchArray(Object @NotNull [] arr) {
        int n = arr.length;
        Object[] ooo = new Object[n];
        for (int i = 0; i < n; i = i + 1) {
            ooo[i] = job(arr[i]);
        }
        return ooo;
    }

    @Contract("_ -> new")
    @NotNull Lot jobPair(@NotNull Lot pair) {
        Few cyc = find(pair);
        if (cyc != null && (boolean) ref1(cyc)) {
            return new LotMark((int) ref2(cyc));
        } else if (cyc != null) {
            set1(cyc, true);
            set2(cyc, count);
            count = count + 1;
            return new LotPairSelf(job(car(pair)), jobNext(cdr(pair)), (int) ref2(cyc));
        } else {
            return new LotPairLinkHead(job(car(pair)), jobNext(cdr(pair)));
        }
    }

    @Contract("_ -> new")
    @NotNull Lot jobNext(@NotNull Lot pair) {
        if (pair.isEmpty()) {
            return new LotEnd();
        }
        Few cyc = find(pair);
        if (cyc != null && (boolean) ref1(cyc)) {
            return new LotMark((int) ref2(cyc));
        } else if (cyc != null) {
            set1(cyc, true);
            set2(cyc, count);
            count = count + 1;
            return new LotPairSelf(job(car(pair)), jobNext(cdr(pair)), (int) ref2(cyc));
        } else {
            return new LotPairLink(job(car(pair)), jobNext(cdr(pair)));
        }
    }

    @Nullable Few find(Object datum) {
        Lot col = identical;
        while (!col.isEmpty()) {
            Few item = (Few) car(col);
            if (eq(datum, ref0(item))) {
                return item;
            }
            col = cdr(col);
        }
        return null;
    }

    @Contract("_ -> new")
    static @NotNull Object attach(Object datum) {
        return few(datum, false, -1);
    }
}

static Object label(Object datum, Lot cycle) {
    Labeling inst = new Labeling(datum, cycle);
    return inst.process();
}
}
