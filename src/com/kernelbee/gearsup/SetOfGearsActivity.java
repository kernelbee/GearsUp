package com.kernelbee.gearsup;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class SetOfGearsActivity extends Activity {

	private int[] gears;	
	private TextView theader;
	private TextView tgears;
	private EditText tgear;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//welcome in!
		overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
		
		setContentView(R.layout.activity_set_of_gears);
		// Show the Up button in the action bar.
		setupActionBar();
		
		theader = (TextView)findViewById(R.id.text_gears_header);
		tgears = (TextView)findViewById(R.id.text_gears);
		tgear = (EditText)findViewById(R.id.text_tgear);
				
		Intent i = getIntent();
		gears = i.getIntArrayExtra(MainActivity.REQUEST_GEARS_KEY);
		
		showGears();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
		
		//bye bye!
		overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
	}

	private void packResult(){
		
		//DEBUG ONLY
		//gears = GearsFinder.given_gear_sets[2];
		
		Intent i = new Intent();
		i.putExtra(MainActivity.REQUEST_GEARS_KEY, gears);
		setResult(RESULT_OK,i);		
	}
	
	@Override
	public void finish(){
		
		System.out.print("GearsUp: GearsActivity: FINISH\n");
		
		packResult();
		
		super.finish();
	}
	
	@Override
	public void onBackPressed(){
		System.out.print("GearsUp: GearsActivity: BACK\n");
		super.onBackPressed();
	}
	
	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.set_of_gears, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		System.out.print("GearsUp: GearsActivity: MENU\n");
		
		switch (item.getItemId()) {
		case android.R.id.home:

			System.out.print("GearsUp: GearsActivity: MENU [HOME]\n");
									
			//NavUtils.navigateUpFromSameTask(this);
			
			finish();
			return true;			
		}
		return super.onOptionsItemSelected(item);
	}

	private void showGears(){
		
		int size = gears.length;
		
		String h = "Set of gears (" + size + " totally):";
		String s = "";		
		
		for(int i = 0; i < size; i++){
			if(i < size-1)
				s += gears[i] + ", ";
			else
				s += gears[i];
		}
		
		theader.setText(h);
		tgears.setText(s);
	}
	
	private void addGear(int gear){
		
		int size = gears.length;		
		int i;
		
		for(i = 0; i < size; i++){
			if(gears[i]>=gear){
				break;
			}
		}
		
		//add element to array
		
		int[] tmp = new int[size+1];
		
		if(i > 0){
			System.arraycopy(gears, 0, tmp, 0, i);
		}
		tmp[i] = gear;
		if(size - i > 0){
			System.arraycopy(gears, i, tmp, i+1, size - i);
		}
		
		gears = tmp;		
		showGears();
	}
	
	private void delGear(int gear){
		
		int size = gears.length;
		boolean found = false;
		int i;
		
		for(i = 0; i < size; i++){
			if(gears[i]==gear){
				found = true;
				break;								
			}
		}

		if(found){
		
			int[] tmp = new int[size-1];
			
			if(i > 0){
				System.arraycopy(gears, 0, tmp, 0, i);
			}
			if(size - (i + 1) > 0){
				System.arraycopy(gears, i+1, tmp, i, size - (i + 1));
			}
			
			gears = tmp;			
			showGears();
		}		
	}
		
	public void gearPlus(View view){
		
		String str = tgear.getText().toString();
		
		if(str.length()>0){
			int gear = Integer.parseInt(str);
			
			if(gear > 0){			
				addGear(gear);
			}
		}
	}
	
	public void gearMinus(View view){
		
		String str = tgear.getText().toString();
		
		if(str.length()>0){
			int gear = Integer.parseInt(str);
			
			delGear(gear);
		}
	}
	
}
