package com.tonic.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark test methods with their corresponding Xray test keys.
 * This annotation can be used on test methods or test classes to specify
 * the Xray test key that should be reported when the test executes.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface XrayKey {
    /**
     * The Xray test key (e.g., "TONIC-12345")
     * @return the Xray test key
     */
    String value();
} 