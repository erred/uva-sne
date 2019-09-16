package com.google.android.gms.measurement.internal;

import android.os.Parcel;
import android.os.Parcelable.Creator;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.common.internal.safeparcel.AbstractSafeParcelable;
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Class;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Constructor;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Field;
import com.google.android.gms.common.internal.safeparcel.SafeParcelable.Param;

@Class(creator = "ConditionalUserPropertyParcelCreator")
public final class zzl extends AbstractSafeParcelable {
    public static final Creator<zzl> CREATOR = new zzm();
    @Field(mo16385id = 6)
    public boolean active;
    @Field(mo16385id = 5)
    public long creationTimestamp;
    @Field(mo16385id = 3)
    public String origin;
    @Field(mo16385id = 2)
    public String packageName;
    @Field(mo16385id = 11)
    public long timeToLive;
    @Field(mo16385id = 7)
    public String triggerEventName;
    @Field(mo16385id = 9)
    public long triggerTimeout;
    @Field(mo16385id = 4)
    public zzfh zzahb;
    @Field(mo16385id = 8)
    public zzad zzahc;
    @Field(mo16385id = 10)
    public zzad zzahd;
    @Field(mo16385id = 12)
    public zzad zzahe;

    zzl(zzl zzl) {
        Preconditions.checkNotNull(zzl);
        this.packageName = zzl.packageName;
        this.origin = zzl.origin;
        this.zzahb = zzl.zzahb;
        this.creationTimestamp = zzl.creationTimestamp;
        this.active = zzl.active;
        this.triggerEventName = zzl.triggerEventName;
        this.zzahc = zzl.zzahc;
        this.triggerTimeout = zzl.triggerTimeout;
        this.zzahd = zzl.zzahd;
        this.timeToLive = zzl.timeToLive;
        this.zzahe = zzl.zzahe;
    }

    @Constructor
    zzl(@Param(mo16388id = 2) String str, @Param(mo16388id = 3) String str2, @Param(mo16388id = 4) zzfh zzfh, @Param(mo16388id = 5) long j, @Param(mo16388id = 6) boolean z, @Param(mo16388id = 7) String str3, @Param(mo16388id = 8) zzad zzad, @Param(mo16388id = 9) long j2, @Param(mo16388id = 10) zzad zzad2, @Param(mo16388id = 11) long j3, @Param(mo16388id = 12) zzad zzad3) {
        this.packageName = str;
        this.origin = str2;
        this.zzahb = zzfh;
        this.creationTimestamp = j;
        this.active = z;
        this.triggerEventName = str3;
        this.zzahc = zzad;
        this.triggerTimeout = j2;
        this.zzahd = zzad2;
        this.timeToLive = j3;
        this.zzahe = zzad3;
    }

    public final void writeToParcel(Parcel parcel, int i) {
        int beginObjectHeader = SafeParcelWriter.beginObjectHeader(parcel);
        SafeParcelWriter.writeString(parcel, 2, this.packageName, false);
        SafeParcelWriter.writeString(parcel, 3, this.origin, false);
        SafeParcelWriter.writeParcelable(parcel, 4, this.zzahb, i, false);
        SafeParcelWriter.writeLong(parcel, 5, this.creationTimestamp);
        SafeParcelWriter.writeBoolean(parcel, 6, this.active);
        SafeParcelWriter.writeString(parcel, 7, this.triggerEventName, false);
        SafeParcelWriter.writeParcelable(parcel, 8, this.zzahc, i, false);
        SafeParcelWriter.writeLong(parcel, 9, this.triggerTimeout);
        SafeParcelWriter.writeParcelable(parcel, 10, this.zzahd, i, false);
        SafeParcelWriter.writeLong(parcel, 11, this.timeToLive);
        SafeParcelWriter.writeParcelable(parcel, 12, this.zzahe, i, false);
        SafeParcelWriter.finishObjectHeader(parcel, beginObjectHeader);
    }
}
