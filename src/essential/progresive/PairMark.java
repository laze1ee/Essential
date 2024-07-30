package essential.progresive;

class PairMark extends Lot {

final int count;

PairMark(int count) {
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s#", count);
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof PairMark ooo) {
        return count == ooo.count;
    } else {
        return false;
    }
}
}
