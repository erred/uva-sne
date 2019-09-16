package org.apache.commons.lang3.text.translate;

import java.io.IOException;
import java.io.Writer;

public class UnicodeUnescaper extends CharSequenceTranslator {
    public int translate(CharSequence charSequence, int i, Writer writer) throws IOException {
        int i2;
        if (charSequence.charAt(i) == '\\') {
            int i3 = i + 1;
            if (i3 < charSequence.length() && charSequence.charAt(i3) == 'u') {
                int i4 = 2;
                while (true) {
                    i2 = i + i4;
                    if (i2 < charSequence.length() && charSequence.charAt(i2) == 'u') {
                        i4++;
                    }
                }
                if (i2 < charSequence.length() && charSequence.charAt(i2) == '+') {
                    i4++;
                }
                int i5 = i + i4;
                int i6 = i5 + 4;
                if (i6 <= charSequence.length()) {
                    CharSequence subSequence = charSequence.subSequence(i5, i6);
                    try {
                        writer.write((char) Integer.parseInt(subSequence.toString(), 16));
                        return i4 + 4;
                    } catch (NumberFormatException e) {
                        StringBuilder sb = new StringBuilder();
                        sb.append("Unable to parse unicode value: ");
                        sb.append(subSequence);
                        throw new IllegalArgumentException(sb.toString(), e);
                    }
                } else {
                    StringBuilder sb2 = new StringBuilder();
                    sb2.append("Less than 4 hex digits in unicode value: '");
                    sb2.append(charSequence.subSequence(i, charSequence.length()));
                    sb2.append("' due to end of CharSequence");
                    throw new IllegalArgumentException(sb2.toString());
                }
            }
        }
        return 0;
    }
}
