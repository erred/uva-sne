package com.opengarden.firechat.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.support.p000v4.view.MenuItemCompat;
import android.support.p003v7.widget.LinearLayoutManager;
import android.support.p003v7.widget.RecyclerView;
import android.support.p003v7.widget.SearchView;
import android.support.p003v7.widget.SearchView.OnQueryTextListener;
import android.support.p003v7.widget.SearchView.SearchAutoComplete;
import android.support.p003v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Filter.FilterListener;
import com.google.firebase.analytics.FirebaseAnalytics.Event;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.CountryAdapter;
import com.opengarden.firechat.adapters.CountryAdapter.OnSelectCountryListener;
import com.opengarden.firechat.util.CountryPhoneData;
import com.opengarden.firechat.util.PhoneNumberUtils;
import com.opengarden.firechat.util.ThemeUtils;

public class CountryPickerActivity extends RiotAppCompatActivity implements OnSelectCountryListener, OnQueryTextListener {
    private static final String EXTRA_IN_WITH_INDICATOR = "EXTRA_IN_WITH_INDICATOR";
    private static final String EXTRA_OUT_CALLING_CODE = "EXTRA_OUT_CALLING_CODE";
    public static final String EXTRA_OUT_COUNTRY_CODE = "EXTRA_OUT_COUNTRY_CODE";
    public static final String EXTRA_OUT_COUNTRY_NAME = "EXTRA_OUT_COUNTRY_NAME";
    private CountryAdapter mCountryAdapter;
    /* access modifiers changed from: private */
    public View mCountryEmptyView;
    private RecyclerView mCountryRecyclerView;
    private SearchView mSearchView;
    private boolean mWithIndicator;

    public int getLayoutRes() {
        return C1299R.layout.activity_country_picker;
    }

    public int getTitleRes() {
        return C1299R.string.settings_select_country;
    }

    public boolean onQueryTextSubmit(String str) {
        return true;
    }

    public static Intent getIntent(Context context, boolean z) {
        Intent intent = new Intent(context, CountryPickerActivity.class);
        intent.putExtra(EXTRA_IN_WITH_INDICATOR, z);
        return intent;
    }

    public void initUiAndData() {
        Toolbar toolbar = (Toolbar) findViewById(C1299R.C1301id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowHomeEnabled(true);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }
        this.mWithIndicator = getIntent().getBooleanExtra(EXTRA_IN_WITH_INDICATOR, false);
        initViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1299R.C1302menu.menu_country_picker, menu);
        CommonActivityUtils.tintMenuIcons(menu, ThemeUtils.INSTANCE.getColor(this, C1299R.attr.icon_tint_on_dark_action_bar_color));
        MenuItem findItem = menu.findItem(C1299R.C1301id.action_search);
        if (findItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Event.SEARCH);
            this.mSearchView = (SearchView) MenuItemCompat.getActionView(findItem);
            this.mSearchView.setMaxWidth(Integer.MAX_VALUE);
            this.mSearchView.setSubmitButtonEnabled(false);
            this.mSearchView.setQueryHint(getString(C1299R.string.search_hint));
            this.mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
            this.mSearchView.setOnQueryTextListener(this);
            ((SearchAutoComplete) this.mSearchView.findViewById(C1299R.C1301id.search_src_text)).setHintTextColor(ThemeUtils.INSTANCE.getColor(this, C1299R.attr.default_text_hint_color));
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() != 16908332) {
            return super.onOptionsItemSelected(menuItem);
        }
        setResult(0);
        finish();
        return true;
    }

    public void onDestroy() {
        super.onDestroy();
        if (this.mSearchView != null) {
            this.mSearchView.setOnQueryTextListener(null);
        }
    }

    private void initViews() {
        this.mCountryEmptyView = findViewById(C1299R.C1301id.country_empty_view);
        this.mCountryRecyclerView = (RecyclerView) findViewById(C1299R.C1301id.country_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(1);
        this.mCountryRecyclerView.setLayoutManager(linearLayoutManager);
        this.mCountryAdapter = new CountryAdapter(PhoneNumberUtils.getCountriesWithIndicator(), this.mWithIndicator, this);
        this.mCountryRecyclerView.setAdapter(this.mCountryAdapter);
    }

    private void filterCountries(String str) {
        this.mCountryAdapter.getFilter().filter(str, new FilterListener() {
            public void onFilterComplete(int i) {
                CountryPickerActivity.this.mCountryEmptyView.setVisibility(i > 0 ? 8 : 0);
            }
        });
    }

    public void onSelectCountry(CountryPhoneData countryPhoneData) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_OUT_COUNTRY_NAME, countryPhoneData.getCountryName());
        intent.putExtra(EXTRA_OUT_COUNTRY_CODE, countryPhoneData.getCountryCode());
        intent.putExtra(EXTRA_OUT_CALLING_CODE, countryPhoneData.getCallingCode());
        setResult(-1, intent);
        finish();
    }

    public boolean onQueryTextChange(String str) {
        filterCountries(str);
        return true;
    }
}
