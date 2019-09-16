package com.facebook.react.animated;

import com.facebook.react.bridge.JavaOnlyMap;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.uimanager.ReactStylesDiffMap;
import com.facebook.react.uimanager.UIImplementation;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

class PropsAnimatedNode extends AnimatedNode {
    int mConnectedViewTag = -1;
    private final NativeAnimatedNodesManager mNativeAnimatedNodesManager;
    private final Map<String, Integer> mPropMapping;

    PropsAnimatedNode(ReadableMap readableMap, NativeAnimatedNodesManager nativeAnimatedNodesManager) {
        ReadableMap map = readableMap.getMap("props");
        ReadableMapKeySetIterator keySetIterator = map.keySetIterator();
        this.mPropMapping = new HashMap();
        while (keySetIterator.hasNextKey()) {
            String nextKey = keySetIterator.nextKey();
            this.mPropMapping.put(nextKey, Integer.valueOf(map.getInt(nextKey)));
        }
        this.mNativeAnimatedNodesManager = nativeAnimatedNodesManager;
    }

    public final void updateView(UIImplementation uIImplementation) {
        if (this.mConnectedViewTag == -1) {
            throw new IllegalStateException("Node has not been attached to a view");
        }
        JavaOnlyMap javaOnlyMap = new JavaOnlyMap();
        for (Entry entry : this.mPropMapping.entrySet()) {
            AnimatedNode nodeById = this.mNativeAnimatedNodesManager.getNodeById(((Integer) entry.getValue()).intValue());
            if (nodeById == null) {
                throw new IllegalArgumentException("Mapped property node does not exists");
            } else if (nodeById instanceof StyleAnimatedNode) {
                ((StyleAnimatedNode) nodeById).collectViewUpdates(javaOnlyMap);
            } else if (nodeById instanceof ValueAnimatedNode) {
                javaOnlyMap.putDouble((String) entry.getKey(), ((ValueAnimatedNode) nodeById).getValue());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("Unsupported type of node used in property node ");
                sb.append(nodeById.getClass());
                throw new IllegalArgumentException(sb.toString());
            }
        }
        uIImplementation.synchronouslyUpdateViewOnUIThread(this.mConnectedViewTag, new ReactStylesDiffMap(javaOnlyMap));
    }
}
