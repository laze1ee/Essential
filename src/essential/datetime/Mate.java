package essential.datetime;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;


class Mate {

static final int POS_NANO = 1_000_000_000;
static final int NEG_NANO = -1_000_000_000;

static final int DAYS_400Y = 146097;
static final int DAYS_100Y = 36524;
static final int DAYS_4Y = 1461;
static final int DAYS_Y = 365;

static final long SEC_DAY = 86400;
static final long COMPLEMENT = 62167219200L;

static final int[][] DAY_YEAR =
        new int[][]{{0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31},
                    {0, 31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31}};

static final int[][] SUM_DAYS =
        new int[][]{{0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365},
                    {0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366}};

private static final int START_DAY_OF_WEEK = 5;


private static boolean isLeapYear(int year) {
    if (year % 4 == 0) {
        return year % 100 != 0 || year % 400 == 0;
    } else {
        return false;
    }
}


static boolean checkDayOfMonth(int year, int month, int day_of_month) {
    int day;
    if (isLeapYear(year)) {
        day = DAY_YEAR[1][month];
    } else {
        day = DAY_YEAR[0][month];
    }
    return day_of_month <= day;
}


static int dayOfWeek(int year, int month, int day_of_month) {
    long days = sumOfDays(year, month, day_of_month);
    return traceDayOfWeek(days);
}


@Contract(pure = true)
static @NotNull String stringOfOffset(int offset) {
    int n = offset;
    String sign;
    if (n == 0) {
        sign = "";
    } else if (n < 0) {
        sign = "-";
        n = n * -1;
    } else {
        sign = "+";
    }
    int hour = n / 3600;
    int minute = (n % 3600) % 60;
    return String.format("%s%02d:%02d", sign, hour, minute);
}

@Contract(pure = true)
static @NotNull String stringOfWeek(int day) {
    if (day == 1) {
        return "Monday";
    } else if (day == 2) {
        return "Tuesday";
    } else if (day == 3) {
        return "Wednesday";
    } else if (day == 4) {
        return "Thursday";
    } else if (day == 5) {
        return "Friday";
    } else if (day == 6) {
        return "Saturday";
    } else {
        return "Sunday";
    }
}


//region Time to Date
static final long UTC_MIN = -62167219200L;
static final long UTC_MAX = 63113841832780800L;

static boolean checkTime(@NotNull Time t) {
    return COMPLEMENT * -1 <= t.second() && t.second() <= UTC_MAX;
}

@Contract("_, _ -> new")
static @NotNull Date timeToDate(@NotNull Time t, int offset) {
    long seconds = t.second() + offset + COMPLEMENT;
    long days = seconds / SEC_DAY;
    long seconds_day = seconds % SEC_DAY;

    int[] d1 = trackDate(days);
    int[] d2 = trackTime((int) seconds_day);

    return new Date(d1[0], d1[1], d1[2],
                    d2[0], d2[1], d2[2], t.nanosecond(), offset);
}

private static int @NotNull [] trackDate(long days) {
    int[] degree = new int[4];

    int mod_400y = daysOfMod400Y(degree, days);
    int mod_100y = daysOfMod100Y(degree, mod_400y);
    int mod_4y = daysOfMod4Y(degree, mod_100y);
    int mod_1y = daysOfMod1Y(degree, mod_4y);

    int year = trackYear(degree);
    int[] moo = trackMonthDay(mod_1y + 1, isLeapYear(year));
    int month = moo[0];
    int day_of_month = moo[1];
    return new int[]{year, month, day_of_month};
}

private static int daysOfMod400Y(int @NotNull [] degree, long days) {
    degree[0] = (int) (days / DAYS_400Y);
    return (int) (days % DAYS_400Y);
}

private static int daysOfMod100Y(int[] degree, int days) {
    if (days <= DAYS_100Y + 1) {
        degree[1] = 0;
        return days;
    } else {
        int moo = days - 1;
        degree[1] = moo / DAYS_100Y;
        return moo % DAYS_100Y;
    }
}

private static int daysOfMod4Y(int @NotNull [] degree, int days) {
    if (degree[1] == 0) {
        degree[2] = days / DAYS_4Y;
        return days % DAYS_4Y;
    } else if (days <= DAYS_4Y - 1) {
        degree[2] = 0;
        return days;
    } else {
        int moo = days + 1;
        degree[2] = moo / DAYS_4Y;
        return moo % DAYS_4Y;
    }
}

private static int daysOfMod1Y(int @NotNull [] degree, int days) {
    if (degree[1] > 0 && degree[2] == 0) {
        degree[3] = days / DAYS_Y;
        return days % DAYS_Y;
    } else if (days == DAYS_Y + 1) {
        degree[3] = 1;
        return 0;
    } else if (days < DAYS_Y + 1) {
        degree[3] = 0;
        return days;
    } else {
        int moo = days - 1;
        degree[3] = moo / DAYS_Y;
        return moo % DAYS_Y;
    }
}

@Contract(pure = true)
private static int trackYear(int @NotNull [] degree) {
    return degree[0] * 400 +
           degree[1] * 100 +
           degree[2] * 4 +
           degree[3];
}

@Contract(value = "_, _ -> new", pure = true)
private static int @NotNull [] trackMonthDay(int days, boolean leap_year) {
    int i;
    if (leap_year) {
        i = 1;
    } else {
        i = 0;
    }
    for (int j = 1; j < 13; j = j + 1) {
        if (days <= SUM_DAYS[i][j]) {
            int day_of_month = days - SUM_DAYS[i][j - 1];
            return new int[]{j, day_of_month};
        }
    }
    throw new RuntimeException(String.format("days %d is invalid", days));
}

private static int traceDayOfWeek(long days) {
    long week = (days % 7 + START_DAY_OF_WEEK) % 7;
    return (int) (week + 1);
}

@Contract(value = "_ -> new", pure = true)
private static int @NotNull [] trackTime(int seconds) {
    int hour = seconds / 3600;
    int minute = seconds % 3600 / 60;
    int second = seconds % 3600 % 60;
    return new int[]{hour, minute, second};
}
//endregion


//region Date to Time
static long sumOfDays(int year, int month, int day_of_month) {
    int[] degree = new int[4];
    int y = year;
    degree[0] = y / 400;
    y = y % 400;
    degree[1] = y / 100;
    y = y % 100;
    degree[2] = y / 4;
    y = y % 4;
    degree[3] = y % 4;

    long sum = daysInYears(degree, year);
    sum = sum + daysInThisYear(year, month, day_of_month);
    return sum;
}

@Contract(pure = true)
private static long daysInYears(int @NotNull [] degree, int year) {
    long sum = (long) degree[0] * DAYS_400Y;

    if (degree[1] > 0) {
        sum = sum + (long) degree[1] * DAYS_100Y + 1;
    }

    if (degree[2] > 0) {
        if (degree[1] == 0) {
            sum = sum + (long) degree[2] * DAYS_4Y;
        } else {
            sum = sum + (long) degree[2] * DAYS_4Y - 1;
        }
    }

    if (degree[3] > 0) {
        if (isLeapYear(year - degree[3])) {
            sum = sum + (long) degree[3] * DAYS_Y + 1;
        } else {
            sum = sum + (long) degree[3] * DAYS_Y;
        }
    }
    return sum;
}

private static int daysInThisYear(int year, int month, int day_of_month) {
    int i;
    if (isLeapYear(year)) {
        i = 1;
    } else {
        i = 0;
    }
    return SUM_DAYS[i][month - 1] + day_of_month - 1;
}
//endregion
}
