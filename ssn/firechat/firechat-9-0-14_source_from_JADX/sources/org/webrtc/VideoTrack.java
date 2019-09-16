package org.webrtc;

import java.util.LinkedList;

public class VideoTrack extends MediaStreamTrack {
    private final LinkedList<VideoRenderer> renderers = new LinkedList<>();

    private static native void free(long j);

    private static native void nativeAddRenderer(long j, long j2);

    private static native void nativeRemoveRenderer(long j, long j2);

    public VideoTrack(long j) {
        super(j);
    }

    public void addRenderer(VideoRenderer videoRenderer) {
        this.renderers.add(videoRenderer);
        nativeAddRenderer(this.nativeTrack, videoRenderer.nativeVideoRenderer);
    }

    public void removeRenderer(VideoRenderer videoRenderer) {
        if (this.renderers.remove(videoRenderer)) {
            nativeRemoveRenderer(this.nativeTrack, videoRenderer.nativeVideoRenderer);
            videoRenderer.dispose();
        }
    }

    public void dispose() {
        while (!this.renderers.isEmpty()) {
            removeRenderer((VideoRenderer) this.renderers.getFirst());
        }
        super.dispose();
    }
}
