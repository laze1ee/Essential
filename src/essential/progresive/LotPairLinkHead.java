package essential.progresive;

class LotPairLinkHead extends LotPairLink {

LotPairLinkHead(Object data, Lot next) {
    super(data, next);
}

@Override
public String toString() {
    return String.format("(%s)", new LotPairLink(data, next));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof LotPairLinkHead head) {
        return Pr.equal(data, head.data) &&
               next.equals(head.next);
    } else {
        return false;
    }
}
}
