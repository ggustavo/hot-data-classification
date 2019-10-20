package offline;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import util.List;
import util.Node;



public abstract class Memory { //Algorithm
	
	protected List<Tuple> coldList = new List<>();
	protected long size = 0;
	protected Object acessSize = new Object();
	
	protected long missCount = 0;
	protected long hitCount = 0;
	protected long numberOfOperation = 0;
	
	protected int capacity = 0;
	
	public Memory(int capacity) {
		this.capacity = capacity;
	}
	
	public synchronized void addCold(Tuple t) {
		coldList.add(t);
	}
	
	
	public abstract void request(char operation, Tuple t);
	public abstract void saveData();
	public abstract void remove(Tuple p) ;
	public abstract String getName();
	public abstract List<Tuple> getTuples();
	public abstract int getCurrentNumberOfPages();
	
	public String showStatics() {
		String s = "";
		s+="capacity: " + capacity;
		s+="\noperations: " + numberOfOperation;
		s+="\nmiss: " + missCount;
		s+="\nhit: " + hitCount;
		s+="\nsize: " + size + " bytes";
		return s;
	}
	
	public void saveCold() {
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh-mm");
		String date = dt.format(new Date());
		
		try {
			String name = this.getClass().getSimpleName();
			
			PrintWriter logRequests = new PrintWriter(new FileWriter(new File(date+ " "+ name +" coldlist.tuples") ,  true));
		
			
			Node<Tuple> node = coldList.getHead();
			while (node != null) {
			
				logRequests.println(node.getValue()+",O");
				node = node.getNext();
			}
			
			
			logRequests.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
	}
	
	public List<Tuple> getColdList() {
		return coldList;
	}


	public long getSize() {
		return size;
	}


	public void increase(int i) {
		synchronized (acessSize) {
			size+=i;
		}
	}
	
	public void decrease(int i) {
		synchronized (acessSize) {
			size-=i;
		}
	}


	public long getMissCount() {
		return missCount;
	}


	public long getHitCount() {
		return hitCount;
	}


	public long getNumberOfOperation() {
		return numberOfOperation;
	}


	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	
	
	
	
}
