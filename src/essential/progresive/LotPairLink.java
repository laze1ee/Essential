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
    StringBuilder builder = new StringBuilder();
    builder.append(Pr.stringOf(data));

    Lot lt = next;
    while (true) {
        if (lt instanceof LotEnd) {
            break;
        } else if (lt instanceof LotPairLink link) {
            builder.append(' ');
            builder.append(Pr.stringOf(link.data));
            lt = link.next;
        } else if (lt instanceof LotPairCyc || lt instanceof LotMark) {
            builder.append(" . ");
            builder.append(lt);
            break;
        }
    }
    return builder.toString();
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof LotPairLink link) {
        if (Pr.equal(data, link.data)) {
            Lot lt1 = next;
            Lot lt2 = link.next;
            while (true) {
                if (lt1 instanceof LotPairLink link1 &&
                    lt2 instanceof LotPairLink link2) {
                    if (Pr.equal(link1.data, link2.data)) {
                        lt1 = link1.next;
                        lt2 = link2.next;
                    } else {
                        return false;
                    }
                } else {
                    return lt1.equals(lt2);
                }
            }
        } else {
            return false;
        }
    } else {
        return false;
    }
}
}
