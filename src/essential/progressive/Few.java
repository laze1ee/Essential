/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.progressive;

import essential.functional.Do1;
import essential.functional.Predicate1;
import essential.functional.Predicate2;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;


public class Few {

protected final Object[] data;

protected Few(Object[] data) {
  this.data = data;
}

Object[] data() {
  return data;
}

@Override
public boolean equals(Object datum) {
  if (datum instanceof Few fw) {
    return Equality.process(this, fw);
  }
  else {
    return false;
  }
}

@Override
public String toString() {
  return ToString.process(this);
}

public int length() {
  return data.length;
}

public Object[] toArray() {
  return data;
}

public @NotNull Object ref(int index) {
  if (0 <= index && index < data.length) {
    return data[index];
  }
  else {
    String msg = String.format(Msg.INDEX_OUT, index, this);
    throw new RuntimeException(msg);
  }
}

public void set(int index, Object datum) {
  if (0 <= index && index < this.data.length) {
    this.data[index] = datum;
  }
  else {
    String msg = String.format(Msg.INDEX_OUT, index, this);
    throw new RuntimeException(msg);
  }
}

public void fill(@NotNull Object datum) {
  Arrays.fill(this.data, datum);
}

public @NotNull Few copy() {
  return new Few(data.clone());
}

public Lot toLot() {
  int length = this.data.length;
  Lot lt     = new Lot();
  for (int i = length - 1; 0 <= i; i -= 1) {
    lt = new Lot(this.data[i], lt);
  }
  return lt;
}

public @NotNull Few map(Do1 fn) {
  int      length = this.data.length;
  Object[] arr    = new Object[length];
  for (int i = 0; i < length; i += 1) {
    arr[i] = fn.apply(this.data[i]);
  }
  return new Few(arr);
}

/**
 * Find the first index satisfying the given predicate.
 *
 * @param fn    A predicate taking one argument.
 * @return The index if found, -1 otherwise.
 */
public int find(Predicate1 fn) {
  int length = this.data.length;
  for (int i = 0; i < length; i += 1) {
    if (fn.apply(this.data[i])) {
      return i;
    }
  }
  return -1;
}

/**
 * Sorts this Few in place using the specified comparison predicate.
 *
 * @param compare A predicate to determine the ordering between two elements.
 */
public void sort(Predicate2 compare) {
  Mate.quickSort(compare, this);
}

/**
 * Returns a new Few containing the elements sorted according to the specified comparison predicate.
 *
 * @param compare A predicate to determine the ordering between two elements.
 * @return A new Few with elements sorted based on the given predicate.
 */
public Few sorted(Predicate2 compare) {
  Few fw = this.copy();
  Mate.quickSort(compare, fw);
  return fw;
}

/**
 * Constructs a Few from the given arguments.
 *
 * @param args the elements to be included in the Few.
 *             If no arguments are provided, an empty Few is returned.
 * @return a Few containing the provided elements.
 */
public static @NotNull Few of(@NotNull Object @NotNull ... args) {
  return new Few(args);
}

/**
 * Constructs a Few with the specified length, initializing all elements with the given initial value.
 *
 * @param length the number of elements in the Few. Must be non-negative.
 * @param initial the value to initialize each element in the Few.
 * @return a Few with the specified length, filled with the initial value.
 * @throws RuntimeException if the specified length is negative.
 */
public static @NotNull Few make(int length, @NotNull Object initial) {
  if (0 <= length) {
    Object[] arr = new Object[length];
    Arrays.fill(arr, initial);
    return new Few(arr);
  }
  else {
    String msg = String.format(Msg.LEN_NON_NATURAL, length);
    throw new RuntimeException(msg);
  }
}
}
