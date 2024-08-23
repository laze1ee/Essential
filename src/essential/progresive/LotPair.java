package essential.progresive;

class LotPair extends Lot {

Object data;
Lot next;

LotPair(Object data, Lot next) {
    this.data = data;
    this.next = next;
}

@Override
public String toString() {
    Lot identical = Cycle.detect(this);
    if (identical.isEmpty()) {
        Lot link = Mate.toPairLink(this);
        return link.toString();
    } else {
        Object link = Cycle.label(this, identical);
        return link.toString();
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof LotPair lotPair) {
        Lot identical1 = Cycle.detect(this);
        Lot identical2 = Cycle.detect(lotPair);
        if (identical1.isEmpty() && identical2.isEmpty()) {
            Lot link1 = Mate.toPairLink(this);
            Lot link2 = Mate.toPairLink(lotPair);
            return link1.equals(link2);
        } else {
            Object link1 = Cycle.label(this, identical1);
            Object link2 = Cycle.label(lotPair, identical2);
            return link1.equals(link2);
        }
    } else {
        return false;
    }
}
}
