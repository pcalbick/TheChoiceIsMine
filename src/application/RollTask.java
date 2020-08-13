//Returns pick percentage of each items
//Last item in array is highest percentage and sooner in list's index
//On return, must be cast to Integer

package application;

import java.util.Random;
import java.util.concurrent.Callable;

public class RollTask {
	public int init(int high) {
		Random random = new Random();
		int max = random.nextInt(high);
		return random.nextInt(max+1);
	}
	
	public Callable<double[]> roll(int rolls, int size){
		return new Callable<double[]>() {
			public double[] call() {
				Random random = new Random();
				int[] poll = new int[size];
				
				for(int i=0; i<rolls; i++){
					int a = random.nextInt(size);
					poll[a]++;
				}
				
				int index = 0;
				int max = poll[0];
				double[] response = new double[poll.length+1];
				for(int i=0; i<poll.length; i++) {
					response[i] = poll[i];
					if(poll[i] > max) {
						max = poll[i];
						index = i;
					}
				}
				
				for(int i=0; i<poll.length; i++)
					response[i] = ((double)poll[i]/rolls)*100;

				response[response.length-1] = index;
				return response;
			}
		};
	}
}
