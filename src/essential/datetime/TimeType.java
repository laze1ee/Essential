/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.datetime;

public enum TimeType {
  /**
   * The time elapsed since the "epoch:" 1970-01-01 00:00:00 UTC.
   */
  UTC,

  /**
   * The time elapsed since the current Java Virtual Machine started.
   */
  Monotonic,

  /**
   * The amount of CPU time used by the current thread. Note that this time does not
   * include the time elapsed by calling
   * {@link java.lang.Thread#sleep(long)} or {@link java.lang.Thread#sleep(long, int)}
   */
  Thread
}
