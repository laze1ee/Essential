/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import essential.datetime.Time;
import essential.progressive.Few;
import essential.progressive.Lot;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static essential.progressive.Pr.*;

@SuppressWarnings("SpellCheckingInspection")
public class RandGenerator {

private static final String ASCII = "0123456789" +
                                    "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ" +
                                    "!#$%&()*+,-.;=@[]^_{}~";
private static final String DIGITS = "0123456789";
private static final String HEX_DIGITS = "0123456789ABCDEF";

private static final Random random = new Random(Time.current().nanosecond());


public static @NotNull String ascii(int length) {
  StringBuilder builder = new StringBuilder();
  int bound = ASCII.length();
  for (int i = 0; i < length; i++) {
    int index = random.nextInt(bound);
    builder.append(ASCII.charAt(index));
  }
  return builder.toString();
}

public static @NotNull String digits(int length) {
  StringBuilder builder = new StringBuilder();
  int bound = DIGITS.length();
  for (int i = 0; i < length; i++) {
    int index = random.nextInt(bound);
    builder.append(DIGITS.charAt(index));
  }
  return builder.toString();
}

public static @NotNull String hexDigits(int length) {
  StringBuilder builder = new StringBuilder();
  int bound = HEX_DIGITS.length();
  for (int i = 0; i < length; i++) {
    int index = random.nextInt(bound);
    builder.append(HEX_DIGITS.charAt(index));
  }
  return builder.toString();
}

public static @NotNull String string(String text, int length) {
  StringBuilder builder = new StringBuilder();
  for (int i = 0; i < length; i++) {
    builder.append(text.charAt(random.nextInt(text.length())));
  }
  return builder.toString();
}

public static @NotNull String shuffle(@NotNull String text) {
  int length = text.length();
  if (length == 0) {
    return "";
  }

  boolean[] used = new boolean[length];
  StringBuilder builder = new StringBuilder();
  for (int i = 0; i < length; i += 1) {
    int index = random.nextInt(length);
    if (!used[index]) {
      builder.append(text.charAt(index));
      used[index] = true;
    }
  }
  for (int i = 0; i < length; i += 2) {
    if (!used[i]) {
      builder.append(text.charAt(i));
    }
  }
  for (int i = 1; i < length; i += 2) {
    if (!used[i]) {
      builder.append(text.charAt(i));
    }
  }
  return builder.toString();
}

public static @NotNull Few shuffle(@NotNull Few fw) {
  int length = fw.length();
  if (length == 0) {
    return few();
  }

  boolean[] used = new boolean[length];
  Lot col = lot();
  for (int i = 0; i < length; i += 1) {
    int index = random.nextInt(length);
    if (!used[index]) {
      col = cons(fw.ref(index), col);
      used[index] = true;
    }
  }
  for (int i = 0; i < length; i += 2) {
    if (!used[i]) {
      col = cons(fw.ref(i), col);
    }
  }
  for (int i = 1; i < length; i += 2) {
    if (!used[i]) {
      col = cons(fw.ref(i), col);
    }
  }
  return col.toFew();
}

public static Lot shuffle(@NotNull Lot lt) {
  if (lt.isEmpty()) {
    return lot();
  }

  Few fw = shuffle(lt.toFew());
  return fw.toLot();
}
}
