
public class HackEventC{
 private String src;
 private String proto;
 private String dst;
 private int count;
 
 public HackEventC(String src,String proto,String dst,int Count)
 {
  this.src=src;
  this.proto=proto;
  this.dst=dst;
  this.count=Count;
 }

 public String getSrc() {
  return src;
 }

 public String getProto() {
  return proto;
 }

 public String getDst() {
  return dst;
 }
 
 public int getCount(){
	 return count;
 }
}
