import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class HotDataClassify {
	
	private final static double ALFA = 0.05;
	private final DecimalFormat df = new DecimalFormat("###,##0.0000");
	
	public void backward(String logPah, int k) throws IOException { //Function BackwardClassify(AccessLog L, HotDataSize K)
		HashMap<String, RecordStats> hash = new HashMap<String,RecordStats>(); //initialize hash table
		
		BufferedReader log = backwardLog(logPah);
		String logEntry = null;

		int endTime = -1; 
		int beginTime = 0;
		
		RecordStats kthLower = null;
		
		long acceptThreshold = 0;
		
		//Read back in L to fill H with K unique records with calculated bounds
		while (hash.size() < k && (logEntry = log.readLine()) != null) {
			
			if(logEntry == null || logEntry.isEmpty()) {
				continue; 
			}
			logEntry = logEntry.trim();
			
			String rId = getID(logEntry);


			int currentTime = getTime(logEntry);
			
			if(endTime == -1) {
				endTime = currentTime;
			}
			
			RecordStats r = hash.get(rId);
			
			if(r == null) {
				r = new RecordStats(rId,currentTime);
				hash.put(rId, r);
			}
			
			r.setCurrentEstimation(equation3Estimation(r, currentTime, endTime));		
			r.setLowerBound(equation5LowerBound(r, beginTime, endTime));	
			r.setUpperBound(equation4UpperBound(r, currentTime, endTime));
		
			if(kthLower == null) {
				kthLower = r;
			}else if(r.getLowerBound() < kthLower.getLowerBound()) {
				kthLower = r;
			}
		
		}

		acceptThreshold = (long) ( endTime - (  Math.log(kthLower.getLowerBound()) / Math.log(1 - ALFA) )   );
		if(acceptThreshold < 0) acceptThreshold = endTime;
		
		System.out.println("BeginTime:\t" + beginTime);
		System.out.println("EndTime:\t" + endTime);
		System.out.println();
		System.out.println("Backward Initial kthLower:\t" + kthLower.getLowerBound());
		System.out.println("Backward Initial accept Threshold:\t" + acceptThreshold);
		
		//log.close();
		//log = openLog(logPah); //Reset Log
		
		System.out.println("Backward Initial Hash Table:");
		System.out.println("----------------------------------- size: " + hash.size());
		//printResult(new ArrayList<>(hash.values()));
		System.out.println("-----------------------------------\n");
		
		long count = 0;
		
		while((logEntry = log.readLine()) != null) { //while not at beginning of Log do
			count++;
			if(count % 10000==0)System.out.println(count+"/"+endTime);
			
			if(logEntry == null || logEntry.isEmpty())continue; 
			logEntry = logEntry.trim();
			
			String rId = getID(logEntry);
			int currentTime = getTime(logEntry);
			RecordStats r = hash.get(rId);

			if(r == null) {	
				if(currentTime <= acceptThreshold) { // disregard new record ids read after acceptThreshold time slice 
					continue;
				}else {
					r = new RecordStats(rId,currentTime);
					hash.put(rId, r);
				}
			}
			
			//update r.estb using Equation 3
			r.setCurrentEstimation(equation3Estimation(r, currentTime, endTime));
			r.setLowerBound(equation5LowerBound(r, beginTime, endTime));	
			r.setUpperBound(equation4UpperBound(r, currentTime, endTime));
			
			// begin filter step - inactivate all records that cannot be in hot set
			
			if(hash.size() >= k) { //if end of time slice has been reached then ??
				for (RecordStats rx : hash.values()) { //find value of k th lower bound value in H
					
					if(rx!=r) {
						r.setCurrentEstimation(equation3Estimation(r, currentTime, endTime));
						r.setLowerBound(equation5LowerBound(r, beginTime, endTime));	
						r.setUpperBound(equation4UpperBound(r, currentTime, endTime));						
					}
					
					if(rx.getLowerBound() <= kthLower.getLowerBound()) {
						kthLower = rx;
					}
				}

				//ForAll R in H with r.upEst < kthLower, remove r from H
				List<RecordStats> deletedRecords = new LinkedList<>();
				for (RecordStats rx : hash.values()) {
					if(rx.getUpperBound() < kthLower.getUpperBound()) {
						deletedRecords.add(rx);
				
					}
				}
				for (RecordStats rx : deletedRecords) {
					hash.remove(rx.getId());	
				}
				
				if(hash.size() == k) {
					break;
				}
				acceptThreshold = (long) ( endTime - (  Math.log(kthLower.getLowerBound()) / Math.log(1 - ALFA) )   );
				if(acceptThreshold < 0) acceptThreshold = endTime;
			}
			
		}
		System.out.println();
		System.out.println("----------Backward Result------------- size: " + hash.size());
		
		List<RecordStats> result = new ArrayList<>(hash.values());
		System.out.println("----------Sort Results ...-------------");
		result.sort(Comparator.comparing(RecordStats::getCurrentEstimation));
		
		saveResult(result);
		System.out.println("-----------------------------------\n");
		
		log.close();
		
	}

	public void forward(String logPath, int k) throws IOException {

		HashMap<String, RecordStats> hash = new HashMap<String, RecordStats>(); // initialize hash table

		BufferedReader log = forwardLog(logPath);
		String logEntry = null;
		int currentTime = 0;
		while ((logEntry = log.readLine()) != null) { // while not at beginning of Log do

			if (logEntry == null || logEntry.isEmpty())
				continue;
			logEntry = logEntry.trim();

			String rId = getID(logEntry);
			currentTime = getTime(logEntry);
			
			RecordStats r = hash.get(rId);

			if (r == null) {
				r = new RecordStats(rId, currentTime);
				r.setCurrentEstimation(ALFA + Math.pow((1 - ALFA), currentTime));
				hash.put(rId, r);
			}else {
				r.setCurrentEstimation(ALFA + r.getCurrentEstimation() * Math.pow((1 - ALFA), currentTime - r.getTime()));
				r.setTime(currentTime);
			}

		}
		
		//for each re. H do update rest as of L.curTime
		for (RecordStats r : hash.values()) {
			r.setCurrentEstimation(ALFA + r.getCurrentEstimation() * Math.pow((1 - ALFA), currentTime - r.getTime()));
			//r.setTime(currentTime);
		}
		
		//return K record IDs in H with largest estimate values
		List<RecordStats> result = new ArrayList<>(hash.values());
		result.sort(Comparator.comparing(RecordStats::getCurrentEstimation));
		
		List<RecordStats> resultTopK = new ArrayList<>();
		
		int count = 0;
		System.out.println("----------Forward Result Top 100 -------------");
		
		for (int i = result.size()-1; i >= 0; i--) {
			RecordStats r = result.get(i);
			if(count < 100) {
				System.out.println("id: " + r.getId() + " \testb: " + df.format(r.getCurrentEstimation()));
			}
			resultTopK.add(r);
			count++;
			if(count == k) {
				System.out.println(count);
				break;
			}
		}
		
		System.out.println("...");
		System.out.println("Size: " + resultTopK.size());
		saveResult(resultTopK);
	
		System.out.println("-----------------------------------");
		log.close();
	}
		
	public void analyze(String logPath) throws IOException {
		HashMap<String, RecordStats> hash = new HashMap<String, RecordStats>(); // initialize hash table

		BufferedReader log = forwardLog(logPath);
		String logEntry = null;
		int currentTime = 0;
		while ((logEntry = log.readLine()) != null) { // while not at beginning of Log do
			if (logEntry == null || logEntry.isEmpty())
				continue;
			
			logEntry = logEntry.trim();
			
			String rId = getID(logEntry);
			currentTime = getTime(logEntry);
			
			RecordStats r = hash.get(rId);

			if (r == null) {
				r = new RecordStats(rId, currentTime);
				hash.put(rId, r);
				r.setLowerBound(1);
			}else {
				r.setLowerBound(r.getLowerBound()+1);
			}

		}
		
		System.out.println("----------Analyze Frequency------------- size: "+ hash.size());
		List<RecordStats> result = new ArrayList<>(hash.values());
		result.sort(Comparator.comparing(RecordStats::getLowerBound));
		
		for (int i = result.size()-1; i >= 0; i--) {
			RecordStats r = result.get(i);
			System.out.println("id: " + r.getId() + " \tFrequency: " + (int)r.getLowerBound());
		}
		
		
		System.out.println("-----------------------------------");
		
		log.close();
		
	}
	
	private void saveResult(List<RecordStats> result) {
		try {
			SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh-mm");
			String date = dt.format(new Date());
			PrintWriter logRequests = new PrintWriter(new FileWriter(new File(date + " stoica" + ".pages"), true));

			for (int i = result.size() - 1; i >= 0; i--) {
				RecordStats r = result.get(i);
				// System.out.println("T: " + r.getTime() + " id: " + r.getId() + " \testb: " +
				// df.format(r.getCurrentEstimation()) + "\tlowerBound: "
				// + df.format(r.getLowerBound()) + "\tUpperBound: " +
				// df.format(r.getUpperBound()));
				

				logRequests.println(r.getId()+",R");
	
			}

			logRequests.flush();
			logRequests.close();
			
		} catch (IOException e) {

			e.printStackTrace();
		}

	}
	
	//Bounding Access Frequency Estimates
	private double equation3Estimation(RecordStats r, int currentTime,  int endTime) {
	
		return (ALFA * ( Math.pow( (1 - ALFA) , (endTime - currentTime) ) )) + r.getCurrentEstimation();
		
	}
	
	private double equation5LowerBound(RecordStats r,int beginTime, int endTime) {
		return r.getCurrentEstimation() + ( ( Math.pow( (1 - ALFA) , (endTime - beginTime + 1 ) ) ));

	}
	
	private double equation4UpperBound(RecordStats r, int currentTime, int endTime) {
		
		return r.getCurrentEstimation() + ( ( Math.pow( (1 - ALFA) , (endTime - currentTime + 1 ) ) ));

	}

	private String getID(String logEntry) {
		return logEntry.split(",")[1]; //Use for .requests workloads
		//return logEntry.split(",")[2]; Use for .csv workloads
	}
	private int getTime(String logEntry) {
		return Integer.parseInt(logEntry.split(",")[0]);
	}
	
	
	private BufferedReader backwardLog(String logPath) throws FileNotFoundException {
		
		ReverseLineInputStream reverseLineInputStream = new ReverseLineInputStream(new File(logPath));

		return new BufferedReader(new InputStreamReader(reverseLineInputStream));	
	}
	
	
	private BufferedReader forwardLog(String logPath) throws IOException {
		
		return Files.newBufferedReader(Paths.get(logPath));	
	}
	
	
	double getBaseLog(double x, double y) {
		  return Math.log(y) / Math.log(x);
	}
	
	
}
