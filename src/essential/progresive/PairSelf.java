package essential.progresive;

class PairSelf extends PairLink {

final int count;

PairSelf(Object data, Lot next, int count) {
    super(data, next);
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s=(%s)", count, new PairLink(data, next));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof PairSelf self) {
        return count == self.count &&
               data.equals(self.data) &&
               next.equals(self.next);
    } else {
        return false;
    }
}
}
