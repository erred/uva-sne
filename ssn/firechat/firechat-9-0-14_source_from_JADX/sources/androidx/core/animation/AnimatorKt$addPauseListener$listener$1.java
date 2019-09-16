package androidx.core.animation;

import android.animation.Animator;
import android.animation.Animator.AnimatorPauseListener;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import org.jetbrains.annotations.NotNull;

@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u001b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002*\u0001\u0000\b\n\u0018\u00002\u00020\u0001B\u0005¢\u0006\u0002\u0010\u0002J\u0010\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016J\u0010\u0010\u0007\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u0006H\u0016¨\u0006\b"}, mo21251d2 = {"androidx/core/animation/AnimatorKt$addPauseListener$listener$1", "Landroid/animation/Animator$AnimatorPauseListener;", "(Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;)V", "onAnimationPause", "", "animator", "Landroid/animation/Animator;", "onAnimationResume", "core-ktx_release"}, mo21252k = 1, mo21253mv = {1, 1, 9})
/* compiled from: Animator.kt */
public final class AnimatorKt$addPauseListener$listener$1 implements AnimatorPauseListener {
    final /* synthetic */ Function1 $onPause;
    final /* synthetic */ Function1 $onResume;

    AnimatorKt$addPauseListener$listener$1(Function1 function1, Function1 function12) {
        this.$onPause = function1;
        this.$onResume = function12;
    }

    public void onAnimationPause(@NotNull Animator animator) {
        Function1 function1 = this.$onPause;
        if (function1 != null) {
            Unit unit = (Unit) function1.invoke(animator);
        }
    }

    public void onAnimationResume(@NotNull Animator animator) {
        Function1 function1 = this.$onResume;
        if (function1 != null) {
            Unit unit = (Unit) function1.invoke(animator);
        }
    }
}
