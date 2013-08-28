package com.kernelbee.gearsup;
import java.util.Arrays;
import java.util.Comparator;

import com.kernelbee.gearsup.MainActivity.IFCallback;

public class GearsFinder {
	
	public static int given_gear_sets[][] = {
		//#0	lathe
	{20,24,25,28,30,32,36,40,44,45,48,50,55,60,70,80,90,95,100,110,113,120,127},
		//#1	milling
	{20,25,30,40,50,60,70,80,90,100},
		//#2	zatylovochny
	{20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,
	46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,
	72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,
	97,98,99,100,105,108,110,112,113,120,127},
		//#3	gear making
	{20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,35,36,37,38,39,40,41,42,43,44,45,
	46,47,48,49,50,51,52,53,54,55,56,57,58,59,60,61,62,63,64,65,66,67,68,69,70,71,
	72,73,74,75,76,77,78,79,80,81,82,83,84,85,86,87,88,89,90,91,92,93,94,95,96,
	97,98,99,100,105,110,113,115,120,127},
	{}
	};		
		
	public static long knPermutations(int n, int k)
	{
		long t;
		int x;

		if(n - k > 0)
			t = n - k + 1;
		else
			t = 1;

		x = (int) t;
		while( x < n )	t *= ++x;

		return t;// = n! / (n-k)!
	}

	protected static Comparator<GearTrain> compare_by_error;
	
	static {
		compare_by_error = new Comparator<GearTrain>(){

			@Override
			public int compare(GearTrain o1, GearTrain o2) {
				
				if( o1.error < o2.error)
					return -1;
				
				if(o1.error > o2.error)
					return 1;
				
				return 0;
			}		
		};
	}

	protected static int worstGeartrain(double[] errors,int n_actually){
		int worst = 0;
				
		for(int i = 1; i < n_actually; i++)
			if(errors[worst] < errors[i])
				worst = i;
	
		return worst;
	}	
	
	public static int prepareGeartrainSetups
		(int cores,int type, int[] set,GearTrain[] selected, double ratio, boolean sort) {		
				
		if(cores < 1)//Brother, no CPU - no work! #:0)
			return 0;
		
		int n_found = 0;
		
		int start = 0,finish = 0;
		int step = set.length / cores;
		
		//prepare and run threads (one per core)
		
		Thread[] threads = new Thread[cores];
		GearsFinderThread[] runners = new GearsFinderThread[cores];
		GearTrain[][] found = new GearTrain[cores][selected.length];
		
		for (int i = 0; i < threads.length; i++) {
			
			finish = ( (finish + step) < set.length ) ? (finish + step): (set.length-1);				
			
			runners[i] = new GearsFinderThread(start, finish, type, set, found[i], ratio);
			threads[i] = new Thread(runners[i]);			
			threads[i].start();
			
			start = finish + 1;
		}		
		
		//wait for all threads to be done
		
		for (int i = 0; i < threads.length; i++) {
		    try {
		        threads[i].join();
		    } catch (InterruptedException e) {
		    	//TODO ?
		    }
		}
		
		//compose results of all threads
		
		n_found = 0;			
		for (int i = 0; i < threads.length; i++) {
			n_found += runners[i].n_found;
		}
		
		GearTrain[] total = new GearTrain[n_found];
		
		n_found = 0;
		for (int i = 0; i < threads.length; i++) {			
			System.arraycopy(found[i], 0, total, n_found, runners[i].n_found);
			n_found += runners[i].n_found;
		}		
								
		//prepare final result in one output array
		
		n_found = (n_found >= selected.length) ? selected.length:n_found;			
		if(n_found > 0){		
			Arrays.sort(total, compare_by_error);			
			System.arraycopy(total, 0, selected, 0, n_found);
		}
		
		return n_found;
	}
	
	static volatile boolean stopit = false;
	
	public static void cancelSearching(boolean cancel){
		stopit = cancel;
	}
	
	private static int progress_prev = -1;
	public static volatile IFCallback ifcallback;
	
	public static int arrangeGears(int start, int finish, int type, int[] set, GearTrain[] selected, double ratio){
		
		int n_found = 0;
						
		if(selected.length > 0){
			
			int n_max = selected.length;
			int[] found_gears = new int[n_max*type];			
			double[] found_errors = new double[n_max];			
			int[] curr_gears;									
			int worst = -1;//any negative value
			
			GearTrainGenerator gtg = new GearTrainGenerator(type,start,finish,set);
			
			while( stopit==false && (curr_gears = gtg.generateNext()) != null) {
				
				boolean update_worst = false;
				double curr_error = Math.abs(gtg.getCurrRatio() - ratio);
												
				if(n_found == n_max){
					if(curr_error < found_errors[worst]) {
						//replace the most worst setup by new setup
						System.arraycopy(curr_gears, 0, found_gears, worst*type, type);						
						found_errors[worst] = curr_error;
						update_worst = true;
					}					
				}else{
					//add new setup at the end
					System.arraycopy(curr_gears, 0, found_gears, n_found*type, type);					
					found_errors[n_found] = curr_error;
					
					if(worst >= 0){
						if(curr_error > found_errors[worst])
							worst = n_found;
					}else{						
						worst = 0;
					}
					n_found++;
				}
				
				if(update_worst){
					worst = worstGeartrain(found_errors,n_found);					
				}
				
                // set progress by last thread progress
                if(finish == set.length-1){
                        int sa = gtg.getProgress();
                        if (progress_prev != sa) {
                                progress_prev = sa;
                                //if(ifcallback != null){
                                	ifcallback.updateProgress((int) (((double) (sa-start) / (double) (finish-start)) * 100));	
                                //}                                
                        }
                }                
                				
			}//while loop
			
			if(stopit){
				n_found = 0;
			}
			
			//pack results into output buffer
			for(int i = 0; i < n_found; i++){
				selected[i] = new GearTrain(type);
				System.arraycopy(found_gears, i*type, selected[i].gears, 0, type);				
				selected[i].ratio = GearTrainGenerator.calcRatio(selected[i].gears);
				selected[i].error = found_errors[i];
			}
		}
		
		return n_found;
	}
}
