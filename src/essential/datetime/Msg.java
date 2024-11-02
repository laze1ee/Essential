/*
 * Copyright (c) 2022-2024. Laze Lee
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, You can obtain one at
 * https://mozilla.org/MPL/2.0/
 */

package essential.datetime;

class Msg {

// Time
static final String OUT_RANGE_NANO = "nanosecond %s is out of range [%s %s]";
static final String OUT_TIME = "the time %s converting to date is not in range [%d %d]";

// Date
static final String OUT_YEAR = "year %d is out of range [0 2000000000]";
static final String OUT_MONTH = "month %d is out of range [1 12]";
static final String OUT_DAY = "day of month %d-%02d-%02d is invalid";
static final String OUT_HOUR = "hour %d is out of range [0 23]";
static final String OUT_MINUTE = "minute %d is out of range [0 59]";
static final String OUT_SEC = "second %d is out of range [0 59]";
static final String OUT_NANO = "nanosecond %s is out of range [0 999999999]";
static final String OUT_OFFSET = "zone offset %d is out of range [-64800 64800]";
}
