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
    int n = Mate.theHareAndTortoise(this);
    if (n == -1) {
        throw new RuntimeException(String.format(Msg.BREADTH_CIRCLE, this));
    } else {
        return n;
    }
}

public boolean isBreadthCircle() {
    int n = Mate.theHareAndTortoise(this);
    return n == -1;
}
}
