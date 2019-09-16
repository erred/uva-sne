package com.opengarden.firechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.matrixsdk.data.Room;
import com.opengarden.firechat.matrixsdk.util.Log;
import com.opengarden.firechat.widgets.Widget;
import com.opengarden.firechat.widgets.WidgetsManager;
import com.opengarden.firechat.widgets.WidgetsManager.onWidgetUpdateListener;
import java.util.ArrayList;
import java.util.List;

public class ActiveWidgetsBanner extends RelativeLayout {
    /* access modifiers changed from: private */
    public static final String LOG_TAG = "ActiveWidgetsBanner";
    /* access modifiers changed from: private */
    public List<Widget> mActiveWidgets = new ArrayList();
    private View mCloseWidgetIcon;
    private Context mContext;
    private Room mRoom;
    private MXSession mSession;
    /* access modifiers changed from: private */
    public onUpdateListener mUpdateListener;
    private final onWidgetUpdateListener mWidgetListener = new onWidgetUpdateListener() {
        public void onWidgetUpdate(Widget widget) {
            ActiveWidgetsBanner.this.refresh();
        }
    };
    private TextView mWidgetTypeTextView;

    public interface onUpdateListener {
        void onActiveWidgetsListUpdate();

        void onClick(List<Widget> list);

        void onCloseWidgetClick(Widget widget);
    }

    public ActiveWidgetsBanner(Context context) {
        super(context);
        initView(context);
    }

    public ActiveWidgetsBanner(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        initView(context);
    }

    public ActiveWidgetsBanner(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        initView(context);
    }

    private void initView(Context context) {
        this.mContext = context;
        View.inflate(getContext(), C1299R.layout.active_widget_banner, this);
        this.mWidgetTypeTextView = (TextView) findViewById(C1299R.C1301id.widget_type_text_view);
        this.mCloseWidgetIcon = findViewById(C1299R.C1301id.close_widget_icon_container);
        this.mCloseWidgetIcon.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ActiveWidgetsBanner.this.mUpdateListener != null) {
                    try {
                        ActiveWidgetsBanner.this.mUpdateListener.onCloseWidgetClick((Widget) ActiveWidgetsBanner.this.mActiveWidgets.get(0));
                    } catch (Exception e) {
                        String access$300 = ActiveWidgetsBanner.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## initView() : onCloseWidgetClick failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$300, sb.toString());
                    }
                }
            }
        });
        setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                if (ActiveWidgetsBanner.this.mUpdateListener != null) {
                    try {
                        ActiveWidgetsBanner.this.mUpdateListener.onClick(ActiveWidgetsBanner.this.mActiveWidgets);
                    } catch (Exception e) {
                        String access$300 = ActiveWidgetsBanner.LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## initView() : onClick failed ");
                        sb.append(e.getMessage());
                        Log.m211e(access$300, sb.toString());
                    }
                }
            }
        });
    }

    public void initRoomInfo(MXSession mXSession, Room room) {
        this.mSession = mXSession;
        this.mRoom = room;
    }

    public void setOnUpdateListener(onUpdateListener onupdatelistener) {
        this.mUpdateListener = onupdatelistener;
    }

    /* access modifiers changed from: private */
    public void refresh() {
        if (this.mRoom != null && this.mSession != null) {
            List<Widget> activeWebviewWidgets = WidgetsManager.getSharedInstance().getActiveWebviewWidgets(this.mSession, this.mRoom);
            Widget widget = null;
            if (activeWebviewWidgets.size() != this.mActiveWidgets.size() || !this.mActiveWidgets.containsAll(activeWebviewWidgets)) {
                this.mActiveWidgets = activeWebviewWidgets;
                if (1 == this.mActiveWidgets.size()) {
                    Widget widget2 = (Widget) this.mActiveWidgets.get(0);
                    this.mWidgetTypeTextView.setText(widget2.getHumanName());
                    widget = widget2;
                } else if (this.mActiveWidgets.size() > 1) {
                    this.mWidgetTypeTextView.setText(this.mContext.getResources().getQuantityString(C1299R.plurals.active_widgets, this.mActiveWidgets.size(), new Object[]{Integer.valueOf(this.mActiveWidgets.size())}));
                }
                if (this.mUpdateListener != null) {
                    try {
                        this.mUpdateListener.onActiveWidgetsListUpdate();
                    } catch (Exception e) {
                        String str = LOG_TAG;
                        StringBuilder sb = new StringBuilder();
                        sb.append("## refresh() : onActiveWidgetUpdate failed ");
                        sb.append(e.getMessage());
                        Log.m211e(str, sb.toString());
                    }
                }
            }
            int i = 8;
            setVisibility(this.mActiveWidgets.size() > 0 ? 0 : 8);
            View view = this.mCloseWidgetIcon;
            if (widget != null && WidgetsManager.getSharedInstance().checkWidgetPermission(this.mSession, this.mRoom) == null) {
                i = 0;
            }
            view.setVisibility(i);
        }
    }

    public void onActivityResume() {
        refresh();
        WidgetsManager.getSharedInstance();
        WidgetsManager.addListener(this.mWidgetListener);
    }

    public void onActivityPause() {
        WidgetsManager.getSharedInstance();
        WidgetsManager.removeListener(this.mWidgetListener);
    }
}
