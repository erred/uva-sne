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
import com.opengarden.firechat.VectorApp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

public class LanguagesAdapter extends Adapter<LanguageViewHolder> implements Filterable {
    /* access modifiers changed from: private */
    public final List<Locale> mFilteredLocalesList;
    /* access modifiers changed from: private */
    public final OnSelectLocaleListener mListener;
    /* access modifiers changed from: private */
    public final List<Locale> mLocalesList;

    class LanguageViewHolder extends ViewHolder {
        private final TextView vLocaleNameTextView;

        private LanguageViewHolder(View view) {
            super(view);
            this.vLocaleNameTextView = (TextView) view.findViewById(C1299R.C1301id.locale_text_view);
        }

        /* access modifiers changed from: private */
        public void populateViews(final Locale locale) {
            this.vLocaleNameTextView.setText(VectorApp.localeToLocalisedString(locale));
            this.itemView.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    LanguagesAdapter.this.mListener.onSelectLocale(locale);
                }
            });
        }
    }

    public interface OnSelectLocaleListener {
        void onSelectLocale(Locale locale);
    }

    public LanguagesAdapter(List<Locale> list, OnSelectLocaleListener onSelectLocaleListener) {
        this.mLocalesList = list;
        this.mFilteredLocalesList = new ArrayList(list);
        this.mListener = onSelectLocaleListener;
    }

    public LanguageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        return new LanguageViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(C1299R.layout.item_locale, viewGroup, false));
    }

    public void onBindViewHolder(LanguageViewHolder languageViewHolder, int i) {
        if (i < this.mFilteredLocalesList.size()) {
            languageViewHolder.populateViews((Locale) this.mFilteredLocalesList.get(i));
        }
    }

    public int getItemCount() {
        return this.mFilteredLocalesList.size();
    }

    public Filter getFilter() {
        return new Filter() {
            /* access modifiers changed from: protected */
            public FilterResults performFiltering(CharSequence charSequence) {
                LanguagesAdapter.this.mFilteredLocalesList.clear();
                FilterResults filterResults = new FilterResults();
                if (TextUtils.isEmpty(charSequence)) {
                    LanguagesAdapter.this.mFilteredLocalesList.addAll(LanguagesAdapter.this.mLocalesList);
                } else {
                    Pattern compile = Pattern.compile(Pattern.quote(charSequence.toString().trim()), 2);
                    for (Locale locale : LanguagesAdapter.this.mLocalesList) {
                        if (compile.matcher(VectorApp.localeToLocalisedString(locale)).find()) {
                            LanguagesAdapter.this.mFilteredLocalesList.add(locale);
                        }
                    }
                }
                filterResults.values = LanguagesAdapter.this.mFilteredLocalesList;
                filterResults.count = LanguagesAdapter.this.mFilteredLocalesList.size();
                return filterResults;
            }

            /* access modifiers changed from: protected */
            public void publishResults(CharSequence charSequence, FilterResults filterResults) {
                LanguagesAdapter.this.notifyDataSetChanged();
            }
        };
    }
}
