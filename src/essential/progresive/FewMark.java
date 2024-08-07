package essential.progresive;

class FewMark extends Fer {

final int count;

FewMark(int count) {
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s#", count);
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof FewMark fm) {
        return count == fm.count;
    } else {
        return false;
    }
}
}
