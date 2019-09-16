package com.opengarden.firechat.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.p000v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.ImageCompressionDescription;
import com.opengarden.firechat.adapters.ImageSizesAdapter;
import java.util.ArrayList;
import java.util.Collection;

public class ImageSizeSelectionDialogFragment extends DialogFragment {
    private static final String SELECTIONS_LIST = "SELECTIONS_LIST";
    private ArrayList<ImageCompressionDescription> mEntries = null;
    /* access modifiers changed from: private */
    public ImageSizeListener mListener = null;

    public interface ImageSizeListener {
        void onSelected(int i);
    }

    public static ImageSizeSelectionDialogFragment newInstance(Collection<ImageCompressionDescription> collection) {
        ImageSizeSelectionDialogFragment imageSizeSelectionDialogFragment = new ImageSizeSelectionDialogFragment();
        imageSizeSelectionDialogFragment.setArguments(new Bundle());
        imageSizeSelectionDialogFragment.setEntries(collection);
        return imageSizeSelectionDialogFragment;
    }

    private void setEntries(Collection<ImageCompressionDescription> collection) {
        this.mEntries = new ArrayList<>(collection);
    }

    public void setListener(ImageSizeListener imageSizeListener) {
        this.mListener = imageSizeListener;
    }

    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        if (this.mEntries != null) {
            bundle.putSerializable(SELECTIONS_LIST, this.mEntries);
        }
    }

    public Dialog onCreateDialog(Bundle bundle) {
        Dialog onCreateDialog = super.onCreateDialog(bundle);
        if (bundle != null && bundle.containsKey(SELECTIONS_LIST)) {
            this.mEntries = (ArrayList) bundle.getSerializable(SELECTIONS_LIST);
        }
        onCreateDialog.setTitle(getString(C1299R.string.compression_options));
        return onCreateDialog;
    }

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        super.onCreateView(layoutInflater, viewGroup, bundle);
        View inflate = layoutInflater.inflate(C1299R.layout.fragment_dialog_accounts_list, viewGroup, false);
        ListView listView = (ListView) inflate.findViewById(C1299R.C1301id.listView_accounts);
        ImageSizesAdapter imageSizesAdapter = new ImageSizesAdapter(getActivity(), C1299R.layout.adapter_item_image_size);
        if (this.mEntries != null) {
            imageSizesAdapter.addAll(this.mEntries);
        }
        listView.setAdapter(imageSizesAdapter);
        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long j) {
                if (ImageSizeSelectionDialogFragment.this.mListener != null) {
                    ImageSizeSelectionDialogFragment.this.mListener.onSelected(i);
                }
                ImageSizeSelectionDialogFragment.this.dismiss();
            }
        });
        return inflate;
    }
}
