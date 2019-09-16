package com.facebook.react.util;

import com.facebook.common.util.UriUtil;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.devsupport.StackTraceHelper;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

public class JSStackTrace {
    private static final Pattern mJsModuleIdPattern = Pattern.compile("(?:^|[/\\\\])(\\d+\\.js)$");

    public static String format(String str, ReadableArray readableArray) {
        StringBuilder sb = new StringBuilder(str);
        sb.append(", stack:\n");
        for (int i = 0; i < readableArray.size(); i++) {
            ReadableMap map = readableArray.getMap(i);
            sb.append(map.getString("methodName"));
            sb.append("@");
            sb.append(stackFrameToModuleId(map));
            sb.append(map.getInt(StackTraceHelper.LINE_NUMBER_KEY));
            if (map.hasKey(StackTraceHelper.COLUMN_KEY) && !map.isNull(StackTraceHelper.COLUMN_KEY) && map.getType(StackTraceHelper.COLUMN_KEY) == ReadableType.Number) {
                sb.append(":");
                sb.append(map.getInt(StackTraceHelper.COLUMN_KEY));
            }
            sb.append(StringUtils.f158LF);
        }
        return sb.toString();
    }

    private static String stackFrameToModuleId(ReadableMap readableMap) {
        if (readableMap.hasKey(UriUtil.LOCAL_FILE_SCHEME) && !readableMap.isNull(UriUtil.LOCAL_FILE_SCHEME) && readableMap.getType(UriUtil.LOCAL_FILE_SCHEME) == ReadableType.String) {
            Matcher matcher = mJsModuleIdPattern.matcher(readableMap.getString(UriUtil.LOCAL_FILE_SCHEME));
            if (matcher.find()) {
                StringBuilder sb = new StringBuilder();
                sb.append(matcher.group(1));
                sb.append(":");
                return sb.toString();
            }
        }
        return "";
    }
}
