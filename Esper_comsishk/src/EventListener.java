
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

public class EventListener implements UpdateListener{
 public void update(EventBean[] newEvents,EventBean[] oldEvents)
 {
  EventBean event1= newEvents[0];
  System.out.println("src :"+event1.get("src")+
               " proto :"+event1.get("proto")+
               " count :"+event1.get("count"));
 }
}
