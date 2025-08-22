package com.vrize.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to enable retry logic for test methods.
 * Can be applied to individual test methods or test classes.
 * 
 * @author Tonic Automation Team
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Retry {
    
    /**
     * The number of retry attempts for this test.
     * Default is 3 if not specified.
     * 
     * @return The number of retry attempts
     */
    int value() default 3;
    
    /**
     * Whether retry is enabled for this test.
     * Default is true if not specified.
     * 
     * @return true if retry is enabled, false otherwise
     */
    boolean enabled() default true;
} 