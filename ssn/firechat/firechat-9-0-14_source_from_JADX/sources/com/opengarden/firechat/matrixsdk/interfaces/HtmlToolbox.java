package com.opengarden.firechat.matrixsdk.interfaces;

import android.support.annotation.Nullable;
import android.text.Html.ImageGetter;
import android.text.Html.TagHandler;

public interface HtmlToolbox {
    String convert(String str);

    @Nullable
    ImageGetter getImageGetter();

    @Nullable
    TagHandler getTagHandler(String str);
}
