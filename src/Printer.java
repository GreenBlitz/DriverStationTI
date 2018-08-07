import java.io.PrintStream;


public class Printer extends Thread {
	private String name;
	private Thread thread;
	private PrintStream ps;
	private String mes;
	
	public Printer(PrintStream ps, String mes){
		this.mes = mes;
		this.ps = ps;
		this.name = "PrintEv3Message";
	}
	
	@Override
	public void run(){
		if(mes != null)
			ps.println(mes);
	}
	
	public void start(){
		if(thread == null){
			thread = new Thread(this, name);
			thread.start();
		}
	}
}
