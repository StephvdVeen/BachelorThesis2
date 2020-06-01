package Replication1;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Distances {
	
	public static int M = 20; //weight of a vehicle
	public static int m; //total number of vehicles
	public static int veh_cap; //vehicle capacity
	
	
	public static void main(String [] args) throws FileNotFoundException{
		
		File instance = new File("C:\\Users\\Gebruiker\\Documents\\Thesis\\Data\\P_test.txt");
		
		
		ArrayList<Customer> customer = read(instance);
		
		Double [][] distances = new Double[customer.size()][customer.size()];
		
		for (int i = 0; i < customer.size(); i++) {
			for (int j = 0; j < customer.size(); j++) {
				double x_dif = customer.get(j).getX() - customer.get(i).getX();
				double y_dif = customer.get(j).getY() - customer.get(i).getY();
				distances[i][j] = Math.sqrt(Math.pow(x_dif,2) + Math.pow(y_dif, 2));
				System.out.print(distances[i][j]);
				System.out.println();
			}
			System.out.println();
		}
	}
	
	/**
	 * Method that puts all customers in a list
	 * @param file is the file containing all the information
	 * @return the list containing all the customers
	 * @throws FileNotFoundException
	 */
	public static ArrayList<Customer> read(File file) throws FileNotFoundException{
		try (Scanner scanning = new Scanner(file)){
			
			ArrayList<Customer> found = new ArrayList<Customer>();
			
			boolean firstline = true;
			
			while (scanning.hasNext()) {
				if (firstline == true) {
					m = scanning.nextInt();
					veh_cap = scanning.nextInt();	
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

}
