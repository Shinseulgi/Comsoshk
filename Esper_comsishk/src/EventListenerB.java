
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;


public class EventListenerB implements UpdateListener{
public String EMail;
	
	public EventListenerB(String mail){
		this.EMail=mail;
	}
 public void update(EventBean[] newEvents,EventBean[] oldEvents)
 { 
  EventBean event1= newEvents[0];
  System.out.println("2 src :"+event1.get("src")+
               " proto :"+event1.get("proto")+
               " count :"+event1.get("count"));
  MailThread mailthread = new MailThread(event1,this.EMail,1);
  mailthread.run();
 }
}
