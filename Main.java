package Replication1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	
	public static int cust_size; //the total number of customers + depot in this instance
	public static int M; //weight of a vehicle
	public static int m; //total number of vehicles
	public static int Q; //vehicle capacity
	public static double Lambda;
	
	static Route opt;
	
	public static void main(String [] args) throws FileNotFoundException{
		File instance_customers = new File("C:\\Users\\Gebruiker\\Documents\\Thesis\\Data\\P11.txt");
		File instance_distances = new File("Distances_P11.txt");
		
		ArrayList<Customer> customer = readcustomers(instance_customers);
		m = cust_size -1;
		//m = m/2;
		
		M = (int) (Q/Lambda);
						
		double[][] distances = readdistances(instance_distances);
		
		for (int i = 0; i < 10; i++) { //perform every run 10 times
			long start = System.currentTimeMillis();
			MMAS_SD mmas_sd = new MMAS_SD(m,Q, customer,distances,M);
			System.out.println("mmas-SD " + mmas_sd.ObjectValue());
			long end = System.currentTimeMillis();
			long time = end - start;
			System.out.println("time " + time);			
		}
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
					Lambda = scanning.nextDouble();
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
