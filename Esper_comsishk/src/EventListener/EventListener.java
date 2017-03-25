import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;


public class EventListener implements UpdateListener{
	
 public void update(EventBean[] newEvents,EventBean[] oldEvents)
 { 
  EventBean event1= newEvents[0];
  System.out.println("src :"+event1.get("src")+
               " proto :"+event1.get("proto")+
               " count :"+event1.get("count"));
  
  MailThread mailthread = new MailThread(event1);
  mailthread.run();
 }

class MailThread extends Thread{
	EventBean mailevent;
	
	public MailThread(EventBean event1){
		  this.mailevent = event1;
	}
	
	@Override
	public void run(){
		  Date dt=new Date();
				 SimpleDateFormat date=new SimpleDateFormat("yyyy-MM-dd, hh:mm:ss a");
				  Properties p = System.getProperties();
				  p.put("mail.smtp.starttls.enable", "true");     // gmail은 무조건 true 고정
				  p.put("mail.smtp.host", "smtp.gmail.com");      // smtp 서버 주소
				  p.put("mail.smtp.auth","true");                 // gmail은 무조건 true 고정
				  p.put("mail.smtp.port", "587");                 // gmail 포트
				  Authenticator auth = new MyAuthentication();  
				  //session 생성 및  MimeMessage생성
				  Session session = Session.getDefaultInstance(p, auth);
				  MimeMessage msg = new MimeMessage(session);  
				  try{
				      //편지보낸시간
				      msg.setSentDate(new Date());
				      InternetAddress from = new InternetAddress() ;
				      from = new InternetAddress("notifymail.comsoshk@gmail");
				      // 이메일 발신자
				      msg.setFrom(from);
				      // 이메일 수신자
				      InternetAddress to = new InternetAddress("tls948@naver.com");
				      msg.setRecipient(Message.RecipientType.TO, to);
				      // 이메일 제목
				      msg.setSubject(date.format(dt).toString()+" Notify Mail", "UTF-8");
				      // 이메일 내용
				      msg.setText(date.format(dt).toString()+'\n'+'\n'+'\n'+
				    		       "src :"+mailevent.get("src")+'\n'+
				                 " proto :"+mailevent.get("proto")+'\n'+
				                 " count :"+mailevent.get("count"), "UTF-8");
				      // 이메일 헤더
				      msg.setHeader("content-Type", "text/html");
				      //메일보내기
				      javax.mail.Transport.send(msg);
				  }catch (AddressException addr_e) {
				      addr_e.printStackTrace();
				  }catch (MessagingException msg_e) {
				      msg_e.printStackTrace();
				  }
				}
	}
}


 class MyAuthentication extends Authenticator {
     PasswordAuthentication pa;
     public MyAuthentication(){
    	 String id = "notifymail.comsoshk@gmail.com";       // 구글 ID
    	 String pw = "comsoshk";          // 구글 비밀번호
    	 // ID와 비밀번호를 입력한다.
    	 pa = new PasswordAuthentication(id, pw);
     }
	 // 시스템에서 사용하는 인증정보
     public PasswordAuthentication getPasswordAuthentication() {
    	 return pa;
     }
}
 