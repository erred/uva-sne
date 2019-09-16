package com.opengarden.firechat.matrixsdk.fragments;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.os.Bundle;
import android.support.p000v4.app.DialogFragment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.adapters.IconAndTextAdapter;
import java.util.ArrayList;
import java.util.Arrays;

public class IconAndTextDialogFragment extends DialogFragment {
    public static final String ARG_BACKGROUND_COLOR = "com.opengarden.firechat.matrixsdk.fragments.IconAndTextDialogFragment.ARG_BACKGROUND_COLOR";
    public static final String ARG_ICONS_LIST_ID = "com.opengarden.firechat.matrixsdk.fragments.IconAndTextDialogFragment.ARG_ICONS_LIST_ID";
    public static final String ARG_TEXTS_LIST_ID = "com.opengarden.firechat.matrixsdk.fragments.IconAndTextDialogFragment.ARG_TEXTS_LIST_ID";
    public static final String ARG_TEXT_COLOR = "com.opengarden.firechat.matrixsdk.fragments.IconAndTextDialogFragment.ARG_TEXT_COLOR";
    private static final String LOG_TAG = "IconAndTextDialogFragment";
    private Integer mBackgroundColor = null;
    private ArrayList<Integer> mIconResourcesList;
    private ListView mListView;
    /* access modifiers changed from: private */
    public OnItemClickListener mOnItemClickListener;
    private Integer mTextColor = null;
    private ArrayList<Integer> mTextResourcesList;

    public interface OnItemClickListener {
        void onItemClick(IconAndTextDialogFragment iconAndTextDialogFragment, int i);
    }

    public static IconAndTextDialogFragment newInstance(Integer[] numArr, Integer[] numArr2) {
        return newInstance(numArr, numArr2, null, null);
    }

    public static IconAndTextDialogFragment newInstance(Integer[] numArr, Integer[] numArr2, Integer num, Integer num2) {
        IconAndTextDialogFragment iconAndTextDialogFragment = new IconAndTextDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(ARG_ICONS_LIST_ID, new ArrayList(Arrays.asList(numArr)));
        bundle.putIntegerArrayList(ARG_TEXTS_LIST_ID, new ArrayList(Arrays.asList(numArr2)));
        if (num != null) {
            bundle.putInt(ARG_BACKGROUND_COLOR, num.intValue());
        }
        if (num2 != null) {
            bundle.putInt(ARG_TEXT_COLOR, num2.intValue());
        }
        iconAndTextDialogFragment.setArguments(bundle);
        return iconAndTextDialogFragment;
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        this.mIconResourcesList = getArguments().getIntegerArrayList(ARG_ICONS_LIST_ID);
        this.mTextResourcesList = getArguments().getIntegerArrayList(ARG_TEXTS_LIST_ID);
        if (getArguments().containsKey(ARG_BACKGROUND_COLOR)) {
            this.mBackgroundColor = Integer.valueOf(getArguments().getInt(ARG_BACKGROUND_COLOR));
        }
        if (getArguments().containsKey(ARG_TEXT_COLOR)) {
            this.mTextColor = Integer.valueOf(getArguments().getInt(ARG_TEXT_COLOR));
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Builder builder = new Builder(getActivity());
        View inflate = getActivity().getLayoutInflater().inflate(C1299R.layout.fragment_dialog_icon_text_list, null);
        builder.setView(inflate);
        initView(inflate);
        this.mListView.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (IconAndTextDialogFragment.this.mOnItemClickListener != null) {
                    IconAndTextDialogFragment.this.mOnItemClickListener.onItemClick(IconAndTextDialogFragment.this, i);
                }
                IconAndTextDialogFragment.this.dismiss();
            }
        });
        return builder.create();
    }

    /* access modifiers changed from: 0000 */
    public void initView(View view) {
        this.mListView = (ListView) view.findViewById(C1299R.C1301id.listView_icon_and_text);
        IconAndTextAdapter iconAndTextAdapter = new IconAndTextAdapter(getActivity(), C1299R.layout.adapter_item_icon_and_text);
        for (int i = 0; i < this.mIconResourcesList.size(); i++) {
            iconAndTextAdapter.add(((Integer) this.mIconResourcesList.get(i)).intValue(), ((Integer) this.mTextResourcesList.get(i)).intValue());
        }
        if (this.mBackgroundColor != null) {
            this.mListView.setBackgroundColor(this.mBackgroundColor.intValue());
            iconAndTextAdapter.setBackgroundColor(this.mBackgroundColor);
        }
        if (this.mTextColor != null) {
            iconAndTextAdapter.setTextColor(this.mTextColor);
        }
        this.mListView.setAdapter(iconAndTextAdapter);
    }

    public void setOnClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
