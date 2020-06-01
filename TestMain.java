dpackage Replication1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class TestMain {
	
	public static int cust_size; //the total number of customers + depot in this instance
	public static int M = 70; //weight of a vehicle
	public static int m; //total number of vehicles
	public static int Q; //vehicle capacity
	
	public static void main(String [] args) throws FileNotFoundException{
		File instance_customers = new File("C:\\Users\\Gebruiker\\Documents\\Thesis\\Data\\P1.txt");
		File instance_distances = new File("Distances_P1.txt");
		
		ArrayList<Customer> customer = readcustomers(instance_customers);
		m = cust_size -1;
		
		double[][] distances = readdistances(instance_distances);
		
		GreedyHeuristic heur = new GreedyHeuristic(distances, customer, Q);
		
		Objective f = new Objective(heur.getRoute(), distances, M,Q);
		System.out.println(f.getObjective());
		
		
	}
	
	/**
	 * Method that puts all customers in a list
	 * @param file is the file containing all the information
	 * @return the list containing all the customers
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Customer> readcustomers(File file) throws FileNotFoundException{
		try (Scanner scanning = new Scanner(file)){
			
			ArrayList<Customer> found = new ArrayList<Customer>();
			
			boolean firstline = true;
			
			while (scanning.hasNext()) {
				if (firstline == true) {
					cust_size = scanning.nextInt();
					Q = scanning.nextInt();	
					firstline = false;
				}
				else {
				int index = scanning.nextInt();
				double x_coor = scanning.nextDouble();
				double y_coor = scanning.nextDouble();
				double demand_full = scanning.nextDouble();
				
				Customer customer = new Customer(index, x_coor, y_coor, demand_full);
				found.add(customer);
				}
				scanning.nextLine();
				
			}
			scanning.close();
			return found;
		}
	}
	
	/**
	 * Method that puts all distances in a matrix
	 * @param file is the file containing all the information
	 * @return the matrix of doubles containing all distances
	 * @throws FileNotFoundException
	 */
	public static double[][] readdistances(File file) throws FileNotFoundException{
		try (Scanner scanning = new Scanner(file)){
			
			double [][] dist = new double[cust_size][cust_size];
			
			int i = 0;
			int j = 0;
				
			while (scanning.hasNext()) {
				
				dist[i][j] = scanning.nextDouble();
				if (j == (cust_size-1)) {
					i++;
					j= 0;
					scanning.nextLine();
				}
				else {
					j++;
				}
				scanning.nextLine();
				
			}
			scanning.close();
			return dist;
		}
	}

}
