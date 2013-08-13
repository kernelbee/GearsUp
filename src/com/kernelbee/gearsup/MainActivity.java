package com.kernelbee.gearsup;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends Activity {

	int n_cores;
	
	EditText output;
	EditText tratio;
	ProgressBar progressbar;
	Button button2;
	Button button4;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		n_cores = Runtime.getRuntime().availableProcessors();		
		
		output = (EditText)findViewById(R.id.text_output);
		tratio = (EditText)findViewById(R.id.text_tratio);
		progressbar = (ProgressBar)findViewById(R.id.progressBar);
		button2 = (Button)findViewById(R.id.Button0);
		button4 = (Button)findViewById(R.id.Button1);		
						
		//adjust text size:
		//output.setTextSize();		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void start_searching(int ptrain_type){
		
		int set = 2;
		int n_max = 20;
				
		String str = tratio.getText().toString(); 
				
		if(str.length()>0){
		
			double ratio = Double.parseDouble(str);
			
			String s = "";
			s += "\n\t{" 
					+ ptrain_type
					+ "} Processing " 
					+ GearsFinderUIExt.kn_permutations(GearsFinderUIExt.given_gear_sets[set].length,ptrain_type) 
					+ " cases on "
					+ n_cores
					+ " core(s)...\t"
					;			
			output.setText(s);
			
			//run separate working thread
			new WorkingThread().execute(Integer.toString(set),
									Double.toString(ratio),
									Integer.toString(n_max),
									Integer.toString(ptrain_type)
									);			
		}		
	}
	
	public void search_ptrain_type_2(View view){
		start_searching(2);
	}

	public void search_ptrain_type_4(View view){
		start_searching(4);
	}

	public interface IFCallback{
		public void callback(Integer value); 
	}
	
	class WorkingThread extends AsyncTask<String, Integer, String> 
											implements IFCallback {		
		@Override
		protected String doInBackground(String... params) {
			
			int set = Integer.parseInt(params[0]);
			double ratio = Double.parseDouble(params[1]);
			int n_max = Integer.parseInt(params[2]);
			int type = Integer.parseInt(params[3]);
			
			if(GearsFinderUIExt.given_gear_sets[set].length > 0){
				
				GearsFinderUIExt.ifcallback = this;
				
				System.out.format(
						"input: set{=%d, %d gears}, ratio{%.16f}\n",
						set,GearsFinderUIExt.given_gear_sets[set].length,ratio
						);

				String s = "";
				
				GearTrain[] selected = new GearTrain[n_max];									

				long time = System.nanoTime();
				int n_actual = GearsFinderUIExt.prepare_geartrain_setups
								(	GearsFinderUIExt.class.getName(),
									null,
									n_cores,
									type,
									GearsFinderUIExt.given_gear_sets[set],
									selected,ratio,true
								);
				long duration = System.nanoTime() - time;
									
				s += "done in: " + (long)(duration/1000000.0) + " ms\n\n";
				
				for(int j = 0; j < n_actual; j++){					
					s += selected[j].toString() + "\n";
				}
				
				return s;
			}
			
			return null;
		}
		
		public void callback(Integer value){
			publishProgress(value);
		}
	
		protected void onProgressUpdate(Integer...progress){
			progressbar.setProgress(progress[0]);			
		}
		
		@Override
		protected void onPreExecute(){
			super.onPreExecute();
			tratio.setEnabled(false);
			button2.setEnabled(false);
			button4.setEnabled(false);			
			publishProgress(0);//reset
		}
		
		@Override
		protected void onPostExecute(String result){
			super.onPostExecute(result);
			tratio.setEnabled(true);
			button2.setEnabled(true);
			button4.setEnabled(true);
			output.append(result);
			publishProgress(0);//job is done!
		}
	}
}
