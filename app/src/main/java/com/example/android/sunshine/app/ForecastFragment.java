package com.example.android.sunshine.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by zhaowenhao on 16/5/17.
 */
public class ForecastFragment extends Fragment {

    public ForecastFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ArrayList<String> mForecastList = new ArrayList<String>();
        for(int i = 0; i < 100; i++){
            mForecastList.add("Today " + i);
        }

        ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_forecast, R.id.list_item_forecast_text_view, mForecastList);

        ListView forecastListView = (ListView) rootView.findViewById(R.id.list);
        forecastListView.setAdapter(mForecastAdapter);

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String forecastJsonStr = null;

        try{
            URL url = new URL("http://api.openweathermap.org/data/2.5/forecast?q=518057,cn&mode=json&cnt=7&units=metric");
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null){
                forecastJsonStr = null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line = reader.readLine()) != null){
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0){
                forecastJsonStr = null;
            }

            forecastJsonStr = buffer.toString();

        } catch (IOException e){
            Log.e("PlaceholderFragment", "Error", e);
            forecastJsonStr = null;
        } finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }

            if (reader != null){
                try {
                    reader.close();
                } catch (final IOException e){
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }


        return rootView;
    }
}