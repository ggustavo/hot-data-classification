package online;
import java.io.IOException;

public class Main {
	
	
	public static void main(String[] args) throws IOException {
			
		
		HotDataClassify c = new HotDataClassify();
		//c.analyze("read.csv");
		//c.backward("tpce.csv", 6000);
		//c.backward("log.requests", 100000);
		//c.forward("read.csv", 30);
		c.forward("log.requests", 100000);
		
		
	} 
	
	
}
