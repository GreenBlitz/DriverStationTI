import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

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
		// this.ps = ps;
		this.ps = output;
		time = 0;
		working = true;
		alliance = RobotData.Alliance.NONE;
	}

	@Override
	public void run() {
		try {
			Printer print;
			File file = new File("prints.txt");
			file.setWritable(true);
			FileOutputStream fileOutput = new FileOutputStream(file ,true);
			PrintStream fileStream = new PrintStream(fileOutput);
			DSEv3Communication com = DSEv3Communication.init();
			alliance = DServerCommunication.init().getAlliance();
			ControllerBoy cb = ControllerBoy.init();
			Gson j = new Gson();
			RobotData.MatchSpecificData m = new RobotData.MatchSpecificData();
			m.alliance = alliance;
			m.eventName = "";
			m.gameSpecificMessage = "";
			System.out.println(RobotData.JSON_MATCH_DATA_PARSER.toJson(m));
			ps.println(RobotData.JSON_MATCH_DATA_PARSER.toJson(m));
			ps.flush();
			while (working) {
				RobotData.NativeJoystickData[] joy = new RobotData.NativeJoystickData[1];
				joy[0] = new RobotData.NativeJoystickData(ControllerBoy.init().getAxis(),
						ControllerBoy.init().getButtons(), true);
				RobotData.StationDataCache s = new RobotData.StationDataCache(joy,
						DSEv3Communication.init().getGameState(),
						DSEv3Communication.init().getRobotState() == "Enable");
				String send = RobotData.JSON_CACHE_PARSER.toJson(s);
				System.out.println(send + System.currentTimeMillis());
				fileStream.println(send);
				fileStream.flush();
				//ps.println(send);
				//ps.flush();
				print = new Printer("PrinterNumber"+time, ps, send);
				print.start();
				Thread.sleep(200);
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
