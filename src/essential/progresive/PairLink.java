package essential.progresive;

class PairLink extends Lot {

final Object data;
Lot next;

PairLink(Object data, Lot next) {
    this.data = data;
    this.next = next;
}

@Override
public String toString() {
    if (next instanceof LotEnd) {
        return data.toString();
    } else if (next instanceof PairSelf || next instanceof LotMark) {
        return String.format("%s . %s", Pr.stringOf(data), next);
    } else {
        return String.format("%s %s", Pr.stringOf(data), next);
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof PairLink link) {
        return data.equals(link.data) &&
               next.equals(link.next);
    } else {
        return false;
    }
}
}
