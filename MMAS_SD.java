package Replication1;

import java.util.ArrayList;
import java.util.Random;

public class MMAS_SD {

	private int m; // still need to set
	private int Q;
	private ArrayList<Customer> customer;
	private double[][] distances;;
	private int M; // need to set still
	// private ArrayList<Vehicle> vehicles = new ArrayList<>();
	private ArrayList<Route> route_set = new ArrayList<>();
	private int IterNum = 10000;
	//private int IterNum = 2;
	private double q_0 = 0.9;
	private int alpha = 1;
	private int beta = 2;
	private double rho = 0.02;
	
	private ArrayList<Customer> Allowed_k = new ArrayList<>();
	private Customer depot;

	Route initial;

	int nc;
	double Q_left;
	int k; // ant index
	double f_opt;
	Route iteration_opt; // route of the iteration optimal ant
	double f_GB;
	Route global_opt; // route of the global optimal ant
	int veh_number;
	double tau_max;
	double tau_min;
	double[][] tau;

	private int n; // number of customers
	int next_j = 0;
	Customer next_cust_j;
	int no_change = 0;

	public MMAS_SD(int m, int Q, ArrayList<Customer> customer, double[][] distances, int M) {
		this.m = m;
		this.Q = Q;
		this.customer = customer;
		this.distances = distances;
		this.M = M;

		n = customer.size() - 1;
		// Initialisation
		nc = 0;
		Q_left = Q;
		k = 1; // first ant
		f_opt = 0;
		veh_number = 1;
		
		depot = this.customer.get(0).copyCustomer();
		ArrayList<Customer> route_construct = new ArrayList<>(); 
		route_construct.add(depot); // set the first route to start at the depot
				
		GreedyHeuristic greedy = new GreedyHeuristic(distances, customer, Q);
		initial = greedy.getRoute();
		Objective first_obj = new Objective(initial, distances, M, Q);
		f_GB = first_obj.getObjective(); // set global optimum equal to initial objective value
		tau = new double[customer.size()][customer.size()];
		tau_max = 1 / f_GB;
		tau_min = tau_max * (1 - Math.pow(0.05, 1 / n)) / ((n - 2) * Math.pow(0.05, 1 / n) / 2);
		global_opt = initial.copyRoute();

		for (int i = 0; i < customer.size(); i++) {
			for (int j = 0; j < customer.size(); j++) {
				tau[i][j] = tau_max;
			}
		}

		while (nc < IterNum) {
			performIteration(customer, Q_left, tau, route_set, route_construct);
		}
	}

	public int performIteration(ArrayList<Customer> customer, double Q_left, double[][] tau, ArrayList<Route> route_set, ArrayList<Customer> route_construct) { 
		ArrayList<Ant> colony = new ArrayList<>();

		for (int k = 1; k <= m; k++) { // set all ants at a random place
			Random rand = new Random();
			int random_place = rand.nextInt(n) + 1; // generate random place, which is not the depot
			Ant k_ = new Ant(random_place, random_place, 0); // put ant in random place
			colony.add(k_);
		}

		for (int k = 1; k <= m; k++) {
			for (int j = 1 ; j <customer.size(); j++) {
				customer.get(j).setFullDemand();
				Allowed_k.add(this.customer.get(j));
			}
			int first_cust_index = colony.get(k - 1).getCurrent();
			Customer first_cust = customer.get(first_cust_index);
			Allowed_k.remove(first_cust);
			route_construct.add(first_cust);
			Q_left = Q - first_cust.getDemand();
			
			while (Allowed_k.size() != 0) {
				int fit_k_size = 0;
				next_j = 0;
				
				//determine the set Fit_k;
				for (int j = 0; j < Allowed_k.size(); j++) {// exclude depot
					if (Allowed_k.get(j).getDemand() <= Q_left ) {
						fit_k_size ++;
					}
				}
				
				if (fit_k_size > 0) {
					Random q_rand = new Random();
					double q = q_rand.nextDouble();
					if (q <= q_0) {
						double next_j_func = 0;
						for (int f = 0; f < Allowed_k.size(); f++) {
							if (Allowed_k.get(f).getDemand() <= Q_left) {
								double eta = Allowed_k.get(f).getDemand() / distances[colony.get(k - 1).getCurrent()][Allowed_k.get(f).getIndex()-1];
								double func = Math.pow(tau[colony.get(k - 1).getCurrent()][Allowed_k.get(f).getIndex()-1], alpha)* Math.pow(eta, beta);
								if (func > next_j_func) {
									next_j_func = func;
									next_j = f;
								}
							}
						}
						next_cust_j = Allowed_k.get(next_j);
						route_construct.add(next_cust_j);
						Allowed_k.remove(next_j);
						Q_left = Q_left - next_cust_j.getDemand();
						colony.get(k - 1).changeCurrent(next_cust_j.getIndex()-1);
					} 
					else {
						double[][] prob = new double[fit_k_size][2];
						double[] num = new double [fit_k_size];
						int [] Allowed_index = new int [fit_k_size];
						double prob_denom = 0;
						double add = 0;
						int index_prob = 0;
						double eta = 0; //eta tot de macht beta
						for (int p = 0; p < Allowed_k.size(); p++) {
							if (Allowed_k.get(p).getDemand() <= Q_left) {
								eta = Math.pow((Allowed_k.get(p).getDemand()/ distances[colony.get(k - 1).getCurrent()][Allowed_k.get(p).getIndex()-1]), beta);
								add = Math.pow(tau[colony.get(k - 1).getCurrent()][Allowed_k.get(p).getIndex()-1], alpha)* eta;
								num[index_prob] = add;
								prob_denom = prob_denom + add;
								Allowed_index[index_prob] = p; //indicates the index in Allowed_k
								index_prob++;
							}
						}
						for (int j = 0; j < fit_k_size; j++) {
							prob[j][0] = num[j] / prob_denom;
							prob[j][1] = Allowed_index[j];
						}
						int index_prob_next_j = performRandomWheelSelection(prob);
						next_j = (int) prob[index_prob_next_j][1];
						route_construct.add(Allowed_k.get(next_j));
						Q_left = Q_left - Allowed_k.get(next_j).getDemand();
						colony.get(k - 1).changeCurrent(Allowed_k.get(next_j).getIndex()-1);
						Allowed_k.remove(Allowed_k.get(next_j));
					}
					if (Q_left == 0) {
						route_construct.add(depot); // add the depot if vehicle capacity is zero
						veh_number++;
						Q_left = Q;
					}
				} 
				else {// split delivery
					double argmin = Double.POSITIVE_INFINITY;
					for (int j = 0; j < Allowed_k.size(); j++) {	
						if (Allowed_k.get(j).getDemand() > Q_left) {
							double part1 = (Q_left + M) * distances[colony.get(k - 1).getCurrent()][Allowed_k.get(j).getIndex()-1];
							double part2 = M * distances[Allowed_k.get(j).getIndex()-1][0];
							double arg = part1 + part2;
							if (arg < argmin) {
								argmin = arg;
								next_j = j;
							}
						}
					}
					next_cust_j = Allowed_k.get(next_j);
					route_construct.add(next_cust_j);
					Allowed_k.get(next_j).changeDemand(Q_left);
					Q_left = Q;
					veh_number++;
					route_construct.add(depot); // route goes by the depot so add depot to route construct
					colony.get(k - 1).changeCurrent(0);
				}
			}
			for (int i = 0; i < customer.size(); i++) {
				customer.get(i).setFullDemand();
			}
			if (route_construct.get(route_construct.size()-1).getIndex() != 1) {
				route_construct.add(depot);
			}
			
			Route route_rk = new Route(route_construct, veh_number);
			Objective fk = new Objective(route_rk, distances, M, Q);
			double f_k = fk.getObjective();
			colony.get(k - 1).changeF(f_k);
			route_set.add(route_rk);

			
			route_construct.clear(); //start a new route for the next ant
			route_construct.add(depot); //the route starts at the depot.

		}

		f_opt = colony.get(0).getF();
		iteration_opt = route_set.get(k-1);
		for (int k = 1; k < colony.size(); k++) {
			if (colony.get(k).getF() < f_opt) {
				f_opt = colony.get(k).getF();
				iteration_opt = route_set.get(k);
			}
		}

		pheromoneUpdate(tau, iteration_opt, f_opt);
		nc++;
		
		
		if (f_GB > f_opt) {
			f_GB = f_opt;
			tau_max = 1 / f_GB;
			tau_min = tau_max * (1 - Math.pow(0.05, 1 / n)) / ((n - 2) * Math.pow(0.05, 1 / n) / 2);
			global_opt = iteration_opt.copyRoute();
		}
		
		route_set.clear();
		return nc;
	}


	 /* performs the random wheel selection
	 * 
	 * @param prob
	 * @return the index of the next customer j in prob.
	 */
	private int performRandomWheelSelection(double[][] prob) {
		double sum_prob = 0;
		if (prob.length == 1) {
			next_j = 0;
		}
		else {
			for (int i = 0; i < prob.length; i++) {
				sum_prob = sum_prob + prob[i][0];
			}
			double[][] rsw = new double[prob.length][2];
			rsw[0][0] = 0;
			rsw[0][1] = prob[0][0];
			for (int j = 1; j < prob.length; j++) {
				rsw[j][0] = rsw[j-1][1];
				rsw[j][1] = rsw[j][0] + prob[j][0];
			}
			Random random_rsw = new Random();
			double rsw_rand = sum_prob * random_rsw.nextDouble();
			for (int l = 0; l < prob.length; l++) {
				if (rsw_rand >= rsw[l][0] && rsw[l][1] > rsw_rand) {
					next_j = l;
					break;
				}
			}
		}
		return next_j;
	}

	private void pheromoneUpdate(double[][] tau, Route iteration_opt, double f_opt) {
		double delta_tau = 1 / f_opt; // can also be f_GB
		ArrayList<Customer> opt_Route = iteration_opt.copyRoute().getRoute();

		for (int i = 0; i < customer.size(); i++) { // evaporation of pheromone
			for (int j = 0; j < customer.size(); j++) {
				tau[i][j] = (1 - rho) * tau[i][j];
			}
		}

		for (int i = 0; i < opt_Route.size()-1; i++) { // add delta_tau if (i,j) belongs to iteration best tour
			int start_index = opt_Route.get(i).getIndex();
			int next_index = opt_Route.get(i + 1).getIndex();
			tau[start_index][next_index] = tau[start_index][next_index] + delta_tau;
		}

		for (int i = 0; i < customer.size(); i++) { // keep the pheromone trail between tau_min and tau_max
			for (int j = 0; j < customer.size(); j++) {
				if (tau[i][j] < tau_min) {
					tau[i][j] = tau_min;
				} 
				else if (tau[i][j] > tau_max) {
					tau[i][j] = tau_max;
				}
			}
		}
	}

	public double ObjectValue() {
		return f_GB;
	}

	public Route getOptRoute() {
		return global_opt.copyRoute();
	}
}
