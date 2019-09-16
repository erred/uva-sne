package com.facebook.stetho.inspector.elements.android;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewDebug.ExportedProperty;
import android.view.ViewDebug.FlagToString;
import android.view.ViewDebug.IntToString;
import com.facebook.react.uimanager.ViewProps;
import com.facebook.stetho.common.ExceptionUtil;
import com.facebook.stetho.common.LogUtil;
import com.facebook.stetho.common.ReflectionUtil;
import com.facebook.stetho.common.StringUtil;
import com.facebook.stetho.common.android.ResourcesUtil;
import com.facebook.stetho.inspector.elements.AbstractChainedDescriptor;
import com.facebook.stetho.inspector.elements.AttributeAccumulator;
import com.facebook.stetho.inspector.elements.ComputedStyleAccumulator;
import com.facebook.stetho.inspector.elements.StyleAccumulator;
import com.facebook.stetho.inspector.elements.StyleRuleNameAccumulator;
import com.facebook.stetho.inspector.helper.IntegerFormatter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.annotation.concurrent.GuardedBy;

final class ViewDescriptor extends AbstractChainedDescriptor<View> implements HighlightableDescriptor<View> {
    private static final String ACCESSIBILITY_STYLE_RULE_NAME = "Accessibility Properties";
    private static final String ID_NAME = "id";
    private static final String NONE_MAPPING = "<no mapping>";
    private static final String NONE_VALUE = "(none)";
    private static final String VIEW_STYLE_RULE_NAME = "<this_view>";
    private static final boolean sHasSupportNodeInfo = (ReflectionUtil.tryGetClassForName("android.support.v4.view.accessibility.AccessibilityNodeInfoCompat") != null);
    private final MethodInvoker mMethodInvoker;
    @GuardedBy("this")
    @Nullable
    private volatile List<ViewCSSProperty> mViewProperties;
    @Nullable
    private Pattern mWordBoundaryPattern;

    private final class FieldBackedCSSProperty extends ViewCSSProperty {
        private final Field mField;

        public FieldBackedCSSProperty(Field field, String str, @Nullable ExportedProperty exportedProperty) {
            super(str, exportedProperty);
            this.mField = field;
            this.mField.setAccessible(true);
        }

        public Object getValue(View view) throws InvocationTargetException, IllegalAccessException {
            return this.mField.get(view);
        }
    }

    private final class MethodBackedCSSProperty extends ViewCSSProperty {
        private final Method mMethod;

        public MethodBackedCSSProperty(Method method, String str, @Nullable ExportedProperty exportedProperty) {
            super(str, exportedProperty);
            this.mMethod = method;
            this.mMethod.setAccessible(true);
        }

        public Object getValue(View view) throws InvocationTargetException, IllegalAccessException {
            return this.mMethod.invoke(view, new Object[0]);
        }
    }

    private abstract class ViewCSSProperty {
        private final ExportedProperty mAnnotation;
        private final String mCSSName;

        public abstract Object getValue(View view) throws InvocationTargetException, IllegalAccessException;

        public ViewCSSProperty(String str, @Nullable ExportedProperty exportedProperty) {
            this.mCSSName = str;
            this.mAnnotation = exportedProperty;
        }

        public final String getCSSName() {
            return this.mCSSName;
        }

        @Nullable
        public final ExportedProperty getAnnotation() {
            return this.mAnnotation;
        }
    }

    @Nullable
    public View getViewAndBoundsForHighlighting(View view, Rect rect) {
        return view;
    }

    private Pattern getWordBoundaryPattern() {
        if (this.mWordBoundaryPattern == null) {
            this.mWordBoundaryPattern = Pattern.compile("(?<=\\p{Lower})(?=\\p{Upper})");
        }
        return this.mWordBoundaryPattern;
    }

    private List<ViewCSSProperty> getViewProperties() {
        Method[] declaredMethods;
        Field[] declaredFields;
        if (this.mViewProperties == null) {
            synchronized (this) {
                if (this.mViewProperties == null) {
                    ArrayList arrayList = new ArrayList();
                    for (Method method : View.class.getDeclaredMethods()) {
                        ExportedProperty exportedProperty = (ExportedProperty) method.getAnnotation(ExportedProperty.class);
                        if (exportedProperty != null) {
                            arrayList.add(new MethodBackedCSSProperty(method, convertViewPropertyNameToCSSName(method.getName()), exportedProperty));
                        }
                    }
                    for (Field field : View.class.getDeclaredFields()) {
                        ExportedProperty exportedProperty2 = (ExportedProperty) field.getAnnotation(ExportedProperty.class);
                        if (exportedProperty2 != null) {
                            arrayList.add(new FieldBackedCSSProperty(field, convertViewPropertyNameToCSSName(field.getName()), exportedProperty2));
                        }
                    }
                    Collections.sort(arrayList, new Comparator<ViewCSSProperty>() {
                        public int compare(ViewCSSProperty viewCSSProperty, ViewCSSProperty viewCSSProperty2) {
                            return viewCSSProperty.getCSSName().compareTo(viewCSSProperty2.getCSSName());
                        }
                    });
                    this.mViewProperties = Collections.unmodifiableList(arrayList);
                }
            }
        }
        return this.mViewProperties;
    }

    public ViewDescriptor() {
        this(new MethodInvoker());
    }

    public ViewDescriptor(MethodInvoker methodInvoker) {
        this.mMethodInvoker = methodInvoker;
    }

    /* access modifiers changed from: protected */
    public String onGetNodeName(View view) {
        String name = view.getClass().getName();
        return StringUtil.removePrefix(name, "android.view.", StringUtil.removePrefix(name, "android.widget."));
    }

    /* access modifiers changed from: protected */
    public void onGetAttributes(View view, AttributeAccumulator attributeAccumulator) {
        String idAttribute = getIdAttribute(view);
        if (idAttribute != null) {
            attributeAccumulator.store(ID_NAME, idAttribute);
        }
    }

    /* access modifiers changed from: protected */
    public void onSetAttributesAsText(View view, String str) {
        for (Entry entry : parseSetAttributesAsTextArg(str).entrySet()) {
            StringBuilder sb = new StringBuilder();
            sb.append("set");
            sb.append(capitalize((String) entry.getKey()));
            this.mMethodInvoker.invoke(view, sb.toString(), (String) entry.getValue());
        }
    }

    @Nullable
    private static String getIdAttribute(View view) {
        int id = view.getId();
        if (id == -1) {
            return null;
        }
        return ResourcesUtil.getIdStringQuietly(view, view.getResources(), id);
    }

    @Nullable
    public Object getElementToHighlightAtPosition(View view, int i, int i2, Rect rect) {
        rect.set(0, 0, view.getWidth(), view.getHeight());
        return view;
    }

    /* access modifiers changed from: protected */
    public void onGetStyleRuleNames(View view, StyleRuleNameAccumulator styleRuleNameAccumulator) {
        styleRuleNameAccumulator.store(VIEW_STYLE_RULE_NAME, false);
        if (sHasSupportNodeInfo) {
            styleRuleNameAccumulator.store(ACCESSIBILITY_STYLE_RULE_NAME, false);
        }
    }

    /* access modifiers changed from: protected */
    public void onGetStyles(View view, String str, StyleAccumulator styleAccumulator) {
        if (VIEW_STYLE_RULE_NAME.equals(str)) {
            List viewProperties = getViewProperties();
            int size = viewProperties.size();
            for (int i = 0; i < size; i++) {
                ViewCSSProperty viewCSSProperty = (ViewCSSProperty) viewProperties.get(i);
                try {
                    getStyleFromValue(view, viewCSSProperty.getCSSName(), viewCSSProperty.getValue(view), viewCSSProperty.getAnnotation(), styleAccumulator);
                } catch (Exception e) {
                    if ((e instanceof IllegalAccessException) || (e instanceof InvocationTargetException)) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("failed to get style property ");
                        sb.append(viewCSSProperty.getCSSName());
                        sb.append(" of element= ");
                        sb.append(view.toString());
                        LogUtil.m163e((Throwable) e, sb.toString());
                    } else {
                        throw ExceptionUtil.propagate(e);
                    }
                }
            }
        } else if (ACCESSIBILITY_STYLE_RULE_NAME.equals(str) && sHasSupportNodeInfo) {
            boolean ignored = AccessibilityNodeInfoWrapper.getIgnored(view);
            getStyleFromValue(view, "ignored", Boolean.valueOf(ignored), null, styleAccumulator);
            if (ignored) {
                getStyleFromValue(view, "ignored-reasons", AccessibilityNodeInfoWrapper.getIgnoredReasons(view), null, styleAccumulator);
            }
            View view2 = view;
            getStyleFromValue(view2, "focusable", Boolean.valueOf(!ignored), null, styleAccumulator);
            if (!ignored) {
                View view3 = view;
                StyleAccumulator styleAccumulator2 = styleAccumulator;
                getStyleFromValue(view3, "focusable-reasons", AccessibilityNodeInfoWrapper.getFocusableReasons(view), null, styleAccumulator2);
                View view4 = view;
                getStyleFromValue(view4, "focused", Boolean.valueOf(AccessibilityNodeInfoWrapper.getIsAccessibilityFocused(view)), null, styleAccumulator);
                getStyleFromValue(view3, "description", AccessibilityNodeInfoWrapper.getDescription(view), null, styleAccumulator2);
                getStyleFromValue(view4, "actions", AccessibilityNodeInfoWrapper.getActions(view), null, styleAccumulator);
            }
        }
    }

    /* access modifiers changed from: protected */
    public void onGetComputedStyles(View view, ComputedStyleAccumulator computedStyleAccumulator) {
        computedStyleAccumulator.store(ViewProps.LEFT, Integer.toString(view.getLeft()));
        computedStyleAccumulator.store(ViewProps.TOP, Integer.toString(view.getTop()));
        computedStyleAccumulator.store(ViewProps.RIGHT, Integer.toString(view.getRight()));
        computedStyleAccumulator.store(ViewProps.BOTTOM, Integer.toString(view.getBottom()));
    }

    private static boolean canIntBeMappedToString(@Nullable ExportedProperty exportedProperty) {
        return (exportedProperty == null || exportedProperty.mapping() == null || exportedProperty.mapping().length <= 0) ? false : true;
    }

    private static String mapIntToStringUsingAnnotation(int i, @Nullable ExportedProperty exportedProperty) {
        IntToString[] mapping;
        if (!canIntBeMappedToString(exportedProperty)) {
            throw new IllegalStateException("Cannot map using this annotation");
        }
        for (IntToString intToString : exportedProperty.mapping()) {
            if (intToString.from() == i) {
                return intToString.to();
            }
        }
        return NONE_MAPPING;
    }

    private static boolean canFlagsBeMappedToString(@Nullable ExportedProperty exportedProperty) {
        return (exportedProperty == null || exportedProperty.flagMapping() == null || exportedProperty.flagMapping().length <= 0) ? false : true;
    }

    private static String mapFlagsToStringUsingAnnotation(int i, @Nullable ExportedProperty exportedProperty) {
        FlagToString[] flagMapping;
        if (!canFlagsBeMappedToString(exportedProperty)) {
            throw new IllegalStateException("Cannot map using this annotation");
        }
        StringBuilder sb = null;
        boolean z = false;
        for (FlagToString flagToString : exportedProperty.flagMapping()) {
            if (flagToString.outputIf() == ((flagToString.mask() & i) == flagToString.equals())) {
                if (sb == null) {
                    sb = new StringBuilder();
                }
                if (z) {
                    sb.append(" | ");
                }
                sb.append(flagToString.name());
                z = true;
            }
        }
        return z ? sb.toString() : NONE_MAPPING;
    }

    private String convertViewPropertyNameToCSSName(String str) {
        String[] split = getWordBoundaryPattern().split(str);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < split.length; i++) {
            if (!split[i].equals("get") && !split[i].equals("m")) {
                sb.append(split[i].toLowerCase());
                if (i < split.length - 1) {
                    sb.append('-');
                }
            }
        }
        return sb.toString();
    }

    private void getStyleFromValue(View view, String str, Object obj, @Nullable ExportedProperty exportedProperty, StyleAccumulator styleAccumulator) {
        if (str.equals(ID_NAME)) {
            getIdStyle(view, styleAccumulator);
        } else if (obj instanceof Integer) {
            getStyleFromInteger(str, (Integer) obj, exportedProperty, styleAccumulator);
        } else {
            boolean z = true;
            if (obj instanceof Float) {
                String valueOf = String.valueOf(obj);
                if (((Float) obj).floatValue() != 0.0f) {
                    z = false;
                }
                styleAccumulator.store(str, valueOf, z);
            } else if (obj instanceof Boolean) {
                styleAccumulator.store(str, String.valueOf(obj), false);
            } else if (obj instanceof Short) {
                String valueOf2 = String.valueOf(obj);
                if (((Short) obj).shortValue() != 0) {
                    z = false;
                }
                styleAccumulator.store(str, valueOf2, z);
            } else if (obj instanceof Long) {
                String valueOf3 = String.valueOf(obj);
                if (((Long) obj).longValue() != 0) {
                    z = false;
                }
                styleAccumulator.store(str, valueOf3, z);
            } else if (obj instanceof Double) {
                String valueOf4 = String.valueOf(obj);
                if (((Double) obj).doubleValue() != 0.0d) {
                    z = false;
                }
                styleAccumulator.store(str, valueOf4, z);
            } else if (obj instanceof Byte) {
                String valueOf5 = String.valueOf(obj);
                if (((Byte) obj).byteValue() != 0) {
                    z = false;
                }
                styleAccumulator.store(str, valueOf5, z);
            } else if (obj instanceof Character) {
                String valueOf6 = String.valueOf(obj);
                if (((Character) obj).charValue() != 0) {
                    z = false;
                }
                styleAccumulator.store(str, valueOf6, z);
            } else if (obj instanceof CharSequence) {
                String valueOf7 = String.valueOf(obj);
                if (((CharSequence) obj).length() != 0) {
                    z = false;
                }
                styleAccumulator.store(str, valueOf7, z);
            } else {
                getStylesFromObject(view, str, obj, exportedProperty, styleAccumulator);
            }
        }
    }

    private void getIdStyle(View view, StyleAccumulator styleAccumulator) {
        String idAttribute = getIdAttribute(view);
        if (idAttribute == null) {
            styleAccumulator.store(ID_NAME, NONE_VALUE, false);
        } else {
            styleAccumulator.store(ID_NAME, idAttribute, false);
        }
    }

    private void getStyleFromInteger(String str, Integer num, @Nullable ExportedProperty exportedProperty, StyleAccumulator styleAccumulator) {
        String format = IntegerFormatter.getInstance().format(num, exportedProperty);
        if (canIntBeMappedToString(exportedProperty)) {
            StringBuilder sb = new StringBuilder();
            sb.append(format);
            sb.append(" (");
            sb.append(mapIntToStringUsingAnnotation(num.intValue(), exportedProperty));
            sb.append(")");
            styleAccumulator.store(str, sb.toString(), false);
        } else if (canFlagsBeMappedToString(exportedProperty)) {
            StringBuilder sb2 = new StringBuilder();
            sb2.append(format);
            sb2.append(" (");
            sb2.append(mapFlagsToStringUsingAnnotation(num.intValue(), exportedProperty));
            sb2.append(")");
            styleAccumulator.store(str, sb2.toString(), false);
        } else {
            Boolean valueOf = Boolean.valueOf(true);
            if (num.intValue() != 0 || canFlagsBeMappedToString(exportedProperty) || canIntBeMappedToString(exportedProperty)) {
                valueOf = Boolean.valueOf(false);
            }
            styleAccumulator.store(str, format, valueOf.booleanValue());
        }
    }

    /* JADX WARNING: Code restructure failed: missing block: B:28:0x006a, code lost:
        if (r9.equals("topMargin") != false) goto L_0x0078;
     */
    /* JADX WARNING: Removed duplicated region for block: B:34:0x007b  */
    /* JADX WARNING: Removed duplicated region for block: B:37:0x0083  */
    /* JADX WARNING: Removed duplicated region for block: B:38:0x0086  */
    /* JADX WARNING: Removed duplicated region for block: B:39:0x0089  */
    /* JADX WARNING: Removed duplicated region for block: B:40:0x008c  */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private void getStylesFromObject(android.view.View r16, java.lang.String r17, java.lang.Object r18, @javax.annotation.Nullable android.view.ViewDebug.ExportedProperty r19, com.facebook.stetho.inspector.elements.StyleAccumulator r20) {
        /*
            r15 = this;
            r1 = r18
            if (r19 == 0) goto L_0x00e3
            boolean r3 = r19.deepExport()
            if (r3 == 0) goto L_0x00e3
            if (r1 != 0) goto L_0x000e
            goto L_0x00e3
        L_0x000e:
            java.lang.Class r3 = r18.getClass()
            java.lang.reflect.Field[] r3 = r3.getFields()
            int r4 = r3.length
            r5 = 0
            r6 = 0
        L_0x0019:
            if (r6 >= r4) goto L_0x00e1
            r7 = r3[r6]
            int r8 = r7.getModifiers()
            boolean r8 = java.lang.reflect.Modifier.isStatic(r8)
            if (r8 == 0) goto L_0x002a
            r8 = r15
            goto L_0x00b7
        L_0x002a:
            r8 = 1
            r7.setAccessible(r8)     // Catch:{ IllegalAccessException -> 0x00bb }
            java.lang.Object r12 = r7.get(r1)     // Catch:{ IllegalAccessException -> 0x00bb }
            java.lang.String r9 = r7.getName()
            r10 = -1
            int r11 = r9.hashCode()
            r13 = -599904534(0xffffffffdc3e2eea, float:-2.14127313E17)
            if (r11 == r13) goto L_0x006d
            r13 = -414179485(0xffffffffe7501f63, float:-9.828312E23)
            if (r11 == r13) goto L_0x0064
            r8 = 1928835221(0x72f7b095, float:9.812003E30)
            if (r11 == r8) goto L_0x005a
            r8 = 2064613305(0x7b0f7fb9, float:7.45089E35)
            if (r11 == r8) goto L_0x0050
            goto L_0x0077
        L_0x0050:
            java.lang.String r8 = "bottomMargin"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0077
            r8 = 0
            goto L_0x0078
        L_0x005a:
            java.lang.String r8 = "leftMargin"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0077
            r8 = 2
            goto L_0x0078
        L_0x0064:
            java.lang.String r11 = "topMargin"
            boolean r11 = r9.equals(r11)
            if (r11 == 0) goto L_0x0077
            goto L_0x0078
        L_0x006d:
            java.lang.String r8 = "rightMargin"
            boolean r8 = r9.equals(r8)
            if (r8 == 0) goto L_0x0077
            r8 = 3
            goto L_0x0078
        L_0x0077:
            r8 = -1
        L_0x0078:
            switch(r8) {
                case 0: goto L_0x008c;
                case 1: goto L_0x0089;
                case 2: goto L_0x0086;
                case 3: goto L_0x0083;
                default: goto L_0x007b;
            }
        L_0x007b:
            java.lang.String r8 = r19.prefix()
            if (r8 != 0) goto L_0x0091
        L_0x0081:
            r8 = r15
            goto L_0x00a1
        L_0x0083:
            java.lang.String r8 = "margin-right"
            goto L_0x008e
        L_0x0086:
            java.lang.String r8 = "margin-left"
            goto L_0x008e
        L_0x0089:
            java.lang.String r8 = "margin-top"
            goto L_0x008e
        L_0x008c:
            java.lang.String r8 = "margin-bottom"
        L_0x008e:
            r11 = r8
            r8 = r15
            goto L_0x00a6
        L_0x0091:
            java.lang.StringBuilder r10 = new java.lang.StringBuilder
            r10.<init>()
            r10.append(r8)
            r10.append(r9)
            java.lang.String r9 = r10.toString()
            goto L_0x0081
        L_0x00a1:
            java.lang.String r9 = r8.convertViewPropertyNameToCSSName(r9)
            r11 = r9
        L_0x00a6:
            java.lang.Class<android.view.ViewDebug$ExportedProperty> r9 = android.view.ViewDebug.ExportedProperty.class
            java.lang.annotation.Annotation r7 = r7.getAnnotation(r9)
            r13 = r7
            android.view.ViewDebug$ExportedProperty r13 = (android.view.ViewDebug.ExportedProperty) r13
            r9 = r8
            r10 = r16
            r14 = r20
            r9.getStyleFromValue(r10, r11, r12, r13, r14)
        L_0x00b7:
            int r6 = r6 + 1
            goto L_0x0019
        L_0x00bb:
            r0 = move-exception
            r8 = r15
            r2 = r0
            java.lang.StringBuilder r3 = new java.lang.StringBuilder
            r3.<init>()
            java.lang.String r4 = "failed to get property of name: \""
            r3.append(r4)
            r4 = r17
            r3.append(r4)
            java.lang.String r4 = "\" of object: "
            r3.append(r4)
            java.lang.String r1 = java.lang.String.valueOf(r18)
            r3.append(r1)
            java.lang.String r1 = r3.toString()
            com.facebook.stetho.common.LogUtil.m163e(r2, r1)
            return
        L_0x00e1:
            r8 = r15
            return
        L_0x00e3:
            r8 = r15
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.stetho.inspector.elements.android.ViewDescriptor.getStylesFromObject(android.view.View, java.lang.String, java.lang.Object, android.view.ViewDebug$ExportedProperty, com.facebook.stetho.inspector.elements.StyleAccumulator):void");
    }

    private static String capitalize(String str) {
        if (str == null || str.length() == 0 || Character.isTitleCase(str.charAt(0))) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        sb.setCharAt(0, Character.toTitleCase(sb.charAt(0)));
        return sb.toString();
    }
}
