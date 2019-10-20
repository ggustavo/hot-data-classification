package util;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Random;

public class WorkloadGenerator {

	
	
	
	public static void main(String[] args) throws IOException {

		Trace writeIntense = new Trace("write.csv");
		//size, intervalStart, intervalEnd, percentRead
		writeIntense.genOperation(1000, 1, 500, 10);
		writeIntense.genOperation(2000, 1, 100, 20);
		writeIntense.genOperation(1000, 1, 200, 30);
		writeIntense.close();
		
		Trace readIntense = new Trace("read.csv");
		//size, intervalStart, intervalEnd, percentRead
		readIntense.genOperation(1000, 1, 500, 90);
		readIntense.genOperation(2000, 1, 100, 80);
		readIntense.genOperation(1000, 1, 200, 70);
		readIntense.close();
		
	}
	
	
	public static class Trace{
		
		
		private Random m  = new Random();
		private BufferedWriter bw;
		private int time = 0;
		
		public Trace(String path) throws IOException {
			FileOutputStream fos = new FileOutputStream(new File(path));
			bw = new BufferedWriter(new OutputStreamWriter(fos));
		}
		
		public void close() throws IOException {
			bw.close();
			System.out.println("\n_________________________________");
			System.out.println("Finish ");
		}
		
		public void genOperation(int size, int intervalStart, int intervalEnd, int percentRead) throws IOException {
				
				int numberOfReads = (size*percentRead)/100;
				int numberOfWrite = size - numberOfReads;
				
				
				boolean newRead = true;
				
				int countReads = 0;
				int countWrites = 0;
				
				
				
				for (int i = 0; i < size; i++) {
					
					newRead = m.nextBoolean();
				
					if(newRead) {
						if(countReads >= numberOfReads) {
							newRead = false;
						}
					}else {
						if(countWrites >= numberOfWrite) {
							newRead = true;
						}
					}
					
					bw.write((time++) + ","+ (newRead ? 'R' : 'W') + "," + (intervalStart + m.nextInt(intervalEnd)));	
					bw.newLine();
					if(newRead) {
						countReads++; 
					}else {
						countWrites++;					
					}
					
				}
				
				System.out.println("------------------------------------------------");
				System.out.println("Read: "+ countReads);
				System.out.println("Writes: " + countWrites);
				System.out.println("Size: " + size);
				System.out.println("------------------------------------------------");
		}
	}
	
}
