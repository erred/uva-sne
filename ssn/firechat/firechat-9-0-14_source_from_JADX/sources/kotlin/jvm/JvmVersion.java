package kotlin.jvm;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import kotlin.Metadata;
import kotlin.annotation.AnnotationRetention;
import kotlin.annotation.AnnotationTarget;
import kotlin.annotation.Retention;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR})
@kotlin.annotation.Target(allowedTargets = {AnnotationTarget.FILE, AnnotationTarget.CLASS, AnnotationTarget.PROPERTY, AnnotationTarget.CONSTRUCTOR, AnnotationTarget.FUNCTION})
@Metadata(mo21249bv = {1, 0, 2}, mo21250d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u001b\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0002\u0018\u00002\u00020\u0001B\u0014\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003R\t\u0010\u0004\u001a\u00020\u0003¢\u0006\u0000R\t\u0010\u0002\u001a\u00020\u0003¢\u0006\u0000¨\u0006\u0005"}, mo21251d2 = {"Lkotlin/jvm/JvmVersion;", "", "minimum", "", "maximum", "kotlin-stdlib"}, mo21252k = 1, mo21253mv = {1, 1, 9})
@Retention(AnnotationRetention.SOURCE)
@java.lang.annotation.Retention(RetentionPolicy.SOURCE)
/* compiled from: JvmVersion.kt */
public @interface JvmVersion {
    int maximum() default 100;

    int minimum() default 6;
}
