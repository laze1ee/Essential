package essential.progresive;


import essential.utilities.Binary;
import essential.utilities.CheckSum;


public class Few extends Fev {

final Object[] data;

Few(Object[] data) {
    this.data = data;
}

@Override
public String toString() {
    Lot identical = Cycle.detect(this);
    if (identical.isEmpty()) {
        return String.format("#(%s)", Mate.consArray(data, data.length));
    } else {
        Object datum = Cycle.label(this, identical);
        return datum.toString();
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Few fw) {
        Lot i1 = Cycle.detect(this);
        Lot i2 = Cycle.detect(fw);
        if (i1.isEmpty() && i2.isEmpty() &&
            data.length == fw.data.length) {
            return Mate.objectArrayEqual(data, fw.data);
        } else if (!i1.isEmpty() && !i2.isEmpty() &&
                   Mate.length(i1) == Mate.length(i2) &&
                   this.data.length == fw.data.length) {
            Object o1 = Cycle.label(this, i1);
            Object o2 = Cycle.label(fw, i2);
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

public int length() {
    return data.length;
}

public Object[] toRaw() {
    return data;
}
}
