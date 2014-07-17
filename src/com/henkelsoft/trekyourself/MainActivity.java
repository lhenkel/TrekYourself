package com.henkelsoft.trekyourself;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;




import org.apache.http.HttpResponse;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;

import java.util.List;

import android.net.http.AndroidHttpClient;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;

import com.henkelsoft.trekyourself.R;

public class MainActivity extends Activity {

	private HashMap<String, MonitoredServer> serverMap;
	public TreeMap<Date, String> calHash;
	
	private static String nagiosHttpLocation;
	private static String calHttpLocation;
	//public static String endParams = "";
	
	static SimpleDateFormat googleMinMaxForamt = new SimpleDateFormat("yyyy-MM-dd");
	
	static long dtMili = System.currentTimeMillis();
	static Date dt1 = new Date(dtMili);
	static String startMinDate =  googleMinMaxForamt.format(dt1);
	
	static Date dt2 = new Date(dtMili + (60 * 60 * 24 * 1000 * 6 ));
	
	static String startMaxDate = googleMinMaxForamt.format(dt2);
	
	public static String endParams = "?singleevents=true&start-min=" + startMinDate + "&start-max=" + startMaxDate;
	
	
	private int NUM_SERVER_COLS = 4;
	
	public Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        updateTime();
	    }
	};	

	public Handler mServerStatusHandler = new Handler() {
	    public void handleMessage(Message msg) {
	    	new HttpGetTask().execute();
	    }
	};	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.primary_layout);	//textViewTrekDate
        Log.d("TREK", "State : Running create stuff");
        updateTime();
        
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
               
        //SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        nagiosHttpLocation = (String) sharedPreferences.getString("nagiosLocation", null);
        calHttpLocation = (String) sharedPreferences.getString("httpCalendarLocation", null);
        //endParams = "sdf";
        Log.d("TREK", "Possibly doing server get to: " + nagiosHttpLocation);
        
        Log.d("TREK", "Possibly doing Cal get to: " + calHttpLocation);
        
/*    	if (nagiosHttpLocation != null) {
    		new HttpGetTask().execute();
    	}*/
    	
    	if (calHttpLocation != null) {
    		Log.d("TREK", "Possibly Executing CalHTTP Task");
    		new CalHttpGetTask().execute();
    		
    		
    	} else {
    		Log.d("TREK", "Cal HTTP is nul..");
    	}

    	    	
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

        
        ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
        Runnable updateTimeTask = new Runnable(){
            @Override
            public void run() {
                try{
                	mHandler.obtainMessage(1).sendToTarget();
                    Thread.sleep(30 * 1000);

                } catch(Exception e){
                     
                }
            }
        };        
        ScheduledFuture<?> periodicFuture = sch.scheduleAtFixedRate(updateTimeTask, 5, 5, TimeUnit.SECONDS);
        
        //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        Runnable updateServerTask = new Runnable(){
            @Override
            public void run() {
                
    			//final SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
                            	
            	//final SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
    			//String nagiosHttpLocation = sharedPref.getInt(getString(R.string.saved_high_score), defaultValue);			
    			//
            	//SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
            	
            	//SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            	//final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            	//final SharedPreferences tmpPrefs = sharedPreferences; //.getString("nagiosLocation", null);

            	
            	nagiosHttpLocation = (String) sharedPreferences.getString("nagiosLocation", null);
            	//nagiosHttpLocation = "http://63.226.80.152:8080/nagios/status.xml";
            	
            	if (nagiosHttpLocation != null) {
                	try{
                    	mServerStatusHandler.obtainMessage(1).sendToTarget();
                        Thread.sleep(60 * 1000);

                    } catch(Exception e){
                         
                    }
            		
            	}
            	

            }
        };        
        periodicFuture = sch.scheduleAtFixedRate(updateServerTask, 5, 5, TimeUnit.SECONDS);
        
        //updateTime();
    }

	private class HttpGetTask extends AsyncTask<Void, Void, HashMap<String, MonitoredServer>> {

		private static final String TAG = "HttpGetTask";
		
		//
		
		//private static final String URL = "http://63.226.80.152:8080/nagios/status.xml";
		private final String URL = nagiosHttpLocation;
		
			

		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
		
		
		@Override
		protected HashMap<String, MonitoredServer> doInBackground(Void... params) {

			HttpGet request = new HttpGet(URL);
			//Log.d("TREK", "Running Server HTTP Grab! BEFORE!!");
			XMLResponseHandler responseHandler = new XMLResponseHandler();
			//HashMap<String, MonitoredServer> myMap = (HashMap<String, MonitoredServer>) responseHandler;
			
			try {
				
				return mClient.execute(request, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute( HashMap<String, MonitoredServer> result) {
			
			if (result == null) {
				Log.d("TREK", "result is null.. nuts");
			} else {
				HashMap<String, MonitoredServer> resServerMap = (HashMap<String, MonitoredServer>) result;
				
				ImageView image = new ImageView(getApplicationContext());
				
				int normalImageId = getResources().getIdentifier("servernormal",  "drawable", getPackageName());
				int warningImageId = getResources().getIdentifier("serverwarn",  "drawable", getPackageName());
				int criticalImageId = getResources().getIdentifier("servererror",  "drawable", getPackageName());

				RelativeLayout mainViewScreen = (RelativeLayout) findViewById(R.id.mainViewScreen);
				
				//TextView text1 = (TextView) findViewById(R.id.hellothere);
				//text1.setText( "Test111" + Integer.toString(resServerMap.size()) );
				
				Log.d("TREK", "size of entry" + resServerMap.size() );
				
				if (resServerMap.size() > 0 ) {
					Iterator it = resServerMap.entrySet().iterator();
				    MonitoredServer curServer = null;
			
				    int curCount = 0;
				    int numberOfItems = resServerMap.size();
				    TableLayout systemsTable = (TableLayout) findViewById(R.id.systemsTable);
				    systemsTable.removeAllViews();
			        TableRow curRow = new TableRow(getApplicationContext());
				    
			        
			        
				    while (it.hasNext()) {
				    	//Log.d("TREK", "Here3");
				        Map.Entry pairs = (Map.Entry)it.next();

				        curServer =  (MonitoredServer) pairs.getValue();
				        //text1.setText(  (String) pairs.getKey() );
				        
				        if ((curCount % NUM_SERVER_COLS == 0) ) {
				        	curRow = new TableRow(getApplicationContext());
				        	systemsTable.addView(curRow);
				        }

						Button curButton =  new Button(getApplicationContext());
						
						if (Integer.parseInt(curServer.getStatus()) == 1) {
							curButton.setBackgroundResource(warningImageId);
						} else if (Integer.parseInt(curServer.getStatus()) == 2) {
							curButton.setBackgroundResource(criticalImageId);
						} else {
							curButton.setBackgroundResource(normalImageId);
						}
						
						Typeface typeface = Typeface.createFromAsset(getAssets(), "swissbt.ttf");
						curButton.setTextSize(40);
						   
						curButton.setText((String) curServer.getName());
						curButton.setTypeface(typeface);
						
						curRow.addView(curButton);
				        
						
				        it.remove(); // avoids a ConcurrentModificationException
				        curCount++;
				        //Log.d("TREK", "Here4");
				    }					
									
					
					
				} else {
					Log.d("TREK", "size of entry is 0" + resServerMap.size() );
				}
								
			}
			


			if (null != mClient)
				mClient.close();
		}
	}

	

	private class CalHttpGetTask extends AsyncTask<Void, Void, TreeMap<Date, String>> {

		private final String URL = calHttpLocation.concat(endParams);
		
		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
		
		
		@Override
		protected TreeMap<Date, String> doInBackground(Void... params) {

			Log.d("CURDEBUG", "Running Cal HTTP Grab! BEFORE!!" + URL);
			
			HttpGet request = new HttpGet(URL);
			Log.d("TREK", "Running Cal HTTP Grab! BEFORE!!");
			XMLCalResponseHandler responseHandler = new XMLCalResponseHandler();
			
			try {
				
				return mClient.execute(request, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute( TreeMap<Date, String> result) {
			
			if (result == null) {
				Log.d("TREK", "result is null.. nuts");
			} else {
				Log.d("TREK", "Got to onpost execute (Calendar)!");
				TreeMap<Date, String> resCalMap = (TreeMap<Date, String>) result;
				calHash = resCalMap;
				updateCalendar();				
				
			}
			


			if (null != mClient)
				mClient.close();
			//setListAdapter(new ArrayAdapter<String>(
			//		NetworkingAndroidHttpClientXMLActivity.this,
			//		R.layout.list_item, result));
		}

	}	
	
    
    private void updateTime() {
        TextViewTrek tvDate = (TextViewTrek) findViewById(R.id.textViewTrekDate);
        TextViewTrek tvTime = (TextViewTrek) findViewById(R.id.textViewTrekTime);
        //MMM dd hh:mm:ss yyyy 
        SimpleDateFormat sdf = new SimpleDateFormat("MMM d, yyyy");
        String currentDateStr = sdf.format(new Date());
        tvDate.setText(currentDateStr);

        SimpleDateFormat stf = new SimpleDateFormat("HH:mm");
        String currentTimeStr = stf.format(new Date());
        tvTime.setText(currentTimeStr);
    	
    	
    }

	private void updateCalendar() {
		RelativeLayout mainViewScreen = (RelativeLayout) findViewById(R.id.mainViewScreen);

		TreeMap<Date, String> todayMap = new TreeMap<Date, String>();
		TreeMap<Date, String> tommorrowMap = new TreeMap<Date, String>();
		
		
		Log.d("TREK", "size of cal entry" + calHash.size() );

    	Date curDate = new Date(System.currentTimeMillis());
    	Date tomDate = new Date(dtMili + (60 * 60 * 24 * 1000 * 1 ));
		
		if (calHash.size() > 0 ) {
			Iterator it = calHash.entrySet().iterator();
	
		    int curCount = 0;
		    int numberOfItems = calHash.size();
		    TextViewTrek tvToday = (TextViewTrek) findViewById(R.id.textViewToday);
		    tvToday.setText("testing123");
		    
		    while (it.hasNext()) {

		        Map.Entry pairs = (Map.Entry)it.next();
		        String eventName =  (String) pairs.getValue();
		        
		        boolean updatedNextMeeting = false;
		        
		        Date calDate = (Date) pairs.getKey(); 
		    	if (googleMinMaxForamt.format(calDate).toString().equals( googleMinMaxForamt.format(curDate).toString()  ))  {
		    		Log.d("TREK", "State : Date is Today!");
		    		if (calDate.compareTo( curDate) > 0) {	// still in the future; don't care about old
		    			todayMap.put(calDate, eventName);
		    			if (updatedNextMeeting == false) {
		    				updateNextMeetingTime(calDate);
		    				updatedNextMeeting = true;
		    			}
		    		}
		    		
		    	} 
		        
		    	if (googleMinMaxForamt.format(calDate).toString().equals( googleMinMaxForamt.format(tomDate).toString()  ))  {
		    		Log.d("TREK", "State : Date is tomrrow!");
		    		tommorrowMap.put(calDate, eventName);
	    			if (updatedNextMeeting == false) {
	    				updateNextMeetingTime(calDate);
	    				updatedNextMeeting = true;
	    			}
		    		
		    	} 
		        
		        //tvToday.setText(eventName);
		        
		        it.remove(); // avoids a ConcurrentModificationException

		    }					
			

		    
		    
		} else {
			Log.d("TREK", "size of entry is 0" + calHash.size() );
		}
	}
    
    
    
    private void updateNextMeetingTime(Date calDate) {
		// TODO Auto-generated method stub
		
	}

	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
        	showPrefs();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPrefs() {
		// TODO Auto-generated method stub
    	Intent prefsIntent = new Intent(MainActivity.this, TrekPrefsActivity.class);
    	MainActivity.this.startActivity(prefsIntent);    	    	
		
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
