package com.tonic.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JiraPolicy {
    boolean logTicketReady();
}
