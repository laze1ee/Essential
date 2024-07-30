package essential.progresive;


import essential.utility.Binary;
import essential.utility.CheckSum;


public class Few extends Fer {

final Object[] data;

Few(Object[] data) {
    this.data = data;
}

@Override
public String toString() {
    Lot same = Identical.detect(this);
    if (same.isEmpty()) {
        return String.format("#(%s)", Mate.consArray(data, data.length));
    } else {
        Object datum = Identical.label(this, same);
        return datum.toString();
    }
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Few fw) {
        Lot i1 = Identical.detect(this);
        Lot i2 = Identical.detect(fw);
        if (i1.isEmpty() && i2.isEmpty() &&
            data.length == fw.data.length) {
            return Mate.objectArrayEqual(data, fw.data);
        } else if (!i1.isEmpty() && !i2.isEmpty() &&
                   Mate.length(i1) == Mate.length(i2) &&
                   this.data.length == fw.data.length) {
            Object o1 = Identical.label(this, i1);
            Object o2 = Identical.label(fw, i2);
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
