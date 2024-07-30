package essential.progresive;

class FerMark extends Fer {

final int count;

FerMark(int count) {
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s#", count);
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof FerMark fm) {
        return count == fm.count;
    } else {
        return false;
    }
}
}
