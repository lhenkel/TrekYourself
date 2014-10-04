package com.henkelsoft.trekyourself;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.os.Build;
import android.preference.PreferenceManager;

public class TrekPrefsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_trek_prefs);

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.trek_prefs, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		
		
		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(
					R.layout.trek_prefs_layout_fragment, container, false);
			
			Button myButton = (Button)rootView.findViewById(R.id.make_it_so);
			//final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
			//final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
			
			final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
			
			//String nagiosHttpLocation = sharedPref.getInt(getString(R.string.saved_high_score), defaultValue);			
			String nagiosHttpLocation = (String) sharedPref.getString("nagiosLocation", null);
			String httpCalendarLocation = (String) sharedPref.getString("httpCalendarLocation", null);
			String httpSoundLocation = (String) sharedPref.getString("httpSoundLocation", null);
			
			EditText nagiosLocationTE = (EditText) rootView.findViewById(R.id.httpNagiosLocation);
			EditText httpCalendarLocationTE2 = (EditText) rootView.findViewById(R.id.httpCalendarLocation);
			EditText httpSoundLocationTE = (EditText) rootView.findViewById(R.id.httpSoundLocation);
			
			if (nagiosHttpLocation != null ) {
				Log.d("TREK", "Prefs String:" + nagiosHttpLocation);
				nagiosLocationTE.setText(nagiosHttpLocation);
				
			} else {
				Log.d("TREK", "Prefs String BLANK");
				nagiosLocationTE.setText("http://");
			}

			//httpCalendarLocationTE2.setText("http://");
			if (httpCalendarLocation != null ) {
				httpCalendarLocationTE2.setText(httpCalendarLocation);
			} else {
				httpCalendarLocationTE2.setText("http://");
			}

			//httpCalendarLocationTE2.setText("http://");
			if (httpSoundLocation != null ) {
				httpSoundLocationTE.setText(httpSoundLocation);
			} else {
				httpSoundLocationTE.setText("http://");
			}
						
			
			myButton.setOnClickListener(new View.OnClickListener() {
				  public void onClick(View view) {
					  
					  SharedPreferences.Editor editor = sharedPref.edit();
					  
					  EditText nagiosLocationTextEdit = (EditText) getActivity().findViewById(R.id.httpNagiosLocation);
					  EditText httpCalendarLocationTextEdit = (EditText) getActivity().findViewById(R.id.httpCalendarLocation);
					  EditText httpSoundLocationTextEdit = (EditText) getActivity().findViewById(R.id.httpSoundLocation);
					  
					  editor.putString("nagiosLocation", nagiosLocationTextEdit.getText().toString());
					  editor.putString("httpCalendarLocation", httpCalendarLocationTextEdit.getText().toString());
					  editor.putString("httpSoundLocation", httpSoundLocationTextEdit.getText().toString());
					  
					  Log.d("TREK", "Setting nagios locatin to: " + nagiosLocationTextEdit.getText().toString());
					  
					  editor.commit();				  
					  
				    	Intent backToMain = new Intent(getActivity(), MainActivity.class);
				    	startActivity(backToMain);    	
				    	getActivity().finish();
				  }
				});									
			
/*			Button myButton = (Button)rootView.findViewById(R.id.make_it_so);
			final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
			//String nagiosHttpLocation = sharedPref.getInt(getString(R.string.saved_high_score), defaultValue);			
			String nagiosHttpLocation = sharedPref.getString("nagiosLocation", "");
			
			EditText nagiosLocationTE = (EditText) getActivity().findViewById(R.id.httpNagiosLocation);
			nagiosLocationTE.setText(nagiosHttpLocation);

			myButton.setOnClickListener(new View.OnClickListener() {
			  public void onClick(View view) {
				  //finish();
				// getActivity().getFragmentManager().beginTransaction().remove(view).commit();
				  //getActivity().getFragmentManager().beginTransaction().remove( (Fragment) getActivity() ).commit();
				  SharedPreferences.Editor editor = sharedPref.edit();
				  EditText nagiosLocationTextEdit = (EditText) getActivity().findViewById(R.id.httpNagiosLocation);
				  editor.putString("nagiosLocation", nagiosLocationTextEdit.getText().toString());
				  
				  editor.commit();				  
				 
		    	Intent backToMain = new Intent(getActivity(), MainActivity.class);
		    	startActivity(backToMain);    	

			  }
			});						
*/			
			
			return rootView;
		}
	}

}
