import java.io.IOException;

public class Main {
	
	
	public static void main(String[] args) throws IOException {
			
		
		HotDataClassify c = new HotDataClassify();
		c.analyze("read.csv");
		c.backward("read.csv", 30);
		//c.forward("read.csv", 30);
	} 
	
	
}
