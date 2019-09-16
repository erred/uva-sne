package com.facebook.soloader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class DirectorySoSource extends SoSource {
    public static final int ON_LD_LIBRARY_PATH = 2;
    public static final int RESOLVE_DEPENDENCIES = 1;
    protected final int flags;
    protected final File soDirectory;

    public DirectorySoSource(File file, int i) {
        this.soDirectory = file;
        this.flags = i;
    }

    public int loadLibrary(String str, int i) throws IOException {
        return loadLibraryFrom(str, i, this.soDirectory);
    }

    /* access modifiers changed from: protected */
    public int loadLibraryFrom(String str, int i, File file) throws IOException {
        File file2 = new File(file, str);
        if (!file2.exists()) {
            return 0;
        }
        if ((i & 1) != 0 && (this.flags & 2) != 0) {
            return 2;
        }
        if ((this.flags & 1) != 0) {
            String[] dependencies = getDependencies(file2);
            for (String str2 : dependencies) {
                if (!str2.startsWith("/")) {
                    SoLoader.loadLibraryBySoName(str2, i | 1);
                }
            }
        }
        System.load(file2.getAbsolutePath());
        return 1;
    }

    private static String[] getDependencies(File file) throws IOException {
        return MinElf.extract_DT_NEEDED(file);
    }

    public File unpackLibrary(String str) throws IOException {
        File file = new File(this.soDirectory, str);
        if (file.exists()) {
            return file;
        }
        return null;
    }

    public void addToLdLibraryPath(Collection<String> collection) {
        collection.add(this.soDirectory.getAbsolutePath());
    }
}
