package com.facebook.react.uimanager;

import java.util.Arrays;
import java.util.HashSet;

public class ViewProps {
    public static final String ALIGN_CONTENT = "alignContent";
    public static final String ALIGN_ITEMS = "alignItems";
    public static final String ALIGN_SELF = "alignSelf";
    public static final String ALLOW_FONT_SCALING = "allowFontScaling";
    public static final String ASPECT_RATIO = "aspectRatio";
    public static final String BACKGROUND_COLOR = "backgroundColor";
    public static final String BORDER_BOTTOM_COLOR = "borderBottomColor";
    public static final String BORDER_BOTTOM_LEFT_RADIUS = "borderBottomLeftRadius";
    public static final String BORDER_BOTTOM_RIGHT_RADIUS = "borderBottomRightRadius";
    public static final String BORDER_BOTTOM_WIDTH = "borderBottomWidth";
    public static final String BORDER_COLOR = "borderColor";
    public static final String BORDER_LEFT_COLOR = "borderLeftColor";
    public static final String BORDER_LEFT_WIDTH = "borderLeftWidth";
    public static final String BORDER_RADIUS = "borderRadius";
    public static final String BORDER_RIGHT_COLOR = "borderRightColor";
    public static final String BORDER_RIGHT_WIDTH = "borderRightWidth";
    public static final int[] BORDER_SPACING_TYPES = {8, 4, 5, 1, 3};
    public static final String BORDER_TOP_COLOR = "borderTopColor";
    public static final String BORDER_TOP_LEFT_RADIUS = "borderTopLeftRadius";
    public static final String BORDER_TOP_RIGHT_RADIUS = "borderTopRightRadius";
    public static final String BORDER_TOP_WIDTH = "borderTopWidth";
    public static final String BORDER_WIDTH = "borderWidth";
    public static final String BOTTOM = "bottom";
    public static final String COLLAPSABLE = "collapsable";
    public static final String COLOR = "color";
    public static final String DISPLAY = "display";
    public static final String ELLIPSIZE_MODE = "ellipsizeMode";
    public static final String ENABLED = "enabled";
    public static final String FLEX = "flex";
    public static final String FLEX_BASIS = "flexBasis";
    public static final String FLEX_DIRECTION = "flexDirection";
    public static final String FLEX_GROW = "flexGrow";
    public static final String FLEX_SHRINK = "flexShrink";
    public static final String FLEX_WRAP = "flexWrap";
    public static final String FONT_FAMILY = "fontFamily";
    public static final String FONT_SIZE = "fontSize";
    public static final String FONT_STYLE = "fontStyle";
    public static final String FONT_WEIGHT = "fontWeight";
    public static final String HEIGHT = "height";
    public static final String INCLUDE_FONT_PADDING = "includeFontPadding";
    public static final String JUSTIFY_CONTENT = "justifyContent";
    private static final HashSet<String> LAYOUT_ONLY_PROPS = new HashSet<>(Arrays.asList(new String[]{ALIGN_SELF, ALIGN_ITEMS, COLLAPSABLE, FLEX, FLEX_BASIS, FLEX_DIRECTION, FLEX_GROW, FLEX_SHRINK, FLEX_WRAP, JUSTIFY_CONTENT, OVERFLOW, ALIGN_CONTENT, DISPLAY, POSITION, RIGHT, TOP, BOTTOM, LEFT, "width", "height", MIN_WIDTH, MAX_WIDTH, MIN_HEIGHT, MAX_HEIGHT, MARGIN, MARGIN_VERTICAL, MARGIN_HORIZONTAL, MARGIN_LEFT, MARGIN_RIGHT, MARGIN_TOP, MARGIN_BOTTOM, PADDING, PADDING_VERTICAL, PADDING_HORIZONTAL, PADDING_LEFT, PADDING_RIGHT, PADDING_TOP, PADDING_BOTTOM}));
    public static final String LEFT = "left";
    public static final String LINE_HEIGHT = "lineHeight";
    public static final String MARGIN = "margin";
    public static final String MARGIN_BOTTOM = "marginBottom";
    public static final String MARGIN_HORIZONTAL = "marginHorizontal";
    public static final String MARGIN_LEFT = "marginLeft";
    public static final String MARGIN_RIGHT = "marginRight";
    public static final String MARGIN_TOP = "marginTop";
    public static final String MARGIN_VERTICAL = "marginVertical";
    public static final String MAX_HEIGHT = "maxHeight";
    public static final String MAX_WIDTH = "maxWidth";
    public static final String MIN_HEIGHT = "minHeight";
    public static final String MIN_WIDTH = "minWidth";
    public static final String NEEDS_OFFSCREEN_ALPHA_COMPOSITING = "needsOffscreenAlphaCompositing";
    public static final String NUMBER_OF_LINES = "numberOfLines";

    /* renamed from: ON */
    public static final String f85ON = "on";
    public static final String OPACITY = "opacity";
    public static final String OVERFLOW = "overflow";
    public static final String PADDING = "padding";
    public static final String PADDING_BOTTOM = "paddingBottom";
    public static final String PADDING_HORIZONTAL = "paddingHorizontal";
    public static final String PADDING_LEFT = "paddingLeft";
    public static final int[] PADDING_MARGIN_SPACING_TYPES = {8, 7, 6, 4, 5, 1, 3};
    public static final String PADDING_RIGHT = "paddingRight";
    public static final String PADDING_TOP = "paddingTop";
    public static final String PADDING_VERTICAL = "paddingVertical";
    public static final String POINTER_EVENTS = "pointerEvents";
    public static final String POSITION = "position";
    public static final int[] POSITION_SPACING_TYPES = {4, 5, 1, 3};
    public static final String RESIZE_METHOD = "resizeMethod";
    public static final String RESIZE_MODE = "resizeMode";
    public static final String RIGHT = "right";
    public static final String TEXT_ALIGN = "textAlign";
    public static final String TEXT_ALIGN_VERTICAL = "textAlignVertical";
    public static final String TEXT_BREAK_STRATEGY = "textBreakStrategy";
    public static final String TEXT_DECORATION_LINE = "textDecorationLine";
    public static final String TOP = "top";
    public static final String VIEW_CLASS_NAME = "RCTView";
    public static final String WIDTH = "width";
    public static boolean sIsOptimizationsEnabled;

    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static boolean isLayoutOnly(com.facebook.react.bridge.ReadableMap r5, java.lang.String r6) {
        /*
            java.util.HashSet<java.lang.String> r0 = LAYOUT_ONLY_PROPS
            boolean r0 = r0.contains(r6)
            r1 = 1
            if (r0 == 0) goto L_0x000a
            return r1
        L_0x000a:
            java.lang.String r0 = "pointerEvents"
            boolean r0 = r0.equals(r6)
            r2 = 0
            if (r0 == 0) goto L_0x002a
            java.lang.String r5 = r5.getString(r6)
            java.lang.String r6 = "auto"
            boolean r6 = r6.equals(r5)
            if (r6 != 0) goto L_0x0029
            java.lang.String r6 = "box-none"
            boolean r5 = r6.equals(r5)
            if (r5 == 0) goto L_0x0028
            goto L_0x0029
        L_0x0028:
            r1 = 0
        L_0x0029:
            return r1
        L_0x002a:
            boolean r0 = sIsOptimizationsEnabled
            if (r0 == 0) goto L_0x019a
            r0 = -1
            int r3 = r6.hashCode()
            switch(r3) {
                case -1989576717: goto L_0x00d0;
                case -1971292586: goto L_0x00c5;
                case -1470826662: goto L_0x00bb;
                case -1452542531: goto L_0x00b0;
                case -1308858324: goto L_0x00a6;
                case -1290574193: goto L_0x009b;
                case -1267206133: goto L_0x0091;
                case -242276144: goto L_0x0087;
                case -223992013: goto L_0x007c;
                case 529642498: goto L_0x0071;
                case 722830999: goto L_0x0066;
                case 741115130: goto L_0x005a;
                case 1287124693: goto L_0x004f;
                case 1288688105: goto L_0x0043;
                case 1349188574: goto L_0x0038;
                default: goto L_0x0036;
            }
        L_0x0036:
            goto L_0x00da
        L_0x0038:
            java.lang.String r3 = "borderRadius"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 2
            goto L_0x00db
        L_0x0043:
            java.lang.String r3 = "onLayout"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 13
            goto L_0x00db
        L_0x004f:
            java.lang.String r3 = "backgroundColor"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 1
            goto L_0x00db
        L_0x005a:
            java.lang.String r3 = "borderWidth"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 8
            goto L_0x00db
        L_0x0066:
            java.lang.String r3 = "borderColor"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 3
            goto L_0x00db
        L_0x0071:
            java.lang.String r3 = "overflow"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 14
            goto L_0x00db
        L_0x007c:
            java.lang.String r3 = "borderLeftWidth"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 9
            goto L_0x00db
        L_0x0087:
            java.lang.String r3 = "borderLeftColor"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 4
            goto L_0x00db
        L_0x0091:
            java.lang.String r3 = "opacity"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 0
            goto L_0x00db
        L_0x009b:
            java.lang.String r3 = "borderBottomWidth"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 12
            goto L_0x00db
        L_0x00a6:
            java.lang.String r3 = "borderBottomColor"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 7
            goto L_0x00db
        L_0x00b0:
            java.lang.String r3 = "borderTopWidth"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 10
            goto L_0x00db
        L_0x00bb:
            java.lang.String r3 = "borderTopColor"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 6
            goto L_0x00db
        L_0x00c5:
            java.lang.String r3 = "borderRightWidth"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 11
            goto L_0x00db
        L_0x00d0:
            java.lang.String r3 = "borderRightColor"
            boolean r6 = r6.equals(r3)
            if (r6 == 0) goto L_0x00da
            r6 = 5
            goto L_0x00db
        L_0x00da:
            r6 = -1
        L_0x00db:
            r3 = 0
            switch(r6) {
                case 0: goto L_0x018b;
                case 1: goto L_0x0180;
                case 2: goto L_0x015b;
                case 3: goto L_0x0150;
                case 4: goto L_0x0145;
                case 5: goto L_0x013a;
                case 6: goto L_0x012f;
                case 7: goto L_0x0124;
                case 8: goto L_0x0117;
                case 9: goto L_0x010a;
                case 10: goto L_0x00fd;
                case 11: goto L_0x00f0;
                case 12: goto L_0x00e3;
                case 13: goto L_0x00e2;
                case 14: goto L_0x00e1;
                default: goto L_0x00e0;
            }
        L_0x00e0:
            return r2
        L_0x00e1:
            return r1
        L_0x00e2:
            return r1
        L_0x00e3:
            java.lang.String r6 = "borderBottomWidth"
            double r5 = r5.getDouble(r6)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x00ee
            goto L_0x00ef
        L_0x00ee:
            r1 = 0
        L_0x00ef:
            return r1
        L_0x00f0:
            java.lang.String r6 = "borderRightWidth"
            double r5 = r5.getDouble(r6)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x00fb
            goto L_0x00fc
        L_0x00fb:
            r1 = 0
        L_0x00fc:
            return r1
        L_0x00fd:
            java.lang.String r6 = "borderTopWidth"
            double r5 = r5.getDouble(r6)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0108
            goto L_0x0109
        L_0x0108:
            r1 = 0
        L_0x0109:
            return r1
        L_0x010a:
            java.lang.String r6 = "borderLeftWidth"
            double r5 = r5.getDouble(r6)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0115
            goto L_0x0116
        L_0x0115:
            r1 = 0
        L_0x0116:
            return r1
        L_0x0117:
            java.lang.String r6 = "borderWidth"
            double r5 = r5.getDouble(r6)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0122
            goto L_0x0123
        L_0x0122:
            r1 = 0
        L_0x0123:
            return r1
        L_0x0124:
            java.lang.String r6 = "borderBottomColor"
            int r5 = r5.getInt(r6)
            if (r5 != 0) goto L_0x012d
            goto L_0x012e
        L_0x012d:
            r1 = 0
        L_0x012e:
            return r1
        L_0x012f:
            java.lang.String r6 = "borderTopColor"
            int r5 = r5.getInt(r6)
            if (r5 != 0) goto L_0x0138
            goto L_0x0139
        L_0x0138:
            r1 = 0
        L_0x0139:
            return r1
        L_0x013a:
            java.lang.String r6 = "borderRightColor"
            int r5 = r5.getInt(r6)
            if (r5 != 0) goto L_0x0143
            goto L_0x0144
        L_0x0143:
            r1 = 0
        L_0x0144:
            return r1
        L_0x0145:
            java.lang.String r6 = "borderLeftColor"
            int r5 = r5.getInt(r6)
            if (r5 != 0) goto L_0x014e
            goto L_0x014f
        L_0x014e:
            r1 = 0
        L_0x014f:
            return r1
        L_0x0150:
            java.lang.String r6 = "borderColor"
            int r5 = r5.getInt(r6)
            if (r5 != 0) goto L_0x0159
            goto L_0x015a
        L_0x0159:
            r1 = 0
        L_0x015a:
            return r1
        L_0x015b:
            java.lang.String r6 = "backgroundColor"
            boolean r6 = r5.hasKey(r6)
            if (r6 == 0) goto L_0x016c
            java.lang.String r6 = "backgroundColor"
            int r6 = r5.getInt(r6)
            if (r6 == 0) goto L_0x016c
            return r2
        L_0x016c:
            java.lang.String r6 = "borderWidth"
            boolean r6 = r5.hasKey(r6)
            if (r6 == 0) goto L_0x017f
            java.lang.String r6 = "borderWidth"
            double r5 = r5.getDouble(r6)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 == 0) goto L_0x017f
            return r2
        L_0x017f:
            return r1
        L_0x0180:
            java.lang.String r6 = "backgroundColor"
            int r5 = r5.getInt(r6)
            if (r5 != 0) goto L_0x0189
            goto L_0x018a
        L_0x0189:
            r1 = 0
        L_0x018a:
            return r1
        L_0x018b:
            java.lang.String r6 = "opacity"
            double r5 = r5.getDouble(r6)
            r3 = 4607182418800017408(0x3ff0000000000000, double:1.0)
            int r0 = (r5 > r3 ? 1 : (r5 == r3 ? 0 : -1))
            if (r0 != 0) goto L_0x0198
            goto L_0x0199
        L_0x0198:
            r1 = 0
        L_0x0199:
            return r1
        L_0x019a:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.facebook.react.uimanager.ViewProps.isLayoutOnly(com.facebook.react.bridge.ReadableMap, java.lang.String):boolean");
    }
}
