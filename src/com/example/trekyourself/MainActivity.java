package com.example.trekyourself;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ScheduledFuture;

import android.os.Handler;

public class MainActivity extends Activity {

	public Handler mHandler = new Handler() {
	    public void handleMessage(Message msg) {
	        updateTime();
	    }
	};	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
        setContentView(R.layout.primary_layout);	//textViewTrekDate
        
        /*if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/
        
        ScheduledThreadPoolExecutor sch = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(5);
        Runnable periodicTask = new Runnable(){
            @Override
            public void run() {
                try{
                	mHandler.obtainMessage(1).sendToTarget();
                    Thread.sleep(30 * 1000);

                } catch(Exception e){
                     
                }
            }
        };        
        ScheduledFuture<?> periodicFuture = sch.scheduleAtFixedRate(periodicTask, 5, 5, TimeUnit.SECONDS);
        //updateTime();
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
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

}
