/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package test;

import essential.datetime.Date;
import essential.datetime.Time;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static essential.progresive.Pr.equal;
import static org.junit.jupiter.api.Assertions.assertTrue;


class DateTimeTest {

final Time t1;
final Time t2;

DateTimeTest() {
    t1 = new Time(100, 700_000_000);
    t2 = new Time(-100, -900_000_000);
}

@Test
void testTimeDateConvert() {
    Random rd = new Random();
    rd.setSeed(Time.current().nanosecond());
    for (int i = 0; i < 100; i = i + 1) {
        Date d1 = new Date(rd.nextInt(0, 1000000),
                           rd.nextInt(1, 13),
                           rd.nextInt(1, 29),
                           rd.nextInt(0, 24),
                           rd.nextInt(0, 60),
                           rd.nextInt(0, 60),
                           0,
                           0);
        Time t = d1.toTime();
        Date d2 = t.toDate(0);
        assertTrue(equal(d1, d2));
    }
}

@Test
void add() {
    Time t3 = Time.add(t1, t2);
    Time t4 = Time.add(t1.neg(), t2.neg());
    assertTrue(equal(t3, t4.neg()));
}
}