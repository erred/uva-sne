package com.opengarden.firechat.matrixsdk.view;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.p000v4.content.ContextCompat;
import android.text.Editable;
import android.text.Html.TagHandler;
import android.text.Layout.Alignment;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan.Standard;
import android.text.style.StrikethroughSpan;
import android.text.style.TypefaceSpan;
import java.util.Stack;
import org.apache.commons.lang3.StringUtils;
import org.xml.sax.XMLReader;

public class HtmlTagHandler implements TagHandler {
    private static final BulletSpan bullet = new BulletSpan(10);
    private static final int indent = 10;
    private static final int listItemIndent = 20;
    private final Stack<String> lists = new Stack<>();
    public int mCodeBlockBackgroundColor = -1;
    public Context mContext;
    private final Stack<Integer> olNextIndex = new Stack<>();
    StringBuilder tableHtmlBuilder = new StringBuilder();
    int tableTagLevel = 0;

    private static class Center {
        private Center() {
        }
    }

    private static class Code {
        private Code() {
        }
    }

    /* renamed from: com.opengarden.firechat.matrixsdk.view.HtmlTagHandler$Ol */
    private static class C2877Ol {
        private C2877Ol() {
        }
    }

    private static class Strike {
        private Strike() {
        }
    }

    private static class Table {
        private Table() {
        }
    }

    /* renamed from: com.opengarden.firechat.matrixsdk.view.HtmlTagHandler$Td */
    private static class C2878Td {
        private C2878Td() {
        }
    }

    /* renamed from: com.opengarden.firechat.matrixsdk.view.HtmlTagHandler$Th */
    private static class C2879Th {
        private C2879Th() {
        }
    }

    /* renamed from: com.opengarden.firechat.matrixsdk.view.HtmlTagHandler$Tr */
    private static class C2880Tr {
        private C2880Tr() {
        }
    }

    /* renamed from: com.opengarden.firechat.matrixsdk.view.HtmlTagHandler$Ul */
    private static class C2881Ul {
        private C2881Ul() {
        }
    }

    public void setCodeBlockBackgroundColor(@ColorInt int i) {
        this.mCodeBlockBackgroundColor = i;
    }

    public void handleTag(boolean z, String str, Editable editable, XMLReader xMLReader) {
        int i = 10;
        if (z) {
            if (str.equalsIgnoreCase("ul")) {
                this.lists.push(str);
            } else if (str.equalsIgnoreCase("ol")) {
                this.lists.push(str);
                this.olNextIndex.push(Integer.valueOf(1));
            } else if (str.equalsIgnoreCase("li")) {
                if (editable.length() > 0 && editable.charAt(editable.length() - 1) != 10) {
                    editable.append(StringUtils.f158LF);
                }
                String str2 = (String) this.lists.peek();
                if (str2.equalsIgnoreCase("ol")) {
                    start(editable, new C2877Ol());
                    editable.append(((Integer) this.olNextIndex.peek()).toString()).append(". ");
                    this.olNextIndex.push(Integer.valueOf(((Integer) this.olNextIndex.pop()).intValue() + 1));
                } else if (str2.equalsIgnoreCase("ul")) {
                    start(editable, new C2881Ul());
                }
            } else if (str.equalsIgnoreCase("code")) {
                start(editable, new Code());
            } else if (str.equalsIgnoreCase("center")) {
                start(editable, new Center());
            } else if (str.equalsIgnoreCase("s") || str.equalsIgnoreCase("strike")) {
                start(editable, new Strike());
            } else if (str.equalsIgnoreCase("table")) {
                start(editable, new Table());
                if (this.tableTagLevel == 0) {
                    this.tableHtmlBuilder = new StringBuilder();
                    editable.append("table placeholder");
                }
                this.tableTagLevel++;
            } else if (str.equalsIgnoreCase("tr")) {
                start(editable, new C2880Tr());
            } else if (str.equalsIgnoreCase("th")) {
                start(editable, new C2879Th());
            } else if (str.equalsIgnoreCase("td")) {
                start(editable, new C2878Td());
            }
        } else if (str.equalsIgnoreCase("ul")) {
            this.lists.pop();
        } else if (str.equalsIgnoreCase("ol")) {
            this.lists.pop();
            this.olNextIndex.pop();
        } else if (str.equalsIgnoreCase("li")) {
            if (((String) this.lists.peek()).equalsIgnoreCase("ul")) {
                if (editable.length() > 0 && editable.charAt(editable.length() - 1) != 10) {
                    editable.append(StringUtils.f158LF);
                }
                if (this.lists.size() > 1) {
                    i = 10 - bullet.getLeadingMargin(true);
                    if (this.lists.size() > 2) {
                        i -= (this.lists.size() - 2) * 20;
                    }
                }
                end(editable, C2881Ul.class, false, new Standard((this.lists.size() - 1) * 20), new BulletSpan(i));
            } else if (((String) this.lists.peek()).equalsIgnoreCase("ol")) {
                if (editable.length() > 0 && editable.charAt(editable.length() - 1) != 10) {
                    editable.append(StringUtils.f158LF);
                }
                int size = (this.lists.size() - 1) * 20;
                if (this.lists.size() > 2) {
                    size -= (this.lists.size() - 2) * 20;
                }
                end(editable, C2877Ol.class, false, new Standard(size));
            }
        } else if (str.equalsIgnoreCase("code")) {
            if (-1 == this.mCodeBlockBackgroundColor) {
                this.mCodeBlockBackgroundColor = ContextCompat.getColor(this.mContext, 17170432);
            }
            end(editable, Code.class, false, new BackgroundColorSpan(this.mCodeBlockBackgroundColor), new TypefaceSpan("monospace"));
        } else if (str.equalsIgnoreCase("center")) {
            end(editable, Center.class, true, new AlignmentSpan.Standard(Alignment.ALIGN_CENTER));
        } else if (str.equalsIgnoreCase("s") || str.equalsIgnoreCase("strike")) {
            end(editable, Strike.class, false, new StrikethroughSpan());
        } else if (str.equalsIgnoreCase("table")) {
            this.tableTagLevel--;
            end(editable, Table.class, false, new Object[0]);
        } else if (str.equalsIgnoreCase("tr")) {
            end(editable, C2880Tr.class, false, new Object[0]);
        } else if (str.equalsIgnoreCase("th")) {
            end(editable, C2879Th.class, false, new Object[0]);
        } else if (str.equalsIgnoreCase("td")) {
            end(editable, C2878Td.class, false, new Object[0]);
        }
        storeTableTags(z, str);
    }

    private void storeTableTags(boolean z, String str) {
        if (this.tableTagLevel > 0 || str.equalsIgnoreCase("table")) {
            this.tableHtmlBuilder.append("<");
            if (!z) {
                this.tableHtmlBuilder.append("/");
            }
            StringBuilder sb = this.tableHtmlBuilder;
            sb.append(str.toLowerCase());
            sb.append(">");
        }
    }

    private void start(Editable editable, Object obj) {
        int length = editable.length();
        editable.setSpan(obj, length, length, 17);
    }

    private void end(Editable editable, Class cls, boolean z, Object... objArr) {
        Object last = getLast(editable, cls);
        int spanStart = editable.getSpanStart(last);
        int length = editable.length();
        if (this.tableTagLevel > 0) {
            this.tableHtmlBuilder.append(extractSpanText(editable, cls));
        }
        editable.removeSpan(last);
        if (spanStart != length) {
            if (z) {
                editable.append(StringUtils.f158LF);
                length++;
            }
            for (Object span : objArr) {
                editable.setSpan(span, spanStart, length, 33);
            }
        }
    }

    private CharSequence extractSpanText(Editable editable, Class cls) {
        int spanStart = editable.getSpanStart(getLast(editable, cls));
        int length = editable.length();
        CharSequence subSequence = editable.subSequence(spanStart, length);
        editable.delete(spanStart, length);
        return subSequence;
    }

    private static Object getLast(Editable editable, Class cls) {
        Object[] spans = editable.getSpans(0, editable.length(), cls);
        if (spans.length == 0) {
            return null;
        }
        for (int length = spans.length; length > 0; length--) {
            int i = length - 1;
            if (editable.getSpanFlags(spans[i]) == 17) {
                return spans[i];
            }
        }
        return null;
    }
}
