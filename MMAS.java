package Replication1;

import java.util.ArrayList;
import java.util.Random;

public class MMAS {

	private int m; // still need to set
	private int Q;
	private ArrayList<Customer> customer;
	private double[][] distances;;
	private int M; // need to set still
	// private ArrayList<Vehicle> vehicles = new ArrayList<>();
	private ArrayList<Route> route_set = new ArrayList<>();
	private int IterNum = 10000;
	private double q_0 = 0.9;
	private int alpha = 1;
	private int beta = 5;
	private double rho = 0.02;

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
	
	double total_distance;

	public MMAS(int m, int Q, ArrayList<Customer> customer, double[][] distances, int M) {
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
		
		ArrayList<Customer> route_construct = new ArrayList<>(); 
		route_construct.add(customer.get(0).copyCustomer()); // set the first route to start at the depot

		GreedyHeuristic greedy = new GreedyHeuristic(distances, customer, Q);
		initial = greedy.getRoute();
		Objective first_obj = new Objective(initial, distances, M, Q);
		f_GB = first_obj.getObjective(); // set global optimum equal to initial objective value
		//double[][] tau = new double[customer.size()][customer.size()];
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
			for (int j = 0; j < customer.size(); j++) {
				customer.get(j).setFullDemand();
			}
			performIteration(customer, Q_left, tau, route_set, route_construct);
		}
	}

	public int performIteration(ArrayList<Customer> customer, double Q_left, double[][] tau, ArrayList<Route> route_set, ArrayList<Customer> route_construct) { 
		ArrayList<Ant> colony = new ArrayList<>();

		for (int k = 1; k < m; k++) { // set all ants at a random place
			Random rand = new Random();
			int random_place = rand.nextInt(n) + 1; // generate random place
			Ant k_ = new Ant(random_place, random_place, 0); // put ant in random place
			colony.add(k_);
		}

		for (int k = 1; k < m; k++) {
			int first_cust_index = colony.get(k - 1).getCurrent();
			Customer first_cust = customer.get(first_cust_index).copyCustomer();
			route_construct.add(first_cust);
			Q_left = Q - first_cust.getDemand();
			customer.get(first_cust_index).SatisfyDemand();

			int cust_satisfied = 1; // indicates the number of customers for whom demand is fully satisfied

			while (cust_satisfied != customer.size() - 1) {
				int fit_k = 0;
				next_j = 0;

				for (int j = 1; j < customer.size(); j++) {// exclude depot
					if (customer.get(j).getDemand() <= Q_left && customer.get(j).getDemand() > 0) {
						fit_k++;
					}
				}
				if (fit_k > 0) {
					Random q_rand = new Random();
					double q = q_rand.nextDouble();
					if (q <= q_0) {
						double next_j_func = 0;
						for (int f = 1; f < customer.size(); f++) {
							if (customer.get(f).getDemand() <= Q_left && customer.get(f).getDemand() > 0) { //choose from allowed
								//double eta = customer.get(f).getDemand() / distances[colony.get(k - 1).getCurrent()][f];
								double eta = 1/distances[colony.get(k-1).getCurrent()][f];
								double func = Math.pow(tau[colony.get(k - 1).getCurrent()][f], alpha)
										* Math.pow(eta, beta);
								if (func > next_j_func) {
									next_j_func = func;
									next_j = f;
								}
							}
						}
						route_construct.add(customer.get(next_j).copyCustomer());
						Q_left = Q_left - customer.get(next_j).getDemand();
						customer.get(next_j).SatisfyDemand();
						colony.get(k - 1).changeCurrent(next_j);
						cust_satisfied++;
					} else {
						double[][] prob = new double[fit_k][2];
						int prob_index = 0;
						double prob_denom = 0;
						for (int p = 1; p < customer.size(); p++) {
							if (customer.get(p).getDemand() <= Q_left && customer.get(p).getDemand() > 0) { //sum over feasible set of customers
								//prob_denom = prob_denom + Math.pow(tau[colony.get(k - 1).getCurrent()][p], alpha)
										//* Math.pow((customer.get(p).getDemand()
											//	/ distances[colony.get(k - 1).getCurrent()][p]), beta);
								  prob_denom = prob_denom + Math.pow(tau[colony.get(k - 1).getCurrent()][p], alpha)
										* Math.pow((1/ distances[colony.get(k - 1).getCurrent()][p]), beta);
							}
						}
						for (int j = 1; j < customer.size(); j++) {
							double prob_num = 0;
							if (customer.get(j).getDemand() > 0 && customer.get(j).getDemand() <= Q_left) {
								//prob_num = Math.pow(tau[colony.get(k - 1).getCurrent()][j], alpha) * Math.pow(
								//		(customer.get(j).getDemand() / distances[colony.get(k - 1).getCurrent()][j]),
								//		beta);
								prob_num = Math.pow(tau[colony.get(k - 1).getCurrent()][j], alpha) * Math.pow(
										(1 / distances[colony.get(k - 1).getCurrent()][j]),
										beta);
								prob[prob_index][0] = prob_num / prob_denom;
								prob[prob_index][1] = j;
								prob_index++;
							}
						}
						int index_prob_next_j = performRandomWheelSelection(prob);
						next_j = (int) prob[index_prob_next_j][1];
						route_construct.add(customer.get(next_j).copyCustomer());
						Q_left = Q_left - customer.get(next_j).getDemand();
						customer.get(next_j).SatisfyDemand();
						colony.get(k - 1).changeCurrent(next_j);
						cust_satisfied++;
					}
					if (Q_left == 0) {
						route_construct.add(customer.get(0).copyCustomer()); // add the depot if vehicle capacity is zero
						veh_number++;
						Q_left = Q;
					}
				} else {// split delivery
					double argmin = Double.POSITIVE_INFINITY;
					for (int j = 1; j < customer.size(); j++) {
						if (customer.get(j).getDemand() > Q_left) {
							double part1 = (Q_left + M) * distances[colony.get(k - 1).getCurrent()][j];
							double part2 = M * distances[j][0];
							double arg = part1 + part2;
							if (arg < argmin) {
								argmin = arg;
								next_j = j;
							}
						}
					}
					route_construct.add(customer.get(next_j).copyCustomer());
					customer.get(next_j).changeDemand(Q_left);
					Q_left = Q;
					veh_number++;
					route_construct.add(customer.get(0).copyCustomer()); // route goes by the depot so add depot to route construct
					colony.get(k - 1).changeCurrent(0);
				}
			}
			Route route_rk = new Route(route_construct, veh_number);
			Objective fk = new Objective(route_rk, distances, M, Q);
			double f_k = fk.getObjective();
			colony.get(k - 1).changeF(f_k);
			route_set.add(route_rk.copyRoute());

			for (int i = 0; i < customer.size(); i++) {
				customer.get(i).setFullDemand();
			}

			route_construct.clear(); //start a new route for the next ant
			route_construct.add(customer.get(0).copyCustomer()); //the route starts at the depot.

		}

		f_opt = colony.get(0).getF();
		iteration_opt = route_set.get(k-1).copyRoute();
		for (int k = 1; k < colony.size(); k++) {
			if (colony.get(k).getF() < f_opt) {
				f_opt = colony.get(k).getF();
				iteration_opt = route_set.get(k).copyRoute();
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
		return global_opt;
	}
}
