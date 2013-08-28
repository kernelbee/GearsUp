package com.kernelbee.gearsup;

public class GearsFinderThread implements Runnable {
	
	protected int start;
	protected int finish;
	protected int type;
	protected int[] set;
	protected GearTrain[] selected;
	protected double ratio;	
	
	public int n_found;
	
	public GearsFinderThread(			 
			int start, int finish, int type, int[] set, GearTrain[] selected, double ratio){
		
		this.start = start;
		this.finish = finish;
		this.type = type;
		this.set = set;
		this.selected = selected;
		this.ratio = ratio;
		this.n_found = 0;		
	}
	
	@Override
	public void run() {		
		n_found = GearsFinder.arrangeGears(start, finish, type, set, selected, ratio);
	}
}
