package com.facebook.react.bridge.queue;

import android.os.Looper;
import android.os.Process;
import com.facebook.common.logging.FLog;
import com.facebook.proguard.annotations.DoNotStrip;
import com.facebook.react.bridge.SoftAssertions;
import com.facebook.react.bridge.UiThreadUtil;
import com.facebook.react.common.ReactConstants;
import com.facebook.react.common.futures.SimpleSettableFuture;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.apache.commons.lang3.StringUtils;

@DoNotStrip
public class MessageQueueThreadImpl implements MessageQueueThread {
    private final String mAssertionErrorMessage;
    private final MessageQueueThreadHandler mHandler;
    private volatile boolean mIsFinished = false;
    private final Looper mLooper;
    private final String mName;

    private MessageQueueThreadImpl(String str, Looper looper, QueueThreadExceptionHandler queueThreadExceptionHandler) {
        this.mName = str;
        this.mLooper = looper;
        this.mHandler = new MessageQueueThreadHandler(looper, queueThreadExceptionHandler);
        StringBuilder sb = new StringBuilder();
        sb.append("Expected to be called from the '");
        sb.append(getName());
        sb.append("' thread!");
        this.mAssertionErrorMessage = sb.toString();
    }

    @DoNotStrip
    public void runOnQueue(Runnable runnable) {
        if (this.mIsFinished) {
            String str = ReactConstants.TAG;
            StringBuilder sb = new StringBuilder();
            sb.append("Tried to enqueue runnable on already finished thread: '");
            sb.append(getName());
            sb.append("... dropping Runnable.");
            FLog.m105w(str, sb.toString());
        }
        this.mHandler.post(runnable);
    }

    @DoNotStrip
    public <T> Future<T> callOnQueue(final Callable<T> callable) {
        final SimpleSettableFuture simpleSettableFuture = new SimpleSettableFuture();
        runOnQueue(new Runnable() {
            public void run() {
                try {
                    simpleSettableFuture.set(callable.call());
                } catch (Exception e) {
                    simpleSettableFuture.setException(e);
                }
            }
        });
        return simpleSettableFuture;
    }

    @DoNotStrip
    public boolean isOnThread() {
        return this.mLooper.getThread() == Thread.currentThread();
    }

    @DoNotStrip
    public void assertIsOnThread() {
        SoftAssertions.assertCondition(isOnThread(), this.mAssertionErrorMessage);
    }

    @DoNotStrip
    public void assertIsOnThread(String str) {
        boolean isOnThread = isOnThread();
        StringBuilder sb = new StringBuilder();
        sb.append(this.mAssertionErrorMessage);
        sb.append(StringUtils.SPACE);
        sb.append(str);
        SoftAssertions.assertCondition(isOnThread, sb.toString());
    }

    @DoNotStrip
    public void quitSynchronous() {
        this.mIsFinished = true;
        this.mLooper.quit();
        if (this.mLooper.getThread() != Thread.currentThread()) {
            try {
                this.mLooper.getThread().join();
            } catch (InterruptedException unused) {
                StringBuilder sb = new StringBuilder();
                sb.append("Got interrupted waiting to join thread ");
                sb.append(this.mName);
                throw new RuntimeException(sb.toString());
            }
        }
    }

    public Looper getLooper() {
        return this.mLooper;
    }

    public String getName() {
        return this.mName;
    }

    public static MessageQueueThreadImpl create(MessageQueueThreadSpec messageQueueThreadSpec, QueueThreadExceptionHandler queueThreadExceptionHandler) {
        switch (messageQueueThreadSpec.getThreadType()) {
            case MAIN_UI:
                return createForMainThread(messageQueueThreadSpec.getName(), queueThreadExceptionHandler);
            case NEW_BACKGROUND:
                return startNewBackgroundThread(messageQueueThreadSpec.getName(), messageQueueThreadSpec.getStackSize(), queueThreadExceptionHandler);
            default:
                StringBuilder sb = new StringBuilder();
                sb.append("Unknown thread type: ");
                sb.append(messageQueueThreadSpec.getThreadType());
                throw new RuntimeException(sb.toString());
        }
    }

    private static MessageQueueThreadImpl createForMainThread(String str, QueueThreadExceptionHandler queueThreadExceptionHandler) {
        MessageQueueThreadImpl messageQueueThreadImpl = new MessageQueueThreadImpl(str, Looper.getMainLooper(), queueThreadExceptionHandler);
        if (UiThreadUtil.isOnUiThread()) {
            Process.setThreadPriority(-4);
        } else {
            UiThreadUtil.runOnUiThread(new Runnable() {
                public void run() {
                    Process.setThreadPriority(-4);
                }
            });
        }
        return messageQueueThreadImpl;
    }

    private static MessageQueueThreadImpl startNewBackgroundThread(String str, long j, QueueThreadExceptionHandler queueThreadExceptionHandler) {
        final SimpleSettableFuture simpleSettableFuture = new SimpleSettableFuture();
        C08143 r3 = new Runnable() {
            public void run() {
                Process.setThreadPriority(-4);
                Looper.prepare();
                simpleSettableFuture.set(Looper.myLooper());
                Looper.loop();
            }
        };
        StringBuilder sb = new StringBuilder();
        sb.append("mqt_");
        sb.append(str);
        Thread thread = new Thread(null, r3, sb.toString(), j);
        thread.start();
        return new MessageQueueThreadImpl(str, (Looper) simpleSettableFuture.getOrThrow(), queueThreadExceptionHandler);
    }
}
