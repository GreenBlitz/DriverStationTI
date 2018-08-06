

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class UI extends Application{
	private boolean canActivate = false;
	private boolean gameStateTele = true;
	private ComboBox teamComboBox;
	private TextArea ipArea;
	private boolean enable = false;
	private static DServerCommunication serverCommunication;
	
	public static void main(String[] args){
		try{
            serverCommunication = DServerCommunication.init();
            serverCommunication.start();
		}catch (Exception x){
			x.printStackTrace();
		}
		launch(args);
		
	}
	
	@Override
	public void start(Stage dsStage) throws Exception{
		dsStage.setTitle("Treasure Island Driver Station"); //Setting app title
		
		//Creating console
		final TextArea console = new TextArea();
		console.setTranslateX(250);
		console.setTranslateY(10);
		console.setEditable(false);

        ToggleGroup toggleEnableDisable = new ToggleGroup();

        ToggleButton tbEnable = new ToggleButton("Enable");
        tbEnable.setToggleGroup(toggleEnableDisable);
        tbEnable.setStyle("-fx-text-fill: #00990a;");
        tbEnable.setTranslateX(72);
        tbEnable.setTranslateY(240);
        tbEnable.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                enable();
                console.appendText("The robot has been enabled \n");
            }
        });
        ToggleButton tbDisable = new ToggleButton("Disable");
        tbDisable.setToggleGroup(toggleEnableDisable);
        tbDisable.setStyle("-fx-text-fill: #f21f1f;");
        tbDisable.setTranslateX(70.5);
        tbDisable.setTranslateY(240);
        tbDisable.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                disable();
                console.appendText("The robot has been disabled \n");
            }
        });

        ToggleGroup setState = new ToggleGroup();

        ToggleButton tbTele = new ToggleButton("TeleOperated");
        tbTele.setToggleGroup(setState);
        tbTele.setTranslateX(25);
        tbTele.setTranslateY(290);
        tbTele.setPrefWidth(100);
        tbTele.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                setGameStateTele();
                console.appendText("The robot has entered Teleoperated mode \n");
            }
        });
        ToggleButton tbAuto = new ToggleButton("Autonomous");
        tbAuto.setToggleGroup(setState);
        tbAuto.setTranslateX(25.5);
        tbAuto.setTranslateY(290);
        tbAuto.setPrefWidth(100);
        tbAuto.setOnAction(new EventHandler<ActionEvent>() {
            @Override public void handle(ActionEvent e) {
                setGameStateAuto();
                console.appendText("The robot has entered Autonomous mode \n");
            }
        });

        HBox hGameState = new HBox(tbTele, tbAuto);
        HBox hEnableDisable = new HBox(tbEnable, tbDisable);
		//Creating list of options for the ComboBox
		ObservableList<String> teamOptions =
				FXCollections.observableArrayList(
						"Red 1",
						"Red 2",
						"Blue 1",
						"Blue 2");
		//Creating the ComboBox
		teamComboBox = new ComboBox(teamOptions);
		teamComboBox.setTranslateX(85);
		teamComboBox.setTranslateY(335);
		teamComboBox.getSelectionModel().selectFirst();
		setTeam(teamComboBox.getValue().toString());
		teamComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                console.appendText("Team set as: " + teamComboBox.getValue().toString() + "\n"
                );
            }
        });
		//Adding a Text with teamNum
		Text teamTxt = new Text("Team #" + getTeamNum());
		teamTxt.setStyle("-fx-font-size: 16;");
		teamTxt.setTranslateX(25);
		teamTxt.setTranslateY(30);
		
		//Adding robot battery Text
		Text batteryTxt = new Text("Robot Battery");
		batteryTxt.setTranslateX(25);
		batteryTxt.setTranslateY(60);
		//Adding a corresponding ProgressBar based on the robot's battery
		ProgressBar batteryPB = new ProgressBar();
		batteryPB.setTranslateX(35);
		batteryPB.setTranslateY(60);
		batteryPB.setProgress(getBattery());
		//Placing the Text and ProgressBar in a HBox
		HBox battery = new HBox(batteryTxt, batteryPB);
		
		//Adding communication Text
		Text commTxt = new Text("Communication");
		commTxt.setTranslateX(25);
		commTxt.setTranslateY(100);
		//Adding a Rectangle which is colored Red or Green based on robot communication status
		Rectangle commRG = new Rectangle(15,5);
		commRG.setStyle("-fx-fill: #ff0000");
		commRG.setTranslateX(35);
		commRG.setTranslateY(107.5);
		if(getRobotComm() == true){
			commRG.setStyle("-fx-fill: #00ff00");
		}
		//Placing the Text and Rectangle in a HBox
		HBox comm = new HBox(commTxt, commRG);
		
		//Adding robot code Text
		Text codeTxt = new Text("Robot Code");
		codeTxt.setTranslateX(25);
		codeTxt.setTranslateY(115);
		//Adding a Rectangle which is colored Red or Green based on robot code status
		Rectangle codeRG = new Rectangle(15,5);
		codeRG.setStyle("-fx-fill: #ff0000");
		codeRG.setTranslateX(35);
		codeRG.setTranslateY(122.5);
		//Placing the Text and Rectangle in a HBox
		HBox code = new HBox(codeTxt, codeRG);
		
		//Adding joystick Text
		Text joystickTxt = new Text("Joysticks");
		joystickTxt.setTranslateX(25);
		joystickTxt.setTranslateY(130);
		//Adding a Rectangle which is colored Red or Green based on joystick(controller) status
		Rectangle joystickRG = new Rectangle(15,5);
		joystickRG.setStyle("-fx-fill: #ff0000");
		joystickRG.setTranslateX(35);
		joystickRG.setTranslateY(137.5);
		if(getJoystick() == true){
			commRG.setStyle("-fx-fill: #00ff00");
		}
		//Placing the Text and Rectangle in a HBox
		HBox joystick = new HBox(joystickTxt, joystickRG);
		
		//Creating IP text
		Text ipTxt = new Text("IP: ");
		ipTxt.setTranslateX(25);
		ipTxt.setTranslateY(182.5);
		//Creating IP textarea
		TextArea ipArea = new TextArea();
		ipArea.setTranslateX(35);
		ipArea.setTranslateY(175);
		ipArea.setPrefRowCount(1);
		ipArea.setPrefColumnCount(6);
		//Creating SetIP button
		Button setIPButton = new Button("Set IP");
		setIPButton.setTranslateX(45);
		setIPButton.setTranslateY(180);
		setIPButton.setOnAction((new EventHandler<ActionEvent>() {
		    @Override public void handle(ActionEvent e) {
		        setIP(ipArea.getText());
		        console.appendText("The IP has been set \n");
		    }}));
		//Putting all IP related objects into a HBOX
		HBox hIP = new HBox(ipTxt, ipArea, setIPButton);

		if(getRobotMessage() != null){console.appendText(getRobotMessage() + "\n");}
		
		//Creating Pane
		Pane driverStation = new Pane();
		//Setup of driverStation color and visible features
		driverStation.setStyle("-fx-background-color: #79847a");
		driverStation.getChildren().addAll(hGameState, hEnableDisable, teamTxt, battery, comm, code, joystick, hIP,teamComboBox, console);

		//Creating Scene and loading it
		Scene scene = new Scene(driverStation, 750, 375);
		dsStage.setScene(scene);
		dsStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
		    @Override
		    public void handle(WindowEvent t) {
		        Platform.exit();
		        serverCommunication.kill();
		        while (!serverCommunication.isClosed()) {}
		        System.exit(0);
		    }
		});
		dsStage.show();
	}

	public void setActivate(){
	    canActivate = DServerCommunication.init().canChange();
	}
	
	public boolean getActivate(){
		return canActivate;
	}

	public void disable() {
        if (DServerCommunication.init().canChange()) {
            enable = false;
            if (gameStateTele) {
                DSEv3Communication.init().disableTele();
                return;
            }
            DSEv3Communication.init().disableAuto();

        }
    }

	public void enable(){
        if (DServerCommunication.init().canChange()) {
            enable = true;
            if (gameStateTele) {
                DSEv3Communication.init().enableTele();
                return;
            }
            DSEv3Communication.init().enableAuto();
        }
	}
	
	public void setGameStateAuto(){
	    if(DServerCommunication.init().canChange()) {
            gameStateTele = false;
            if(enable){
                DSEv3Communication.init().enableAuto();
                return;
            }
            DSEv3Communication.init().disableAuto();
        }
	}

	public void setGameStateTele(){
        if(DServerCommunication.init().canChange()) {
            gameStateTele = true;
            if(enable){
                DSEv3Communication.init().enableTele();
                return;
            }
            DSEv3Communication.init().disableTele();
        }
	}

	public RobotData.Alliance getTeamNum(){
		return DServerCommunication.init().getAlliance();
	}
	
	public int getBattery(){
		return DSEv3Communication.init().getRobotBaterry();
	}
	
	public boolean getRobotComm(){
		return DSEv3Communication.init().getRobotState() =="NotConnected" ? false : true;
	}
	
	public boolean getJoystick(){
		return ControllerBoy.init().isConnected();
	}
	
	public void setTeam(String team){
		team = teamComboBox.getValue().toString();
	}

	public String getRobotMessage(){
		return DSEv3Communication.init().getExtra();
	}
	
	public void setIP(String ip){
		DSEv3Communication.init().setIp(ip);
	}
}
