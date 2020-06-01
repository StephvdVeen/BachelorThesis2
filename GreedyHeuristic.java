package Replication1;

import java.util.ArrayList;
import java.util.Random;

public class GreedyHeuristic {
	//private final double[][] distances;
	//private ArrayList<Customer> customers = new ArrayList<>();
	//private double Q;
	private Customer depot;
	
	int veh_number;
	double Q_left;
	
	ArrayList<Customer> initial_route = new ArrayList<>();
	Route initial;
	
	public GreedyHeuristic(double[][] distances, ArrayList<Customer> customers, double Q) {
		//this.distances = distances;
		//this.customers = customers;
		//this.Q = Q;
		
		depot = customers.get(0).copyCustomer();
		initial_route.add(depot);
		
		veh_number = 1;
		double Q_left = Q;
		
		Random rand = new Random();
		int start = rand.nextInt(customers.size()-1) +1;
		initial_route.add(customers.get(start));
		
		//Assume all demand lower than vehicle capacity.
		Q_left = Q_left - customers.get(start).getDemand();
		customers.get(start).SatisfyDemand();
		int cust_satisfied = 1;
		
		int current = start;
		int next;
		while (cust_satisfied != customers.size()-1) {
			double dist_min = Double.POSITIVE_INFINITY;
			next = -1;
			for (int j = 1; j < customers.size(); j++) {
				if (customers.get(j).getDemand() != 0) {
					if (dist_min > distances[current][j]) {
						next = j;
						dist_min = distances[current][j];
					}
				}
			}
			current = next;
			initial_route.add(customers.get(next));
			if (customers.get(next).getDemand() <= Q_left) {
				cust_satisfied++;
				Q_left = Q_left - customers.get(next).getDemand();
				customers.get(next).SatisfyDemand();
				if (Q_left == 0) {
					initial_route.add(depot);
					veh_number++;
					current = 0;
					Q_left = Q;
				}
			}
			else {
				customers.get(next).changeDemand(Q_left);
				initial_route.add(depot);
				Q_left = Q;
				veh_number++;
				current = 0;
			}
		}
		int route_size = initial_route.size();
		if (initial_route.get(route_size-1).getIndex() != 1 ) {
			initial_route.add(depot); //always end at the depot
		}
		initial = new Route(initial_route, veh_number);
		
		for (int i = 0; i < customers.size(); i++) {
			customers.get(i).setFullDemand();
		}
		
	}
	
	public ArrayList<Customer> getInitialRoute() {
		return initial_route;
	}
	
	public int getVeh() {
		return veh_number;
	}
	
	public Route getRoute() {
		return initial;
	}

}
