package com.facebook.react.bridge.queue;

import android.os.Build.VERSION;
import com.facebook.infer.annotation.Assertions;
import javax.annotation.Nullable;

public class ReactQueueConfigurationSpec {
    private static final long LEGACY_STACK_SIZE_BYTES = 2000000;
    private final MessageQueueThreadSpec mJSQueueThreadSpec;
    private final MessageQueueThreadSpec mNativeModulesQueueThreadSpec;
    @Nullable
    private final MessageQueueThreadSpec mUIBackgroundQueueThreadSpec;

    public static class Builder {
        @Nullable
        private MessageQueueThreadSpec mJSQueueSpec;
        @Nullable
        private MessageQueueThreadSpec mNativeModulesQueueSpec;
        @Nullable
        private MessageQueueThreadSpec mUIBackgroundQueueSpec;

        public Builder setUIBackgroundQueueThreadSpec(MessageQueueThreadSpec messageQueueThreadSpec) {
            Assertions.assertCondition(this.mUIBackgroundQueueSpec == null, "Setting UI background queue multiple times!");
            this.mUIBackgroundQueueSpec = messageQueueThreadSpec;
            return this;
        }

        public Builder setNativeModulesQueueThreadSpec(MessageQueueThreadSpec messageQueueThreadSpec) {
            Assertions.assertCondition(this.mNativeModulesQueueSpec == null, "Setting native modules queue spec multiple times!");
            this.mNativeModulesQueueSpec = messageQueueThreadSpec;
            return this;
        }

        public Builder setJSQueueThreadSpec(MessageQueueThreadSpec messageQueueThreadSpec) {
            Assertions.assertCondition(this.mJSQueueSpec == null, "Setting JS queue multiple times!");
            this.mJSQueueSpec = messageQueueThreadSpec;
            return this;
        }

        public ReactQueueConfigurationSpec build() {
            return new ReactQueueConfigurationSpec(this.mUIBackgroundQueueSpec, (MessageQueueThreadSpec) Assertions.assertNotNull(this.mNativeModulesQueueSpec), (MessageQueueThreadSpec) Assertions.assertNotNull(this.mJSQueueSpec));
        }
    }

    private ReactQueueConfigurationSpec(@Nullable MessageQueueThreadSpec messageQueueThreadSpec, MessageQueueThreadSpec messageQueueThreadSpec2, MessageQueueThreadSpec messageQueueThreadSpec3) {
        this.mUIBackgroundQueueThreadSpec = messageQueueThreadSpec;
        this.mNativeModulesQueueThreadSpec = messageQueueThreadSpec2;
        this.mJSQueueThreadSpec = messageQueueThreadSpec3;
    }

    @Nullable
    public MessageQueueThreadSpec getUIBackgroundQueueThreadSpec() {
        return this.mUIBackgroundQueueThreadSpec;
    }

    public MessageQueueThreadSpec getNativeModulesQueueThreadSpec() {
        return this.mNativeModulesQueueThreadSpec;
    }

    public MessageQueueThreadSpec getJSQueueThreadSpec() {
        return this.mJSQueueThreadSpec;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static ReactQueueConfigurationSpec createDefault() {
        MessageQueueThreadSpec messageQueueThreadSpec;
        if (VERSION.SDK_INT < 21) {
            messageQueueThreadSpec = MessageQueueThreadSpec.newBackgroundThreadSpec("native_modules", LEGACY_STACK_SIZE_BYTES);
        } else {
            messageQueueThreadSpec = MessageQueueThreadSpec.newBackgroundThreadSpec("native_modules");
        }
        return builder().setJSQueueThreadSpec(MessageQueueThreadSpec.newBackgroundThreadSpec("js")).setNativeModulesQueueThreadSpec(messageQueueThreadSpec).build();
    }

    public static ReactQueueConfigurationSpec createWithSeparateUIBackgroundThread() {
        MessageQueueThreadSpec messageQueueThreadSpec;
        if (VERSION.SDK_INT < 21) {
            messageQueueThreadSpec = MessageQueueThreadSpec.newBackgroundThreadSpec("native_modules", LEGACY_STACK_SIZE_BYTES);
        } else {
            messageQueueThreadSpec = MessageQueueThreadSpec.newBackgroundThreadSpec("native_modules");
        }
        return builder().setJSQueueThreadSpec(MessageQueueThreadSpec.newBackgroundThreadSpec("js")).setNativeModulesQueueThreadSpec(messageQueueThreadSpec).setUIBackgroundQueueThreadSpec(MessageQueueThreadSpec.newUIBackgroundTreadSpec("ui_background")).build();
    }
}
