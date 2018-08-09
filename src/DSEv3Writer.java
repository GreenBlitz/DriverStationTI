import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import com.google.gson.Gson;

public class DSEv3Writer extends Thread {
	private String name;
	private Thread thread;
	private PrintStream ps;
	int time;
	private RobotData.Alliance alliance;
	private boolean working;
	// private DataOutputStream output;

	public DSEv3Writer(PrintStream output) {
		this.name = "Ev3WritingThread";
		this.ps = output;
		time = 0;
		working = true;
		alliance = RobotData.Alliance.NONE;
	}

	@Override
	public void run() {
		try {
			DSEv3Communication com = DSEv3Communication.init();
			alliance = DServerCommunication.init().getAlliance();
			ControllerBoy cb = ControllerBoy.init();
			RobotData.InitialMatchData m = new RobotData.InitialMatchData();
			m.alliance = alliance;
			m.eventName = "";
			m.gameMessage = "";
			m.ip = com.getIp();
			System.out.println(RobotData.JSON_MATCH_DATA_PARSER.toJson(m));
			ps.println(RobotData.JSON_MATCH_DATA_PARSER.toJson(m));
			ps.flush();

			while (working) {
				RobotData.NativeJoystickData[] joy = new RobotData.NativeJoystickData[1];
				joy[0] = new RobotData.NativeJoystickData(ControllerBoy.init().getAxis(),
						ControllerBoy.init().getButtons(), true);
				RobotData.PeriodicMatchData s = new RobotData.PeriodicMatchData(joy,
						DSEv3Communication.init().getGameState(),
						DSEv3Communication.init().getRobotState() == "Enable");
				String send = RobotData.JSON_CACHE_PARSER.toJson(s);
				System.out.println(send);
				ps.println(send);
				ps.flush();
				Thread.sleep(50);
			}
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public void start() {
		if (thread == null) {
			thread = new Thread(this, name);
			thread.start();
		}
	}

	public void kill() {
		working = false;
	}
}
