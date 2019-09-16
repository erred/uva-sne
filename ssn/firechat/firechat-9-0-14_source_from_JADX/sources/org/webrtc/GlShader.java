package org.webrtc;

import android.opengl.GLES20;
import java.nio.FloatBuffer;

public class GlShader {
    private static final String TAG = "GlShader";
    private int program = GLES20.glCreateProgram();

    private static int compileShader(int i, String str) {
        int glCreateShader = GLES20.glCreateShader(i);
        if (glCreateShader == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("glCreateShader() failed. GLES20 error: ");
            sb.append(GLES20.glGetError());
            throw new RuntimeException(sb.toString());
        }
        GLES20.glShaderSource(glCreateShader, str);
        GLES20.glCompileShader(glCreateShader);
        int[] iArr = {0};
        GLES20.glGetShaderiv(glCreateShader, 35713, iArr, 0);
        if (iArr[0] != 1) {
            String str2 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Could not compile shader ");
            sb2.append(i);
            sb2.append(":");
            sb2.append(GLES20.glGetShaderInfoLog(glCreateShader));
            Logging.m315e(str2, sb2.toString());
            throw new RuntimeException(GLES20.glGetShaderInfoLog(glCreateShader));
        }
        GlUtil.checkNoGLES2Error("compileShader");
        return glCreateShader;
    }

    public GlShader(String str, String str2) {
        int compileShader = compileShader(35633, str);
        int compileShader2 = compileShader(35632, str2);
        if (this.program == 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("glCreateProgram() failed. GLES20 error: ");
            sb.append(GLES20.glGetError());
            throw new RuntimeException(sb.toString());
        }
        GLES20.glAttachShader(this.program, compileShader);
        GLES20.glAttachShader(this.program, compileShader2);
        GLES20.glLinkProgram(this.program);
        int[] iArr = {0};
        GLES20.glGetProgramiv(this.program, 35714, iArr, 0);
        if (iArr[0] != 1) {
            String str3 = TAG;
            StringBuilder sb2 = new StringBuilder();
            sb2.append("Could not link program: ");
            sb2.append(GLES20.glGetProgramInfoLog(this.program));
            Logging.m315e(str3, sb2.toString());
            throw new RuntimeException(GLES20.glGetProgramInfoLog(this.program));
        }
        GLES20.glDeleteShader(compileShader);
        GLES20.glDeleteShader(compileShader2);
        GlUtil.checkNoGLES2Error("Creating GlShader");
    }

    public int getAttribLocation(String str) {
        if (this.program == -1) {
            throw new RuntimeException("The program has been released");
        }
        int glGetAttribLocation = GLES20.glGetAttribLocation(this.program, str);
        if (glGetAttribLocation >= 0) {
            return glGetAttribLocation;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Could not locate '");
        sb.append(str);
        sb.append("' in program");
        throw new RuntimeException(sb.toString());
    }

    public void setVertexAttribArray(String str, int i, FloatBuffer floatBuffer) {
        if (this.program == -1) {
            throw new RuntimeException("The program has been released");
        }
        int attribLocation = getAttribLocation(str);
        GLES20.glEnableVertexAttribArray(attribLocation);
        GLES20.glVertexAttribPointer(attribLocation, i, 5126, false, 0, floatBuffer);
        GlUtil.checkNoGLES2Error("setVertexAttribArray");
    }

    public int getUniformLocation(String str) {
        if (this.program == -1) {
            throw new RuntimeException("The program has been released");
        }
        int glGetUniformLocation = GLES20.glGetUniformLocation(this.program, str);
        if (glGetUniformLocation >= 0) {
            return glGetUniformLocation;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Could not locate uniform '");
        sb.append(str);
        sb.append("' in program");
        throw new RuntimeException(sb.toString());
    }

    public void useProgram() {
        if (this.program == -1) {
            throw new RuntimeException("The program has been released");
        }
        GLES20.glUseProgram(this.program);
        GlUtil.checkNoGLES2Error("glUseProgram");
    }

    public void release() {
        Logging.m314d(TAG, "Deleting shader.");
        if (this.program != -1) {
            GLES20.glDeleteProgram(this.program);
            this.program = -1;
        }
    }
}
