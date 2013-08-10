package com.kernelbee.gearsup;
public class GearTrain{
	
	public int gears[];
	public double ratio;
	public double error;
	
	public GearTrain(GearTrain other){
		
		this.gears = new int[other.gears.length];
		System.arraycopy(other.gears, 0, this.gears, 0, other.gears.length);
		
		this.ratio = other.ratio;
		this.error = other.error;
	}
	
	public GearTrain(int type) {
		this.gears = new int[type];
	}

	@Override
	public String toString() {
		
		String s = "";
		
		switch(gears.length){
		
		case 2:
			s += String.format("{%d\t%d}\t\t%.14f\t%e", 
					gears[0],gears[1],ratio,error);
			break;
		case 4:
			s += String.format("{%d\t%d\t%d\t%d}\t\t%.14f\t%e", 
					gears[0],gears[1],gears[2],gears[3],ratio,error);
			break;
		}
		
		return s;		
	}		
}	
