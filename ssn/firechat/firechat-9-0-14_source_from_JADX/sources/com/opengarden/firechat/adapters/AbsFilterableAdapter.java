package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.p003v7.widget.RecyclerView.Adapter;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.widget.Filter;
import android.widget.Filterable;
import com.opengarden.firechat.Matrix;
import com.opengarden.firechat.adapters.AbsAdapter.GroupInvitationListener;
import com.opengarden.firechat.adapters.AbsAdapter.MoreGroupActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.MoreRoomActionListener;
import com.opengarden.firechat.adapters.AbsAdapter.RoomInvitationListener;
import com.opengarden.firechat.matrixsdk.MXSession;

public abstract class AbsFilterableAdapter<T extends ViewHolder> extends Adapter<T> implements Filterable {
    final Context mContext;
    CharSequence mCurrentFilterPattern;
    private final Filter mFilter = createFilter();
    GroupInvitationListener mGroupInvitationListener;
    MoreGroupActionListener mMoreGroupActionListener;
    MoreRoomActionListener mMoreRoomActionListener;
    RoomInvitationListener mRoomInvitationListener;
    final MXSession mSession;

    /* access modifiers changed from: protected */
    public abstract Filter createFilter();

    AbsFilterableAdapter(Context context) {
        this.mContext = context;
        this.mSession = Matrix.getInstance(context).getDefaultSession();
    }

    AbsFilterableAdapter(Context context, RoomInvitationListener roomInvitationListener, MoreRoomActionListener moreRoomActionListener) {
        this.mContext = context;
        this.mRoomInvitationListener = roomInvitationListener;
        this.mMoreRoomActionListener = moreRoomActionListener;
        this.mSession = Matrix.getInstance(context).getDefaultSession();
    }

    AbsFilterableAdapter(Context context, GroupInvitationListener groupInvitationListener, MoreGroupActionListener moreGroupActionListener) {
        this.mContext = context;
        this.mGroupInvitationListener = groupInvitationListener;
        this.mMoreGroupActionListener = moreGroupActionListener;
        this.mSession = Matrix.getInstance(context).getDefaultSession();
    }

    public Filter getFilter() {
        return this.mFilter;
    }

    public void onFilterDone(CharSequence charSequence) {
        this.mCurrentFilterPattern = charSequence;
    }
}
