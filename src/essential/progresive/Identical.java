package essential.progresive;

import essential.utilities.RBTree;

import static essential.progresive.Pr.car;
import static essential.progresive.Pr.cdr;


class Identical {

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
        collectArray(fw.data);
    } else if (datum instanceof LotPair pair) {
        collectPair(pair);
    }
}

private void collectArray(Object[] arr) {
    int key = System.identityHashCode(arr);
    boolean success = RBTree.insert(collector, key, false);
    if (success) {
        for (Object datum : arr) {
            collect(datum);
        }
    } else {
        RBTree.insert(identical, key, arr);
    }
}

private void collectPair(Lot pair) {
    while (true) {
        if (pair instanceof LotEnd) {
            break;
        } else {
            int key = System.identityHashCode(pair);
            boolean success = RBTree.insert(collector, key, false);
            if (success) {
                collect(car(pair));
                pair = cdr(pair);
            } else {
                RBTree.insert(identical, key, pair);
                break;
            }
        }
    }
}

static RBTree detect(Object datum) {
    Identical inst = new Identical();
    return inst.process(datum);
}
}
