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
import android.os.SystemClock;
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
import android.widget.Toast;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

















import org.apache.http.HttpResponse;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
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

	
	protected static final int REFRESH = 0;

	private Handler _hRedraw;
	
	private HashMap<String, MonitoredServer> serverMap;
	public TreeMap<Date, String> calHash;
	public HashMap<String, Integer> soundHash;
	public List<Map<String, String>> taskList;
	private static String nagiosHttpLocation;
	private static String calHttpLocation;
	private static String soundHttpLocation;
	
	private SoundMeter mSensor;
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

	public Handler mSoundHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        new SoundHttpGetTask().execute();		
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

        _hRedraw=new Handler(){
            @Override
            public void handleMessage(Message msg)
            {
                switch (msg.what) {
                case REFRESH:
                    redrawEverything();
                    break;
                }
            }

			private void redrawEverything() {
				TableLayout systemsTable = (TableLayout) findViewById(R.id.systemsTable);
				systemsTable.invalidate();
				systemsTable.refreshDrawableState();
				
			}
        };
        
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
               
        mSensor = new SoundMeter();
        
        //SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        nagiosHttpLocation = (String) sharedPreferences.getString("nagiosLocation", null);
        calHttpLocation = (String) sharedPreferences.getString("httpCalendarLocation", null);
        soundHttpLocation = (String) sharedPreferences.getString("httpSoundLocation", null);
        
        //endParams = "sdf";
        Log.d("TREK", "Possibly doing server get to: " + nagiosHttpLocation);
        
        Log.d("TREK", "Possibly doing Cal get to: " + calHttpLocation);
        
/*    	if (nagiosHttpLocation != null) {
    		new HttpGetTask().execute();
    	}*/

    	if (soundHttpLocation != null) {
    		Log.d("TREK", "Possibly Executing Sound Task");
    		new SoundHttpGetTask().execute();
    		
    		
    	} else {
    		Log.d("TREK", "Sound HTTP is nul..");
    	}
        
        
    	if (calHttpLocation != null) {
    		Log.d("TREKDATE", "Possibly Executing CalHTTP Task (not null)");
    		new CalHttpGetTask().execute();
    		
    		
    	} else {
    		Log.d("TREKDATE", "Cal HTTP is nul..");
    	}

		Log.d("TREKTASk", "Possibly Executing Sound Task");
		new TaskHttpGetTask().execute();
    	    	
        //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

    	updateAcademyFiles();
        
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


        Runnable updateCalendarTask = new Runnable(){
            @Override
            public void run() {
                try{
                	updateCalendar();
                    Thread.sleep(30 * 1000);

                } catch(Exception e){
                     
                }
            }
        };        
        periodicFuture = sch.scheduleAtFixedRate(updateCalendarTask, 30, 45, TimeUnit.SECONDS);
        
        
        
        
// Check sound data
/*    Runnable mPollSoundTask = new Runnable() {
            public void run() {
            	Log.d("TREKSENSOR", "Logging Sensor. HERE ");
            	mSensor.start();
            	
            	try {
					Thread.sleep(1000,0);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
            	double amp = mSensor.getAmplitude();
            	Log.d("TREKSENSOR", "Logging Sensor. Got " + String.valueOf(amp));
            	mSensor.stop();
            	//TextViewTrek tvDebugSound = (TextViewTrek) findViewById(R.id.debugSoundLevel);
            	//tvDebugSound.setText( String.valueOf(amp));

            }
    };        
*/
    //periodicFuture = sch.scheduleAtFixedRate(mPollSoundTask, 3, 5, TimeUnit.SECONDS);

    //SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        Runnable updateServerTask = new Runnable(){
            @Override
            public void run() {
                
            	nagiosHttpLocation = (String) sharedPreferences.getString("nagiosLocation", null);
            	//nagiosHttpLocation = "http://63.226.80.152:8080/nagios/status.xml";
            	
            	if (nagiosHttpLocation != null) {
            		Log.d("TREKNAG", "String isnt null");
                	try{
                    	mServerStatusHandler.obtainMessage(1).sendToTarget();
                        Thread.sleep(60 * 1000);

                    } catch(Exception e){
                         
                    }
            		
            	} else {
            		Log.d("TREKNAG", "String IS null");
            	}
            	

            }
        };        
        //periodicFuture = sch.scheduleAtFixedRate(updateServerTask, 25, 25, TimeUnit.SECONDS);
        periodicFuture = sch.scheduleAtFixedRate(updateServerTask, 5, 10, TimeUnit.SECONDS);
        
        //
        
        Runnable updateSoundTask = new Runnable(){
            @Override
            public void run() {
            	Log.d("TREKSOUND", "Doing Scheduled Sound Update Runnable");                
            	// TODO : make this variable... came up nul land was lazy..
            	soundHttpLocation = "http://63.226.80.152:8080/nagios/soundlevel.xml";
            	
            	//nagiosHttpLocation = "http://63.226.80.152:8080/nagios/status.xml";
            	
            	if (soundHttpLocation != null) {
            		
            		Log.d("TREKSOUND", "Doing Scheduled Sound Update Runnable - Not Null");
            		
                	try{
                		//XXX Here
                		//mSoundHandler.obtainMessage(1).sendToTarget();
                		new SoundHttpGetTask().execute();
                        Thread.sleep(6 * 1000);

                    } catch(Exception e){
                         
                    }
            		
            	} else {
            		Log.d("TREKSOUND", "Doing Scheduled Sound Update Runnable -IS Null location.. crap..");
            	}
            	

            }
        };        
        periodicFuture = sch.scheduleAtFixedRate(updateSoundTask, 30, 30, TimeUnit.SECONDS);

        
        
    }

	private void updateAcademyFiles() {
		//getContext();
		//String[] array = context.getResources().getStringArray(R.array.facts);
		String[] array = getResources().getStringArray(R.array.facts);
		String randomFact = array[new Random().nextInt(array.length)];
		//academyRandomFact
		TextViewTrek tvRandomFact = (TextViewTrek) findViewById(R.id.academyRandomFact);
		tvRandomFact.setText(randomFact);
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
				
				Log.d("TREK", "size of entry (on post execute)" + resServerMap.size() );
				
				if (resServerMap.size() > 0 ) {
					
					//XX Make copy here
					HashMap<String, MonitoredServer> resServerCopy = new HashMap<String, MonitoredServer>(resServerMap);
					Iterator it = resServerCopy.entrySet().iterator();
				    MonitoredServer curServer = null;
			
				    int curCount = 0;
				    int numberOfItems = resServerMap.size();
				    TableLayout systemsTable = (TableLayout) findViewById(R.id.systemsTable);
				    
				    TableLayout systemsDynamicTable =  new TableLayout(getApplicationContext());
				    systemsDynamicTable.setLayoutParams(new TableRow.LayoutParams(
				    			TableRow.LayoutParams.WRAP_CONTENT));
				    systemsDynamicTable.setStretchAllColumns(true);
					RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
					        ViewGroup.LayoutParams.WRAP_CONTENT);
					
					p.addRule(RelativeLayout.BELOW, R.id.baseSpace);
					int left = 370;
					int top = 170;
					int right = 380;
					int bottom = 0;
					systemsDynamicTable.setPadding(left, top, right, bottom);
					
				    
				    systemsTable.removeAllViews();
				    
				    _hRedraw.sendEmptyMessage(REFRESH);
				    
				    //systemsTable = (TableLayout) findViewById(R.id.systemsTable);		//XXX

				    mainViewScreen.addView(systemsDynamicTable);
				    //systemsDynamicTable.setWeightSum((float) 0.5);
				    //systemsDynamicTable.setWeightSum((float) 0.0);
				    systemsTable = systemsDynamicTable;
				    
/*				    int rowCount = systemsTable.getChildCount();
				    for (int i = 0; i < rowCount; i++) {
				        View child = systemsTable.getChildAt(i);
				        if (child instanceof TableRow) ((ViewGroup) child).removeAllViews();
				    }				    
				    //systemsTable.set
*/			        
				    TableRow curRow = new TableRow(getApplicationContext());
				    
			        
			        
				    while (it.hasNext()) {
				    	Log.d("TREK", "Iterating over Servers!!");
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

						final String serverName = (String) curServer.getName();
						curButton.setId(123);
						//curButton.setId(serverName);
						curButton.setText(serverName);
						curButton.setTypeface(typeface);
						
						final int buttonID = curButton.getId();
						final HashMap<String,Object> sendMap = curServer.getStatusMap();
						
						curRow.addView(curButton);
						Log.d("TREK", "Adding Button!!!");
						curButton.setOnClickListener(new View.OnClickListener() {
					        public void onClick(View view) {

					        	Intent intent = new Intent(MainActivity.this, ServerDetail.class);
					        	Bundle b = new Bundle();
					        	b.putString("servername", serverName); 
					        	
					        	b.putSerializable("services", sendMap);
					        	//b.put
					        	intent.putExtras(b); //Put your id to your next Intent
					        	startActivity(intent);
					        	//finish();					        	
					        	/*Toast.makeText(view.getContext(),
					                    "Button clicked index = " + serverName, Toast.LENGTH_SHORT)
					                    .show();
					                    	*/
					        }
					    });				        
						
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

		//private final String URL = calHttpLocation.concat(endParams);
		private final String URL = "http://63.226.80.152:8080/nagios/curCal.xml";
		
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
				Log.d("TREK", "size of cal entry right after update " + calHash.size() );
				updateCalendar();				
				
			}
			

			if (null != mClient)
				mClient.close();
			//setListAdapter(new ArrayAdapter<String>(
			//		NetworkingAndroidHttpClientXMLActivity.this,
			//		R.layout.list_item, result));
		}

	}	
	

	private class SoundHttpGetTask extends AsyncTask<Void, Void, HashMap<String, Integer>> {

		//private final String URL = calHttpLocation.concat(endParams);
		private final String URL = "http://63.226.80.152:8080/nagios/soundlevel.xml";
		
		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
		
		
		@Override
		protected HashMap<String, Integer> doInBackground(Void... params) {

			Log.d("CURDEBUG", "Running Sound HTTP Grab! BEFORE!!" + URL);
			
			HttpGet request = new HttpGet(URL);
			Log.d("TREK", "Running Sound HTTP Grab! BEFORE!!");
			XMLSoundResponseHandler responseHandler = new XMLSoundResponseHandler();
			
			try {
				
				return (HashMap<String, Integer>) mClient.execute(request, responseHandler);
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute( HashMap<String, Integer> result) {
			
			if (result == null) {
				Log.d("TREKSOUND", "result is null.. nuts");
			} else {
				Log.d("TREKSOUND", "Got to onpost execute (Sound)!");
				HashMap<String, Integer> resSoundMap = (HashMap<String, Integer>) result;
				soundHash = resSoundMap;
				updateSound();				
				
			}
			


			if (null != mClient)
				mClient.close();
			//setListAdapter(new ArrayAdapter<String>(
			//		NetworkingAndroidHttpClientXMLActivity.this,
			//		R.layout.list_item, result));
		}

	}		
	
	private class TaskHttpGetTask extends AsyncTask<Void, Void, List<Map<String, String>>> {

		//private final String URL = calHttpLocation.concat(endParams);
		private final String URL = "http://63.226.80.152:8080/nagios/tasks.xml";
		
		AndroidHttpClient mClient = AndroidHttpClient.newInstance("");
		
		
		@Override
		protected List<Map<String, String>> doInBackground(Void... params) {

			Log.d("CURDEBUG", "Running Task HTTP Grab! BEFORE!!" + URL);
			
			HttpGet request = new HttpGet(URL);
			Log.d("TREK", "Running Task HTTP Grab! BEFORE!!");
			XMLTaskResponseHandler responseHandler = new XMLTaskResponseHandler();
			
			try {
				
				return (List<Map<String, String>>) mClient.execute(request, responseHandler);	//TODO
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute( List<Map<String, String>> result) {
			
			if (result == null) {
				Log.d("TREKTASK", "result is null.. nuts");
			} else {
				Log.d("TREKTASK", "Got to onpost execute (Sound)!");
				List<Map<String, String>> resTaskList = (List<Map<String, String>>) result;
				taskList = resTaskList;
				updateTasks();				
				
			}
			


			if (null != mClient)
				mClient.close();
			//setListAdapter(new ArrayAdapter<String>(
			//		NetworkingAndroidHttpClientXMLActivity.this,
			//		R.layout.list_item, result));
		}

		private void updateTasks() {
			// TODO Auto-generated method stub
			
			int MAX_DISPLAY = 5;
			
			TextViewTrek tvTasks = (TextViewTrek) findViewById(R.id.taskText);
			
			String tasksString = "";
			int x =0;
			for (Map<String, String> curTaskHash : taskList) {
				
				if (x < MAX_DISPLAY) {
					tasksString = tasksString + curTaskHash.get("title") + "  [" + curTaskHash.get("status") + "]\n";
				}
				x++;

				
			}			
			tvTasks.setText(tasksString);
			
		}

	}			
	
	//END
	
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

	public void updateSound() {
		
		Log.d("TREKSOUND", "updatesound() called" );
		
		RelativeLayout mainViewScreen = (RelativeLayout) findViewById(R.id.mainViewScreen);
		if (soundHash.size() > 0 ) {
			//debugSoundLevel
			TextViewTrek tvDBSound = (TextViewTrek) findViewById(R.id.debugSoundLevel);
			int dbSound = (int) soundHash.get("irritation_level");
			tvDBSound.setText(Integer.toString(dbSound));

/*			Context context = getApplicationContext();
			CharSequence text = "Updating Sound!";
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(context, text, duration);
			toast.show();*/
			
			ImageView radImage = (ImageView) findViewById(R.id.imageRadLevel);
			
			if (dbSound == 0) {
				radImage.setImageResource(R.drawable.rad_green);
			} else if (dbSound == 1) {
				radImage.setImageResource(R.drawable.rad_blue);
			} else if (dbSound == 2) {
				radImage.setImageResource(R.drawable.rad_yellow);
			} else if (dbSound == 3) {
				radImage.setImageResource(R.drawable.rad_yellow);
			} else if (dbSound == 4) {
				radImage.setImageResource(R.drawable.rad_mauve);
			} else if (dbSound == 5) {
				radImage.setImageResource(R.drawable.rad_red);
			} else {
				radImage.setImageResource(R.drawable.rad_yellow);
			}
			
			radImage.setVisibility(View.VISIBLE);
			
		}
	}

	private void updateCalendar() {
		RelativeLayout mainViewScreen = (RelativeLayout) findViewById(R.id.mainViewScreen);

		TreeMap<Date, String> todayMap = new TreeMap<Date, String>();
		TreeMap<Date, String> tommorrowMap = new TreeMap<Date, String>();
		
		
		Log.d("TREK", "size of cal entry" + calHash.size() );

    	Date curDate = new Date(System.currentTimeMillis());
    	Date tomDate = new Date(dtMili + (60 * 60 * 24 * 1000 * 1 ));
		
		if (calHash.size() > 0 ) {
			TreeMap<Date, String> calCopy = new TreeMap<Date, String>(calHash);
			Iterator it = calCopy.entrySet().iterator();
	
		    int curCount = 0;
		    int numberOfItems = calHash.size();
		    
		    SimpleDateFormat calformat = new SimpleDateFormat("HH:mm");
		    
		    String tvTodayStr = "";
		    String tommStr = "";
		    int maxCalDisplayCount = 4;
		    int todayCount = 0;
		    int tommCount = 0;
		    
		    boolean updatedNextMeeting = false;
		    
		    while (it.hasNext()) {

		        Map.Entry pairs = (Map.Entry)it.next();
		        String eventName =  (String) pairs.getValue();
		        
		        Date calDate = (Date) pairs.getKey(); 
		        
		        if (googleMinMaxForamt.format(calDate).toString().equals( googleMinMaxForamt.format(curDate).toString()  ))  {
		    		Log.d("TREK", "State : Date is Today!");
		    		if (calDate.compareTo( curDate) > 0) {	// still in the future; don't care about old
		    		//if (1 == 1) {	// still in the future; don't care about old
//		    			todayMap.put(calDate, eventName);
		    			if (updatedNextMeeting == false) {
		    				updateNextMeetingTime(calDate);
		    				Log.d("TREK", "Updating Next Meeting!" + calformat.format(calDate));
		    				updatedNextMeeting = true;
		    			}
		    			
		    			if (todayCount < maxCalDisplayCount) {
			    	
		    				if (todayCount > 0) {
			    				tvTodayStr = tvTodayStr + "\n";
			    			}

			    			tvTodayStr = tvTodayStr + calformat.format(calDate) + " " + eventName ;
			    			todayCount++;

		    			}
		    		
		    		}
		    		
		    		
		    	} 
		        
		        
		    	if (googleMinMaxForamt.format(calDate).toString().equals( googleMinMaxForamt.format(tomDate).toString()  ))  {
		    		Log.d("TREK", "State : Date is tomrrow!!!");

	    			if (updatedNextMeeting == false) {
	    				Log.d("TREKDATE", "Updateing next meeting ...");
	    				updateNextMeetingTime(calDate);
	    				updatedNextMeeting = true;
	    			}
	    			if (todayCount + tommCount < maxCalDisplayCount) {
				    	
	    				if (tommCount > 0) {
	    					tommStr = tommStr + "\n";
		    			}

	    				tommStr = tommStr + calformat.format(calDate) + " " + eventName ;
		    			tommCount++;

	    			}

	    			
		    	} 
		        
		        //tvToday.setText(eventName);
		        
		        it.remove(); // avoids a ConcurrentModificationException

		    }					
			

		    TextViewTrek tvToday = (TextViewTrek) findViewById(R.id.textViewToday);
		    if (tvTodayStr.length() > 0) {
			    tvToday.setText(tvTodayStr);
		    } else {
		    	tvToday.setText("Clear");
		    }
		    
		    TextViewTrek tvTomm = (TextViewTrek) findViewById(R.id.textViewTomorrow);
		    if (tommStr.length() > 0) {
			    tvTomm.setText(tommStr);
		    } else {
		    	tvTomm.setText("Clear");
		    }
		    
		} else {
			Log.d("TREK", "size of entry is 0" + calHash.size() );
		}
	}
    
    
    
    private void updateNextMeetingTime(Date calDate) {

    	RelativeLayout mainViewScreen = (RelativeLayout) findViewById(R.id.mainViewScreen);
    	SimpleDateFormat calformat = new SimpleDateFormat("HH:mm");    	
		long dtMili = System.currentTimeMillis();
		Date curDate = new Date(dtMili);
    	
    	long diff = calDate.getTime() - curDate.getTime();
		
    	int diffDays = (int) (diff / (24 * 60 * 60 * 1000));
		diff = diff - (long) diffDays * (24 * 60 * 60 * 1000);
		
		int diffHours = (int) (diff / (60 * 60 * 1000));
		diff = diff - (long) diffHours * (60 * 60 * 1000);
		
		int diffMins = (int) (diff / (60 * 1000));
		
		String timeToNextStr = "";
		if (diffDays > 0) {
			timeToNextStr = timeToNextStr +  Integer.toString(diffDays) + " Days ";
		}
		if (diffHours > 0) {
			timeToNextStr = timeToNextStr +  Integer.toString(diffHours) + " Hrs ";
		}
		if (diffMins > 0) {
			timeToNextStr = timeToNextStr +  Integer.toString(diffMins) + " Min";
		}

	    //TextViewTrek tvNextMeeting = (TextViewTrek) findViewById(R.id.textViewTrekDate);
		TextViewTrek tvNextMeeting = (TextViewTrek) findViewById(R.id.textViewNextMeeting);
	    tvNextMeeting.setText(timeToNextStr);
	    //tvNextMeeting.setText("blej");
	    Log.d("TREKDATE", "Writing Next Time: " + timeToNextStr + " From: " + calformat.format(calDate)  + " and " + calformat.format(curDate));
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
