import com.google.gson.Gson;

import java.io.PrintStream;

public class DSEv3Writer extends Thread{
    private String name;
    private Thread thread;
    private PrintStream ps;
    private RobotData.Alliance alliance;
    private boolean working;

    public DSEv3Writer(PrintStream ps){
        this.name = "Ev3WritingThread";
        this.ps = ps;
        working =true;
        alliance = RobotData.Alliance.NONE;
    }

    @Override
    public void run(){
        try{
            DSEv3Communication com = DSEv3Communication.init();
            alliance = DServerCommunication.init().getAlliance();
            ControllerBoy cb = ControllerBoy.init();
            Gson j = new Gson();
            RobotData.MatchSpecificData m = new RobotData.MatchSpecificData();
            m.alliance = alliance;
            m.eventName = "";
            m.gameSpecificMessage = "";
            ps.println(RobotData.JSON_MATCH_DATA_PARSER.toJson(m));
            Thread.sleep(20);
            while(working){

                RobotData.NativeJoystickData[] joy = new RobotData.NativeJoystickData[1];
                joy[0] = new RobotData.NativeJoystickData(ControllerBoy.init().getAxis(), ControllerBoy.init().getButtons(), true);
                RobotData.StationDataCache s = new RobotData.StationDataCache(joy, DSEv3Communication.init().getGameState(), DSEv3Communication.init().getRobotState() == "Enable");
                ps.println(RobotData.JSON_CACHE_PARSER.toJson(s));
                Thread.sleep(20);
            }
        }catch (Exception x){
            x.printStackTrace();
        }
    }

    public void start(){
        if(thread == null){
            thread = new Thread(this, name);
            thread.start();
        }
    }
}
