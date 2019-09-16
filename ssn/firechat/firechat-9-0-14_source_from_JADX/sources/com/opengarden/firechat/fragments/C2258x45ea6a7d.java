package com.opengarden.firechat.fragments;

import android.os.AsyncTask;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.text.format.Formatter;
import com.bumptech.glide.Glide;
import com.opengarden.firechat.matrixsdk.p007db.MXMediasCache;
import com.opengarden.firechat.matrixsdk.rest.callback.SimpleApiCallback;
import com.opengarden.firechat.matrixsdk.util.Log;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0010\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u0003H\n¢\u0006\u0002\b\u0005"}, mo21251d2 = {"<anonymous>", "", "it", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "onPreferenceClick"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* renamed from: com.opengarden.firechat.fragments.VectorSettingsPreferencesFragment$onCreate$$inlined$let$lambda$10 */
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class C2258x45ea6a7d implements OnPreferenceClickListener {
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    C2258x45ea6a7d(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment) {
        this.this$0 = vectorSettingsPreferencesFragment;
    }

    public final boolean onPreferenceClick(final Preference preference) {
        this.this$0.displayLoadingView();
        C22591 r0 = new AsyncTask<Void, Void, Void>(this) {
            final /* synthetic */ C2258x45ea6a7d this$0;

            {
                this.this$0 = r1;
            }

            /* access modifiers changed from: protected */
            @Nullable
            public Void doInBackground(@NotNull Void... voidArr) {
                Intrinsics.checkParameterIsNotNull(voidArr, "params");
                VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0.this$0).getMediasCache().clear();
                Glide.get(this.this$0.this$0.getActivity()).clearDiskCache();
                return null;
            }

            /* access modifiers changed from: protected */
            public void onPostExecute(@Nullable Void voidR) {
                this.this$0.this$0.hideLoadingView();
                MXMediasCache.getCachesSize(this.this$0.this$0.getActivity(), new SimpleApiCallback<Long>(this) {
                    final /* synthetic */ C22591 this$0;

                    {
                        this.this$0 = r1;
                    }

                    public /* bridge */ /* synthetic */ void onSuccess(Object obj) {
                        onSuccess(((Number) obj).longValue());
                    }

                    public void onSuccess(long j) {
                        Preference preference = preference;
                        Intrinsics.checkExpressionValueIsNotNull(preference, "it");
                        preference.setSummary(Formatter.formatFileSize(this.this$0.this$0.this$0.getActivity(), j));
                    }
                });
            }
        };
        try {
            r0.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new Void[0]);
        } catch (Exception e) {
            String access$getLOG_TAG$p = VectorSettingsPreferencesFragment.Companion.getLOG_TAG();
            StringBuilder sb = new StringBuilder();
            sb.append("## mSession.getMediasCache().clear() failed ");
            sb.append(e.getMessage());
            Log.m211e(access$getLOG_TAG$p, sb.toString());
            r0.cancel(true);
            this.this$0.hideLoadingView();
        }
        return false;
    }
}
