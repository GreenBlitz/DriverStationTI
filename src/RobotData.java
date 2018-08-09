import java.util.Arrays;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created by ofeke on 8/3/2018.
 */
public class RobotData {
	public enum GameType {
		AUTO, TELEOP, INVALID
	}

	public enum Alliance {
		RED1(1), RED2(2), BLUE1(1), BLUE2(2), NONE(-1);

		public final int position;

		private Alliance(int position) {
			this.position = position;
		}

		public boolean isRedAlliance() {
			return this == RED1 || this == RED2;
		}

		public boolean isBlueAlliance() {
			return this == BLUE1 || this == BLUE2;
		}
	}

	public static final class NativeJoystickData {
		public static final int AXES_COUNT = 6;
		public static final int BUTTON_COUNT = 18;

		public float[] axes;
		public boolean[] buttons;
		public boolean isValid;

		public NativeJoystickData() {
			axes = new float[AXES_COUNT];
			buttons = new boolean[BUTTON_COUNT];
			isValid = true;
		}

		public NativeJoystickData(float[] axes, boolean[] buttons,
				boolean isValid) {
			this.axes = axes;
			this.buttons = buttons;
			this.isValid = isValid;
		}

		@Override
		public String toString() {
			return "NativeJoystickData [axes=" + Arrays.toString(axes)
					+ ", buttons=" + Arrays.toString(buttons) + ", isValid="
					+ isValid + "]";
		}

	}

	public static final class InitialMatchData {
		public String eventName;
		public String gameMessage;
		public Alliance alliance;
		public String ip;

		@Override
		public String toString() {
			return "InitialMatchData [eventName=" + eventName
					+ ", gameMessage=" + gameMessage + ", alliance=" + alliance
					+ ", ip=" + ip + "]";
		}
	}

	public static final class PeriodicMatchData {
		public static final int JOYSTICK_COUNT = 2;

		public NativeJoystickData[] joysticksData;
		public boolean isEnabled;
		public GameType gameType;

		public PeriodicMatchData(NativeJoystickData[] joysticksData,
				GameType gameType, boolean isEnabled) {
			this.joysticksData = joysticksData;
			this.isEnabled = isEnabled;
			this.gameType = gameType;
		}

		@Override
		public String toString() {
			return "PeriodicMatchData [joysticksData="
					+ Arrays.toString(joysticksData) + ", isEnabled="
					+ isEnabled + ", gameType=" + gameType + "]";
		}

	}

	public static class DriverStationData {
		public int battery;
		public String extra;
	}

	/**
	 * Maximum amount of joysticks to connect
	 */
	public static final int JOYSTICK_COUNT = 2;

	private static Gson gson = new Gson();

	public static final TypeAdapter<PeriodicMatchData> JSON_CACHE_PARSER = gson
			.getAdapter(PeriodicMatchData.class);
	public static final TypeAdapter<InitialMatchData> JSON_MATCH_DATA_PARSER = gson
			.getAdapter(InitialMatchData.class);
	public static final TypeAdapter<DriverStationData> JSON_Station_DATA_PARSER = gson
			.getAdapter(DriverStationData.class);
}
