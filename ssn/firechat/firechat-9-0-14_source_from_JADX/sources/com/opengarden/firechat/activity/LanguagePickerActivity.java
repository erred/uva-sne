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
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.adapters.LanguagesAdapter;
import com.opengarden.firechat.adapters.LanguagesAdapter.OnSelectLocaleListener;
import com.opengarden.firechat.util.ThemeUtils;
import java.util.Locale;

public class LanguagePickerActivity extends RiotAppCompatActivity implements OnSelectLocaleListener, OnQueryTextListener {
    private LanguagesAdapter mAdapter;
    /* access modifiers changed from: private */
    public View mLanguagesEmptyView;
    private SearchView mSearchView;

    public int getLayoutRes() {
        return C1299R.layout.activity_langagues_picker;
    }

    public int getTitleRes() {
        return C1299R.string.settings_select_language;
    }

    public boolean onQueryTextSubmit(String str) {
        return true;
    }

    public static Intent getIntent(Context context) {
        return new Intent(context, LanguagePickerActivity.class);
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
        initViews();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(C1299R.C1302menu.menu_languages_picker, menu);
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
        this.mLanguagesEmptyView = findViewById(C1299R.C1301id.languages_empty_view);
        RecyclerView recyclerView = (RecyclerView) findViewById(C1299R.C1301id.languages_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(1);
        recyclerView.setLayoutManager(linearLayoutManager);
        this.mAdapter = new LanguagesAdapter(VectorApp.getApplicationLocales(this), this);
        recyclerView.setAdapter(this.mAdapter);
    }

    private void filterLocales(String str) {
        this.mAdapter.getFilter().filter(str, new FilterListener() {
            public void onFilterComplete(int i) {
                LanguagePickerActivity.this.mLanguagesEmptyView.setVisibility(i > 0 ? 8 : 0);
            }
        });
    }

    public void onSelectLocale(Locale locale) {
        VectorApp.updateApplicationLocale(locale);
        setResult(-1);
        finish();
    }

    public boolean onQueryTextChange(String str) {
        filterLocales(str);
        return true;
    }
}
