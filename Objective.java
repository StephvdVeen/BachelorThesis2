package Replication1;

import java.util.ArrayList;

public class Objective {
	private ArrayList<Customer> customers;
	private double [][] distances;
	private ArrayList<Customer> initial_cust;
	private int veh_initial;
	private Route initial;
	private double Q;
	
	double[][] y;
	double f;
	double M;
	double Q_left;
	
	public Objective(Route initial, double[][] distances, double M, double Q) {
		this.initial = initial;
		this.distances = distances;
		this.M = M;
		this.Q = Q;
		
		Q_left = this.Q;
		
		initial_cust = initial.getRoute();
		//veh_initial = initial.getVeh();
		
		y = new double [initial_cust.size()-1][2];
		double demand = 0;
		for (int i = 1; i < initial_cust.size(); i++) {
			demand = initial_cust.get(i).getDemand();
			if (demand <= Q_left) {
				y[i-1][0] = demand;
				Q_left = Q_left - demand;
				if (Q_left == 0) {
					Q_left = this.Q;
				}
			}
			else {
				y[i-1][0] = Q_left;
				initial_cust.get(i).changeDemand(Q_left);
				Q_left = this.Q;
			}
		}
		
		int start = 0;
		double weight = M;
		for (int i = 0; i < initial_cust.size() -1; i++) {
			if (y[i][0] != 0) {
				weight = weight + y[i][0];	
			}
			else {
				for (int j = start; j <= i; j++) {
					y[j][1] = weight;
					weight = weight - y[j][0];
				}
				start = i+1;
				weight = M;
			}
		}
		
		int from;
		int to;
		for (int i = 0 ; i < initial_cust.size()-1 ; i++) {
			from = initial_cust.get(i).getIndex() -1;
			to = initial_cust.get(i+1).getIndex() -1;
			f = f + distances[from][to]*y[i][1];
		}
	}
	
	public double getObjective() {
		return f;
	}

}
