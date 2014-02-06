package com.ignite.boycott;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.InstantiationException;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        CatalogFragment.CatalogCallbacks,
        HistoryFragment.HistoryCallbacks {

    public enum Fragments {
        CATALOG(0, R.string.title_section2),
        HISTORY(1, R.string.title_section3);
        public final int n;
        public final int titleId;

        Fragments(int n, int titleId) {
            this.n = n;
            this.titleId = titleId;
        }

        public static Fragments valueOf(int i) {
            switch(i) {
                case 0 : return CATALOG;
                case 1 : return HISTORY;
                default : throw new IllegalStateException("Unknown fragment no #" + i);
            }
        }
    }
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private Map<String, Class<? extends Fragment>> drawerFragmentClassMap;
    private Map<String, Fragment> drawerFragmentMap = new HashMap<>();
    private BlacklistDao mBlacklist;
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!BuildConfig.DEBUG) Crashlytics.start(this);

        setUpNavigationDrawerElements();
        setContentView(R.layout.activity_main);

        mTitle = getTitle();
        mBlacklist = new BlacklistDao(this);

        // Set up the drawer.
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    private void setUpNavigationDrawerElements() {
        drawerFragmentClassMap = new HashMap<>();
        drawerFragmentClassMap.put(fragmentTag(Fragments.CATALOG), CatalogFragment.class);
        drawerFragmentClassMap.put(fragmentTag(Fragments.HISTORY), HistoryFragment.class);
    }

    @Override
    public void onNavigationDrawerItemSelected(Fragments position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        String fragmentTag = fragmentTag(position);

        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (fragment == null) {
                fragment = drawerFragmentMap.get(fragmentTag);
        } else {
            if (!drawerFragmentMap.containsKey(fragmentTag)) {
                drawerFragmentMap.put(fragmentTag, fragment);
            }
        }
        if (fragment == null) {
            fragment = newInstance(drawerFragmentClassMap.get(fragmentTag));
            drawerFragmentMap.put(fragmentTag, fragment);
        }
        if (fragment == null) {
            throw new RuntimeException("Fragment " + fragmentTag + " could not be created nor found!");
        }

        if (findViewById(R.id.maker_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-large and
            // res/values-sw600dp). If this view is present, then the
            // activity should be in two-pane mode.
            mTwoPane = true;

            // In two-pane mode, list items should be given the
            // 'activated' state when touched.
            ((CatalogFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.item_list))
                    .setActivateOnItemClick(true);
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment, fragmentTag)
                .commit();
    }

    private String fragmentTag(Fragments position) {
        return MainActivity.class.getName() + "." + position;
    }

    private<T> T newInstance(Class<? extends T> aClass) {
        try {
            return aClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void onSectionAttached(Fragments position) {
        mTitle = getString(position.titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult == null) {
            Toast.makeText(this, getString(R.string.scan_failed), Toast.LENGTH_SHORT).show();
            return;
        }

        String code = scanResult.getContents();
        if (code == null) {
            Toast.makeText(this, getString(R.string.scan_cancelled), Toast.LENGTH_SHORT).show();
            return;
        }

        if (code.length() != 13) {
            Toast.makeText(this, getString(R.string.code_too_short), Toast.LENGTH_LONG).show();
            return;
        }

        Intent scanResultsIntent = new Intent(this, ScanResultsActivity.class);
        scanResultsIntent.putExtra(ScanResultsActivity.BARCODE, code);
        startActivity(scanResultsIntent);
    }

    @Override
    public void onMakerSelected(long blacklistId) {
        BlacklistedMaker maker = mBlacklist.getBlacklistedMaker(blacklistId);
        MakerDetailsFragment fragment = MakerDetailsFragment.newInstance(maker);
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.maker_detail_container, fragment)
                    .commit();
        } else {
            // In single-pane mode, simply start the detail activity
            // for the selected item ID.
            Intent detailIntent = new Intent(this, MakerDetailActivity.class);
            detailIntent.putExtra(MakerDetailsFragment.MAKER, maker);
            startActivity(detailIntent);
        }
    }
}
