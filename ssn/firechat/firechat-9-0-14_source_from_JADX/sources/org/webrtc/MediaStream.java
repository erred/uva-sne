package org.webrtc;

import java.util.LinkedList;

public class MediaStream {
    public final LinkedList<AudioTrack> audioTracks = new LinkedList<>();
    final long nativeStream;
    public final LinkedList<VideoTrack> preservedVideoTracks = new LinkedList<>();
    public final LinkedList<VideoTrack> videoTracks = new LinkedList<>();

    private static native void free(long j);

    private static native boolean nativeAddAudioTrack(long j, long j2);

    private static native boolean nativeAddVideoTrack(long j, long j2);

    private static native String nativeLabel(long j);

    private static native boolean nativeRemoveAudioTrack(long j, long j2);

    private static native boolean nativeRemoveVideoTrack(long j, long j2);

    public MediaStream(long j) {
        this.nativeStream = j;
    }

    public boolean addTrack(AudioTrack audioTrack) {
        if (!nativeAddAudioTrack(this.nativeStream, audioTrack.nativeTrack)) {
            return false;
        }
        this.audioTracks.add(audioTrack);
        return true;
    }

    public boolean addTrack(VideoTrack videoTrack) {
        if (!nativeAddVideoTrack(this.nativeStream, videoTrack.nativeTrack)) {
            return false;
        }
        this.videoTracks.add(videoTrack);
        return true;
    }

    public boolean addPreservedTrack(VideoTrack videoTrack) {
        if (!nativeAddVideoTrack(this.nativeStream, videoTrack.nativeTrack)) {
            return false;
        }
        this.preservedVideoTracks.add(videoTrack);
        return true;
    }

    public boolean removeTrack(AudioTrack audioTrack) {
        this.audioTracks.remove(audioTrack);
        return nativeRemoveAudioTrack(this.nativeStream, audioTrack.nativeTrack);
    }

    public boolean removeTrack(VideoTrack videoTrack) {
        this.videoTracks.remove(videoTrack);
        this.preservedVideoTracks.remove(videoTrack);
        return nativeRemoveVideoTrack(this.nativeStream, videoTrack.nativeTrack);
    }

    public void dispose() {
        while (!this.audioTracks.isEmpty()) {
            AudioTrack audioTrack = (AudioTrack) this.audioTracks.getFirst();
            removeTrack(audioTrack);
            audioTrack.dispose();
        }
        while (!this.videoTracks.isEmpty()) {
            VideoTrack videoTrack = (VideoTrack) this.videoTracks.getFirst();
            removeTrack(videoTrack);
            videoTrack.dispose();
        }
        while (!this.preservedVideoTracks.isEmpty()) {
            removeTrack((VideoTrack) this.preservedVideoTracks.getFirst());
        }
        free(this.nativeStream);
    }

    public String label() {
        return nativeLabel(this.nativeStream);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append(label());
        sb.append(":A=");
        sb.append(this.audioTracks.size());
        sb.append(":V=");
        sb.append(this.videoTracks.size());
        sb.append("]");
        return sb.toString();
    }
}
