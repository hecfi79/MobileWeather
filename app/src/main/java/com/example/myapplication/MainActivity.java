package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private TextView logo, resultinfo;
    private EditText town;
    private Button getweather;

    public TextView getLogo() {
        return this.logo;
    }

    public TextView getResultinfo() {
        return this.resultinfo;
    }

    public EditText getTown() {
        return this.town;
    }

    public Button getButton() {
        return this.getweather;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        town = findViewById(R.id.town);
        logo = findViewById(R.id.logo);
        resultinfo = findViewById(R.id.resultinfo);
        getweather = findViewById(R.id.getweather);

        getweather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(town.getText().toString().trim().equals("")) { Toast.makeText(MainActivity.this, R.string.empty, Toast.LENGTH_LONG).show(); }
                else {
                    new GetRequest().execute(
                            "https://api.openweathermap.org/data/2.5/weather?q=" +
                            town.getText().toString() +
                            "&appid=" +
                            "14d39398cffc85b3076e47f93fc06bae" +
                            "&units=metric&lang=en"
                    );
                }
            }
        });
    }

    private class GetRequest extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            resultinfo.setText("Loading...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String l = "";
                while((l = reader.readLine()) != null) { buffer.append(l).append("\n"); }
                return buffer.toString();
            }
            catch (MalformedURLException e) { e.printStackTrace(); }
            catch (IOException e) { e.printStackTrace(); }
            finally {
                if (connection != null) { connection.disconnect(); }
                try { if (reader != null) { reader.close(); } }
                catch (IOException e) { e.printStackTrace(); }
            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject obj = new JSONObject(result);
                resultinfo.setText(
                        "Temperature: " + obj.getJSONObject("main").getDouble("temp") + "*C\n" +
                        "Description: " + obj.getJSONArray("weather").getJSONObject(0).getString("description") + "\n" +
                        "Feels like: " + obj.getJSONObject("main").getDouble("feels_like") + "*C\n" +
                        "Min. temperature: " + obj.getJSONObject("main").getDouble("temp_min") + "*C\n" +
                        "Max. temperature: " + obj.getJSONObject("main").getDouble("temp_max") + "*C\n" +
                        "Pressure: " + obj.getJSONObject("main").getDouble("pressure") + "\n" +
                        "Humidity: " + obj.getJSONObject("main").getDouble("humidity") + "\n" +
                        "Visibility: " + obj.getInt("visibility") + "\n" +
                        "Wind speed: " + obj.getJSONObject("wind").getDouble("speed") + "\n" +
                        "Wind deg: " + obj.getJSONObject("wind").getInt("deg") + "\n" +
                        "Wind gust: " + obj.getJSONObject("wind").getInt("gust")
                );
            }
            catch (JSONException e) { e.printStackTrace(); }
        }
    }
}