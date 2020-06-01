package Replication1;

public class Ant {
	private int start;
	private int current;
	private double f_k;
	
	public Ant(int start, int current, double f_k) {
		this.start = start;
		this.current = current;
		this.f_k = f_k;
	}
	
	public int getCurrent() { //indicates the index of the customer where the ant is currently at, corresponds to .getIndex of 
		return current;
	}
	
	public void changeCurrent( int next_j) {
		current = next_j;
	}
	
	public double getF() {
		return f_k;
	}
	
	public void changeF(double f) {
		f_k = f;
	}
	
}
