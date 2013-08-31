package com.kernelbee.gearsup;

import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

public class MainActivity extends ActionBarActivity {

	static private int ncores;
	static private WorkingThread worker;
	
	static public Preferences prefs = new Preferences();
	static final int REQUEST_GEARS_CODE = 777; 
	static String REQUEST_GEARS_KEY;
		
	private EditText output;
	private EditText tratio;
	private Spinner gttype;
	private ProgressBar progressbar;
	private Button buttonRun;	
	private boolean enable_menuItemGears;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		System.out.print("GearsUp: MainActivity[onCreate]!\n");
		
		setContentView(R.layout.activity_main);
		
		REQUEST_GEARS_KEY = getApplicationContext().getPackageName() + ".gearsSet";
		
		ncores = Runtime.getRuntime().availableProcessors();
		
		output = (EditText)findViewById(R.id.text_output);
		tratio = (EditText)findViewById(R.id.text_tratio);
		gttype = (Spinner)findViewById(R.id.spinner1);
		progressbar = (ProgressBar)findViewById(R.id.progressBar);
		buttonRun = (Button)findViewById(R.id.Button0);
				
		//is it cold start or just activity's reincarnation?
		if(savedInstanceState == null){
			//System.out.print("GearsUp: MainActivity[onCreate COLD START]!\n");
			
			//restore preferences
			Context c = this.getApplicationContext();
			Preferences.restoreAll(c,prefs);
			
			//FOR DEBUG ONLY !!!
			//prefs.gearsSet = GearsFinder.given_gear_sets[2];
			
			//setup UI controls: tratio, output, etc.
			tratio.setText(Double.toString(prefs.trRatio));
			gttype.setSelection((prefs.trType-2)/2);
			output.setText(prefs.foundText);
			//enable UI controls
			enableTaskControls(true);

		}else{			
			//manage reference to background thread
			if(worker != null){
				if(worker.getStatus().equals(AsyncTask.Status.RUNNING)){
					//disable UI controls (prevent restarting background thread)
					enableTaskControls(false);
					//connect activity with still active background thread
					worker.activity = this;
				}else{
					//thread is not working now, so clean it up: allow GC to do it's job
					worker = null;
				}
			}
			if(worker == null){
				//enable UI controls
				enableTaskControls(true);
			}
		}
		
		//adjust text size:
		//output.setTextSize();		
	}
	
	@Override
	protected void onPause(){
		super.onPause();		
		System.out.print("GearsUp: MainActivity[onPause]!\n");
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		System.out.print("GearsUp: MainActivity[onStop]!\n");
	}
	
	@Override	
	protected void onDestroy(){
		super.onDestroy();
		
		System.out.print("GearsUp: MainActivity[onDestroy]!\n");

		boolean isDying = isFinishing(); 
		
		if(isDying){
			//save preferences
			Context c = this.getApplicationContext();
			Preferences.saveAll(c, prefs);
		}
		//cancel background work if any
		if(worker != null) {			
			//unlink from background thread
			worker.activity = null;
			//cancel background thread if it is time to die really... 
			if(isDying){
				if(worker.getStatus().equals(AsyncTask.Status.RUNNING)){
					//stop searching loop
					GearsFinder.cancelSearching(true);
					//cancel background thread
					worker.cancel(true);
					
					System.out.print("GearsUp: MainActivity[onDestroy: cancel WORKER]!\n");
				}else{
					//background thread is dead actually: nothing to do. 
				}
			}
		}		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);						
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle presses on the action bar items
	    switch (item.getItemId()) {
	        case R.id.action_gears_set:
	        	System.out.print("GearsUp: MainActivity: Action: [SET OF GEARS]\n");
	        	
				openSetOfGears();
				
				//update preferences
				//prefs.gearsSet = 
				
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}	
	
	private void openSetOfGears(){
		
		//run new activity with current set of gears
		Intent i = new Intent(getApplicationContext(), SetOfGearsActivity.class);		
		i.putExtra(REQUEST_GEARS_KEY, prefs.gearsSet);
		startActivityForResult(i,REQUEST_GEARS_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(requestCode == REQUEST_GEARS_CODE){
			if(resultCode == RESULT_OK){
				//get new set of gears from user
				prefs.gearsSet = data.getIntArrayExtra(REQUEST_GEARS_KEY);
			}else{
				System.out.print("GearsUp:onActivityResult [result != RESULT_OK]\n");
			}
		}
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.getItem(0).setEnabled(enable_menuItemGears);		
		return true;
	}
	
	private void enableTaskControls(boolean enable){
		//views
		tratio.setEnabled(enable);
		buttonRun.setEnabled(enable);
		gttype.setEnabled(enable);
		//menu
		enable_menuItemGears = enable;		
		invalidateOptionsMenu();//to trigger onPrepareOptionsMenu() later 
	}
	
	public void runSearch(View view){
				
		int n_max = 20;
				
		String str = tratio.getText().toString();
				
		if(str.length()>0){
		
			double ratio = Double.parseDouble(str);
			int ptrain_type = 2 + 2*gttype.getSelectedItemPosition();
			
			String s = "";
			s += "\n\tProcessing " 
					+ GearsFinder.knPermutations(prefs.gearsSet.length,ptrain_type) 
					+ " cases on "
					+ ncores
					+ " core(s)...\n"
					;			
			output.setText(s);
			
			//create background thread (and link it to activity)
			worker = new WorkingThread(this, prefs.gearsSet);			
			
			//run thread in background (separately from UI)
			worker.execute(	Double.toString(ratio),
							Integer.toString(n_max),
							Integer.toString(ptrain_type),
							Integer.toString(ncores)
							);
			
			//update preferences
			prefs.trType = ptrain_type;
			prefs.trRatio = ratio;
			//reset result
			prefs.found = null;
			prefs.foundText = null;
		}
	}
	
	public interface IFCallback{
		public void updateProgress(Integer value);
	}
	
	static private class WorkingThread extends AsyncTask<String, Integer, String> 
											implements IFCallback {
		
		volatile MainActivity activity = null;
		
		private int[] setOfGears;
		private GearTrain[] results_buffer;
		private int n_found;
		
		public WorkingThread(MainActivity activity, int[] gears){
			this.activity = activity;
			this.setOfGears = gears;
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			double ratio = Double.parseDouble(params[0]);
			int n_max = Integer.parseInt(params[1]);
			int type = Integer.parseInt(params[2]);
			int n_cores = Integer.parseInt(params[3]);
			
			if(setOfGears.length > 0){				
				
				System.out.format(
						"GearsUp: input: set{%d gears}, ratio{%.16f}\n",
						setOfGears.length,ratio
						);
								
				results_buffer = new GearTrain[n_max];		
				
				long time = System.nanoTime();
				//run searching loop
				n_found = GearsFinder.prepareGeartrainSetups
							(	n_cores,
								type,
								setOfGears,
								results_buffer,ratio,true
							);
				long duration = System.nanoTime() - time;
				
				//prepare text with results
				String s = "";
				s += "Gear train type: " + type + "-gears;\n";
				s += "done in: " + (long)(duration/1000000.0) + " ms\n\n";				
				for(int j = 0; j < n_found; j++){					
					s += results_buffer[j].toString() + "\n";
				}
				//return result in text form
				return s;
			}			
			return null;
		}
		//runs on the background thread (callback from inner searching loop)
		public void updateProgress(Integer value){
			publishProgress(value);			
		}
		
		//runs on the UI thread
		protected void onProgressUpdate(Integer...progress){
			if(activity != null){
				activity.progressbar.setProgress(progress[0]);
			}			
		}		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			
			//setup UI controls
			if(activity != null){
				activity.enableTaskControls(false);				
				publishProgress(0);//reset				
			}			
			//link finder to this background thread
			GearsFinder.ifcallback = this;
			//allow searching in loop
			GearsFinder.cancelSearching(false);
		}		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			
			//unlink finder
			GearsFinder.ifcallback = null;
			
			//setup UI controls
			if(activity != null){				
				activity.enableTaskControls(true);				
				//activity.output.append(result);
				activity.output.setText(result);
				publishProgress(0);//job is done!
				
				//update preferences
				prefs.found = new GearTrain[n_found];
				System.arraycopy(results_buffer, 0, prefs.found, 0, n_found);
				prefs.foundText = result;
			}
		}		
		@Override
		protected void onCancelled() {			
			super.onCancelled();
			
			//unlink finder
			GearsFinder.ifcallback = null;
			//setup UI controls
			if(activity != null){				
				activity.enableTaskControls(true);
				publishProgress(0);//job is done!
			}
			
			System.out.print("GearsUp: MainActivity: Worker [CANCELLED]!\n");
		}
	}
}
