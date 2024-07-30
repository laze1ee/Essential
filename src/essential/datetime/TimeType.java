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
