package com.opengarden.firechat.adapters;

import android.support.p003v7.widget.RecyclerView.Adapter;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.Filterable;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.util.CountryPhoneData;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class CountryAdapter extends Adapter<CountryViewHolder> implements Filterable {
    /* access modifiers changed from: private */
    public final List<CountryPhoneData> mFilteredList;
    /* access modifiers changed from: private */
    public final List<CountryPhoneData> mHumanCountryData;
    /* access modifiers changed from: private */
    public final OnSelectCountryListener mListener;
    /* access modifiers changed from: private */
    public final boolean mWithIndicator;

    class CountryViewHolder extends ViewHolder {
        final TextView vCallingCode;
        final TextView vCountryName;

        private CountryViewHolder(View view) {
            super(view);
            this.vCountryName = (TextView) view.findViewById(C1299R.C1301id.country_name);
            this.vCallingCode = (TextView) view.findViewById(C1299R.C1301id.country_calling_code);
        }

        /* access modifiers changed from: private */
        public void populateViews(final CountryPhoneData countryPhoneData) {
            this.vCountryName.setText(countryPhoneData.getCountryName());
            if (CountryAdapter.this.mWithIndicator) {
                this.vCallingCode.setText(countryPhoneData.getFormattedCallingCode());
            }
            this.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    CountryAdapter.this.mListener.onSelectCountry(countryPhoneData);
                }
            });
        }
    }

    public interface OnSelectCountryListener {
        void onSelectCountry(CountryPhoneData countryPhoneData);
    }

    public CountryAdapter(List<CountryPhoneData> list, boolean z, OnSelectCountryListener onSelectCountryListener) {
        this.mHumanCountryData = list;
        this.mFilteredList = new ArrayList(list);
        this.mWithIndicator = z;
        this.mListener = onSelectCountryListener;
    }

    public CountryViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new CountryViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C1299R.layout.item_country, viewGroup, false));
    }

    public void onBindViewHolder(CountryViewHolder countryViewHolder, int i) {
        if (i < this.mFilteredList.size()) {
            countryViewHolder.populateViews((CountryPhoneData) this.mFilteredList.get(i));
        }
    }

    public int getItemCount() {
        return this.mFilteredList.size();
    }

    public Filter getFilter() {
        return new Filter() {
            /* access modifiers changed from: protected */
            public FilterResults performFiltering(CharSequence charSequence) {
                CountryAdapter.this.mFilteredList.clear();
                FilterResults filterResults = new FilterResults();
                if (TextUtils.isEmpty(charSequence)) {
                    CountryAdapter.this.mFilteredList.addAll(CountryAdapter.this.mHumanCountryData);
                } else {
                    String trim = charSequence.toString().trim();
                    for (CountryPhoneData countryPhoneData : CountryAdapter.this.mHumanCountryData) {
                        Pattern compile = Pattern.compile(Pattern.quote(trim), 2);
                        StringBuilder sb = new StringBuilder();
                        sb.append(countryPhoneData.getCountryName());
                        sb.append(countryPhoneData.getCallingCode());
                        if (compile.matcher(sb.toString()).find()) {
                            CountryAdapter.this.mFilteredList.add(countryPhoneData);
                        }
                    }
                }
                filterResults.values = CountryAdapter.this.mFilteredList;
                filterResults.count = CountryAdapter.this.mFilteredList.size();
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                CountryAdapter.this.notifyDataSetChanged();
            }
        };
    }
}
