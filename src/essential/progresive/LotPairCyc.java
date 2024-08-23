package essential.progresive;

class LotPairCyc extends Lot {

final Object data;
Lot next;
final int count;

LotPairCyc(Object data, Lot next, int count) {
    this.data = data;
    this.next = next;
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s=(%s)", count, new LotPairLink(data, next));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof LotPairCyc cyc) {
        return count == cyc.count &&
               Pr.equal(data, cyc.data) &&
               next.equals(cyc.next);
    } else {
        return false;
    }
}
}
