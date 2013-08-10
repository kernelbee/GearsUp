package com.kernelbee.gearsup;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class GearsFinderThread implements Runnable {
	
	protected int start;
	protected int finish;
	protected int type;
	protected int[] set;
	protected GearTrain[] selected;
	protected double ratio;
	
	protected String class_name;
	protected String static_method_name;
	
	public int n_found;
	
	public GearsFinderThread(
			String class_name, String static_method_name, 
			int start, int finish, int type, int[] set, GearTrain[] selected, double ratio){
		
		this.start = start;
		this.finish = finish;
		this.type = type;
		this.set = set;
		this.selected = selected;
		this.ratio = ratio;
		this.n_found = 0;
		
		this.class_name = class_name;
		this.static_method_name = static_method_name;
	}
	
	@Override
	public void run() {		
	
		Class<?> c = null;
		Method m = null;
		
		try {
			c = Class.forName(class_name);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			m = c.getDeclaredMethod(static_method_name, 
											int.class, int.class, int.class,
											int[].class, GearTrain[].class, double.class);
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			n_found = (Integer) m.invoke(null,start,finish,type,set,selected,ratio);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
