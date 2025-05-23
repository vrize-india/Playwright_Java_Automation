package com.tonic.driver;


import io.appium.java_client.AppiumDriver;

import java.util.Objects;

public class DriverManager {

    private DriverManager(){
    }

    private static final ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    static void setDriver(AppiumDriver ad){
        if(Objects.nonNull(ad)){
            driver.set(ad);
        }
    }

    static void unload(){

        driver.remove();

    }
}
