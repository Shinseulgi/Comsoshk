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

class MailThread extends Thread{
	EventBean mailevent;
	String Email,src1,dst1,proto1,count1;
	int Type;
	public MailThread(EventBean event1,String mail,int type){
		  this.mailevent=event1;
		  this.src1=(String) this.mailevent.get("src");
		  this.dst1=(String) this.mailevent.get("dst");
		  this.proto1=(String) this.mailevent.get("proto");
		  this.Email=mail;
		  this.Type=type;
	}
	
	public MailThread(String mail,String src, String dst,String proto,int type){
		  this.Email=mail;
		  this.src1=src;
		  this.dst1=dst;
		  this.proto1=proto;
		  this.Type=type;
	}
	
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
				      InternetAddress to = new InternetAddress(this.Email);
				      msg.setRecipient(Message.RecipientType.TO, to);
				      // 이메일 제목
				      if(this.Type==1){
				    	  msg.setSubject(date.format(dt).toString()+"Service Enumeration attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==2){
				    	  msg.setSubject(date.format(dt).toString()+"Port Scan attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==3){
				    	  msg.setSubject(date.format(dt).toString()+"MS FrontPage Server Extension Buffer Overflow attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==4){
				    	  msg.setSubject(date.format(dt).toString()+"MS Messenger Heap Overflow attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==5){
				    	  msg.setSubject(date.format(dt).toString()+"LSASS.DLL RPC Buffer Overflow attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==6){
				    	  msg.setSubject(date.format(dt).toString()+"IPswitch IMAIL LDAP remote attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==7){
				    	  msg.setSubject(date.format(dt).toString()+"Windows XP/2000 Return into Libc attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==8){
				    	  msg.setSubject(date.format(dt).toString()+"Splaris  /bin/login Remote Root Exploit attempt Notify Mail", "UTF-8");
				      }
				      else if(this.Type==9){
				    	  msg.setSubject(date.format(dt).toString()+"WFTPD STAT Command Exploit attempt Notify Mail", "UTF-8");
				      }
				      // 이메일 내용
				      msg.setText(date.format(dt).toString()+'\n'+'\n'+'\n'+
				    		  "src :"+this.src1+'\n'+
				                 " proto :"+this.proto1+'\n'+
				                 " dst :"+this.dst1 + "\n"
				    		       , "UTF-8");
				      // 이메일 헤더
				      msg.setHeader("content-Type", "text/html");
				      //메일보내기
				      javax.mail.Transport.send(msg);
				  }catch (AddressException addr_e) {
					  System.out.println("addr:"+addr_e);
				      addr_e.printStackTrace();
				  }catch (MessagingException msg_e) {
					  System.out.println("msg:"+msg_e);
				      msg_e.printStackTrace();
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