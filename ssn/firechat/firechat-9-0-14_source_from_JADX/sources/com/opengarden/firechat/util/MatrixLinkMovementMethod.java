package com.opengarden.firechat.util;

import android.net.Uri;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.Touch;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.MotionEvent;
import android.widget.TextView;
import com.opengarden.firechat.listeners.IMessagesAdapterActionsListener;

public class MatrixLinkMovementMethod extends LinkMovementMethod {
    private IMessagesAdapterActionsListener mListener;

    public MatrixLinkMovementMethod(IMessagesAdapterActionsListener iMessagesAdapterActionsListener) {
        this.mListener = iMessagesAdapterActionsListener;
    }

    public void updateListener(IMessagesAdapterActionsListener iMessagesAdapterActionsListener) {
        this.mListener = iMessagesAdapterActionsListener;
    }

    public boolean onTouchEvent(TextView textView, Spannable spannable, MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action != 1 && action != 0) {
            return Touch.onTouchEvent(textView, spannable, motionEvent);
        }
        int x = (((int) motionEvent.getX()) - textView.getTotalPaddingLeft()) + textView.getScrollX();
        int y = (((int) motionEvent.getY()) - textView.getTotalPaddingTop()) + textView.getScrollY();
        Layout layout = textView.getLayout();
        int offsetForHorizontal = layout.getOffsetForHorizontal(layout.getLineForVertical(y), (float) x);
        ClickableSpan[] clickableSpanArr = (ClickableSpan[]) spannable.getSpans(offsetForHorizontal, offsetForHorizontal, ClickableSpan.class);
        if (clickableSpanArr.length != 0) {
            if (action == 1) {
                if (!(clickableSpanArr[0] instanceof URLSpan)) {
                    clickableSpanArr[0].onClick(textView);
                } else if (this.mListener != null) {
                    this.mListener.onURLClick(Uri.parse(((URLSpan) clickableSpanArr[0]).getURL()));
                }
            } else if (action == 0) {
                Selection.setSelection(spannable, spannable.getSpanStart(clickableSpanArr[0]), spannable.getSpanEnd(clickableSpanArr[0]));
            }
            return true;
        }
        Selection.removeSelection(spannable);
        Touch.onTouchEvent(textView, spannable, motionEvent);
        return false;
    }
}
