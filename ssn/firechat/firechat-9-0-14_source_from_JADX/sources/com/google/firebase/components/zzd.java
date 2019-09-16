package com.google.firebase.components;

import com.google.android.gms.common.internal.Preconditions;
import com.google.firebase.events.Publisher;
import com.google.firebase.events.Subscriber;
import com.google.firebase.inject.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/* compiled from: com.google.firebase:firebase-common@@16.0.1 */
public final class zzd implements ComponentContainer {
    private final List<Component<?>> zza;
    private final Map<Class<?>, zzh<?>> zzb = new HashMap();
    private final zzf zzc;

    public final Object get(Class cls) {
        return ComponentContainer$$CC.get(this, cls);
    }

    public zzd(Executor executor, Iterable<ComponentRegistrar> iterable, Component<?>... componentArr) {
        this.zzc = new zzf(executor);
        ArrayList arrayList = new ArrayList();
        arrayList.add(Component.m208of(this.zzc, zzf.class, Subscriber.class, Publisher.class));
        for (ComponentRegistrar components : iterable) {
            arrayList.addAll(components.getComponents());
        }
        Collections.addAll(arrayList, componentArr);
        this.zza = Collections.unmodifiableList(zze.zza((List<Component<?>>) arrayList));
        for (Component zza2 : this.zza) {
            zza(zza2);
        }
        zza();
    }

    public final <T> Provider<T> getProvider(Class<T> cls) {
        Preconditions.checkNotNull(cls, "Null interface requested.");
        return (Provider) this.zzb.get(cls);
    }

    public final void zza(boolean z) {
        for (Component component : this.zza) {
            if (component.zze() || (component.zzf() && z)) {
                get((Class) component.zza().iterator().next());
            }
        }
        this.zzc.zza();
    }

    private <T> void zza(Component<T> component) {
        zzh zzh = new zzh(component.zzc(), new zzj(component, this));
        for (Class put : component.zza()) {
            this.zzb.put(put, zzh);
        }
    }

    private void zza() {
        for (Component component : this.zza) {
            Iterator it = component.zzb().iterator();
            while (true) {
                if (it.hasNext()) {
                    Dependency dependency = (Dependency) it.next();
                    if (dependency.zzb() && !this.zzb.containsKey(dependency.zza())) {
                        throw new MissingDependencyException(String.format("Unsatisfied dependency for component %s: %s", new Object[]{component, dependency.zza()}));
                    }
                }
            }
        }
    }
}
