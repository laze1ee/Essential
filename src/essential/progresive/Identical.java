package essential.progresive;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static essential.progresive.Pr.*;


class Identical {

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
        _job(datum);
        return identical;
    }

    private void _job(Object datum) {
        if (datum instanceof Few fw) {
            _collectArray(fw.data);
        } else if (datum instanceof Pair pair) {
            _collectPair(pair);
        }
    }

    private void _collectArray(Object[] arr) {
        if (Mate.isBelong(arr, collector)) {
            if (!Mate.isBelong(arr, identical)) {
                identical = cons(arr, identical);
            }
        } else {
            collector = cons(arr, collector);
            for (Object datum : arr) {
                _job(datum);
            }
        }
    }

    private void _collectPair(Lot pair) {
        if (!(pair instanceof PairEnd)) {
            if (Mate.isBelong(pair, collector)) {
                if (!Mate.isBelong(pair, identical)) {
                    identical = cons(pair, identical);
                }
            } else {
                collector = cons(pair, collector);
                _job(car(pair));
                _collectPair(cdr(pair));
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
        return _job(datum);
    }

    Object _job(Object datum) {
        if (datum instanceof Few fw) {
            return _jobArray(fw.data);
        } else if (datum instanceof Pair pair) {
            return _jobPair(pair);
        } else {
            return datum;
        }
    }

    @Contract("_ -> new")
    private @NotNull Fer _jobArray(Object[] arr) {
        Few cyc = _find(arr);
        if (cyc != null &&
            (boolean) ref1(cyc)) {
            return new FerMark((int) ref2(cyc));
        } else if (cyc != null) {
            set1(cyc, true);
            set2(cyc, count);
            count = count + 1;
            Object[] ooo = _batchArray(arr);
            return new FerId(ooo, (int) ref2(cyc));
        } else {
            Object[] ooo = _batchArray(arr);
            return new Few(ooo);
        }
    }

    Object @NotNull [] _batchArray(Object @NotNull [] arr) {
        int n = arr.length;
        Object[] ooo = new Object[n];
        for (int i = 0; i < n; i = i + 1) {
            ooo[i] = _job(arr[i]);
        }
        return ooo;
    }

    @Contract("_ -> new")
    @NotNull
    Lot _jobPair(@NotNull Lot pair) {
        if (pair.isEmpty()) {
            return new PairEnd();
        }
        Few cyc = _find(pair);
        if (cyc != null && (boolean) ref1(cyc)) {
            return new PairMark((int) ref2(cyc));
        } else if (cyc != null) {
            set1(cyc, true);
            set2(cyc, count);
            count = count + 1;
            return new PairId(_job(car(pair)), _jobPair(cdr(pair)), (int) ref2(cyc));
        } else {
            return new PairLink(_job(car(pair)), _jobPair(cdr(pair)));
        }
    }

    @Nullable
    Few _find(Object datum) {
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
