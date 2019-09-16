package com.opengarden.firechat.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.p003v7.widget.AppCompatMultiAutoCompleteTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Filter.FilterListener;
import android.widget.FrameLayout;
import android.widget.ListPopupWindow;
import android.widget.MultiAutoCompleteTextView.Tokenizer;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.adapters.AutoCompletedCommandLineAdapter;
import com.opengarden.firechat.adapters.AutoCompletedUserAdapter;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.rest.model.RoomMember;
import com.opengarden.firechat.matrixsdk.rest.model.User;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.util.SlashCommandsParser.SlashCommand;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

public class VectorAutoCompleteTextView extends AppCompatMultiAutoCompleteTextView {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "VectorAutoCompleteTextView";
    public AutoCompletedCommandLineAdapter mAdapterCommand;
    public AutoCompletedUserAdapter mAdapterUser;
    private boolean mAddColonOnFirstItem;
    private ListPopupWindow mListPopupWindow;
    /* access modifiers changed from: private */
    public String mPendingFilter;
    private Field mPopupCanBeUpdatedField;

    private static class VectorAutoCompleteTokenizer implements Tokenizer {
        static final List<Character> mAllowedTokens = Arrays.asList(new Character[]{Character.valueOf(','), Character.valueOf(';'), Character.valueOf(ClassUtils.PACKAGE_SEPARATOR_CHAR), Character.valueOf(' '), Character.valueOf(10), Character.valueOf(9)});

        private VectorAutoCompleteTokenizer() {
        }

        public int findTokenStart(CharSequence charSequence, int i) {
            int i2 = i;
            while (i2 > 0 && !mAllowedTokens.contains(Character.valueOf(charSequence.charAt(i2 - 1)))) {
                i2--;
            }
            while (i2 < i && charSequence.charAt(i2) == ' ') {
                i2++;
            }
            return i2;
        }

        public int findTokenEnd(CharSequence charSequence, int i) {
            int length = charSequence.length();
            while (i < length) {
                if (mAllowedTokens.contains(Character.valueOf(charSequence.charAt(i)))) {
                    return i;
                }
                i++;
            }
            return length;
        }

        public CharSequence terminateToken(CharSequence charSequence) {
            int length = charSequence.length();
            while (length > 0 && charSequence.charAt(length - 1) == ' ') {
                length--;
            }
            StringBuilder sb = new StringBuilder();
            sb.append(charSequence);
            sb.append(StringUtils.SPACE);
            return sb.toString();
        }
    }

    public VectorAutoCompleteTextView(Context context) {
        super(context, null);
    }

    public VectorAutoCompleteTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        setInputType(getInputType() & (getInputType() ^ 65536));
    }

    public VectorAutoCompleteTextView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        setInputType(getInputType() & (getInputType() ^ 65536));
    }

    public void initAutoCompletion(MXSession mXSession) {
        initAutoCompletion(mXSession, mXSession.getDataHandler().getStore().getUsers());
    }

    public void initAutoCompletion(MXSession mXSession, String str) {
        ArrayList arrayList = new ArrayList();
        if (!TextUtils.isEmpty(str)) {
            Room room = mXSession.getDataHandler().getStore().getRoom(str);
            if (room != null) {
                for (RoomMember userId : room.getMembers()) {
                    User user = mXSession.getDataHandler().getUser(userId.getUserId());
                    if (user != null) {
                        arrayList.add(user);
                    }
                }
            }
        }
        initAutoCompletion(mXSession, (Collection<User>) arrayList);
    }

    public void initAutoCompletionCommandLine(MXSession mXSession, String str) {
        initAutoCompletionCommandLine(mXSession, (Collection<SlashCommand>) new ArrayList<SlashCommand>(Arrays.asList(SlashCommand.values())));
    }

    private void initAutoCompletion(MXSession mXSession, Collection<User> collection) {
        this.mAdapterUser = new AutoCompletedUserAdapter(getContext(), C1299R.layout.item_user_auto_complete, mXSession, collection);
        setTokenizer(new VectorAutoCompleteTokenizer());
        if (this.mPopupCanBeUpdatedField == null) {
            try {
                this.mPopupCanBeUpdatedField = AutoCompleteTextView.class.getDeclaredField("mPopupCanBeUpdated");
                this.mPopupCanBeUpdatedField.setAccessible(true);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initAutoCompletion() : failed to retrieve mPopupCanBeUpdated ");
                sb.append(e.getMessage());
                Log.m212e(str, sb.toString(), e);
            }
        }
        if (this.mListPopupWindow == null) {
            try {
                Field declaredField = AutoCompleteTextView.class.getDeclaredField("mPopup");
                declaredField.setAccessible(true);
                this.mListPopupWindow = (ListPopupWindow) declaredField.get(this);
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## initAutoCompletion() : failed to retrieve mListPopupWindow ");
                sb2.append(e2.getMessage());
                Log.m212e(str2, sb2.toString(), e2);
            }
        }
    }

    private void initAutoCompletionCommandLine(MXSession mXSession, Collection<SlashCommand> collection) {
        this.mAdapterCommand = new AutoCompletedCommandLineAdapter(getContext(), C1299R.layout.item_command_auto_complete, mXSession, collection);
        setTokenizer(new VectorAutoCompleteTokenizer());
        if (this.mPopupCanBeUpdatedField == null) {
            try {
                this.mPopupCanBeUpdatedField = AutoCompleteTextView.class.getDeclaredField("mPopupCanBeUpdated");
                this.mPopupCanBeUpdatedField.setAccessible(true);
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb = new StringBuilder();
                sb.append("## initAutoCompletion() : failed to retrieve mPopupCanBeUpdated ");
                sb.append(e.getMessage());
                Log.m212e(str, sb.toString(), e);
            }
        }
        if (this.mListPopupWindow == null) {
            try {
                Field declaredField = AutoCompleteTextView.class.getDeclaredField("mPopup");
                declaredField.setAccessible(true);
                this.mListPopupWindow = (ListPopupWindow) declaredField.get(this);
            } catch (Exception e2) {
                String str2 = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## initAutoCompletion() : failed to retrieve mListPopupWindow ");
                sb2.append(e2.getMessage());
                Log.m212e(str2, sb2.toString(), e2);
            }
        }
    }

    public void setProvideMatrixIdOnly(boolean z) {
        this.mAdapterUser.setProvideMatrixIdOnly(z);
    }

    /* access modifiers changed from: private */
    public void adjustPopupSize() {
        if (this.mListPopupWindow != null) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            int count = this.mAdapterUser.getCount();
            View view = null;
            int i = 0;
            for (int i2 = 0; i2 < count; i2++) {
                view = this.mAdapterUser.getView(i2, view, frameLayout, false);
                view.measure(0, 0);
                i = Math.max(i, view.getMeasuredWidth());
            }
            this.mListPopupWindow.setContentWidth(i);
        }
    }

    /* access modifiers changed from: private */
    public void adjustPopupSizeCommand() {
        if (this.mListPopupWindow != null) {
            FrameLayout frameLayout = new FrameLayout(getContext());
            int viewTypeCount = this.mAdapterCommand.getViewTypeCount();
            View view = null;
            int i = 0;
            for (int i2 = 0; i2 < viewTypeCount; i2++) {
                view = this.mAdapterCommand.getView(i2, view, frameLayout);
                view.measure(0, 0);
                i = Math.max(i, view.getMeasuredWidth());
            }
            this.mListPopupWindow.setContentWidth(i);
        }
    }

    public void setAddColonOnFirstItem(boolean z) {
        this.mAddColonOnFirstItem = z;
    }

    /* access modifiers changed from: protected */
    public void replaceText(CharSequence charSequence) {
        String obj = getText().toString();
        super.replaceText(charSequence);
        if (this.mAddColonOnFirstItem) {
            try {
                Editable text = getText();
                if (obj != null && !obj.startsWith(charSequence.toString()) && text.toString().startsWith(charSequence.toString()) && charSequence.toString().startsWith("@")) {
                    int length = charSequence.length();
                    StringBuilder sb = new StringBuilder();
                    sb.append(charSequence);
                    sb.append(":");
                    text.replace(0, length, sb.toString());
                }
            } catch (Exception e) {
                String str = LOG_TAG;
                StringBuilder sb2 = new StringBuilder();
                sb2.append("## replaceText() : failed ");
                sb2.append(e.getMessage());
                Log.m212e(str, sb2.toString(), e);
            }
        }
        setInputType(getInputType() & getInputType() & -65537);
        setInputType(getInputType() & (getInputType() ^ 65536));
    }

    /* access modifiers changed from: protected */
    public void performFiltering(final CharSequence charSequence, final int i, final int i2, int i3) {
        if (this.mPopupCanBeUpdatedField == null) {
            super.performFiltering(charSequence, i, i2, i3);
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(charSequence == null ? "" : charSequence.toString());
        sb.append(i);
        sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
        sb.append(i2);
        String sb2 = sb.toString();
        if (!TextUtils.equals(sb2, this.mPendingFilter)) {
            dismissDropDown();
        }
        try {
            this.mPopupCanBeUpdatedField.setBoolean(this, true);
        } catch (Exception e) {
            String str = LOG_TAG;
            StringBuilder sb3 = new StringBuilder();
            sb3.append("## performFiltering() : mPopupCanBeUpdatedField.setBoolean failed ");
            sb3.append(e.getMessage());
            Log.m212e(str, sb3.toString(), e);
        }
        this.mPendingFilter = sb2;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            public void run() {
                StringBuilder sb = new StringBuilder();
                sb.append(VectorAutoCompleteTextView.this.getText() == null ? "" : VectorAutoCompleteTextView.this.getText().toString());
                sb.append(i);
                sb.append(HelpFormatter.DEFAULT_OPT_PREFIX);
                sb.append(i2);
                if (TextUtils.equals(sb.toString(), VectorAutoCompleteTextView.this.mPendingFilter)) {
                    CharSequence charSequence = "";
                    try {
                        charSequence = charSequence.subSequence(i, i2);
                    } catch (Exception e) {
                        String access$200 = VectorAutoCompleteTextView.LOG_TAG;
                        StringBuilder sb2 = new StringBuilder();
                        sb2.append("## performFiltering() failed ");
                        sb2.append(e.getMessage());
                        Log.m212e(access$200, sb2.toString(), e);
                    }
                    if (charSequence.toString().startsWith("@")) {
                        VectorAutoCompleteTextView.this.mAdapterUser.getFilter().filter(charSequence, new FilterListener() {
                            public void onFilterComplete(int i) {
                                VectorAutoCompleteTextView.this.adjustPopupSize();
                                VectorAutoCompleteTextView.this.onFilterComplete(i);
                            }
                        });
                    } else if (charSequence.toString().startsWith("/")) {
                        VectorAutoCompleteTextView.this.mAdapterCommand.getFilter().filter(charSequence, new FilterListener() {
                            public void onFilterComplete(int i) {
                                VectorAutoCompleteTextView.this.adjustPopupSizeCommand();
                                VectorAutoCompleteTextView.this.onFilterComplete(i);
                            }
                        });
                    }
                }
            }
        }, 700);
    }
}
