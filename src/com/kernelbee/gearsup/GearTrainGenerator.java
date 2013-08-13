package com.kernelbee.gearsup;

public class GearTrainGenerator {

	private final int finish;
	
	private final int type;
	private final int[] gears;
	private final int[] set;
	private final int n_gears;
	
	private double ratio;
	private int sa, sb, sc, sd;
			
	public GearTrainGenerator(int type,int start,int finish,int[] set){
		
		this.finish = finish;
		
		this.type = type;
		this.gears = new int[type];		
		this.set = set;
		this.n_gears = set.length;
		
		this.sa = start;		
	}
	
	public int getProgress(){
		return sa;
	}
	
	public double getCurrRatio(){
		return ratio;
	}
	
	public static double calcRatio(int[] gears){
		double r;
		
		switch(gears.length){
		
		case 2:
			r = (double) gears[0] / (double) gears[1];
			break;
			
		case 4:
			double ratio_ab = (double) gears[0] / (double) gears[1];
			double ratio_cd = (double) gears[2] / (double) gears[3];
			r = ratio_ab * ratio_cd;
			break;
			
		default: r = 0.0;
		}
		
		return r;
	}
	
	public int[] generateNext(){
						
		switch(type){
				
		case 2:
			for(int ia = sa; ia <= finish; ia++) {

				for(int ib = sb; ib < n_gears; ib++) {
					if( ib == ia )
						continue;
					
					gears[0] = set[ia];
					gears[1] = set[ib];

					if(isValid()==false)
						continue;
					
					//save iterators state
					sa = ia;
					sb = ++ib;//skip done case

					return gears;
				}
				sb = 0;
			}
			sa = 0;
			break;
			
		case 4:
			for(int ia = sa; ia <= finish; ia++) {

				for(int ib = sb; ib < n_gears; ib++) {
					if( ib == ia )
						continue;

					for(int ic = sc; ic < n_gears; ic++) {
						if( ic == ia || ic == ib )
							continue;

						for(int id = sd; id < n_gears; id++) {

							if( id == ia || id == ib || id == ic )
								continue;
						
							gears[0] = set[ia];
							gears[1] = set[ib];
							gears[2] = set[ic];
							gears[3] = set[id];

							if(isValid()==false)
								continue;
							
							//save iterators state
							sa = ia;
							sb = ib;
							sc = ic;
							sd = ++id;//skip done case

							return gears;
						}
						sd = 0;
					}
					sc = 0;
				}
				sb = 0;
			}
			sa = 0;
			break;		
		}		
		return null;
	}	
	
	public boolean isValid(){
		
		switch(type){
		
		case 2:
		{
			int a, b;
			double ratio_ab;

			a = gears[0];
			b = gears[1];
		
			// if(a <= 0 || b <= 0 )
			// return false;

			ratio_ab = (double) a / (double) b;

			if (ratio_ab < 1.0 / 5.0 || ratio_ab > 2.8)
				return false;

			ratio = ratio_ab;
		}
		break;
			
		case 4:
		{
			int a,b,c,d;
			double ratio_ab;
			double ratio_cd;

			a = gears[0];
			b = gears[1];
			c = gears[2];
			d = gears[3];

			//if(a <= 0 || b <= 0 || c <= 0 || d <= 0)
			//	return false;

			if( (a+b <= c+15) || (c+d <= b+15) )
				return false;

			ratio_ab = (double)a/(double)b;
			ratio_cd = (double)c/(double)d;

			if(ratio_ab < 1.0/5.0 || ratio_cd < 1.0/5.0)
				return false;

			if(ratio_ab > 2.8 || ratio_cd > 2.8)
				return false;

			ratio = ratio_ab * ratio_cd;
		}
		break;
			
		default:
				return false;
		}
		return true;
	}
}
