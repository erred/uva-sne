package com.google.android.gms.measurement.internal;

import java.util.List;
import java.util.concurrent.Callable;

final class zzcc implements Callable<List<zzfj>> {
    private final /* synthetic */ String zzaeh;
    private final /* synthetic */ String zzaeo;
    private final /* synthetic */ zzbv zzaqo;
    private final /* synthetic */ String zzaqq;

    zzcc(zzbv zzbv, String str, String str2, String str3) {
        this.zzaqo = zzbv;
        this.zzaqq = str;
        this.zzaeh = str2;
        this.zzaeo = str3;
    }

    public final /* synthetic */ Object call() throws Exception {
        
        /*  JADX ERROR: Method code generation error
            jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0006: INVOKE  (wrap: com.google.android.gms.measurement.internal.zzfa
              0x0002: INVOKE  (r0v1 com.google.android.gms.measurement.internal.zzfa) = (wrap: com.google.android.gms.measurement.internal.zzbv
              0x0000: IGET  (r0v0 com.google.android.gms.measurement.internal.zzbv) = (r4v0 'this' com.google.android.gms.measurement.internal.zzcc A[THIS]) com.google.android.gms.measurement.internal.zzcc.zzaqo com.google.android.gms.measurement.internal.zzbv) com.google.android.gms.measurement.internal.zzbv.zza(com.google.android.gms.measurement.internal.zzbv):com.google.android.gms.measurement.internal.zzfa type: STATIC) com.google.android.gms.measurement.internal.zzfa.zzly():void type: VIRTUAL in method: com.google.android.gms.measurement.internal.zzcc.call():java.lang.Object, dex: classes.dex
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:245)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:213)
            	at jadx.core.codegen.RegionGen.makeSimpleBlock(RegionGen.java:109)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:55)
            	at jadx.core.codegen.RegionGen.makeSimpleRegion(RegionGen.java:92)
            	at jadx.core.codegen.RegionGen.makeRegion(RegionGen.java:58)
            	at jadx.core.codegen.MethodGen.addRegionInsns(MethodGen.java:210)
            	at jadx.core.codegen.MethodGen.addInstructions(MethodGen.java:203)
            	at jadx.core.codegen.ClassGen.addMethod(ClassGen.java:316)
            	at jadx.core.codegen.ClassGen.addMethods(ClassGen.java:262)
            	at jadx.core.codegen.ClassGen.addClassBody(ClassGen.java:225)
            	at jadx.core.codegen.ClassGen.addClassCode(ClassGen.java:110)
            	at jadx.core.codegen.ClassGen.makeClass(ClassGen.java:76)
            	at jadx.core.codegen.CodeGen.wrapCodeGen(CodeGen.java:44)
            	at jadx.core.codegen.CodeGen.generateJavaCode(CodeGen.java:32)
            	at jadx.core.codegen.CodeGen.generate(CodeGen.java:20)
            	at jadx.core.ProcessClass.process(ProcessClass.java:36)
            	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:311)
            	at jadx.api.JavaClass.decompile(JavaClass.java:62)
            	at jadx.api.JadxDecompiler.lambda$appendSourcesSave$0(JadxDecompiler.java:217)
            Caused by: jadx.core.utils.exceptions.CodegenException: Error generate insn: 0x0002: INVOKE  (r0v1 com.google.android.gms.measurement.internal.zzfa) = (wrap: com.google.android.gms.measurement.internal.zzbv
              0x0000: IGET  (r0v0 com.google.android.gms.measurement.internal.zzbv) = (r4v0 'this' com.google.android.gms.measurement.internal.zzcc A[THIS]) com.google.android.gms.measurement.internal.zzcc.zzaqo com.google.android.gms.measurement.internal.zzbv) com.google.android.gms.measurement.internal.zzbv.zza(com.google.android.gms.measurement.internal.zzbv):com.google.android.gms.measurement.internal.zzfa type: STATIC in method: com.google.android.gms.measurement.internal.zzcc.call():java.lang.Object, dex: classes.dex
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:245)
            	at jadx.core.codegen.InsnGen.addArg(InsnGen.java:105)
            	at jadx.core.codegen.InsnGen.addArgDot(InsnGen.java:88)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:682)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:357)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:239)
            	... 19 more
            Caused by: java.util.ConcurrentModificationException
            	at java.base/java.util.ArrayList.removeIf(ArrayList.java:1714)
            	at java.base/java.util.ArrayList.removeIf(ArrayList.java:1689)
            	at jadx.core.dex.instructions.args.SSAVar.removeUse(SSAVar.java:86)
            	at jadx.core.utils.InsnRemover.unbindArgUsage(InsnRemover.java:90)
            	at jadx.core.dex.nodes.InsnNode.replaceArg(InsnNode.java:130)
            	at jadx.core.dex.nodes.InsnNode.replaceArg(InsnNode.java:134)
            	at jadx.core.codegen.InsnGen.inlineMethod(InsnGen.java:892)
            	at jadx.core.codegen.InsnGen.makeInvoke(InsnGen.java:669)
            	at jadx.core.codegen.InsnGen.makeInsnBody(InsnGen.java:357)
            	at jadx.core.codegen.InsnGen.makeInsn(InsnGen.java:223)
            	... 24 more
            */
        /*
            this = this;
            com.google.android.gms.measurement.internal.zzbv r0 = r4.zzaqo
            com.google.android.gms.measurement.internal.zzfa r0 = r0.zzamz
            r0.zzly()
            com.google.android.gms.measurement.internal.zzbv r0 = r4.zzaqo
            com.google.android.gms.measurement.internal.zzfa r0 = r0.zzamz
            com.google.android.gms.measurement.internal.zzq r0 = r0.zzjq()
            java.lang.String r1 = r4.zzaqq
            java.lang.String r2 = r4.zzaeh
            java.lang.String r3 = r4.zzaeo
            java.util.List r0 = r0.zzb(r1, r2, r3)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.google.android.gms.measurement.internal.zzcc.call():java.lang.Object");
    }
}
