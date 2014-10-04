package com.henkelsoft.trekyourself;

import java.io.IOException;

import android.media.MediaRecorder;
import android.media.MediaPlayer;

import android.util.Log;

public class SoundMeter {
    static final private double EMA_FILTER = 0.6;

    private MediaRecorder mRecorder = null;
    private double mEMA = 0.0;

    public void start() {
            if (mRecorder == null) {
                mRecorder = new MediaRecorder();
                mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null"); 
                try {
					mRecorder.prepare();
				} catch (IllegalStateException e) {
					Log.d("TREKSENSOR", "Logging Sensor. Got IllegalStateExceotuiibn " + e.getMessage());
					
				} catch (IOException e) {
					
					Log.d("TREKSENSOR", "Logging Sensor. IOException: " + e.getMessage());
				}
                mRecorder.start();
                mEMA = 0.0;
            }
    }

    public void stop() {
            if (mRecorder != null) {
                    mRecorder.stop();       
                    mRecorder.release();
                    mRecorder = null;
            }
    }

    public double getAmplitude() {
            if (mRecorder != null)
                    return  (mRecorder.getMaxAmplitude()/2700.0);
            else
                    return 0;

    }

    public double getAmplitudeEMA() {
            double amp = getAmplitude();
            mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
            return mEMA;
    }
}
