package essential.progresive;

class LotPairLink extends Lot {

final Object data;
Lot next;

LotPairLink(Object data, Lot next) {
    this.data = data;
    this.next = next;
}

@Override
public String toString() {
    if (next instanceof LotEnd) {
        return Pr.stringOf(data);
    } else if (next instanceof LotPairSelf || next instanceof LotMark) {
        return String.format("%s . %s", Pr.stringOf(data), next);
    } else {
        return String.format("%s %s", Pr.stringOf(data), next);
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof LotPairLink link) {
        return data.equals(link.data) &&
               next.equals(link.next);
    } else {
        return false;
    }
}
}
