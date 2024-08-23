package essential.progresive;

class FewCyc extends Fev {

final Object[] array;
final int count;

FewCyc(Object[] array, int count) {
    this.array = array;
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s=#(%s)", count, Mate.consArray(array, array.length));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof FewCyc cyc) {
        return count == cyc.count &&
               Mate.objectArrayEqual(array, cyc.array);
    } else {
        return false;
    }
}
}
