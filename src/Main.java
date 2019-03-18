import java.io.IOException;

public class Main {
	
	
	public static void main(String[] args) throws IOException {
			
		
		HotDataClassify c = new HotDataClassify();
		c.analyze("log.txt");
		c.backward("log.txt", 30);
		//c.forward("log.txt", 30);
	} 
	
	
}
