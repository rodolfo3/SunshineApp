package xyz.rodolfo.sunshine;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment {
    protected static String TAG = ForecastFragment.class.getSimpleName();
    protected ArrayAdapter<String> forecastAdapter;

    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onStart(){
        super.onStart();
        refresh();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.forcast_menu, menu);

        menu.findItem(R.id.action_location).setEnabled(isLocationIntentAvailable());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "Item selected: " + Integer.toString(id));

        switch (id) {
            case R.id.action_refresh:
                refresh();
                return true;
            case R.id.action_settings:
                settings();
                return true;
            case R.id.action_location:
                configureLocation();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void settings() {
        Intent settings = new Intent(getActivity(), SettingsActivity.class);
        startActivity(settings);
    }

    protected Intent getLocationIntent() {
        Intent location = new Intent(Intent.ACTION_VIEW);
        location.setData(Uri.parse("geo:0,0?q=" + getLocationPreference()));

        return location;
    }

    protected boolean isLocationIntentAvailable() {
        return getLocationIntent().resolveActivity(getActivity().getPackageManager()) != null;
    }

    protected void configureLocation() {
        if (isLocationIntentAvailable()) {
            startActivityForResult(getLocationIntent(), 1);
        }
    }

    protected String getLocationPreference() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        return settings.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );
    }

    protected void refresh() {
        ForecastFetcherTask d = new ForecastFetcherTask() {
            protected void onPostExecute(String[] result) {
                forecastAdapter.clear();
                forecastAdapter.addAll(result);
            }
        };
        String postCode = getLocationPreference();
        // Toast.makeText(getActivity(), postCode, Toast.LENGTH_LONG).show();
        d.execute(postCode);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        forecastAdapter = new ArrayAdapter<String>(
                getActivity(),
                R.layout.list_item_forecast,
                R.id.list_item_forecast_textview,
                new ArrayList<String>()
        );

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        listView.setAdapter(forecastAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = forecastAdapter.getItem(position);
                Intent viewDetails = new Intent(getActivity(), DetailForecastActivity.class);
                viewDetails.putExtra(Intent.EXTRA_TEXT, item);
                startActivity(viewDetails);
                // Toast.makeText(getActivity(), item, Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }



    class ForecastFetcherTask extends AsyncTask<String, Void, String[]> {
        protected final String TAG = ForecastFetcherTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
            return loadData(params[0]);
        }

        protected String[] loadData(String postCode) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;

            int daysToLoad = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                Uri uri = new Uri.Builder()
                        .scheme("http")
                        .authority("openweathermap.org")
                        .path("/data/2.5/forecast/daily")
                        .appendQueryParameter("mode", "json")
                        .appendQueryParameter("units", "metric")
                        .appendQueryParameter("cnt", Integer.toString(daysToLoad))
                        .appendQueryParameter("q", postCode)
                        .build();

                URL url = new URL(uri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return (new WeatherDataParser(getActivity())).getWeatherDataFromJson(forecastJsonStr, daysToLoad);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing json", e.getCause());
                return null;
            }
        }
    }
}