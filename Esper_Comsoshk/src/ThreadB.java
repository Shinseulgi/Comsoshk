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
	   reader=new BufferedReader(new FileReader("/home/seulgi/Project/alarm/result2.txt"));
	   String line=null;
	   while((line=reader.readLine())!=null){
		  String src=line.substring(1,line.indexOf("/"));
		  String dst=line.substring(line.indexOf("/")+1,line.indexOf(","));
		  String port=line.substring(line.indexOf(",")+1,line.length()-1);
		  int count=0;
		  if(port.contains("21/tcp"))
			  count++;
		  if(port.contains("23/tcp"))
			  count++;
		  if(port.contains("79/tcp"))
			  count++;
		  if(port.contains("110/tcp"))
			  count++;
		  if(port.contains("111/tcp"))
			  count++;
		  if(port.contains("135/tcp"))
			  count++;
		  if(port.contains("135/udp"))
			  count++;
		  if(port.contains("161/udp"))
			  count++;
		  if(port.contains("512/tcp"))
			  count++;
		  if(port.contains("513/tcp"))
			  count++;
		  if(port.contains("514/tcp"))
			  count++;
		  if(port.contains("1433/tcp"))
			  count++;
		  if(port.contains("1434/tcp"))
			  count++;
		  if(port.contains("1434/udp"))
			  count++;
		  if(port.contains("32771/tcp"))
			  count++;
		  if(port.contains("32771/udp"))
			  count++;
		  HackEventB event=new HackEventB(src,port,dst,count);
		  epService.getEPRuntime().sendEvent(event);
	   }
    Thread.sleep(10000);
   } catch (Exception e) {
    e.printStackTrace();
   }  
  }
 }
}
