package Replication1;

public class Customer {
	
	private final int index; //indicates the index number in the customer list
	private final double x_coor;
	private final double y_coor;
	private final double demand_full; //gives the original demand of the customer
	
	double demand_left; //the demand that still needs to be satisfied, can be demand_full, 0, or some other number if split demand
	
	//private double fits; is true if demand_left is smaller than or equal to Q_left
	
	public Customer(int index, double x_coor, double y_coor, double demand_full){
		this.x_coor = x_coor;
		this.y_coor = y_coor;
		this.demand_full = demand_full;
		this.index = index;
		demand_left = demand_full;
	}
	
	public double getDemand() { //returns the demand still to be satisfied
		return demand_left;
	}
	
	public void changeDemand(double demand) {
		demand_left = demand_left - demand;
	}
	public void SatisfyDemand() {
		demand_left = 0;
	}
	public int getIndex() { //returns the customers index
		return index;
	}
	
	public double getX() {
		return x_coor;
	}
	
	public double getY() {
		return y_coor;
	}
	
	public Customer copyCustomer() {
		return new Customer(index, x_coor, y_coor, demand_full);
	}
	
	public void setFullDemand() {
		demand_left = demand_full;
	}
	
	public double getFullDemand() {
		return demand_full;
	}

}
