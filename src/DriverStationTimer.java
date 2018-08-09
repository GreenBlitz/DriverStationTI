
public class DriverStationTimer {
	private final long mStartTime;

    private static DriverStationTimer instance = new DriverStationTimer();

    private DriverStationTimer() {
        mStartTime = System.currentTimeMillis();
    }

    public long getCurrentServerTime() {
        return System.currentTimeMillis() - mStartTime;
    }

    public static final DriverStationTimer getInstance() {
        return instance;
    }
}
