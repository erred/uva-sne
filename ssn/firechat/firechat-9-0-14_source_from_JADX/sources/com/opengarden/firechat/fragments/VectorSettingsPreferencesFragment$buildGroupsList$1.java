package com.opengarden.firechat.fragments;

import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import com.opengarden.firechat.matrixsdk.groups.GroupsManager;
import com.opengarden.firechat.matrixsdk.rest.callback.ApiCallback;
import com.opengarden.firechat.matrixsdk.rest.model.MatrixError;
import com.opengarden.firechat.matrixsdk.rest.model.group.Group;
import com.opengarden.firechat.preference.VectorGroupPreference;
import java.util.Set;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0016\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0000\n\u0000\u0010\u0000\u001a\u00020\u00012\u000e\u0010\u0002\u001a\n \u0004*\u0004\u0018\u00010\u00030\u00032\u000e\u0010\u0005\u001a\n \u0004*\u0004\u0018\u00010\u00060\u0006H\n¢\u0006\u0002\b\u0007"}, mo21251d2 = {"<anonymous>", "", "<anonymous parameter 0>", "Landroid/preference/Preference;", "kotlin.jvm.PlatformType", "newValue", "", "onPreferenceChange"}, mo21252k = 3, mo21253mv = {1, 1, 9})
/* compiled from: VectorSettingsPreferencesFragment.kt */
final class VectorSettingsPreferencesFragment$buildGroupsList$1 implements OnPreferenceChangeListener {
    final /* synthetic */ Group $group;
    final /* synthetic */ Set $publicisedGroups;
    final /* synthetic */ VectorGroupPreference $vectorGroupPreference;
    final /* synthetic */ VectorSettingsPreferencesFragment this$0;

    VectorSettingsPreferencesFragment$buildGroupsList$1(VectorSettingsPreferencesFragment vectorSettingsPreferencesFragment, Group group, VectorGroupPreference vectorGroupPreference, Set set) {
        this.this$0 = vectorSettingsPreferencesFragment;
        this.$group = group;
        this.$vectorGroupPreference = vectorGroupPreference;
        this.$publicisedGroups = set;
    }

    public final boolean onPreferenceChange(Preference preference, final Object obj) {
        if (obj instanceof Boolean) {
            Set access$getMPublicisedGroups$p = this.this$0.mPublicisedGroups;
            if (access$getMPublicisedGroups$p == null) {
                Intrinsics.throwNpe();
            }
            Group group = this.$group;
            Intrinsics.checkExpressionValueIsNotNull(group, "group");
            if (!Intrinsics.areEqual(obj, (Object) Boolean.valueOf(access$getMPublicisedGroups$p.contains(group.getGroupId())))) {
                this.this$0.displayLoadingView();
                GroupsManager groupsManager = VectorSettingsPreferencesFragment.access$getMSession$p(this.this$0).getGroupsManager();
                Group group2 = this.$group;
                Intrinsics.checkExpressionValueIsNotNull(group2, "group");
                groupsManager.updateGroupPublicity(group2.getGroupId(), ((Boolean) obj).booleanValue(), new ApiCallback<Void>(this) {
                    final /* synthetic */ VectorSettingsPreferencesFragment$buildGroupsList$1 this$0;

                    {
                        this.this$0 = r1;
                    }

                    public void onSuccess(@Nullable Void voidR) {
                        this.this$0.this$0.hideLoadingView();
                        if (((Boolean) obj).booleanValue()) {
                            Set access$getMPublicisedGroups$p = this.this$0.this$0.mPublicisedGroups;
                            if (access$getMPublicisedGroups$p == null) {
                                Intrinsics.throwNpe();
                            }
                            Group group = this.this$0.$group;
                            Intrinsics.checkExpressionValueIsNotNull(group, "group");
                            String groupId = group.getGroupId();
                            Intrinsics.checkExpressionValueIsNotNull(groupId, "group.groupId");
                            access$getMPublicisedGroups$p.add(groupId);
                            return;
                        }
                        Set access$getMPublicisedGroups$p2 = this.this$0.this$0.mPublicisedGroups;
                        if (access$getMPublicisedGroups$p2 == null) {
                            Intrinsics.throwNpe();
                        }
                        Group group2 = this.this$0.$group;
                        Intrinsics.checkExpressionValueIsNotNull(group2, "group");
                        access$getMPublicisedGroups$p2.remove(group2.getGroupId());
                    }

                    private final void onError() {
                        this.this$0.this$0.hideLoadingView();
                        VectorGroupPreference vectorGroupPreference = this.this$0.$vectorGroupPreference;
                        Set set = this.this$0.$publicisedGroups;
                        Group group = this.this$0.$group;
                        Intrinsics.checkExpressionValueIsNotNull(group, "group");
                        vectorGroupPreference.setChecked(set.contains(group.getGroupId()));
                    }

                    public void onNetworkError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        onError();
                    }

                    public void onMatrixError(@NotNull MatrixError matrixError) {
                        Intrinsics.checkParameterIsNotNull(matrixError, "e");
                        onError();
                    }

                    public void onUnexpectedError(@NotNull Exception exc) {
                        Intrinsics.checkParameterIsNotNull(exc, "e");
                        onError();
                    }
                });
            }
        }
        return true;
    }
}
