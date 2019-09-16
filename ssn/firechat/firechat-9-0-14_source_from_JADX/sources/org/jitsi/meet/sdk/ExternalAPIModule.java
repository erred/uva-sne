package org.jitsi.meet.sdk;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

class ExternalAPIModule extends ReactContextBaseJavaModule {
    private static final Map<String, Method> JITSI_MEET_VIEW_LISTENER_METHODS = new HashMap();

    public String getName() {
        return "ExternalAPI";
    }

    static {
        Method[] declaredMethods;
        Pattern compile = Pattern.compile("^on[A-Z]+");
        Pattern compile2 = Pattern.compile("([a-z0-9]+)([A-Z0-9]+)");
        for (Method method : JitsiMeetViewListener.class.getDeclaredMethods()) {
            if (Modifier.isPublic(method.getModifiers()) && Void.TYPE.equals(method.getReturnType())) {
                String name = method.getName();
                if (compile.matcher(name).find()) {
                    Class[] parameterTypes = method.getParameterTypes();
                    if (parameterTypes.length == 1 && parameterTypes[0].isAssignableFrom(HashMap.class)) {
                        JITSI_MEET_VIEW_LISTENER_METHODS.put(compile2.matcher(name.substring(2)).replaceAll("$1_$2").toUpperCase(Locale.ROOT), method);
                    }
                }
            }
        }
    }

    public ExternalAPIModule(ReactApplicationContext reactApplicationContext) {
        super(reactApplicationContext);
    }

    @ReactMethod
    public void sendEvent(String str, ReadableMap readableMap, String str2) {
        JitsiMeetView findViewByExternalAPIScope = JitsiMeetView.findViewByExternalAPIScope(str2);
        if (findViewByExternalAPIScope != null) {
            JitsiMeetViewListener listener = findViewByExternalAPIScope.getListener();
            if (listener != null) {
                Method method = (Method) JITSI_MEET_VIEW_LISTENER_METHODS.get(str);
                if (method != null) {
                    try {
                        method.invoke(listener, new Object[]{toHashMap(readableMap)});
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private HashMap<String, Object> toHashMap(ReadableMap readableMap) {
        HashMap<String, Object> hashMap = new HashMap<>();
        ReadableMapKeySetIterator keySetIterator = readableMap.keySetIterator();
        while (keySetIterator.hasNextKey()) {
            String nextKey = keySetIterator.nextKey();
            hashMap.put(nextKey, readableMap.getString(nextKey));
        }
        return hashMap;
    }
}
