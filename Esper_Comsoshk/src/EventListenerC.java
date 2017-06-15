
import javax.swing.table.DefaultTableModel;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;


public class EventListenerC implements UpdateListener{
	public String EMail;
	public DefaultTableModel Tablemodel;
	public EventListenerC(String mail,DefaultTableModel tablemodel){
		this.EMail=mail;
		this.Tablemodel=tablemodel;
	}
 public void update(EventBean[] newEvents,EventBean[] oldEvents)
 { 
  EventBean event1= newEvents[0];
	this.Tablemodel.addRow(new String[]{"3",(String) event1.get("src"),(String) event1.get("dst"),(String) event1.get("proto")});
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
