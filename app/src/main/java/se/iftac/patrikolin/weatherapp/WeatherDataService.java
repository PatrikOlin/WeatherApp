package se.iftac.patrikolin.weatherapp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.io.InputStream;

/**
 * Created by Vegeterran on 2018-03-18.
 */

public class WeatherDataService extends Service implements Runnable {

    private boolean runWorkerThread = true;
    private long updateFrequency = 1;
    private static WeatherDataService instance = null;

    public static WeatherDataService getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void onDestroy() {
        runWorkerThread = false;
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void run() {

        HttpHandler handler;
        String res = "";

        while(runWorkerThread) {
            String url = "https://api.met.no/weatherapi/locationforecast/1.9/?lat=61.72;lon=17.10";

            handler = new HttpHandler();
            InputStream is = handler.callServer(url);
            if(is != null) {
                res = handler.streamToString(is);
            } else {
                res = "Not connected";
            }

            //Broadcast to all listeners
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("se.iftac.patrikolin.weatherapp.BROADCAST");
            broadcastIntent.putExtra("forecasts", res);
            sendBroadcast(broadcastIntent);
            for(int i = 0; i < updateFrequency; i++){
                try {
                    Thread.sleep(1000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void setUpdateFrequency(int numberOfMinutes) {
        this.updateFrequency = numberOfMinutes * 60;
    }

    public class MyBinder extends Binder {
        public void setUpdateFrequency(int minutes){
            if(minutes > 0 && minutes <= 60) {
                WeatherDataService.this.setUpdateFrequency(minutes);
            }
        }
    }
}
