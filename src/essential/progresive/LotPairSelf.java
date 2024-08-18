package essential.progresive;

class LotPairSelf extends LotPairLink {

final int count;

LotPairSelf(Object data, Lot next, int count) {
    super(data, next);
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s=(%s)", count, new LotPairLink(data, next));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof LotPairSelf self) {
        return count == self.count &&
               data.equals(self.data) &&
               next.equals(self.next);
    } else {
        return false;
    }
}
}
