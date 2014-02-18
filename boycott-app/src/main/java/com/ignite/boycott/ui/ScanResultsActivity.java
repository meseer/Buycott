package com.ignite.boycott.ui;

import android.content.Intent;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ignite.boycott.R;
import com.ignite.boycott.dao.HistoryDao;
import com.ignite.boycott.dao.ProductsDao;
import com.ignite.boycott.dao.model.HistoryEntry;
import com.ignite.boycott.dao.model.Product;
import com.ignite.boycott.dao.model.MakerFrequency;
import com.ignite.boycott.io.model.BoycottList;
import com.ignite.boycott.loader.BlacklistLoader;
import com.ignite.boycott.reporting.Reporting;
import com.ignite.boycott.reporting.crashlytics.CrashlyticsReporting;

import java.util.ArrayList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

//TODO: Allow opening navigation drawer from here ??
public class ScanResultsActivity extends ActionBarActivity implements MakerNotFoundFragment.MakerNotFoundCallbacks,
        ScanResultsFragment.ScanResultCallbacks, LoaderManager.LoaderCallbacks<BoycottList> {

    public static final String BARCODE = "barcode";
    private final Reporting reporting = new CrashlyticsReporting();
    private String mBarcode;
    private ProductsDao mDb;
    private HistoryDao historyDao;
    private BoycottList mBlacklist;
    private Semaphore blacklistAvailable = new Semaphore(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_results);

        // Show the Up button in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mDb = ProductsDao.instance(getApplicationContext());
        historyDao = HistoryDao.instance(getApplicationContext());

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            Intent intent = getIntent();
            if (intent != null) {
                IntentIntegrator integrator = new IntentIntegrator(this);
                integrator.initiateScan(IntentIntegrator.PRODUCT_CODE_TYPES);
            }
        }

        getSupportLoaderManager().initLoader(0, null, this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            Toast.makeText(this, getString(R.string.scan_failed), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        mBarcode = scanResult.getContents();
        if (mBarcode == null) {
            Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (mBarcode.length() != 13) {
            Toast.makeText(this, getString(R.string.code_too_short), Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        getSupportFragmentManager().beginTransaction()
                .add(R.id.scan_results_container, createFragment())
                .commit();
    }

    private Fragment createFragment() {
        Product product = mDb.getProduct(mBarcode);
        if (product != null) {
            //barcode in the database => show product page
            //check blacklisted status according to maker code, not maker name (create mapping between blacklisted maker name and codes)
            //TODO: Add to History in background
            historyDao.log(new HistoryEntry(product));
            return ScanResultsFragment.newInstance(product, getBoycottList());
        }

        //TODO: Add to History in background
        historyDao.log(mBarcode);
        ArrayList<MakerFrequency> makers = mDb.getMakers(mBarcode);
        if (makers != null && !makers.isEmpty()) {
            //barcode not in the database => show blacklist status for the maker names related to the maker code (sorted), add option to choose correct maker name (or write) and specify product name
            return ScanResultsFragment.newInstance(makers, mBarcode, getBoycottList());
        }

        //barcode not in the database and maker is unknown => show MakerNotFounFragment
        return MakerNotFoundFragment.newInstance(mBarcode);
    }

    @Override
    public void reportMakerNotFound(String barcode, String maker, String product, Boolean blacklisted) {
        reporting.reportMakerNotFound(barcode, maker, product, blacklisted);
        Toast.makeText(this, R.string.thank_you, Toast.LENGTH_SHORT).show();
        finish();
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
        if (id == R.id.action_scan) {
            Intent scanResultsIntent = new Intent(this, ScanResultsActivity.class);
            startActivity(scanResultsIntent);
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
            NavUtils.navigateUpTo(this, new Intent(this, BoycottActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<BoycottList> onCreateLoader(int i, Bundle bundle) {
        return BlacklistLoader.instance(getApplicationContext());
    }

    @Override
    public void onLoadFinished(Loader<BoycottList> loader, BoycottList list) {
        this.mBlacklist = list;
        blacklistAvailable.release();
    }

    @Override
    public void onLoaderReset(Loader<BoycottList> loader) {
        this.mBlacklist = null;
        blacklistAvailable.acquireUninterruptibly();
    }

    private BoycottList getBoycottList() {
        try {
            blacklistAvailable.tryAcquire(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Toast.makeText(getApplicationContext(), getString(R.string.owner_loading_timeout), Toast.LENGTH_SHORT).show();
            return null;
        }

        try {
            return mBlacklist;
        } finally {
            blacklistAvailable.release();
        }
    }

    @Override
    public void reportMistake(String barcode) {

    }
}
