package com.facebook.soloader;

import java.io.File;

public class NoopSoSource extends SoSource {
    public int loadLibrary(String str, int i) {
        return 1;
    }

    public File unpackLibrary(String str) {
        throw new UnsupportedOperationException("unpacking not supported in test mode");
    }
}
