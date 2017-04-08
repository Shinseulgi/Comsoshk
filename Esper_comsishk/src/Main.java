import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
public class Main {
 public static void main(String[] args) {
      Configuration config = new Configuration();
      config.addEventTypeAutoName("pack");
      EPServiceProvider epService= EPServiceProviderManager.getDefaultProvider(config);
      String epl="select src,proto,dst,count from HackEvent.win:time(30 sec) where count>5";
      EPStatement statement = epService.getEPAdministrator().createEPL(epl);
      EventListener listener = new EventListener();
      statement.addListener(listener);
      Runnable r1 = new ThreadA(epService);
      Thread t1= new Thread(r1);
      t1.start();
 }
}