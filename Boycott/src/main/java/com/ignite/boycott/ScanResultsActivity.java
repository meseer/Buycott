package com.ignite.boycott;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.ignite.buycott.R;

public class ScanResultsActivity extends ActionBarActivity implements MakerNotFoundFragment.MakerNotFoundCallbacks,
        ScanResultsFragment.ScanResultCallbacks {

    public static final String BARCODE = "barcode";
    private String mBarcode;
    private Makers mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_results);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = Makers.instance(this);
        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        //TODO: Check if it's ok to use db with different context, perhaps - not!
        if (savedInstanceState == null) {
            mBarcode = getIntent().getStringExtra(BARCODE);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, createFragment())
                    .commit();
        }
    }

    private Fragment createFragment() {
        Cursor cursor = mDb.getProduct(mBarcode);
        if (cursor.getCount() > 0) {
            ScanResultsFragment f = new ScanResultsFragment();
            f.onScanResult(cursor);
            return f;
        } else {
            return MakerNotFoundFragment.newInstance(mBarcode);
        }
    }

    @Override
    public void reportMakerNotFound(String barcode, String maker, String product) {
        Crashlytics.logException(new MakerNotFoundException(barcode, maker, product));
        this.finish();
        Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.scan_results, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void reportMistake(String barcode) {

    }
}
