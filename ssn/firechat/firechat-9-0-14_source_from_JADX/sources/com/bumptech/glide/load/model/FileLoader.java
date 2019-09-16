package com.bumptech.glide.load.model;

import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;
import android.util.Log;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.data.DataFetcher;
import com.bumptech.glide.load.data.DataFetcher.DataCallback;
import com.bumptech.glide.load.model.ModelLoader.LoadData;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.gms.common.util.CrashUtils.ErrorDialogData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class FileLoader<Data> implements ModelLoader<File, Data> {
    private static final String TAG = "FileLoader";
    private final FileOpener<Data> fileOpener;

    public static class Factory<Data> implements ModelLoaderFactory<File, Data> {
        private final FileOpener<Data> opener;

        public final void teardown() {
        }

        public Factory(FileOpener<Data> fileOpener) {
            this.opener = fileOpener;
        }

        @NonNull
        public final ModelLoader<File, Data> build(@NonNull MultiModelLoaderFactory multiModelLoaderFactory) {
            return new FileLoader(this.opener);
        }
    }

    public static class FileDescriptorFactory extends Factory<ParcelFileDescriptor> {
        public FileDescriptorFactory() {
            super(new FileOpener<ParcelFileDescriptor>() {
                public ParcelFileDescriptor open(File file) throws FileNotFoundException {
                    return ParcelFileDescriptor.open(file, ErrorDialogData.BINDER_CRASH);
                }

                public void close(ParcelFileDescriptor parcelFileDescriptor) throws IOException {
                    parcelFileDescriptor.close();
                }

                public Class<ParcelFileDescriptor> getDataClass() {
                    return ParcelFileDescriptor.class;
                }
            });
        }
    }

    private static final class FileFetcher<Data> implements DataFetcher<Data> {
        private Data data;
        private final File file;
        private final FileOpener<Data> opener;

        public void cancel() {
        }

        FileFetcher(File file2, FileOpener<Data> fileOpener) {
            this.file = file2;
            this.opener = fileOpener;
        }

        public void loadData(@NonNull Priority priority, @NonNull DataCallback<? super Data> dataCallback) {
            try {
                this.data = this.opener.open(this.file);
                dataCallback.onDataReady(this.data);
            } catch (FileNotFoundException e) {
                if (Log.isLoggable(FileLoader.TAG, 3)) {
                    Log.d(FileLoader.TAG, "Failed to open file", e);
                }
                dataCallback.onLoadFailed(e);
            }
        }

        public void cleanup() {
            if (this.data != null) {
                try {
                    this.opener.close(this.data);
                } catch (IOException unused) {
                }
            }
        }

        @NonNull
        public Class<Data> getDataClass() {
            return this.opener.getDataClass();
        }

        @NonNull
        public DataSource getDataSource() {
            return DataSource.LOCAL;
        }
    }

    public interface FileOpener<Data> {
        void close(Data data) throws IOException;

        Class<Data> getDataClass();

        Data open(File file) throws FileNotFoundException;
    }

    public static class StreamFactory extends Factory<InputStream> {
        public StreamFactory() {
            super(new FileOpener<InputStream>() {
                public InputStream open(File file) throws FileNotFoundException {
                    return new FileInputStream(file);
                }

                public void close(InputStream inputStream) throws IOException {
                    inputStream.close();
                }

                public Class<InputStream> getDataClass() {
                    return InputStream.class;
                }
            });
        }
    }

    public boolean handles(@NonNull File file) {
        return true;
    }

    public FileLoader(FileOpener<Data> fileOpener2) {
        this.fileOpener = fileOpener2;
    }

    public LoadData<Data> buildLoadData(@NonNull File file, int i, int i2, @NonNull Options options) {
        return new LoadData<>(new ObjectKey(file), new FileFetcher(file, this.fileOpener));
    }
}
