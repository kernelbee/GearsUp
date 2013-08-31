package com.kernelbee.gearsup;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferences {
	
	public static final String PREFS_FILE_NAME = "Prefs";
	public static final int PREFS_FILE_MODE = Context.MODE_PRIVATE;
	
	public static final int PREFS_DEFAULT_TR_TYPE = 2;
	public static final double PREFS_DEFAULT_TR_RATIO = 1.0;
	public static final int PREFS_DEFAULT_SET = 2;
	public static final int PREFS_DEFAULT_NSET = GearsFinder.given_gear_sets[PREFS_DEFAULT_SET].length;
	public static final int PREFS_DEFAULT_NMAX = 20;
		
	public int trType;
	public double trRatio;
	public int[] gearsSet;
	public int nMax;	
	public GearTrain[] found;
	public String foundText;
	
	public static void saveAll(Context c, Preferences obj){
		
		SharedPreferences settings = c.getSharedPreferences(PREFS_FILE_NAME, PREFS_FILE_MODE);
		SharedPreferences.Editor editor = settings.edit();
				
		editor.putInt("trType",obj.trType);
		editor.putLong("trRatio", Double.doubleToRawLongBits(obj.trRatio));
		
		editor.putInt("nMax",obj.nMax);
		
		int nSet = 0;
		if(obj.gearsSet != null){
			nSet = obj.gearsSet.length;
		}
		editor.putInt("nSet",nSet);
		for(int i = 0 ; i < nSet; i++){
			String key = "gear#" + i; 
			editor.putInt(key,obj.gearsSet[i]);
		}			
				
		int nAct = 0;
		if(obj.found != null){
			nAct = obj.found.length;
		}
		editor.putInt("nAct",nAct);		
		for(int i = 0 ; i < nAct; i++){
			
			String key = "result#" + i; 
			
			switch(obj.trType){			
			case 2:
				editor.putInt(key+"[a]",obj.found[i].gears[0]);
				editor.putInt(key+"[b]",obj.found[i].gears[1]);
				break;
			case 4:
				editor.putInt(key+"[a]",obj.found[i].gears[0]);
				editor.putInt(key+"[b]",obj.found[i].gears[1]);
				editor.putInt(key+"[c]",obj.found[i].gears[2]);
				editor.putInt(key+"[d]",obj.found[i].gears[3]);				
				break;
			}
			editor.putLong(key+"[r]", Double.doubleToRawLongBits(obj.found[i].ratio));
			editor.putLong(key+"[e]", Double.doubleToRawLongBits(obj.found[i].error));
		}
		
		if(obj.foundText != null){
			editor.putString("resultText",obj.foundText);
		}else{
			editor.putString("resultText","");
		}
				
		//save all together
		editor.commit();
	}
	
	public static void restoreAll(Context c, Preferences obj){
		
		SharedPreferences settings = c.getSharedPreferences(PREFS_FILE_NAME, PREFS_FILE_MODE);
		
		obj.trType = settings.getInt("trType",PREFS_DEFAULT_TR_TYPE);
		
		if(settings.contains("trRatio")==false)
			obj.trRatio = PREFS_DEFAULT_TR_RATIO;
		else
			obj.trRatio = Double.longBitsToDouble(settings.getLong("trRatio",0));

		obj.nMax = settings.getInt("nMax",PREFS_DEFAULT_NMAX);
		
		int nSet = settings.getInt("nSet",PREFS_DEFAULT_NSET);
		obj.gearsSet = new int[nSet];
		
		for(int i = 0 ; i < nSet; i++){
			String key = "gear#" + i; 
			obj.gearsSet[i] = settings.getInt(key,GearsFinder.given_gear_sets[PREFS_DEFAULT_SET][i]);
		}		
				
		int nAct = settings.getInt("nAct",0);
		obj.found = new GearTrain[nAct];

		for(int i = 0 ; i < nAct; i++){
			
			String key = "result#" + i; 
			
			obj.found[i] = new GearTrain(obj.trType);
			
			switch(obj.trType){
			case 2:							
				obj.found[i].gears[0] = settings.getInt(key+"[a]",0);
				obj.found[i].gears[1] = settings.getInt(key+"[b]",0);
				break;
			case 4:
				obj.found[i].gears[0] = settings.getInt(key+"[a]",0);
				obj.found[i].gears[1] = settings.getInt(key+"[b]",0);
				obj.found[i].gears[2] = settings.getInt(key+"[c]",0);
				obj.found[i].gears[3] = settings.getInt(key+"[d]",0);
				break;
			}
			
			obj.found[i].ratio = Double.longBitsToDouble(settings.getLong(key+"[r]",0));
			obj.found[i].error = Double.longBitsToDouble(settings.getLong(key+"[e]",0));			
		}
		
		obj.foundText = settings.getString("resultText","");
	}
}
