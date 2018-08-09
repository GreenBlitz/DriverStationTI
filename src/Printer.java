import java.io.PrintStream;

/**
 * Created by ofeke on 7/24/2018.
 */
public class Printer extends Thread{

    private PrintStream pw;
    private Thread thread;
    private String threadName;
    private String mes;
    
    public Printer(String name, PrintStream ps, String mes)
    {
    	this.mes = mes;
        threadName = name;
        pw = ps;
    }

    @Override
    public void run(){
        try {
            pw.println(mes);
            pw.flush();
        }catch(Exception x){
            x.printStackTrace();
        }
    }
    public void start () {
        if (thread == null) {
            thread = new Thread (this, threadName);
            thread.start ();
        }
    }
}
