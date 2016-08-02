package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by zhaowenhao on 16/7/25.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int DETAIL_FORECAST_LOADER = 0;

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";

    private static final int COL_WEATHER_ID = 9;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_WIND_DEGREE = 7;
    private static final int COL_WEATHER_PRESSURE = 8;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
    };

    ShareActionProvider mShareActionProvider;
    private static String mForecastStr;

    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        getLoaderManager().initLoader(DETAIL_FORECAST_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle){
        Intent intent = getActivity().getIntent();
        if (intent != null){
            Uri detail_uri = Uri.parse(intent.getDataString());
            return new CursorLoader(
                    getActivity(),
                    detail_uri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor){
        if(!cursor.moveToFirst()){
            return;
        }

        int weatherId = cursor.getInt(COL_WEATHER_ID);
        Log.d(LOG_TAG, "onLoadFinished: weatherID: " + weatherId);
        String dateString = Utility.formatDate(cursor.getLong(COL_WEATHER_DATE));
        String weatherDescription = cursor.getString(COL_WEATHER_DESC);
        boolean isMetric = Utility.isMetric(getActivity());
        String high = Utility.formatTemperature(getActivity().getApplicationContext(), cursor.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity().getApplicationContext(), cursor.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        /*
        mForecastStr = String.format("%s - %s - %s/%s", dateString, weatherDescription, high, low);
        TextView detailTextView = (TextView) getView().findViewById(R.id.detail_text);
        detailTextView.setText(mForecastStr);
        */

        TextView julianDayTextView = (TextView) getView().findViewById(R.id.julian_day);
        TextView dateTextView = (TextView) getView().findViewById(R.id.date);
        TextView highTempTextView = (TextView) getView().findViewById(R.id.high_temp);
        TextView lowTempTextView = (TextView) getView().findViewById(R.id.low_temp);
        TextView humidityTextView = (TextView) getView().findViewById(R.id.humidity);
        TextView windTextView = (TextView) getView().findViewById(R.id.wind);
        TextView pressureTextView = (TextView) getView().findViewById(R.id.pressure);
        TextView descriptionTextView = (TextView) getView().findViewById(R.id.description);
        ImageView iconImageView = (ImageView) getView().findViewById(R.id.icon);

        julianDayTextView.setText(Utility.getFriendlyDayString(getActivity().getApplicationContext(), cursor.getLong(COL_WEATHER_DATE)));
        highTempTextView.setText(high);
        lowTempTextView.setText(low);
        humidityTextView.setText(getActivity().getString(R.string.format_humidity, cursor.getFloat(COL_WEATHER_HUMIDITY)));

        windTextView.setText(Utility.getFormattedWind(getActivity().getApplicationContext(), cursor.getFloat(COL_WEATHER_WIND_SPEED),cursor.getFloat(COL_WEATHER_WIND_DEGREE)));
        pressureTextView.setText(getActivity().getString(R.string.format_pressure, cursor.getFloat(COL_WEATHER_PRESSURE)));
        descriptionTextView.setText(cursor.getString(COL_WEATHER_DESC));
        iconImageView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
        iconImageView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecastStr + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }
}