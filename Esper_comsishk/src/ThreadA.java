import java.util.Random;

import com.espertech.esper.client.EPServiceProvider;
import java.io.*;
public class ThreadA implements Runnable {
BufferedReader reader;
 private EPServiceProvider epService;

 public ThreadA(EPServiceProvider epService) {
  this.epService=epService;
  
 }

 public void run()
 {
  while(true){
   try {
	   reader=new BufferedReader(new FileReader("/home/seulgi/Project/result2.txt"));
	   String line=null;
	   while((line=reader.readLine())!=null){
		  String src=line.substring(1,line.indexOf("/"));
		  String port=line.substring(line.indexOf("/")+1,line.indexOf(","));
		  String dst=line.substring(line.indexOf(",")+1,line.length()-1);
		  int count=0;
		  for(int i=0;i<line.length();i++){
			  if(line.charAt(i)=='+')
				  count++;
		  }
		  HackEvent event=new HackEvent(src,port,dst,count+1);
		  epService.getEPRuntime().sendEvent(event);
	   }
    Thread.sleep(500);
   } catch (Exception e) {
    e.printStackTrace();
   }  
  }
 }
}
