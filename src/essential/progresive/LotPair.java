package essential.progresive;

class LotPair extends Lot {

Object data;
Lot next;

LotPair(Object data, Lot next) {
    this.data = data;
    this.next = next;
}
}
