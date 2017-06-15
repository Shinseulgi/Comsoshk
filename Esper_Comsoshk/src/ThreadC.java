import java.util.Random;

import com.espertech.esper.client.EPServiceProvider;
import java.io.*;
public class ThreadC implements Runnable {
BufferedReader reader;
 private EPServiceProvider epService;

 public ThreadC(EPServiceProvider epService) {
  this.epService=epService;
  
 }

 public void run()
 {
  while(true){
   try {
	   reader=new BufferedReader(new FileReader("/home/seulgi/Project/alarm/result3.txt"));
	   String line=null;
	   while((line=reader.readLine())!=null){
		   String src=line.substring(1,line.indexOf("/"));
		   String dst=line.substring(line.indexOf("/")+1,line.indexOf(","));
		   String port=line.substring(line.indexOf(",")+1,line.length()-1);
		   int count=0;
		  if(port.contains("80+9999")){
			  count=1;
		  }
		  else if(port.contains("135+9191")){
			  count=2;
		  }
		  else if(port.contains("445+4444")){
			  count=3;
		  }
		  else if(port.contains("389+31337")){
			  count=4;
		  }
		  else if(port.contains("135+7175")){
			  count=5;
		  }
		  else if(port.contains("23+2001")){
			  count=6;
		  }
		  else if(port.contains("21+19800")){
			  count=7;
		  }
		  HackEventC event=new HackEventC(src,port,dst,count);
		  epService.getEPRuntime().sendEvent(event);
	   }
    Thread.sleep(10000);
   } catch (Exception e) {
    e.printStackTrace();
   }  
  }
 }
}
