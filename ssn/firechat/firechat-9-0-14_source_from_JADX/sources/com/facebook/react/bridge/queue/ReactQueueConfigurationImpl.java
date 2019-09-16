package com.facebook.react.bridge.queue;

import android.os.Looper;
import com.facebook.react.common.MapBuilder;
import java.util.HashMap;
import javax.annotation.Nullable;

public class ReactQueueConfigurationImpl implements ReactQueueConfiguration {
    private final MessageQueueThreadImpl mJSQueueThread;
    private final MessageQueueThreadImpl mNativeModulesQueueThread;
    @Nullable
    private final MessageQueueThreadImpl mUIBackgroundQueueThread;
    private final MessageQueueThreadImpl mUIQueueThread;

    private ReactQueueConfigurationImpl(MessageQueueThreadImpl messageQueueThreadImpl, @Nullable MessageQueueThreadImpl messageQueueThreadImpl2, MessageQueueThreadImpl messageQueueThreadImpl3, MessageQueueThreadImpl messageQueueThreadImpl4) {
        this.mUIQueueThread = messageQueueThreadImpl;
        this.mUIBackgroundQueueThread = messageQueueThreadImpl2;
        this.mNativeModulesQueueThread = messageQueueThreadImpl3;
        this.mJSQueueThread = messageQueueThreadImpl4;
    }

    public MessageQueueThread getUIQueueThread() {
        return this.mUIQueueThread;
    }

    @Nullable
    public MessageQueueThread getUIBackgroundQueueThread() {
        return this.mUIBackgroundQueueThread;
    }

    public MessageQueueThread getNativeModulesQueueThread() {
        return this.mNativeModulesQueueThread;
    }

    public MessageQueueThread getJSQueueThread() {
        return this.mJSQueueThread;
    }

    public void destroy() {
        if (!(this.mUIBackgroundQueueThread == null || this.mUIBackgroundQueueThread.getLooper() == Looper.getMainLooper())) {
            this.mUIBackgroundQueueThread.quitSynchronous();
        }
        if (this.mNativeModulesQueueThread.getLooper() != Looper.getMainLooper()) {
            this.mNativeModulesQueueThread.quitSynchronous();
        }
        if (this.mJSQueueThread.getLooper() != Looper.getMainLooper()) {
            this.mJSQueueThread.quitSynchronous();
        }
    }

    public static ReactQueueConfigurationImpl create(ReactQueueConfigurationSpec reactQueueConfigurationSpec, QueueThreadExceptionHandler queueThreadExceptionHandler) {
        HashMap newHashMap = MapBuilder.newHashMap();
        MessageQueueThreadSpec mainThreadSpec = MessageQueueThreadSpec.mainThreadSpec();
        MessageQueueThreadImpl create = MessageQueueThreadImpl.create(mainThreadSpec, queueThreadExceptionHandler);
        newHashMap.put(mainThreadSpec, create);
        MessageQueueThreadImpl messageQueueThreadImpl = (MessageQueueThreadImpl) newHashMap.get(reactQueueConfigurationSpec.getJSQueueThreadSpec());
        if (messageQueueThreadImpl == null) {
            messageQueueThreadImpl = MessageQueueThreadImpl.create(reactQueueConfigurationSpec.getJSQueueThreadSpec(), queueThreadExceptionHandler);
        }
        MessageQueueThreadImpl messageQueueThreadImpl2 = (MessageQueueThreadImpl) newHashMap.get(reactQueueConfigurationSpec.getNativeModulesQueueThreadSpec());
        if (messageQueueThreadImpl2 == null) {
            messageQueueThreadImpl2 = MessageQueueThreadImpl.create(reactQueueConfigurationSpec.getNativeModulesQueueThreadSpec(), queueThreadExceptionHandler);
        }
        MessageQueueThreadImpl messageQueueThreadImpl3 = (MessageQueueThreadImpl) newHashMap.get(reactQueueConfigurationSpec.getUIBackgroundQueueThreadSpec());
        if (messageQueueThreadImpl3 == null && reactQueueConfigurationSpec.getUIBackgroundQueueThreadSpec() != null) {
            messageQueueThreadImpl3 = MessageQueueThreadImpl.create(reactQueueConfigurationSpec.getUIBackgroundQueueThreadSpec(), queueThreadExceptionHandler);
        }
        return new ReactQueueConfigurationImpl(create, messageQueueThreadImpl3, messageQueueThreadImpl2, messageQueueThreadImpl);
    }
}
