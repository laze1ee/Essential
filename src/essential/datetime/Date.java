package essential.datetime;

import essential.utility.Binary;
import essential.utility.CheckSum;
import org.jetbrains.annotations.NotNull;

import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;


public class Date {

private final int year;
private final byte month;
private final byte day_of_month;
private final byte day_of_week;
private final byte hour;
private final byte minute;
private final byte second;
private final int nanosecond;
private final int offset;

public Date(int year, int month, int day_of_month, int hour, int minute, int second,
            int nanosecond, int offset) {
    if (year < 0 || 2000000000 < year) {
        throw new RuntimeException(String.format(Msg.OUT_YEAR, year));
    }
    if (month < 1 || 12 < month) {
        throw new RuntimeException(String.format(Msg.OUT_MONTH, month));
    }
    if (!Mate.checkDayOfMonth(year, month, day_of_month)) {
        throw new RuntimeException(String.format(Msg.OUT_DAY, year, month, day_of_month));
    }
    if (hour < 0 || 23 < hour) {
        throw new RuntimeException(String.format(Msg.OUT_HOUR, hour));
    }
    if (minute < 0 || 59 < minute) {
        throw new RuntimeException(String.format(Msg.OUT_MINUTE, minute));
    }
    if (second < 0 || 59 < second) {
        throw new RuntimeException(String.format(Msg.OUT_SEC, second));
    }
    if (nanosecond < 0 || 999999999 < nanosecond) {
        throw new RuntimeException(String.format(Msg.OUT_NANO, nanosecond));
    }
    if (offset < -64800 || 64800 < offset) {
        throw new RuntimeException(String.format(Msg.OUT_OFFSET, offset));
    }

    this.year = year;
    this.month = (byte) month;
    this.day_of_month = (byte) day_of_month;
    this.day_of_week = (byte) Mate.dayOfWeek(year, month, day_of_month);
    this.hour = (byte) hour;
    this.minute = (byte) minute;
    this.second = (byte) second;
    this.nanosecond = nanosecond;
    this.offset = offset;
}

@Override
public String toString() {
    return String.format("#<date %s %d-%02d-%02d %s %02d:%02d:%02d>",
                         Mate.stringOfOffset(offset), year, month, day_of_month,
                         Mate.stringOfWeek(day_of_week), hour, minute, second);
}

@Override
public boolean equals(Object datum) {
    if (datum instanceof Date d) {
        return d.year == year &&
               month == d.month &&
               day_of_month == d.day_of_month &&
               hour == d.hour &&
               minute == d.minute &&
               second == d.second &&
               nanosecond == d.nanosecond &&
               offset == d.offset;
    } else {
        return false;
    }
}

@Override
public int hashCode() {
    byte[] bin = Binary.encode(this);
    return CheckSum.fletcher32(bin);
}

public int year() {
    return year;
}

public int month() {
    return month;
}

public int dayOfMonth() {
    return day_of_month;
}

public int dayOfWeek() {
    return day_of_month;
}

public int hour() {
    return hour;
}

public int minute() {
    return minute;
}

public int second() {
    return second;
}

public int nanosecond() {
    return nanosecond;
}

public int offset() {
    return offset;
}

public Time toTime() {
    long days = Mate.sumOfDays(year, month, day_of_month);
    long secs_day = hour * 3600 + minute * 60 + second;
    long seconds = days * 24 * 3600 + secs_day - offset - Mate.COMPLEMENT;
    return new Time(seconds, nanosecond);
}


public static @NotNull Date current(int offset) {
    String id = ZoneOffset.ofTotalSeconds(offset).getId();
    ZonedDateTime zone = ZonedDateTime.now(ZoneId.of(id));
    return new Date(zone.getYear(),
                    zone.getMonthValue(),
                    zone.getDayOfMonth(),
                    zone.getHour(),
                    zone.getMinute(),
                    zone.getSecond(),
                    zone.getNano(),
                    offset);
}

public static @NotNull Date current() {
    return current(OffsetTime.now().getOffset().getTotalSeconds());
}
}

