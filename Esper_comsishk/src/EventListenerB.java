
import javax.swing.table.DefaultTableModel;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;


public class EventListenerB implements UpdateListener{
	public String EMail;
	public DefaultTableModel Tablemodel;
	public EventListenerB(String mail,DefaultTableModel tablemodel){
		this.EMail=mail;
		this.Tablemodel=tablemodel;
	}
 public void update(EventBean[] newEvents,EventBean[] oldEvents)
 { 
	 EventBean event1= newEvents[0];
	 this.Tablemodel.addRow(new String[]{"2",(String) event1.get("src"),(String) event1.get("dst"),(String) event1.get("proto")});
	 MailThread mailthread = new MailThread(event1,this.EMail,1);
	 mailthread.run();
 }
}
