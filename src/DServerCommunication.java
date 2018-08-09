import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * Created by ofeke on 8/4/2018.
 */
public class DServerCommunication extends Thread {
    private String name;
    private Thread thread;
    private RobotData.Alliance alliance;
    private DataDS.Data data;
    private RobotData.GameType gameState;
    private String serverIp;
    private int port;
    private String extra;
    private boolean working;
    private static DServerCommunication instance;
    private boolean canChange = true;
    private boolean robotConnection;
    private volatile boolean mIsClosed = false;

    public void setRobotConnection(boolean robotC){
    	robotConnection = robotC;
    }
    
    private DServerCommunication(){
        name = "DriverStationServerCommunicator";
        alliance = RobotData.Alliance.NONE;
        gameState = RobotData.GameType.TELEOP;
        serverIp = "127.0.0.1";
        port= 2212;
        robotConnection = false;
        working = true;
    }

    public static DServerCommunication init(){
        if(instance == null){
            instance = new DServerCommunication();
        }
        return instance;
    }

    @Override
    public void run() {
        try {
            DSEv3Communication evcom = DSEv3Communication.init();
            evcom.start();
            Socket soc = null;
            Socket socket = null;
            while (socket == null) {
                try { ///yolo
                    soc = new Socket(serverIp, port);
                    Thread.sleep(100);
                    socket = soc;
                } catch (Exception x) {
                    System.out.println("connect to an server! #1");
                    //  x.printStackTrace();
                }
            }
            working = true;
            DataInputStream input = new DataInputStream(socket.getInputStream());
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            Scanner scan = new Scanner(input);
            PrintStream ps = new PrintStream(output);

            while (working) {
            	ps.println("{\"Battery\":"+DSEv3Communication.init().getRobotBaterry()+",Connection:"+robotConnection+"}");
            	ps.flush();
            	
            	try{
            		Thread.sleep(100);
            	}catch(Exception x){
            		x.printStackTrace();
            	}
                if(scan.hasNext()){
                	String str = scan.next();

                    data  = DataDS.JSON_DSDATA.fromJson(str);
                    extra = data.extra;
                    
                    if(extra == "kill"){
                    	working = false;
                    }

                    switch (data.id){
                        case 0:
                            alliance = RobotData.Alliance.BLUE1;
                            break;
                        case 1:
                            alliance = RobotData.Alliance.BLUE2;
                            break;
                        case 2:
                            alliance = RobotData.Alliance.RED1;
                            break;
                        case 3:
                            alliance = RobotData.Alliance.RED2;
                            break;
                    }

                    switch (data.state){
                        case PreGame:
                            canChange = false;
                            DSEv3Communication.init().disableAuto();
                            break;
                        case PostGame:
                            DSEv3Communication.init().disableTele();
                            break;
                        case FinishGame:
                            canChange = true;
                            DSEv3Communication.init().disableTele();
                            break;
                        case Disable:
                            DSEv3Communication.init().disableTele();
                            break;
                        case Enable:
                            DSEv3Communication.init().enableTele();
                            break;
                        case Auto:
                            DSEv3Communication.init().enableAuto();
                            break;
                        case Tele:
                            DSEv3Communication.init().enableTele();
                            break;
                        default:
                            DSEv3Communication.init().disableTele();
                            break;
                    }
                }

            }

            scan.close();
            ps.close();
            input.close();
            output.close();
            soc.close();
            socket.close();
            mIsClosed = true;
        }catch (IOException x){

        }
    }

    public boolean canChange(){
        return canChange;
    }

    public RobotData.Alliance getAlliance(){
        return alliance;
    }

    public void kill(){
        working = false;
    }

    public boolean isWorking(){
        return working;
    }

    public void start(){
        if(thread == null){
            thread = new Thread(this, name);
            thread.start();
        }
    }
    
    public boolean isClosed() {
    	return mIsClosed;
    }
}
