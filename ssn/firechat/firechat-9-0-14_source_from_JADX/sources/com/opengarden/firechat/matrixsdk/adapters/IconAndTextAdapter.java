package com.opengarden.firechat.matrixsdk.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;

public class IconAndTextAdapter extends ArrayAdapter<Entry> {
    private Integer mBackgroundColor = null;
    private final Context mContext;
    private final LayoutInflater mLayoutInflater;
    private final int mLayoutResourceId;
    private Integer mTextColor = null;

    protected class Entry {
        protected final Integer mIconResId;
        protected final Integer mTextResId;

        protected Entry(Integer num, Integer num2) {
            this.mIconResId = num;
            this.mTextResId = num2;
        }
    }

    public IconAndTextAdapter(Context context, int i) {
        super(context, i);
        this.mContext = context;
        this.mLayoutResourceId = i;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
    }

    public void add(int i, int i2) {
        add(new Entry(Integer.valueOf(i), Integer.valueOf(i2)));
    }

    public void setBackgroundColor(Integer num) {
        this.mBackgroundColor = num;
    }

    public void setTextColor(Integer num) {
        this.mTextColor = num;
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mLayoutResourceId, viewGroup, false);
        }
        Entry entry = (Entry) getItem(i);
        TextView textView = (TextView) view.findViewById(C1299R.C1301id.textView_icon_and_text);
        textView.setText(this.mContext.getString(entry.mTextResId.intValue()));
        if (this.mTextColor != null) {
            textView.setTextColor(this.mTextColor.intValue());
        }
        ((ImageView) view.findViewById(C1299R.C1301id.imageView_icon_and_text)).setImageResource(entry.mIconResId.intValue());
        if (this.mBackgroundColor != null) {
            view.setBackgroundColor(this.mBackgroundColor.intValue());
        }
        return view;
    }
}
