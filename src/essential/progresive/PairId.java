package essential.progresive;

import static essential.progresive.Pr.equal;
import static essential.progresive.Pr.stringOf;


class PairId extends Pair {

final int count;

PairId(Object data, Lot next, int count) {
    super(data, next);
    this.count = count;
}

@Override
public String toString() {
    if (next instanceof PairEnd) {
        return String.format("#%s=(%s)", count, stringOf(data));
    } else if (next instanceof PairId ||
               next instanceof PairMark) {
        return String.format("#%s=(%s . %s)", count, stringOf(data), next);
    } else {
        return String.format("#%s=(%s %s)", count, stringOf(data), next);
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof PairId ooo) {
        return count == ooo.count &&
               equal(data, ooo.data) &&
               next.equals(ooo.next);
    } else {
        return false;
    }
}
}
