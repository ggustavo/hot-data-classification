import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class WorkloadGenerator {

	public static void main(String[] args) throws IOException {
		Random m  = new Random();

		FileOutputStream fos = new FileOutputStream(new File("log.txt"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
		for (int i = 0; i < 6000; i++) {
			
			bw.write(i+":"+m.nextInt(100));
			bw.newLine();
		}
	
		bw.close();
		
		System.out.println("Finish");
	}
}
