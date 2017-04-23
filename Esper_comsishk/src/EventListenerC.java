
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;


public class EventListenerC implements UpdateListener{
public String EMail;
	
	public EventListenerC(String mail){
		this.EMail=mail;
	}
 public void update(EventBean[] newEvents,EventBean[] oldEvents)
 { 
  EventBean event1= newEvents[0];
  System.out.println("3 src :"+event1.get("src")+ 
               " proto :"+event1.get("proto")+
               " count :"+event1.get("count"));
  int count4type=0;
  if((event1.get("count").toString()).contains("1")){
	  count4type=3;
  }
  else if((event1.get("count").toString()).contains("2")){
	  count4type=4;
  }
  else if((event1.get("count").toString()).contains("3")){
	  count4type=5;
  }
  else if((event1.get("count").toString()).contains("4")){
	  count4type=6;
  }
  else if((event1.get("count").toString()).contains("5")){
	  count4type=7;
  }
  else if((event1.get("count").toString()).contains("6")){
	  count4type=8;
  }
  else if((event1.get("count").toString()).contains("7")){
	  count4type=9;
  }
  MailThread mailthread = new MailThread(event1,this.EMail,count4type);
  mailthread.run();
 }
}
