import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;

/**
 * Created by ofeke on 8/3/2018.
 */
public class RobotData {
    /**
     * The alliance and position of a robot in the arena
     *
     * @author karlo
     */
    public enum Alliance {
        RED1(1), RED2(2), BLUE1(1), BLUE2(2), NONE(-1);

        public final int position;

        private Alliance(int position) {
            this.position = position;
        }

        public static boolean isRedAlliance(Alliance a) {
            return a == RED1 || a == RED2;
        }

        public static boolean isBlueAlliance(Alliance a) {
            return a == BLUE1 || a == BLUE2;
        }
    }

    /**
     * Type of the current played game; this may change during match
     *
     * @author karlo
     */
    public enum GameType {
        TELEOP, AUTO, INVALID
    }

    /**
     * All data related to single joystick hid
     *
     * @author karlo
     */
    public static class NativeJoystickData {
        public float[] axes;
        public boolean[] buttons;
        public boolean isValid;

        public NativeJoystickData(int axesCount, int buttonCount, boolean isvalid) {
            axes = new float[axesCount];
            buttons = new boolean[buttonCount];
            isValid = isvalid;
        }
        public NativeJoystickData(float[] axesCount, boolean[] buttonCount, boolean isvalid) {
            axes = axesCount;
            buttons = buttonCount;
            isValid = isvalid;
        }

        public NativeJoystickData() {
            this(6, 18, true);
        }
    }

    /**
     * Data unique to each match
     *
     * @author karlo
     */
    public static class MatchSpecificData {
        public String eventName;
        public String gameSpecificMessage;
        public Alliance alliance;
		public String ip;
    }

    /**
     * Every piece of data available from the station
     *
     * @author karlo
     */
    public static class StationDataCache {
        public NativeJoystickData[] joystickData;
        public GameType gameType;
        public boolean isEnabled;

        public StationDataCache(NativeJoystickData[] joystickCount, GameType game, boolean is) {
            joystickData = joystickCount;
            gameType = game;
            isEnabled = is;
        }
    }


    public static class DriverStationData{
        public int battery;
        public String extra;
    }
    /**
     * Maximum amount of joysticks to connect
     */
    public static final int JOYSTICK_COUNT = 2;

    private static Gson gson = new Gson();

    public static final TypeAdapter<StationDataCache> JSON_CACHE_PARSER =
            gson.getAdapter(StationDataCache.class);
    public static final TypeAdapter<MatchSpecificData> JSON_MATCH_DATA_PARSER =
            gson.getAdapter(MatchSpecificData.class);

    public static final TypeAdapter<DriverStationData> JSON_Station_DATA_PARSER =
            gson.getAdapter(DriverStationData.class);
}
