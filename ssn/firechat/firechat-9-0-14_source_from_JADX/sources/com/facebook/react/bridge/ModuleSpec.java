package com.facebook.react.bridge;

import java.lang.reflect.Constructor;
import javax.annotation.Nullable;
import javax.inject.Provider;

public class ModuleSpec {
    /* access modifiers changed from: private */
    public static final Class[] CONTEXT_SIGNATURE = {ReactApplicationContext.class};
    /* access modifiers changed from: private */
    public static final Class[] EMPTY_SIGNATURE = new Class[0];
    private final Provider<? extends NativeModule> mProvider;
    private final Class<? extends NativeModule> mType;

    private static abstract class ConstructorProvider implements Provider<NativeModule> {
        @Nullable
        protected Constructor<? extends NativeModule> mConstructor;

        public ConstructorProvider(Class<? extends NativeModule> cls, Class[] clsArr) {
        }

        /* access modifiers changed from: protected */
        public Constructor<? extends NativeModule> getConstructor(Class<? extends NativeModule> cls, Class[] clsArr) throws NoSuchMethodException {
            if (this.mConstructor != null) {
                return this.mConstructor;
            }
            return cls.getConstructor(clsArr);
        }
    }

    public static ModuleSpec simple(final Class<? extends NativeModule> cls) {
        return new ModuleSpec(cls, new ConstructorProvider(EMPTY_SIGNATURE, cls) {
            public NativeModule get() {
                try {
                    return (NativeModule) getConstructor(cls, ModuleSpec.EMPTY_SIGNATURE).newInstance(new Object[0]);
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ModuleSpec with class: ");
                    sb.append(cls.getName());
                    throw new RuntimeException(sb.toString(), e);
                }
            }
        });
    }

    public static ModuleSpec simple(final Class<? extends NativeModule> cls, final ReactApplicationContext reactApplicationContext) {
        return new ModuleSpec(cls, new ConstructorProvider(CONTEXT_SIGNATURE, cls) {
            public NativeModule get() {
                try {
                    return (NativeModule) getConstructor(cls, ModuleSpec.CONTEXT_SIGNATURE).newInstance(new Object[]{reactApplicationContext});
                } catch (Exception e) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("ModuleSpec with class: ");
                    sb.append(cls.getName());
                    throw new RuntimeException(sb.toString(), e);
                }
            }
        });
    }

    public ModuleSpec(Class<? extends NativeModule> cls, Provider<? extends NativeModule> provider) {
        this.mType = cls;
        this.mProvider = provider;
    }

    public Class<? extends NativeModule> getType() {
        return this.mType;
    }

    public Provider<? extends NativeModule> getProvider() {
        return this.mProvider;
    }
}
