package essential.progresive;

class PairLinkHead extends PairLink {

PairLinkHead(Object data, Lot next) {
    super(data, next);
}

@Override
public String toString() {
    return String.format("(%s)", new PairLink(data, next));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof PairLinkHead head) {
        return data.equals(head.data) &&
               next.equals(head.next);
    } else {
        return false;
    }
}
}
