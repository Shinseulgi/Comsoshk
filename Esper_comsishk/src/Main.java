import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Main extends JFrame{
	String mail;
	JPanel pane=new JPanel();
	public Main(){
		setTitle("***********HACKING ALERT***********");
		setSize(500,700);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container con=this.getContentPane();
		JLabel label=new JLabel("input administer email:");		
		final JTextField userId=new JTextField(15);
		JButton okButton=new JButton("Start Monitoring");
		JLabel label1=new JLabel("  input receiver email  :");		
		final JTextField userId1=new JTextField(15);
		JButton okButton1=new JButton("      Send Mail      ");
		JLabel label2=new JLabel("______________________________Status______________________________\n");		
		final JTextArea label3=new JTextArea(5,40);	
		label3.setColumns(5);
		final JScrollPane jp=new JScrollPane(label3);
		jp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				mail=userId.getText();
				label3.append("Monitoring Started....\n");
				start();
			}
		});
		okButton1.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				mail=userId1.getText();
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n\n");
				label3.append("Notify Mail sent to : "+mail+"......\n\n\n\n\n\n\n\n\n");
				MailThread mt=new MailThread(mail,"1","2","3",1);
				mt.run();
			}
		});
			
		pane.add(label);
		pane.add(userId);
		pane.add(okButton);
		pane.add(label1);
		pane.add(userId1);
		pane.add(okButton1);
		pane.add(label2);
		pane.add(label3);
		pane.add(jp);
		con.add(pane);
		setVisible(true);
	}

	public static void main(String[] args) {
	   Main m=new Main();
	}
   public void start(){
	   Configuration config = new Configuration();
	      config.addEventTypeAutoName("pack");
	      EPServiceProvider epService= EPServiceProviderManager.getDefaultProvider(config);
	      String epl="select src,proto,dst,count from HackEvent.win:time(30 sec) where count>=5";
	      EPStatement statement = epService.getEPAdministrator().createEPL(epl);
	      EventListener listener = new EventListener(mail);
	      statement.addListener(listener);
	      Runnable r1 = new ThreadA(epService);
	      Thread t1= new Thread(r1);
	      t1.start();
	      
	      EPServiceProvider epServiceB= EPServiceProviderManager.getDefaultProvider(config);
	      String eplB="select src,proto,dst,count from HackEventB.win:time(30 sec) where count>=5";
	      EPStatement statementB = epServiceB.getEPAdministrator().createEPL(eplB);
	      EventListenerB listenerB = new EventListenerB(mail);
	      statementB.addListener(listenerB);
	      Runnable r2 = new ThreadB(epServiceB);
	      Thread t2= new Thread(r2);
	      t2.start();

	      EPServiceProvider epServiceC= EPServiceProviderManager.getDefaultProvider(config);
	      String eplC="select src,proto,dst,count from HackEventC.win:time(30 sec) where count>=1";
	      EPStatement statementC = epServiceC.getEPAdministrator().createEPL(eplC);
	      EventListenerC listenerC = new EventListenerC(mail);
	      statementC.addListener(listenerC);
	      Runnable r3 = new ThreadC(epServiceC);
	      Thread t3= new Thread(r3);
	      t3.start();
   }
}