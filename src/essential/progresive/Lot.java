package essential.progresive;

import essential.utility.Binary;
import essential.utility.CheckSum;


public class Lot {

Lot() {
}

@Override
public String toString() {
    if (this.isEmpty()) {
        return "()";
    } else {
        Lot same = Identical.detect(this);
        if (same.isEmpty()) {
            PairLink link = Mate.toPairLink(this);
            return String.format("(%s)", link);
        } else {
            Object datum = Identical.label(this, same);
            if (datum instanceof PairLink) {
                return String.format("(%s)", datum);
            } else {
                return datum.toString();
            }
        }
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Lot lt) {
        Lot i1 = Identical.detect(this);
        Lot i2 = Identical.detect(lt);
        if (i1.isEmpty() && i2.isEmpty() &&
            this.length() == lt.length()) {
            Pair p1 = Mate.toPairLink(this);
            Pair p2 = Mate.toPairLink(lt);
            return p1.equals(p2);
        } else if (!i1.isEmpty() && !i2.isEmpty() &&
                   Mate.length(i1) == Mate.length(i2)) {
            Object o1 = Identical.label(this, i1);
            Object o2 = Identical.label(lt, i2);
            return o1.equals(o2);
        } else {
            return false;
        }
    } else {
        return false;
    }
}

@Override
public int hashCode() {
    byte[] bin = Binary.encode(this);
    return CheckSum.fletcher32(bin);
}

public boolean isEmpty() {
    return this instanceof PairEnd;
}

public int length() {
    if (this.isBreadthCircle()) {
        throw new RuntimeException(String.format(Msg.BREADTH_CIRCLE, this));
    } else {
        return Mate.length(this);
    }
}

public boolean isBreadthCircle() {
    Lot col = Pr.lot(this);
    Lot lt = this;
    while (!lt.isEmpty()) {
        if (Mate.isBelong(Pr.cdr(lt), col)) {
            return true;
        }
        col = Pr.cons(Pr.cdr(lt), col);
        lt = Pr.cdr(lt);
    }
    return false;
}
}
