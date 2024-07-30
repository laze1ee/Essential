package essential.progresive;

import static essential.progresive.Pr.equal;
import static essential.progresive.Pr.stringOf;


class PairLink extends Pair {

PairLink(Object data, Lot next) {
    super(data, next);
}

@Override
public String toString() {
    if (next instanceof PairEnd) {
        return String.format("%s", stringOf(data));
    } else if (next instanceof PairId ||
               next instanceof PairMark) {
        return String.format("%s . %s", stringOf(data), next);
    } else {
        return String.format("%s %s", stringOf(data), next);
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof PairLink ooo) {
        return equal(data, ooo.data) &&
               next.equals(ooo.next);
    } else {
        return false;
    }
}
}
