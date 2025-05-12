/*
 * Copyright (c) 2022-2025. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.datetime;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


public record Time(long second, int nanosecond) {

public Time(long second, int nanosecond) {
    if (nanosecond <= Mate.NEG_NANO_OF_SECOND ||
        Mate.POS_NANO_OF_SECOND <= nanosecond) {
        String msg = String.format(Msg.OUT_RANGE_NANO, nanosecond, Mate.NEG_NANO_OF_SECOND + 1,
                                   Mate.POS_NANO_OF_SECOND - 1);
        throw new RuntimeException(msg);
    }
    if (second > 0 && nanosecond < 0) {
        this.second = second - 1;
        this.nanosecond = Mate.POS_NANO_OF_SECOND + nanosecond;
    } else if (second < 0 && nanosecond > 0) {
        this.second = second + 1;
        this.nanosecond = nanosecond - Mate.POS_NANO_OF_SECOND;
    } else {
        this.second = second;
        this.nanosecond = nanosecond;
    }
}

@Contract(" -> new")
public @NotNull Time neg() {return new Time(-second, -nanosecond);}

public boolean less(@NotNull Time t) {
    if (this.second() < t.second()) {
        return true;
    } else if (this.second() == t.second()) {
        return this.nanosecond() < t.nanosecond();
    } else {
        return false;
    }
}

@Override
public @NotNull String toString() {return String.format("#[time %d.%09d]", second, Math.abs(nanosecond));}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Time t) {
        return second == t.second &&
               nanosecond == t.nanosecond;
    } else {
        return false;
    }
}

public @NotNull Date toDate(int offset) {
    if (Mate.checkTime(this)) {
        return Mate.timeToDate(this, offset);
    } else {
        String msg = String.format(Msg.OUT_TIME, this, Mate.UTC_MIN, Mate.UTC_MAX);
        throw new RuntimeException(msg);
    }
}


@Contract("_ -> new")
public static @NotNull Time current(@NotNull TimeType type) {
    switch (type) {
    case UTC -> {
        ZonedDateTime utc = ZonedDateTime.now(ZoneOffset.UTC);
        long second = utc.toEpochSecond();
        int nanosecond = utc.getNano();
        return new Time(second, nanosecond);
    }
    case Monotonic -> {
        long stamp = System.nanoTime();
        long second = stamp / Mate.POS_NANO_OF_SECOND;
        int nanosecond = (int) (stamp % Mate.POS_NANO_OF_SECOND);
        return new Time(second, nanosecond);
    }
    default -> {
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        long stamp = bean.getCurrentThreadCpuTime();
        long second = stamp / Mate.POS_NANO_OF_SECOND;
        int nanosecond = (int) (stamp % Mate.POS_NANO_OF_SECOND);
        return new Time(second, nanosecond);
    }
    }
}

@Contract(" -> new")
public static @NotNull Time current() {return current(TimeType.UTC);}

@Contract("_, _ -> new")
public static @NotNull Time add(@NotNull Time t1, @NotNull Time t2) {
    long second = t1.second + t2.second;
    int nanosecond = t1.nanosecond + t2.nanosecond;
    if (nanosecond >= Mate.POS_NANO_OF_SECOND) {
        return new Time(second + 1, nanosecond - Mate.POS_NANO_OF_SECOND);
    } else if (nanosecond <= Mate.NEG_NANO_OF_SECOND) {
        return new Time(second - 1, nanosecond + Mate.POS_NANO_OF_SECOND);
    } else {
        return new Time(second, nanosecond);
    }
}
}
