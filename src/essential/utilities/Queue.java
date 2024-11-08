/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import org.jetbrains.annotations.NotNull;
import essential.progresive.Few;
import essential.progresive.Lot;

import static essential.progresive.Pr.*;


public class Queue {

private final Few pipe;

public Queue(@NotNull Object @NotNull ... args) {
    pipe = few(fewToLot(few(args)), lot());
}

/**
 * Is the queue empty?
 *
 * @return if the queue is empty, returns true else false.
 */
public boolean isEmpty() {
    return ((Lot) ref0(pipe)).isEmpty() &&
           ((Lot) ref1(pipe)).isEmpty();
}

@Override
public String toString() {
    return String.format("#[Queue %s %s]", ref0(pipe), ref1(pipe));
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

/**
 * Put an item into the queue.
 *
 * @param queue the queue
 * @param datum the item
 */
public static void enQ(@NotNull Queue queue, @NotNull Object datum) {
    Lot en = (Lot) ref1(queue.pipe);
    en = cons(datum, en);
    set1(queue.pipe, en);
}

/**
 * Remove an item from the queue.
 *
 * @param queue the queue
 * @return the item removed
 * @throws RuntimeException if the queue is empty
 */
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
