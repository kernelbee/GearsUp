package com.kernelbee.gearsup;

import com.kernelbee.gearsup.MainActivity.IFCallback;

public class GearsFinderUIExt extends GearsFinder{
	
	private static int progress_prev = -1;
	static public IFCallback ifcallback;

	public static int arrange_gears(int start, int finish, int type, int[] set, GearTrain[] selected, double ratio){
		
		int n_found = 0;
						
		if(selected.length > 0){
			
			int n_max = selected.length;
			int[] found_gears = new int[n_max*type];			
			double[] found_errors = new double[n_max];			
			int[] curr_gears;									
			int worst = -1;//any negative value
			
			GearTrainGenerator gtg = new GearTrainGenerator(type,start,finish,set);
			
			while( (curr_gears = gtg.generateNext()) != null ) {
				
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
					worst = worst_geartrain(found_errors,n_found);					
				}
				
                // set progress by last thread progress
                if(finish == set.length-1){
                        int sa = gtg.getProgress();
                        if (progress_prev != sa) {
                                progress_prev = sa;
                                ifcallback.callback((int) (((double) (sa-start) / (double) (finish-start)) * 100));
                        }
                }			
				
			}//while loop
			
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
