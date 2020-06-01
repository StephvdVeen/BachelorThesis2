package Replication1;

import java.util.Random;

public class RandomWheelSelection {
	private double[] prob;
	
	double sum_prob = 0;
	private double[][] rsw;
	int next_j;
	
	public RandomWheelSelection(double[] prob) {
		for (int i = 0; i<prob.length-1; i++) {
			sum_prob = sum_prob + prob[i];
		}
		System.out.println(sum_prob);
		double[][] rsw = new double[prob.length][2];
		rsw[0][0] = 0;
		rsw[0][1] = prob[0];
		for (int j = 1; j < prob.length - 1; j++) {
			rsw[j][0] = prob[j - 1];
			rsw[j][1] = prob[j] + prob[j - 1];
			System.out.println(rsw[j][0] + " - " + rsw[j][1]);
		}
		Random random_rsw = new Random();
		double rsw_rand = sum_prob * random_rsw.nextDouble();
		for (int l = 0; l < prob.length; l++) {
			if (rsw_rand >= rsw[l][0] && rsw[l][1] > rsw_rand) {
				next_j = l;
			}
		}
	}
	
	public int getNext_j() {
		return next_j;
	}

}
