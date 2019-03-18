import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class WorkloadGenerator {

	
	static Random m  = new Random();
	
	public static void main(String[] args) throws IOException {


		FileOutputStream fos = new FileOutputStream(new File("log.txt"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
	 
//		for (int i = 0; i < 6000; i++) {
//			
//			bw.write(i+":"+m.nextInt(100));
//			bw.newLine();
//		}
		
		genOperation(5000, bw, 1, 500);
		genOperation(1050, bw, 1, 20);
		genOperation(1000, bw, 1, 500);
		bw.close();
		
		System.out.println("Finish");
	}
	
	static int time = 0;
	public static void genOperation(int size, BufferedWriter bw, int interval1, int interval2) throws IOException {

		for (int i = 0 ; i < size; i++) {
			bw.write(time++ +":"+(interval1 + m.nextInt(interval2)));
			bw.newLine();
		}
	
	}
	
}
