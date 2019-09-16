package com.opengarden.firechat.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;
import com.opengarden.firechat.C1299R;

public class RecentMediaLayout extends RelativeLayout {
    private ImageView mGifLogoImageView;
    private View mSelectedItemView;
    private ImageView mThumbnailView;
    private ImageView mTypeView;

    public RecentMediaLayout(Context context) {
        super(context);
        init();
    }

    public RecentMediaLayout(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    public RecentMediaLayout(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init();
    }

    private void init() {
        inflate(getContext(), C1299R.layout.recent_media, this);
        this.mThumbnailView = (ImageView) findViewById(C1299R.C1301id.media_thumbnail_view);
        this.mTypeView = (ImageView) findViewById(C1299R.C1301id.media_type_view);
        this.mGifLogoImageView = (ImageView) findViewById(C1299R.C1301id.media_gif_type_view);
        this.mSelectedItemView = findViewById(C1299R.C1301id.media_selected_mask_view);
        this.mSelectedItemView.setVisibility(8);
    }

    public boolean isSelected() {
        return this.mSelectedItemView.getVisibility() == 0;
    }

    public void setThumbnail(Bitmap bitmap) {
        this.mThumbnailView.setImageBitmap(bitmap);
    }

    public void setThumbnailByUri(Uri uri) {
        this.mThumbnailView.setImageURI(uri);
    }

    public void setThumbnailByResource(int i) {
        this.mThumbnailView.setImageResource(i);
    }

    public void setThumbnailScaleType(ScaleType scaleType) {
        this.mThumbnailView.setScaleType(scaleType);
    }

    public void setIsVideo(boolean z) {
        this.mTypeView.setImageResource(z ? C1299R.C1300drawable.ic_material_movie : C1299R.C1300drawable.ic_material_photo);
    }

    public void enableMediaTypeLogoImage(Boolean bool) {
        this.mTypeView.setVisibility(bool.booleanValue() ? 0 : 8);
    }

    public void enableGifLogoImage(boolean z) {
        this.mGifLogoImageView.setVisibility(z ? 0 : 8);
    }
}
