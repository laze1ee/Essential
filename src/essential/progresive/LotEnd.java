package essential.progresive;

class LotEnd extends Lot {
@Override
public String toString() {
    return "()";
}

@Override
public boolean equals(Object datum) {
    return datum instanceof LotEnd;
}
}
