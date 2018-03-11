package se.iftac.patrikolin.weatherapp;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView temperature;
    private TextView humidity;
    private TextView precipitation;
    private TextView cloudiness;
    private ImageView weatherIcon;
    private Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        temperature = findViewById(R.id.textViewTemp);
        humidity = findViewById(R.id.textViewHumidity);
        precipitation = findViewById(R.id.textViewPrecipitation);
        cloudiness = findViewById(R.id.textViewCloudiness);
        weatherIcon = findViewById(R.id.imageView);
        refreshButton = findViewById(R.id.button);
        refreshButton.setOnClickListener(this);

        new getXmlFromServer().execute();
    }

    @Override
    public void onClick(View v) {
        new getXmlFromServer().execute();
    }

    class getXmlFromServer extends AsyncTask<String, Void, String> {

        HttpHandler handler;

        protected String doInBackground(String... strings) {
            String url = "https://api.met.no/weatherapi/locationforecast/1.9/?lat=61.72;lon=17.10";
            String res = "";

            handler = new HttpHandler();
            InputStream is = handler.callServer(url);
            if(is != null) {
                res = handler.streamToString(is);
            } else {
                res = "Not connected";
            }
            return res;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(result.equals("Not connected")) {
                Toast.makeText(getApplicationContext(), "Connection error", Toast.LENGTH_SHORT).show();
            } else {
                parseXml(result);
            }
        }
    }

    public void parseXml(String xmlString) {

        List<String> temperatureList = new ArrayList<>();
        List<String> humidityList = new ArrayList<>();
        List<String> precipitationMinList = new ArrayList<>();
        List<String> precipitationMaxList = new ArrayList<>();
        List<String> cloudList = new ArrayList<>();
        List<Integer> symbolList = new ArrayList<>();

        try {
            XmlPullParserFactory xmlFactoryObject = XmlPullParserFactory.newInstance();
            XmlPullParser parser = xmlFactoryObject.newPullParser();
            parser.setInput(new StringReader(xmlString));

            int event = parser.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                switch (event) {
                    case XmlPullParser.START_TAG:
                        String name = parser.getName();
                        if(name.equalsIgnoreCase("temperature")){
                            String temperatureValue = parser.getAttributeValue(null, "value");
                            temperatureList.add(temperatureValue);
                            // temperature.setText("temp: " +  temperatureValue);
                        }

                        else if(name.equalsIgnoreCase("humidity")) {
                            String humidityValue = parser.getAttributeValue(null, "value");
                            humidityList.add(humidityValue);
                            // humidity.setText("humidity: " + humidityValue + "%");
                        }

                        else if(name.equalsIgnoreCase("precipitation")) {
                            String precipitationMinValue = parser.getAttributeValue(null,"minvalue");
                            String precipitationMaxValue = parser.getAttributeValue(null, "maxvalue");
                            precipitationMinList.add(precipitationMinValue);
                            precipitationMaxList.add(precipitationMaxValue);
                            // / precipitation.setText("precipitation: " + precipitationMinValue + " mm to " + precipitationMaxValue + " mm");
                        }

                        else if(name.equalsIgnoreCase("cloudiness")) {
                            String cloudinessValue = parser.getAttributeValue(null, "percent");
                            cloudList.add(cloudinessValue);
                            // cloudiness.setText("cloudiness: " + cloudinessValue + "%");
                        }

                        else if(name.equalsIgnoreCase("symbol")) {
                            int number = Integer.parseInt(parser.getAttributeValue(null, "number"));
                            symbolList.add(number);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                event = parser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        temperature.setText(temperatureList.get(0) + "\u2103");
        humidity.setText("Humidity: " + humidityList.get(0) + "%");
        precipitation.setText("Precipitation: " + precipitationMinList.get(0) + " mm to " + precipitationMaxList.get(0) + " mm");
        cloudiness.setText("Cloudiness: " + cloudList.get(0) + "%");

        switch (symbolList.get(0)){
            case 1:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.sunny));
                break;
            case 2:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.light_cloud));
                break;
            case 3:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.partly_cloudy));
                break;
            case 4:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.cloudy));
                break;
            case 5:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.light_rain_sun));
                break;
            case 10:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.rain));
                break;
            case 13:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.snow));
                break;
            case 15:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.fog));
                break;
            default:
                weatherIcon.setImageDrawable(getResources().getDrawable(R.drawable.na));
                break;
        }
    }

}
