package com.google.firebase.components;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/* compiled from: com.google.firebase:firebase-common@@16.0.1 */
final class zze {

    /* compiled from: com.google.firebase:firebase-common@@16.0.1 */
    static class zza {
        private final Component<?> zza;
        private final Set<zza> zzb = new HashSet();
        private final Set<zza> zzc = new HashSet();

        zza(Component<?> component) {
            this.zza = component;
        }

        /* access modifiers changed from: 0000 */
        public final void zza(zza zza2) {
            this.zzb.add(zza2);
        }

        /* access modifiers changed from: 0000 */
        public final void zzb(zza zza2) {
            this.zzc.add(zza2);
        }

        /* access modifiers changed from: 0000 */
        public final Set<zza> zza() {
            return this.zzb;
        }

        /* access modifiers changed from: 0000 */
        public final void zzc(zza zza2) {
            this.zzc.remove(zza2);
        }

        /* access modifiers changed from: 0000 */
        public final Component<?> zzb() {
            return this.zza;
        }

        /* access modifiers changed from: 0000 */
        public final boolean zzc() {
            return this.zzc.isEmpty();
        }

        /* access modifiers changed from: 0000 */
        public final boolean zzd() {
            return this.zzb.isEmpty();
        }
    }

    private static Set<zza> zza(Set<zza> set) {
        HashSet hashSet = new HashSet();
        for (zza zza2 : set) {
            if (zza2.zzc()) {
                hashSet.add(zza2);
            }
        }
        return hashSet;
    }

    static List<Component<?>> zza(List<Component<?>> list) {
        HashMap hashMap = new HashMap(list.size());
        for (Component component : list) {
            zza zza2 = new zza(component);
            Iterator it = component.zza().iterator();
            while (true) {
                if (it.hasNext()) {
                    Class cls = (Class) it.next();
                    if (hashMap.put(cls, zza2) != null) {
                        throw new IllegalArgumentException(String.format("Multiple components provide %s.", new Object[]{cls}));
                    }
                }
            }
        }
        for (zza zza3 : hashMap.values()) {
            for (Dependency dependency : zza3.zzb().zzb()) {
                if (dependency.zzc()) {
                    zza zza4 = (zza) hashMap.get(dependency.zza());
                    if (zza4 != null) {
                        zza3.zza(zza4);
                        zza4.zzb(zza3);
                    }
                }
            }
        }
        HashSet<zza> hashSet = new HashSet<>(hashMap.values());
        Set zza5 = zza((Set<zza>) hashSet);
        ArrayList arrayList = new ArrayList();
        while (!zza5.isEmpty()) {
            zza zza6 = (zza) zza5.iterator().next();
            zza5.remove(zza6);
            arrayList.add(zza6.zzb());
            for (zza zza7 : zza6.zza()) {
                zza7.zzc(zza6);
                if (zza7.zzc()) {
                    zza5.add(zza7);
                }
            }
        }
        if (arrayList.size() == list.size()) {
            Collections.reverse(arrayList);
            return arrayList;
        }
        ArrayList arrayList2 = new ArrayList();
        for (zza zza8 : hashSet) {
            if (!zza8.zzc() && !zza8.zzd()) {
                arrayList2.add(zza8.zzb());
            }
        }
        throw new DependencyCycleException(arrayList2);
    }
}
