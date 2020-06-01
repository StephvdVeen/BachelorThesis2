package Replication1;

import java.util.ArrayList;

public class Route {
	
	private int number_customers; //number of customers on the route
	private int veh_number; //
	private ArrayList<Customer> route_construct;
	private boolean finished; //indicates true the route is fully constructed, false otherwise
	
	public Route(ArrayList<Customer> route_construct, int veh_number) {
		this.route_construct = route_construct;
		this.veh_number = veh_number;	
	}
	
	public void updateVehNumber() {
		veh_number++;
	}
	public Route copyRoute() {
		return new Route(route_construct, veh_number);
	}
	
	public int getLength() {
		return route_construct.size();
	}
	
	public ArrayList<Customer> getRoute(){
		return route_construct;
	}
	
	public int getVeh() {
		return veh_number;
	}

}
