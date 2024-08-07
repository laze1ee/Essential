package essential.progresive;

class FewSelf extends Fer {

final Object[] array;
final int count;

FewSelf(Object[] array, int count) {
    this.array = array;
    this.count = count;
}

@Override
public String toString() {
    return String.format("#%s=#(%s)", count, Mate.consArray(array, array.length));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof FewSelf fc) {
        return count == fc.count &&
               Mate.objectArrayEqual(array, fc.array);
    } else {
        return false;
    }
}
}
