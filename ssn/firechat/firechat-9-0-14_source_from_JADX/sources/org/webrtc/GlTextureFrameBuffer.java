package org.webrtc;

import android.opengl.GLES20;

public class GlTextureFrameBuffer {
    private final int frameBufferId;
    private int height;
    private final int pixelFormat;
    private final int textureId;
    private int width;

    public GlTextureFrameBuffer(int i) {
        switch (i) {
            case 6407:
            case 6408:
            case 6409:
                this.pixelFormat = i;
                this.textureId = GlUtil.generateTexture(3553);
                this.width = 0;
                this.height = 0;
                int[] iArr = new int[1];
                GLES20.glGenFramebuffers(1, iArr, 0);
                this.frameBufferId = iArr[0];
                return;
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Invalid pixel format: ");
                sb.append(i);
                throw new IllegalArgumentException(sb.toString());
        }
    }

    public void setSize(int i, int i2) {
        if (i == 0 || i2 == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Invalid size: ");
            sb.append(i);
            sb.append("x");
            sb.append(i2);
            throw new IllegalArgumentException(sb.toString());
        } else if (i != this.width || i2 != this.height) {
            this.width = i;
            this.height = i2;
            GLES20.glActiveTexture(33984);
            GLES20.glBindTexture(3553, this.textureId);
            GLES20.glTexImage2D(3553, 0, this.pixelFormat, i, i2, 0, this.pixelFormat, 5121, null);
            GLES20.glBindTexture(3553, 0);
            GlUtil.checkNoGLES2Error("GlTextureFrameBuffer setSize");
            GLES20.glBindFramebuffer(36160, this.frameBufferId);
            GLES20.glFramebufferTexture2D(36160, 36064, 3553, this.textureId, 0);
            int glCheckFramebufferStatus = GLES20.glCheckFramebufferStatus(36160);
            if (glCheckFramebufferStatus != 36053) {
                StringBuilder sb2 = new StringBuilder();
                sb2.append("Framebuffer not complete, status: ");
                sb2.append(glCheckFramebufferStatus);
                throw new IllegalStateException(sb2.toString());
            }
            GLES20.glBindFramebuffer(36160, 0);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getFrameBufferId() {
        return this.frameBufferId;
    }

    public int getTextureId() {
        return this.textureId;
    }

    public void release() {
        GLES20.glDeleteTextures(1, new int[]{this.textureId}, 0);
        GLES20.glDeleteFramebuffers(1, new int[]{this.frameBufferId}, 0);
        this.width = 0;
        this.height = 0;
    }
}
