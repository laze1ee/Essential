/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import org.jetbrains.annotations.NotNull;
import essential.progressive.Lot;

import static essential.progressive.Pr.*;


public class Queue {

private Lot stack1;
private Lot stack2;

public Queue(@NotNull Object @NotNull ... args) {
  stack1 = few(args).toLot();
  stack2 = lot();
}

/**
 * Is the queue empty?
 *
 * @return if the queue is empty, returns true else false.
 */
public boolean isEmpty() {
  return stack1.isEmpty() &&
         stack2.isEmpty();
}

@Override
public boolean equals(Object datum) {
  if (datum instanceof Queue que) {
    return stack1.equals(que.stack1) &&
           stack2.equals(que.stack2);
  }
  else {
    return false;
  }
}

@Override
public String toString() {
  return String.format("«Queue %s»", append(stack1, stack2.reverse()));
}

/**
 * Add an item to the queue.
 *
 * @param datum the item to add
 */
public void enqueue(Object datum) {
  stack2 = cons(datum, stack2);
}

/**
 * Get an item from the queue. The item is removed from the queue.
 *
 * @return the item from the queue.
 * @throws RuntimeException if the queue is empty.
 */
public Object dequeue() {
  if (isEmpty()) {
    throw new RuntimeException(Msg.EMPTY_QUEUE);
  }
  else if (stack1.isEmpty()) {
    Lot ixx = stack2.reverse();
    stack1 = ixx.cdr();
    stack2 = lot();
    return ixx.car();
  }
  else {
    Object datum = stack1.car();
    stack1 = stack1.cdr();
    return datum;
  }
}
}
