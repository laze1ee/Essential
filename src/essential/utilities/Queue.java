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
    pipe = few(few(args).toLot(), lot());
}

/**
 * Is the queue empty?
 *
 * @return if the queue is empty, returns true else false.
 */
public boolean isEmpty() {
    return ((Lot) pipe.ref(0)).isEmpty() &&
           ((Lot) pipe.ref(1)).isEmpty();
}

@Override
public String toString() {
    return String.format("«Queue %s %s»", pipe.ref(0), pipe.ref(1));
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Queue que) {
        return pipe.ref(0).equals(que.pipe.ref(0)) &&
               pipe.ref(1).equals(que.pipe.ref(1));
    } else {
        return false;
    }
}

/**
 * Add an item to the queue.
 *
 * @param datum the item to add
 */
public void enQueue(Object datum) {
    Lot en = (Lot) pipe.ref(1);
    en = cons(datum, en);
    pipe.set(1, en);
}

/**
 * Get an item from the queue.
 *
 * @return the item from the queue.
 * @throws RuntimeException if the queue is empty.
 */
public Object deQueue() {
    if (isEmpty()) {
        throw new RuntimeException(Msg.EMPTY_QUEUE);
    } else if (((Lot) pipe.ref(0)).isEmpty()) {
        Lot en = (Lot) pipe.ref(1);
        en = en.reverse();
        pipe.set(0, en.cdr());
        pipe.set(1, lot());
        return en.car();
    } else {
        Lot de = (Lot) pipe.ref(0);
        pipe.set(0, de.cdr());
        return de.car();
    }
}
}
