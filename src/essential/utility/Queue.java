package essential.utility;

import org.jetbrains.annotations.NotNull;
import essential.progresive.Few;
import essential.progresive.Lot;

import static essential.progresive.Pr.*;


public class Queue {

// #(dequeue-lot enqueue-lot)
private final Few pipe;

public Queue(@NotNull Object @NotNull ... args) {
    Lot item = fewToLot(few(args));
    pipe = few(item, lot());
}

@Override
public String toString() {
    return String.format("(queue %s %s)", ref0(pipe), ref1(pipe));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Queue queue) {
        return ref0(pipe).equals(ref0(queue.pipe)) &&
               ref1(pipe).equals(ref1(queue.pipe));
    } else {
        return false;
    }
}


public boolean isEmpty() {
    return ((Lot) ref0(pipe)).isEmpty() &&
           ((Lot) ref1(pipe)).isEmpty();
}

public static void enQ(@NotNull Queue queue, @NotNull Object datum) {
    Lot en = (Lot) ref1(queue.pipe);
    en = cons(datum, en);
    set1(queue.pipe, en);
}

public static @NotNull Object deQ(@NotNull Queue queue) {
    if (queue.isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_QUEUE);
    } else if (((Lot) ref0(queue.pipe)).isEmpty()) {
        Lot en = (Lot) ref1(queue.pipe);
        en = reverse(en);
        set0(queue.pipe, cdr(en));
        set1(queue.pipe, lot());
        return car(en);
    } else {
        Lot de = (Lot) ref0(queue.pipe);
        set0(queue.pipe, cdr(de));
        return car(de);
    }
}
}
