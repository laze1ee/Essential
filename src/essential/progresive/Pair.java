package essential.progresive;

class Pair extends Lot {

Object data;
Lot next;

Pair(Object data, Lot next) {
    this.data = data;
    this.next = next;
}

@Override
public String toString() {
    Lot identical = Self.detect(this);
    if (identical.isEmpty()) {
        Lot link = Mate.toPairLink(this);
        return link.toString();
    } else {
        Object link = Self.label(this, identical);
        return link.toString();
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Pair pair) {
        Lot identical1 = Self.detect(this);
        Lot identical2 = Self.detect(pair);
        if (identical1.isEmpty() && identical2.isEmpty()) {
            Lot link1 = Mate.toPairLink(this);
            Lot link2 = Mate.toPairLink(pair);
            return link1.equals(link2);
        } else {
            Object link1 = Self.label(this, identical1);
            Object link2 = Self.label(pair, identical2);
            return link1.equals(link2);
        }
    } else {
        return false;
    }
}
}
