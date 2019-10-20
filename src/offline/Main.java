package offline;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class Main {
	
	public static void main(String[] args) throws IOException {
		
		long startTime =  System.currentTimeMillis();
		int count = 0;
		String logEntry = null;
		
		BufferedReader log = Files.newBufferedReader(Paths.get("log.requests"));	
		
		Memory algorithm = new LRU(100000);
		
		HashMap<String, Tuple> hash = new HashMap<>();
		System.out.println("Exec...");
		
		while ((logEntry = log.readLine()) != null) { 
			count++;
			if (logEntry == null || logEntry.isEmpty()) {
				System.out.println("Read Error: " + count);
				continue;					
			}
			
			String id = getID(logEntry);
			
			Tuple t = hash.get(id);
			if(t==null) {
				t = new Tuple(id);
				hash.put(id, t);
			}
			
			algorithm.request(Tuple.READ, t);
			
			
		}
		
		
		
		long endTime = System.currentTimeMillis();
		System.out.println("Finish " + (endTime - startTime) + " milliseconds");
		System.out.println(algorithm.showStatics());
		
		
		
	}
	
	private static String getID(String logEntry) {
		return logEntry.split(",")[1]; //Use for .requests workloads
		//return logEntry.split(",")[2]; //Use for .csv workloads
	}
//	private static int getTime(String logEntry) {
//		return Integer.parseInt(logEntry.split(",")[0]);
//	}
	
}
