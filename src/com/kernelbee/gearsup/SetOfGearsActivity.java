package com.kernelbee.gearsup;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;

public class SetOfGearsActivity extends Activity {

	private int[] gears;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set_of_gears);
		// Show the Up button in the action bar.
		setupActionBar();
		
		Intent i = getIntent();
		gears = i.getIntArrayExtra(MainActivity.REQUEST_GEARS_KEY);		
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

}
