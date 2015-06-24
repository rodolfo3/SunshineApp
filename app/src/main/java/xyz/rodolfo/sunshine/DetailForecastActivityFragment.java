package xyz.rodolfo.sunshine;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.ActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailForecastActivityFragment extends Fragment {
    protected static final String TAG = DetailForecastActivityFragment.class.getSimpleName();

    protected static final String shareHashTag = "#sunshineapp";
    protected String shareableContent;

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

        shareableContent = text;

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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_detail_forecast, menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);

        //  MenuItemCompat.getActionProvider(menuItem) always returns null in a fragment.
        ShareActionProvider shareActionProvider = new ShareActionProvider(getActivity());
        MenuItemCompat.setActionProvider(menuItem, shareActionProvider);
        shareActionProvider.setShareIntent(getShareIntent());
    }

    protected Intent getShareIntent(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, shareableContent + " " + shareHashTag);
        share.setType("plain/text");
        return share;
    }

    protected void settings() {
        Intent settings = new Intent(getActivity(), SettingsActivity.class);
        startActivity(settings);
    }
}
