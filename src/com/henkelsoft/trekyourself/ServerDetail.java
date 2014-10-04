package com.henkelsoft.trekyourself;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.os.Build;

import com.henkelsoft.trekyourself.TextViewTrek;
import com.henkelsoft.trekyourself.MonitoredServer;

public class ServerDetail extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_server_detail);

		
		
		
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}
		//b.putString("servername", serverName);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.server_detail, menu);
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
			View rootView = inflater.inflate(R.layout.fragment_server_detail,
					container, false);

			//View rootView = inflater.inflate (R.layout.fragment_hmc,container,false);
			TextViewTrek serverDetailsText = (TextViewTrek) rootView.findViewById(R.id.serverDetails);
			
			TextViewTrek serverNameText = (TextViewTrek) rootView.findViewById(R.id.detailTitle);
			
			ImageView shipView1 = (ImageView) rootView.findViewById(R.id.shipView1);
			
			Random rand = new Random();
			int min = 1;
			int max = 3;
			int randomNum = rand.nextInt((max - min) + 1) + min;
			
			if (randomNum == 1) {
				shipView1.setImageResource(R.drawable.ship1);
			} else if (randomNum == 2) {
				shipView1.setImageResource(R.drawable.ship2);
			} else {
				shipView1.setImageResource(R.drawable.ship3);
			}
			
			
			String serverName = getActivity().getIntent().getExtras().getString("servername");
			
			serverNameText.setText("Server Detail: " + serverName.toUpperCase());
			
			String outputString = ""; 
			
			HashMap<String,Object> serviceMap = (HashMap<String,Object>) getActivity().getIntent().getExtras().getSerializable("services");
			Iterator it = serviceMap.entrySet().iterator();
		    String errorStr = "";
		    String warningStr = "";
		    String normalStr = "";
			while (it.hasNext()) {

		        Map.Entry pairs = (Map.Entry)it.next();
		        
		        HashMap<String, Object> statusDetailHash =  (HashMap<String, Object>) pairs.getValue();
		        int statusInt = (Integer) statusDetailHash.get("Status");
		        
		        String statusString = String.valueOf(statusInt);
		        //String statusString = (String) statusDetailHash.get("Status");
		        
		        String serviceName =  (String) pairs.getKey();
		        
		        String longText = (String) statusDetailHash.get("LongText");
		        
		        if (statusInt == 2) {
		        	errorStr = errorStr +  "\n[ERROR] " +  serviceName + " : " + longText;
		        } else if (statusInt == 1) {
		        	warningStr = warningStr +  "\n[WARNING] " +  serviceName + " : " + longText;
		        } else {
		        	normalStr = normalStr +  "\n[NOMINAL] " +  serviceName;
		        }
		        //outputString = outputString + "\n[" + statusString + "] " +  serviceName;
		    }
			if (errorStr != "") {
				outputString = outputString + errorStr;
			}
			if (warningStr != "") {
				outputString = outputString + warningStr;
			}
			if (normalStr != "") {
				outputString = outputString + normalStr;
			}
			
			//outputString = outputString + "\n[" + statusString + "] " +  serviceName;
			//serverDetailsText.setText("hi there");
		     serverDetailsText.setText(outputString);
		        
		        
			return rootView;
		}
	}

}
