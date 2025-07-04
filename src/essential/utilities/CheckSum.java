/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.utilities;

import org.jetbrains.annotations.NotNull;


@SuppressWarnings("DuplicatedCode")
public class CheckSum {

// 0 < n and n * (n+1) / 2 * (2^8-1) < (2^31-1)
// a group size of 4103
private static final int GROUP_SIZE   = 4103;
private static final int ADLER_MOD    = 65521;
private static final int FLETCHER_MOD = 65535;

public static int adler32(byte @NotNull [] bin, int start, int bound) {
  if (bound > bin.length ||
      start > bound) {
    String msg = String.format(Msg.INVALID_RANGE, start, bound);
    throw new RuntimeException(msg);
  }

  int sum0 = 1;
  int sum1 = 0;
  for (int i = start; i < bound; ) {
    int block = bound;
    if (block > i + GROUP_SIZE) {
      block = i + GROUP_SIZE;
    }
    for (; i < block; i += 1) {
      sum0 += bin[i] & 0xFF;
      sum1 += sum0;
    }
    sum0 %= ADLER_MOD;
    sum1 %= ADLER_MOD;
  }
  return sum1 << 16 | sum0;
}

public static int adler32(byte @NotNull [] bin) {
  return adler32(bin, 0, bin.length);
}

public static int fletcher32(byte @NotNull [] bin, int start, int bound) {
  if (bound > bin.length ||
      start > bound) {
    String msg = String.format(Msg.INVALID_RANGE, start, bound);
    throw new RuntimeException(msg);
  }

  int sum0 = 0;
  int sum1 = 0;
  for (int i = start; i < bound; ) {
    int block = bound;
    if (block > i + GROUP_SIZE) {
      block = i + GROUP_SIZE;
    }
    for (; i < block; i += 1) {
      sum0 += bin[i] & 0xFF;
      sum1 += sum0;
    }
    sum0 %= FLETCHER_MOD;
    sum1 %= FLETCHER_MOD;
  }
  return sum1 << 16 | sum0;
}

public static int fletcher32(byte @NotNull [] bin) {
  return fletcher32(bin, 0, bin.length);
}
}
