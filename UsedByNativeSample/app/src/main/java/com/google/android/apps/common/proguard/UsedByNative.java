package com.google.android.apps.common.proguard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicating that a class, method, or field is used by native code.
 * This is used to ensure Proguard/R8 does not remove or obfuscate these members.
 */
@Retention(RetentionPolicy.CLASS)
@Target({
    ElementType.METHOD,
    ElementType.FIELD,
    ElementType.TYPE,
    ElementType.CONSTRUCTOR
})
public @interface UsedByNative {
    String value() default "";
}
