package xyz.rodolfo.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailForecastActivityFragment extends Fragment {
    protected static final String TAG = DetailForecastActivityFragment.class.getSimpleName();

    public DetailForecastActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Intent intent = getActivity().getIntent();

        String text = intent.getStringExtra(Intent.EXTRA_TEXT);
        View ui = inflater.inflate(R.layout.fragment_detail_forecast, container, false);

        TextView textView = (TextView) ui.findViewById(R.id.detailText);
        textView.setText(text);

        return ui;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.d(TAG, "Item selected: " + Integer.toString(id));

        switch (id) {
            case R.id.action_settings:
                settings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void settings() {
        Intent settings = new Intent(getActivity(), SettingsActivity.class);
        startActivity(settings);
    }
}
