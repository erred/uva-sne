package com.opengarden.firechat.adapters;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.support.annotation.UiThread;
import android.support.p003v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filter.FilterResults;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import butterknife.internal.C0487Utils;
import com.opengarden.firechat.C1299R;
import com.opengarden.firechat.VectorApp;
import com.opengarden.firechat.matrixsdk.MXSession;
import com.opengarden.firechat.util.SlashCommandsParser.SlashCommand;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public class AutoCompletedCommandLineAdapter extends ArrayAdapter<String> {
    private static final Comparator<String> mCommandLinesComparator = new Comparator<String>() {
        public int compare(String str, String str2) {
            return str.compareToIgnoreCase(str2);
        }
    };
    /* access modifiers changed from: private */
    public List<SlashCommand> mCommandLines = new ArrayList();
    private final Context mContext;
    private Filter mFilter;
    private final LayoutInflater mLayoutInflater;
    private final int mLayoutResourceId;
    private final MXSession mSession;

    private class AutoCompletedCommandFilter extends Filter {
        private AutoCompletedCommandFilter() {
        }

        /* access modifiers changed from: protected */
        public FilterResults performFiltering(CharSequence charSequence) {
            ArrayList arrayList;
            FilterResults filterResults = new FilterResults();
            if (charSequence == null || charSequence.length() == 0) {
                arrayList = new ArrayList();
            } else {
                arrayList = new ArrayList();
                String lowerCase = charSequence.toString().toLowerCase(VectorApp.getApplicationLocale());
                if (lowerCase.startsWith("/")) {
                    for (SlashCommand slashCommand : AutoCompletedCommandLineAdapter.this.mCommandLines) {
                        if (slashCommand.getCommand() != null && slashCommand.getCommand().toLowerCase(VectorApp.getApplicationLocale()).startsWith(lowerCase)) {
                            arrayList.add(slashCommand.getCommand());
                        }
                    }
                }
            }
            filterResults.values = arrayList;
            filterResults.count = arrayList.size();
            return filterResults;
        }

        /* access modifiers changed from: protected */
        public void publishResults(CharSequence charSequence, FilterResults filterResults) {
            AutoCompletedCommandLineAdapter.this.clear();
            AutoCompletedCommandLineAdapter.this.addAll((List) filterResults.values);
            if (filterResults.count > 0) {
                AutoCompletedCommandLineAdapter.this.notifyDataSetChanged();
            } else {
                AutoCompletedCommandLineAdapter.this.notifyDataSetInvalidated();
            }
        }

        public CharSequence convertResultToString(Object obj) {
            return (String) obj;
        }
    }

    static class CommandViewHolder extends ViewHolder {
        @BindView(2131296637)
        TextView tvCommandDescription;
        @BindView(2131296638)
        TextView tvCommandName;
        @BindView(2131296639)
        TextView tvCommandParameter;

        public CommandViewHolder(View view) {
            super(view);
            ButterKnife.bind((Object) this, view);
        }
    }

    public class CommandViewHolder_ViewBinding implements Unbinder {
        private CommandViewHolder target;

        @UiThread
        public CommandViewHolder_ViewBinding(CommandViewHolder commandViewHolder, View view) {
            this.target = commandViewHolder;
            commandViewHolder.tvCommandName = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.item_command_auto_complete_name, "field 'tvCommandName'", TextView.class);
            commandViewHolder.tvCommandParameter = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.item_command_auto_complete_parameter, "field 'tvCommandParameter'", TextView.class);
            commandViewHolder.tvCommandDescription = (TextView) C0487Utils.findRequiredViewAsType(view, C1299R.C1301id.item_command_auto_complete_description, "field 'tvCommandDescription'", TextView.class);
        }

        @CallSuper
        public void unbind() {
            CommandViewHolder commandViewHolder = this.target;
            if (commandViewHolder == null) {
                throw new IllegalStateException("Bindings already cleared.");
            }
            this.target = null;
            commandViewHolder.tvCommandName = null;
            commandViewHolder.tvCommandParameter = null;
            commandViewHolder.tvCommandDescription = null;
        }
    }

    public AutoCompletedCommandLineAdapter(Context context, int i, MXSession mXSession, Collection<SlashCommand> collection) {
        super(context, i);
        this.mContext = context;
        this.mLayoutResourceId = i;
        this.mLayoutInflater = LayoutInflater.from(this.mContext);
        this.mSession = mXSession;
        this.mCommandLines = new ArrayList(collection);
    }

    public View getView(int i, View view, ViewGroup viewGroup) {
        CommandViewHolder commandViewHolder;
        if (view == null) {
            view = this.mLayoutInflater.inflate(this.mLayoutResourceId, viewGroup, false);
            commandViewHolder = new CommandViewHolder(view);
            view.setTag(commandViewHolder);
        } else {
            commandViewHolder = (CommandViewHolder) view.getTag();
        }
        try {
            SlashCommand slashCommand = SlashCommand.get((String) getItem(i));
            int description = slashCommand.getDescription();
            if (slashCommand != null) {
                commandViewHolder.tvCommandName.setText(slashCommand.getCommand());
                commandViewHolder.tvCommandParameter.setText(slashCommand.getParam());
                commandViewHolder.tvCommandDescription.setText(VectorApp.getInstance().getString(description));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return view;
    }

    public Filter getFilter() {
        if (this.mFilter == null) {
            this.mFilter = new AutoCompletedCommandFilter();
        }
        return this.mFilter;
    }
}
