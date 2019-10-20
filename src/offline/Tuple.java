package offline;
import java.io.Serializable;

import util.Node;






public class Tuple implements Serializable{

	private static final long serialVersionUID = 1L;
	private String data[];
	private String tupleID;
	private Node<Tuple> node;
	public boolean isRecovered = false;
	
	public static final char READ = 'R';
	public static final char WRITE = 'W';
	private char operation = READ;
	
	public boolean isUsed = false;
	public long transactionId = -1;
	
	public Tuple(String tupleId, String... data) {
		this.data = data;
		this.tupleID = tupleId;
	}


	public String[] getData() {
		return data;
	}
	
	public synchronized void setData(String[] data) {

			this.data = data;			
		
	}
	

	
	public String getTupleID() {
		return tupleID;
	}
	
	
	public void setTupleID(String tupleID) {
		this.tupleID = tupleID;
	}

	
	public synchronized int size() {
		int size = 0;
		for (String d : data) {
			size+=d.getBytes().length;
		}
		return size;
	}
	
	
	
	
	
	
	
	public Tuple copy() {
		String[] copyD = new String[data.length]; 
		
		for (int i = 0; i < data.length; i++) {
			copyD[i] = data[i].trim();
		}
		
		Tuple t = new  Tuple(tupleID, data);
		t.setOperation(operation);
		
		return t;
	}
	
	@Override
	public String toString() {
		return tupleID;
	}

	public String getColunmData(int idAtribute){
		return data[idAtribute];
	}


	public Node<Tuple> getNode() {
		return node;
	}


	public void setNode(Node<Tuple> node) {
		this.node = node;
	}


	public char getOperation() {
		return operation;
	}


	public void setOperation(char operation) {
		this.operation = operation;
	}
	

	
}

