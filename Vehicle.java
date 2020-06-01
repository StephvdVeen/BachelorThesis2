package Replication1;

public class Vehicle {
	
	private final int M; //weight of the vehicle
	private final int Q; //capacity of an empty vehicle
	private double Q_left; //leftover capacity
	
	public Vehicle(int M, int Q) {
		this.M = M;
		this.Q = Q;
		Q_left = Q;
	}
	
	public void UpdateCapacity(double demand_left) {
		Q_left = Math.max(0,Q_left - demand_left);
	}
	
	public Double getQ_left(){
		return Q_left;
	}

}
