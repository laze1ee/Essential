package essential.progresive;

import essential.utilities.Binary;
import essential.utilities.CheckSum;


public class Lot {

Lot() {
}

@Override
public int hashCode() {
    byte[] bin = Binary.encode(this);
    return CheckSum.fletcher32(bin);
}

public boolean isEmpty() {
    return this instanceof LotEnd;
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
