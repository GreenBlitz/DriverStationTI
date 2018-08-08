import com.google.gson.Gson;
import javafx.beans.binding.IntegerExpression;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Scanner;

/**
 * Created by ofeke on 8/1/2018.
 */
public class DSEv3Communication extends Thread{
    private String name;
    private Thread thread;
    private static DSEv3Communication instance;
    private Socket socket;
    private DataInputStream input;
    private DataOutputStream output;
    private PrintStream ps;
    private Scanner scan;
    private boolean working;
    private int port = 4444;
    private int battery;
    private String state;
    private RobotData.GameType GameState ;
    private DSEv3Writer dsw;
    private boolean always = true;
    private boolean accepted = false;
    private String ip;
    private String extra;
    Gson jo;

    private DSEv3Communication(){
        state = "Disable";
        GameState = RobotData.GameType.TELEOP;
        battery = 0;
        this.name = "Ev3Communication";
        working = true;
        ip = "10.0.1.1";
    }

    public static DSEv3Communication init(){
        if(instance == null){
            instance = new DSEv3Communication();
        }
        return instance;
    }

    @Override
    public void run(){
        while(ip == ""){}
        while (always) {
            try {
                Socket soc;
                while (socket == null) {
                	System.out.println("attampted ocnnection to {ip= " + ip + ", port= " + port + "}");
                    try { ///yolo
                        soc = new Socket(ip, port);
                        Thread.sleep(100);
                        socket = soc;
                    } catch (Exception x) {
                        System.out.println("connect to an ev3!");
                    }
                }
                DServerCommunication.init().setRobotConnection(true);
                accepted = true;
                working = true;
                input = new DataInputStream(socket.getInputStream());
                output = new DataOutputStream(socket.getOutputStream());
                scan = new Scanner(input);
                ps = new PrintStream(output, true);
                DSEv3Writer writer = new DSEv3Writer(ps);
                writer.start();
                try {
                    while (working) {
                        while (!scan.hasNext()) {}
                       
                        String str = scan.next();
                        
                        battery = RobotData.JSON_Station_DATA_PARSER.fromJson(str).battery;
                        extra = RobotData.JSON_Station_DATA_PARSER.fromJson(str).extra;
                        System.out.println(str);
                        
                        try {
                            if (extra == "kill") {
                                writer.kill();
                                ps.close();
                                scan.close();
                                output.close();
                                input.close();
                                socket.close();
                                socket = null;
                                working = false;
                            }
                        }catch (Exception x){
                            working = false;
                        }
                    }
                } catch (IOException x) {
                    x.printStackTrace();
                }
                ps.close();
                scan.close();
                output.close();
                input.close();
                socket.close();
                socket = null;
                working = true;
            } catch (IOException x) {
                x.printStackTrace();
                System.out.println("the robot have disconnected");
                try{
                    ps.close();
                    scan.close();
                    output.close();
                    input.close();
                    socket.close();
                    socket = null;
                }catch (Exception y){
                    System.out.println("WHAT");
                }
            }
        }
    }

    public boolean hasBeenAccepted(){
        return accepted;
    }

    public boolean isWorking(){
        return working;
    }

    @Override
    public void finalize(){
        working = false;
        try {
            ps.close();
            scan.close();
            output.close();
            input.close();
            socket.close();
        }catch (IOException x){

        }
    }

    public String getExtra(){
        if(extra == null){
            return "";
        }
        return extra;
    }

    public String getIp(){
        return ip;
    }

    public void setIp(String ip){
        this.ip = ip;
    }

    public void start() {
        if (thread == null) {
            thread = new Thread(this, name);
            thread.start();
        }
    }

    public void disableAuto(){
        state = "Disable";
        GameState = RobotData.GameType.AUTO;
        System.out.println("DISABLE AUTO DISABLE AUTO");
    }

    public void disableTele(){
        state = "Disable";
        GameState = RobotData.GameType.TELEOP;
        System.out.println("DISable tele Disable tele");
    }

    public void enableAuto(){
        state = "Enable";
        GameState = RobotData.GameType.AUTO;
        System.out.println("Enable,  AUTO, Enbalem AUTO");
    }

    public void enableTele(){
        state = "Enable";
        GameState = RobotData.GameType.TELEOP;
        System.out.println("Enable, TELEOP Enable, TELEOP Enable, TELEOP Enable, TELEOP Enable, TELEOP");
    }

    public String getRobotState(){
        if(accepted){
            return state;
        }
        return "NotConnected";
    }

    public Integer getRobotBaterry(){
        return battery;
    }

    public RobotData.GameType getGameState(){
        return GameState;
    }
}
