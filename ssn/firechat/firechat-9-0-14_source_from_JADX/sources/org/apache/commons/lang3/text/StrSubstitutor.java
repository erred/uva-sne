package org.apache.commons.lang3.text;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.lang3.StringUtils;

public class StrSubstitutor {
    public static final char DEFAULT_ESCAPE = '$';
    public static final StrMatcher DEFAULT_PREFIX = StrMatcher.stringMatcher("${");
    public static final StrMatcher DEFAULT_SUFFIX = StrMatcher.stringMatcher("}");
    public static final StrMatcher DEFAULT_VALUE_DELIMITER = StrMatcher.stringMatcher(":-");
    private boolean enableSubstitutionInVariables;
    private char escapeChar;
    private StrMatcher prefixMatcher;
    private boolean preserveEscapes;
    private StrMatcher suffixMatcher;
    private StrMatcher valueDelimiterMatcher;
    private StrLookup<?> variableResolver;

    public static <V> String replace(Object obj, Map<String, V> map) {
        return new StrSubstitutor(map).replace(obj);
    }

    public static <V> String replace(Object obj, Map<String, V> map, String str, String str2) {
        return new StrSubstitutor(map, str, str2).replace(obj);
    }

    public static String replace(Object obj, Properties properties) {
        if (properties == null) {
            return obj.toString();
        }
        HashMap hashMap = new HashMap();
        Enumeration propertyNames = properties.propertyNames();
        while (propertyNames.hasMoreElements()) {
            String str = (String) propertyNames.nextElement();
            hashMap.put(str, properties.getProperty(str));
        }
        return replace(obj, (Map<String, V>) hashMap);
    }

    public static String replaceSystemProperties(Object obj) {
        return new StrSubstitutor(StrLookup.systemPropertiesLookup()).replace(obj);
    }

    public StrSubstitutor() {
        this(null, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StrSubstitutor(Map<String, V> map) {
        this(StrLookup.mapLookup(map), DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public <V> StrSubstitutor(Map<String, V> map, String str, String str2) {
        this(StrLookup.mapLookup(map), str, str2, '$');
    }

    public <V> StrSubstitutor(Map<String, V> map, String str, String str2, char c) {
        this(StrLookup.mapLookup(map), str, str2, c);
    }

    public <V> StrSubstitutor(Map<String, V> map, String str, String str2, char c, String str3) {
        this(StrLookup.mapLookup(map), str, str2, c, str3);
    }

    public StrSubstitutor(StrLookup<?> strLookup) {
        this(strLookup, DEFAULT_PREFIX, DEFAULT_SUFFIX, '$');
    }

    public StrSubstitutor(StrLookup<?> strLookup, String str, String str2, char c) {
        this.preserveEscapes = false;
        setVariableResolver(strLookup);
        setVariablePrefix(str);
        setVariableSuffix(str2);
        setEscapeChar(c);
        setValueDelimiterMatcher(DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(StrLookup<?> strLookup, String str, String str2, char c, String str3) {
        this.preserveEscapes = false;
        setVariableResolver(strLookup);
        setVariablePrefix(str);
        setVariableSuffix(str2);
        setEscapeChar(c);
        setValueDelimiter(str3);
    }

    public StrSubstitutor(StrLookup<?> strLookup, StrMatcher strMatcher, StrMatcher strMatcher2, char c) {
        this(strLookup, strMatcher, strMatcher2, c, DEFAULT_VALUE_DELIMITER);
    }

    public StrSubstitutor(StrLookup<?> strLookup, StrMatcher strMatcher, StrMatcher strMatcher2, char c, StrMatcher strMatcher3) {
        this.preserveEscapes = false;
        setVariableResolver(strLookup);
        setVariablePrefixMatcher(strMatcher);
        setVariableSuffixMatcher(strMatcher2);
        setEscapeChar(c);
        setValueDelimiterMatcher(strMatcher3);
    }

    public String replace(String str) {
        if (str == null) {
            return null;
        }
        StrBuilder strBuilder = new StrBuilder(str);
        if (!substitute(strBuilder, 0, str.length())) {
            return str;
        }
        return strBuilder.toString();
    }

    public String replace(String str, int i, int i2) {
        if (str == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(i2).append(str, i, i2);
        if (!substitute(append, 0, i2)) {
            return str.substring(i, i2 + i);
        }
        return append.toString();
    }

    public String replace(char[] cArr) {
        if (cArr == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(cArr.length).append(cArr);
        substitute(append, 0, cArr.length);
        return append.toString();
    }

    public String replace(char[] cArr, int i, int i2) {
        if (cArr == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(i2).append(cArr, i, i2);
        substitute(append, 0, i2);
        return append.toString();
    }

    public String replace(StringBuffer stringBuffer) {
        if (stringBuffer == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(stringBuffer.length()).append(stringBuffer);
        substitute(append, 0, append.length());
        return append.toString();
    }

    public String replace(StringBuffer stringBuffer, int i, int i2) {
        if (stringBuffer == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(i2).append(stringBuffer, i, i2);
        substitute(append, 0, i2);
        return append.toString();
    }

    public String replace(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        return replace(charSequence, 0, charSequence.length());
    }

    public String replace(CharSequence charSequence, int i, int i2) {
        if (charSequence == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(i2).append(charSequence, i, i2);
        substitute(append, 0, i2);
        return append.toString();
    }

    public String replace(StrBuilder strBuilder) {
        if (strBuilder == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(strBuilder.length()).append(strBuilder);
        substitute(append, 0, append.length());
        return append.toString();
    }

    public String replace(StrBuilder strBuilder, int i, int i2) {
        if (strBuilder == null) {
            return null;
        }
        StrBuilder append = new StrBuilder(i2).append(strBuilder, i, i2);
        substitute(append, 0, i2);
        return append.toString();
    }

    public String replace(Object obj) {
        if (obj == null) {
            return null;
        }
        StrBuilder append = new StrBuilder().append(obj);
        substitute(append, 0, append.length());
        return append.toString();
    }

    public boolean replaceIn(StringBuffer stringBuffer) {
        if (stringBuffer == null) {
            return false;
        }
        return replaceIn(stringBuffer, 0, stringBuffer.length());
    }

    public boolean replaceIn(StringBuffer stringBuffer, int i, int i2) {
        if (stringBuffer == null) {
            return false;
        }
        StrBuilder append = new StrBuilder(i2).append(stringBuffer, i, i2);
        if (!substitute(append, 0, i2)) {
            return false;
        }
        stringBuffer.replace(i, i2 + i, append.toString());
        return true;
    }

    public boolean replaceIn(StringBuilder sb) {
        if (sb == null) {
            return false;
        }
        return replaceIn(sb, 0, sb.length());
    }

    public boolean replaceIn(StringBuilder sb, int i, int i2) {
        if (sb == null) {
            return false;
        }
        StrBuilder append = new StrBuilder(i2).append(sb, i, i2);
        if (!substitute(append, 0, i2)) {
            return false;
        }
        sb.replace(i, i2 + i, append.toString());
        return true;
    }

    public boolean replaceIn(StrBuilder strBuilder) {
        if (strBuilder == null) {
            return false;
        }
        return substitute(strBuilder, 0, strBuilder.length());
    }

    public boolean replaceIn(StrBuilder strBuilder, int i, int i2) {
        if (strBuilder == null) {
            return false;
        }
        return substitute(strBuilder, i, i2);
    }

    /* access modifiers changed from: protected */
    public boolean substitute(StrBuilder strBuilder, int i, int i2) {
        return substitute(strBuilder, i, i2, null) > 0;
    }

    private int substitute(StrBuilder strBuilder, int i, int i2, List<String> list) {
        StrMatcher strMatcher;
        int i3;
        char c;
        StrMatcher strMatcher2;
        String str;
        String str2;
        List<String> list2;
        StrBuilder strBuilder2 = strBuilder;
        int i4 = i;
        int i5 = i2;
        StrMatcher variablePrefixMatcher = getVariablePrefixMatcher();
        StrMatcher variableSuffixMatcher = getVariableSuffixMatcher();
        char escapeChar2 = getEscapeChar();
        StrMatcher valueDelimiterMatcher2 = getValueDelimiterMatcher();
        boolean isEnableSubstitutionInVariables = isEnableSubstitutionInVariables();
        boolean z = list == null;
        char[] cArr = strBuilder2.buffer;
        int i6 = i4 + i5;
        List<String> list3 = list;
        int i7 = i4;
        int i8 = 0;
        int i9 = 0;
        while (i7 < i6) {
            int isMatch = variablePrefixMatcher.isMatch(cArr, i7, i4, i6);
            if (isMatch == 0) {
                i7++;
                strMatcher = variablePrefixMatcher;
                strMatcher2 = variableSuffixMatcher;
                c = escapeChar2;
                i3 = i8;
            } else {
                if (i7 > i4) {
                    int i10 = i7 - 1;
                    if (cArr[i10] == escapeChar2) {
                        if (this.preserveEscapes) {
                            i7++;
                        } else {
                            strBuilder2.deleteCharAt(i10);
                            i9--;
                            i6--;
                            strMatcher = variablePrefixMatcher;
                            strMatcher2 = variableSuffixMatcher;
                            c = escapeChar2;
                            cArr = strBuilder2.buffer;
                            i3 = 1;
                        }
                    }
                }
                int i11 = i7 + isMatch;
                int i12 = i11;
                int i13 = 0;
                while (true) {
                    if (i12 >= i6) {
                        strMatcher = variablePrefixMatcher;
                        strMatcher2 = variableSuffixMatcher;
                        c = escapeChar2;
                        i3 = i8;
                        break;
                    }
                    if (isEnableSubstitutionInVariables) {
                        int isMatch2 = variablePrefixMatcher.isMatch(cArr, i12, i4, i6);
                        if (isMatch2 != 0) {
                            i13++;
                            i12 += isMatch2;
                        }
                    }
                    int isMatch3 = variableSuffixMatcher.isMatch(cArr, i12, i4, i6);
                    if (isMatch3 == 0) {
                        i12++;
                    } else if (i13 == 0) {
                        strMatcher2 = variableSuffixMatcher;
                        c = escapeChar2;
                        String str3 = new String(cArr, i11, (i12 - i7) - isMatch);
                        if (isEnableSubstitutionInVariables) {
                            StrBuilder strBuilder3 = new StrBuilder(str3);
                            substitute(strBuilder3, 0, strBuilder3.length());
                            str3 = strBuilder3.toString();
                        }
                        int i14 = i12 + isMatch3;
                        if (valueDelimiterMatcher2 != null) {
                            char[] charArray = str3.toCharArray();
                            i3 = i8;
                            int i15 = 0;
                            while (i15 < charArray.length && (isEnableSubstitutionInVariables || variablePrefixMatcher.isMatch(charArray, i15, i15, charArray.length) == 0)) {
                                int isMatch4 = valueDelimiterMatcher2.isMatch(charArray, i15);
                                if (isMatch4 != 0) {
                                    strMatcher = variablePrefixMatcher;
                                    str2 = str3.substring(0, i15);
                                    str = str3.substring(i15 + isMatch4);
                                    break;
                                }
                                i15++;
                                variablePrefixMatcher = variablePrefixMatcher;
                            }
                            strMatcher = variablePrefixMatcher;
                        } else {
                            strMatcher = variablePrefixMatcher;
                            i3 = i8;
                        }
                        str2 = str3;
                        str = null;
                        if (list3 == null) {
                            list2 = new ArrayList<>();
                            list2.add(new String(cArr, i4, i5));
                        } else {
                            list2 = list3;
                        }
                        checkCyclicSubstitution(str2, list2);
                        list2.add(str2);
                        String resolveVariable = resolveVariable(str2, strBuilder2, i7, i14);
                        if (resolveVariable == null) {
                            resolveVariable = str;
                        }
                        if (resolveVariable != null) {
                            int length = resolveVariable.length();
                            strBuilder2.replace(i7, i14, resolveVariable);
                            int substitute = (substitute(strBuilder2, i7, length, list2) + length) - (i14 - i7);
                            i14 += substitute;
                            i6 += substitute;
                            i9 += substitute;
                            cArr = strBuilder2.buffer;
                            i3 = 1;
                        }
                        list2.remove(list2.size() - 1);
                        list3 = list2;
                    } else {
                        StrMatcher strMatcher3 = variableSuffixMatcher;
                        char c2 = escapeChar2;
                        i13--;
                        i12 += isMatch3;
                        i8 = i8;
                        variablePrefixMatcher = variablePrefixMatcher;
                    }
                }
                i7 = i12;
            }
            variableSuffixMatcher = strMatcher2;
            escapeChar2 = c;
            i8 = i3;
            variablePrefixMatcher = strMatcher;
        }
        return z ? i8 : i9;
    }

    private void checkCyclicSubstitution(String str, List<String> list) {
        if (list.contains(str)) {
            StrBuilder strBuilder = new StrBuilder(256);
            strBuilder.append("Infinite loop in property interpolation of ");
            strBuilder.append((String) list.remove(0));
            strBuilder.append(": ");
            strBuilder.appendWithSeparators((Iterable<?>) list, "->");
            throw new IllegalStateException(strBuilder.toString());
        }
    }

    /* access modifiers changed from: protected */
    public String resolveVariable(String str, StrBuilder strBuilder, int i, int i2) {
        StrLookup variableResolver2 = getVariableResolver();
        if (variableResolver2 == null) {
            return null;
        }
        return variableResolver2.lookup(str);
    }

    public char getEscapeChar() {
        return this.escapeChar;
    }

    public void setEscapeChar(char c) {
        this.escapeChar = c;
    }

    public StrMatcher getVariablePrefixMatcher() {
        return this.prefixMatcher;
    }

    public StrSubstitutor setVariablePrefixMatcher(StrMatcher strMatcher) {
        if (strMatcher == null) {
            throw new IllegalArgumentException("Variable prefix matcher must not be null!");
        }
        this.prefixMatcher = strMatcher;
        return this;
    }

    public StrSubstitutor setVariablePrefix(char c) {
        return setVariablePrefixMatcher(StrMatcher.charMatcher(c));
    }

    public StrSubstitutor setVariablePrefix(String str) {
        if (str != null) {
            return setVariablePrefixMatcher(StrMatcher.stringMatcher(str));
        }
        throw new IllegalArgumentException("Variable prefix must not be null!");
    }

    public StrMatcher getVariableSuffixMatcher() {
        return this.suffixMatcher;
    }

    public StrSubstitutor setVariableSuffixMatcher(StrMatcher strMatcher) {
        if (strMatcher == null) {
            throw new IllegalArgumentException("Variable suffix matcher must not be null!");
        }
        this.suffixMatcher = strMatcher;
        return this;
    }

    public StrSubstitutor setVariableSuffix(char c) {
        return setVariableSuffixMatcher(StrMatcher.charMatcher(c));
    }

    public StrSubstitutor setVariableSuffix(String str) {
        if (str != null) {
            return setVariableSuffixMatcher(StrMatcher.stringMatcher(str));
        }
        throw new IllegalArgumentException("Variable suffix must not be null!");
    }

    public StrMatcher getValueDelimiterMatcher() {
        return this.valueDelimiterMatcher;
    }

    public StrSubstitutor setValueDelimiterMatcher(StrMatcher strMatcher) {
        this.valueDelimiterMatcher = strMatcher;
        return this;
    }

    public StrSubstitutor setValueDelimiter(char c) {
        return setValueDelimiterMatcher(StrMatcher.charMatcher(c));
    }

    public StrSubstitutor setValueDelimiter(String str) {
        if (!StringUtils.isEmpty(str)) {
            return setValueDelimiterMatcher(StrMatcher.stringMatcher(str));
        }
        setValueDelimiterMatcher(null);
        return this;
    }

    public StrLookup<?> getVariableResolver() {
        return this.variableResolver;
    }

    public void setVariableResolver(StrLookup<?> strLookup) {
        this.variableResolver = strLookup;
    }

    public boolean isEnableSubstitutionInVariables() {
        return this.enableSubstitutionInVariables;
    }

    public void setEnableSubstitutionInVariables(boolean z) {
        this.enableSubstitutionInVariables = z;
    }

    public boolean isPreserveEscapes() {
        return this.preserveEscapes;
    }

    public void setPreserveEscapes(boolean z) {
        this.preserveEscapes = z;
    }
}
