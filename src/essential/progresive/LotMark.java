package essential.progresive;

class LotMark extends Lot {

final int count;

LotMark(int count) {
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s#", count);
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof LotMark mark) {
        return count == mark.count;
    } else {
        return false;
    }
}
}
