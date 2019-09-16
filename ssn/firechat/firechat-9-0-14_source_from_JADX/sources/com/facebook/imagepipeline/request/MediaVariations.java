package com.facebook.imagepipeline.request;

import android.net.Uri;
import com.facebook.common.internal.Objects;
import com.facebook.imagepipeline.request.ImageRequest.CacheChoice;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public class MediaVariations {
    public static final String SOURCE_ID_EXTRACTOR = "id_extractor";
    public static final String SOURCE_IMAGE_REQUEST = "request";
    public static final String SOURCE_INDEX_DB = "index_db";
    private final boolean mForceRequestForSpecifiedUri;
    private final String mMediaId;
    private final String mSource;
    @Nullable
    private final List<Variant> mVariants;

    public static class Builder {
        /* access modifiers changed from: private */
        public boolean mForceRequestForSpecifiedUri;
        /* access modifiers changed from: private */
        public final String mMediaId;
        /* access modifiers changed from: private */
        public String mSource;
        /* access modifiers changed from: private */
        public List<Variant> mVariants;

        private Builder(String str) {
            this.mForceRequestForSpecifiedUri = false;
            this.mSource = "request";
            this.mMediaId = str;
        }

        public Builder addVariant(Uri uri, int i, int i2) {
            return addVariant(uri, i, i2, null);
        }

        public Builder addVariant(Uri uri, int i, int i2, CacheChoice cacheChoice) {
            if (this.mVariants == null) {
                this.mVariants = new ArrayList();
            }
            this.mVariants.add(new Variant(uri, i, i2, cacheChoice));
            return this;
        }

        public Builder setForceRequestForSpecifiedUri(boolean z) {
            this.mForceRequestForSpecifiedUri = z;
            return this;
        }

        public Builder setSource(String str) {
            this.mSource = str;
            return this;
        }

        public MediaVariations build() {
            return new MediaVariations(this);
        }
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface Source {
    }

    public static final class Variant {
        @Nullable
        private final CacheChoice mCacheChoice;
        private final int mHeight;
        private final Uri mUri;
        private final int mWidth;

        public Variant(Uri uri, int i, int i2) {
            this(uri, i, i2, null);
        }

        public Variant(Uri uri, int i, int i2, @Nullable CacheChoice cacheChoice) {
            this.mUri = uri;
            this.mWidth = i;
            this.mHeight = i2;
            this.mCacheChoice = cacheChoice;
        }

        public Uri getUri() {
            return this.mUri;
        }

        public int getWidth() {
            return this.mWidth;
        }

        public int getHeight() {
            return this.mHeight;
        }

        @Nullable
        public CacheChoice getCacheChoice() {
            return this.mCacheChoice;
        }

        public boolean equals(Object obj) {
            boolean z = false;
            if (!(obj instanceof Variant)) {
                return false;
            }
            Variant variant = (Variant) obj;
            if (Objects.equal(this.mUri, variant.mUri) && this.mWidth == variant.mWidth && this.mHeight == variant.mHeight && this.mCacheChoice == variant.mCacheChoice) {
                z = true;
            }
            return z;
        }

        public int hashCode() {
            return (((this.mUri.hashCode() * 31) + this.mWidth) * 31) + this.mHeight;
        }

        public String toString() {
            return String.format(null, "%dx%d %s %s", new Object[]{Integer.valueOf(this.mWidth), Integer.valueOf(this.mHeight), this.mUri, this.mCacheChoice});
        }
    }

    private MediaVariations(Builder builder) {
        this.mMediaId = builder.mMediaId;
        this.mVariants = builder.mVariants;
        this.mForceRequestForSpecifiedUri = builder.mForceRequestForSpecifiedUri;
        this.mSource = builder.mSource;
    }

    public String getMediaId() {
        return this.mMediaId;
    }

    public int getVariantsCount() {
        if (this.mVariants == null) {
            return 0;
        }
        return this.mVariants.size();
    }

    public Variant getVariant(int i) {
        return (Variant) this.mVariants.get(i);
    }

    public List<Variant> getSortedVariants(Comparator<Variant> comparator) {
        int variantsCount = getVariantsCount();
        if (variantsCount == 0) {
            return Collections.emptyList();
        }
        ArrayList arrayList = new ArrayList(variantsCount);
        for (int i = 0; i < variantsCount; i++) {
            arrayList.add(this.mVariants.get(i));
        }
        Collections.sort(arrayList, comparator);
        return arrayList;
    }

    public boolean shouldForceRequestForSpecifiedUri() {
        return this.mForceRequestForSpecifiedUri;
    }

    public String getSource() {
        return this.mSource;
    }

    public boolean equals(Object obj) {
        boolean z = false;
        if (!(obj instanceof MediaVariations)) {
            return false;
        }
        MediaVariations mediaVariations = (MediaVariations) obj;
        if (Objects.equal(this.mMediaId, mediaVariations.mMediaId) && this.mForceRequestForSpecifiedUri == mediaVariations.mForceRequestForSpecifiedUri && Objects.equal(this.mVariants, mediaVariations.mVariants)) {
            z = true;
        }
        return z;
    }

    public int hashCode() {
        return Objects.hashCode(this.mMediaId, Boolean.valueOf(this.mForceRequestForSpecifiedUri), this.mVariants, this.mSource);
    }

    public String toString() {
        return String.format(null, "%s-%b-%s-%s", new Object[]{this.mMediaId, Boolean.valueOf(this.mForceRequestForSpecifiedUri), this.mVariants, this.mSource});
    }

    @Nullable
    public static MediaVariations forMediaId(@Nullable String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        return newBuilderForMediaId(str).build();
    }

    public static Builder newBuilderForMediaId(String str) {
        return new Builder(str);
    }
}
