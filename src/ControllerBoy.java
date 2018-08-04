import javafx.util.Pair;
import net.java.games.input.Component;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Controller;
import java.util.ArrayList;

/**
 * Created by ofeke on 8/2/2018.
 */
public class ControllerBoy {
    private Controller control;
    private static ControllerBoy instance;
    private boolean[] buttons;
    private float[] axis;
    private boolean[] povs;// 0 is left up
    private ArrayList<Controller> foundControllers;

    public Controller getController(){
        return control;
    }

    private ControllerBoy(){
        buttons = new boolean[10];
        povs = new boolean[8];
        axis = new float[6];
    }

    public static ControllerBoy init(){
        if(instance == null){
            instance = new ControllerBoy();
        }
        return instance;
    }

    public void waitToConnection(){
        while(!isConnected()){
            System.out.println("Waiting for the fucking son!");
        }
    }


    public boolean[] getPovs(){
        //if(!isConnected()){
            //for(int i = 0;i< povs.length;i++)
          //      povs[i] = false;
        //    return povs;
      //  }
        waitToConnection();
        update();
        return povs;
    }

    public float[] getAxis(){
        /**
         * 0 is left up down
         * 1 is left sides
         * 2 is right up down
         * 3 is right sides
         * 4 is left button
         * 5 is probably right buttons
         */
        //for(int i = 0;i< axis.length;i++)
        //    axis[i] = 0.0f;
        waitToConnection();

        update();
        return axis;
    }

    public void update(){
       // if(!isConnected()) {
      //      System.out.println("SHIT");
     //       return;
      //  }
        control.poll();
        Component[] components = control.getComponents();
        int counterA = 0;
        int counterB = 0;
        int counterP = 0;
        boolean bool = true;
        float f = 0.0f;
        for(int i = 0; i< components.length;i++) {
            // updating the axis!
            if(components[i].isAnalog()){
                f = components[i].getPollData();
                if(f < 0.15 && f > -0.15)
                    f = 0;
                if(f >0.93 || f < -0.93)
                    f = 1 * f/Math.abs(f);
                axis[counterA] = f;
                counterA++;
            }else if(components[i].getIdentifier().getName().matches("^[0-9]*$")){
                bool =true;
                if (components[i].getPollData() == 0.0f){
                    bool = false;
                }
                buttons[counterB] = bool;
                counterB++;
            }
        }
        float h = components[15].getPollData();
        for(int i = 0;i< povs.length;i++)
            povs[i] = false;
        //System.out.println(h);
        if(h>0) {
            int a = new Double(h / 0.125).intValue() - 1;
            povs[a] = true;
        }
    }

    public boolean isWriterConnected(){

        ArrayList<Controller> foundControllers = new ArrayList<>();

        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i = 0; i < controllers.length; i++){
            net.java.games.input.Controller controller = controllers[i];
            if (    controller.getType() == net.java.games.input.Controller.Type.STICK ||
                    controller.getType() == net.java.games.input.Controller.Type.GAMEPAD ||
                    controller.getType() == net.java.games.input.Controller.Type.WHEEL ||
                    controller.getType() == net.java.games.input.Controller.Type.FINGERSTICK
                    )
            {
                return true;
            }
        }
        return false;
    }

    public boolean isConnected(){
        foundControllers = new ArrayList<>();

        Controller[] controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();

        for(int i = 0; i < controllers.length; i++){
            net.java.games.input.Controller controller = controllers[i];
            if (
                    controller.getType() == net.java.games.input.Controller.Type.STICK ||
                            controller.getType() == net.java.games.input.Controller.Type.GAMEPAD ||
                            controller.getType() == net.java.games.input.Controller.Type.WHEEL ||
                            controller.getType() == net.java.games.input.Controller.Type.FINGERSTICK
                    )
            {
                foundControllers.add(controller);
              //  control = controller;
             //   control.poll();
              //  return true;
            }
        }
        if(!foundControllers.isEmpty()) {
            control = foundControllers.get(0);
            control.poll();
            for(int i = 0; control.getType() == Controller.Type.MOUSE  || i < foundControllers.size(); i++) {
                control = foundControllers.get(i);
                control.poll();
            }

            return true;
        }
        return false;
    }

    public boolean[] getButtons(){
       // if(!isConnected()){
        //    for(int i = 0;i< buttons.length;i++) {
       //         buttons[i] = false;
       //     }
           // return buttons;
      //  }
        waitToConnection();
        update();
        boolean[] OhadShitiButtons = new boolean[18];
        for(int i = 0; i< buttons.length; i++)
            OhadShitiButtons[i] = buttons[i];
        for(int i = 0; i< povs.length;i++)
            OhadShitiButtons[i+10] = povs[i];
        return OhadShitiButtons;
    }
}
