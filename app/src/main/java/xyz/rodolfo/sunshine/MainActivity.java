package xyz.rodolfo.sunshine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    protected String getLocationPreference() {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(this);
        return settings.getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default)
        );
    }

    protected Intent getLocationIntent() {
        Intent location = new Intent(Intent.ACTION_VIEW);
        location.setData(Uri.parse("geo:0,0?q=" + getLocationPreference()));

        return location;
    }

    protected boolean isLocationIntentAvailable() {
        return getLocationIntent().resolveActivity(getPackageManager()) != null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);

        menu.findItem(R.id.action_location).setEnabled(isLocationIntentAvailable());
        return true;
    }

    protected void showLocationOnMap() {
        if (isLocationIntentAvailable()) {
            startActivityForResult(getLocationIntent(), 1);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_location:
                showLocationOnMap();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
