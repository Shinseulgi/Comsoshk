import java.util.Random;

import com.espertech.esper.client.EPServiceProvider;
import java.io.*;
public class ThreadB implements Runnable {
BufferedReader reader;
 private EPServiceProvider epService;

 public ThreadB(EPServiceProvider epService) {
  this.epService=epService;
  
 }

 public void run()
 {
  while(true){
   try {
	   reader=new BufferedReader(new FileReader("/home/seulgi/Project/result1.txt"));
	   String line=null;
	   while((line=reader.readLine())!=null){
		  String src=line.substring(1,line.indexOf("/"));
		  String dst=line.substring(line.indexOf("/")+1,line.indexOf(","));
		  String port=line.substring(line.indexOf(",")+1,line.length()-1);
		  int count=0;
		  if(port.contains("21/TCP"))
			  count++;
		  if(port.contains("23/TCP"))
			  count++;
		  if(port.contains("79/TCP"))
			  count++;
		  if(port.contains("110/TCP"))
			  count++;
		  if(port.contains("111/TCP"))
			  count++;
		  if(port.contains("135/TCP"))
			  count++;
		  if(port.contains("135/UDP"))
			  count++;
		  if(port.contains("161/UDP"))
			  count++;
		  if(port.contains("512/TCP"))
			  count++;
		  if(port.contains("513/TCP"))
			  count++;
		  if(port.contains("514/TCP"))
			  count++;
		  if(port.contains("1433/TCP"))
			  count++;
		  if(port.contains("1434/TCP"))
			  count++;
		  if(port.contains("1434/UDP"))
			  count++;
		  if(port.contains("32771/TCP"))
			  count++;
		  if(port.contains("32771/UDP"))
			  count++;
		  HackEventB event=new HackEventB(src,port,dst,count);
		  epService.getEPRuntime().sendEvent(event);
	   }
    Thread.sleep(500);
   } catch (Exception e) {
    e.printStackTrace();
   }  
  }
 }
}
