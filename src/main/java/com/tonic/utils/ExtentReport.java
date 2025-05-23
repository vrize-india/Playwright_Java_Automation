package com.tonic.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.tonic.constants.FrameworkConstants;
import com.tonic.enums.CategoryType;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class ExtentReport {

    private ExtentReport() {
    }

    private static ExtentReports extent;

    public static void initReports() {
        if (Objects.isNull(extent)) {
            extent = new ExtentReports();
            File[] reportFiles = new File(FrameworkConstants.getExtentReportFolderPath()).listFiles();
            for (File f : reportFiles) {
                f.delete();
            }
            File[] screenshotFiles = new File(FrameworkConstants.getExtentReportScreenshotsFolderPath()).listFiles();
            if (screenshotFiles != null) {
                for (File f : screenshotFiles) {
                    f.delete();
                }
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(FrameworkConstants.getExtentReportFilePath());
            extent.attachReporter(spark);
            spark.config().setTheme(Theme.STANDARD);
            spark.config().setDocumentTitle("Tropical Smoothie Report");
            spark.config().setReportName(
                    "Tropical Smoothie : " + FrameworkConstants.ENV.toUpperCase());
        }
    }

    public static void flushReports() {
        if (Objects.nonNull(extent)) {
            extent.flush();
        }
        ExtentManager.unload();
        try {
            Desktop.getDesktop().browse(new File(FrameworkConstants.getExtentReportFolderPath()).toURI());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createTest(String testCaseName) {
        ExtentManager.setExtentTest(extent.createTest(testCaseName));
    }

    public static void addCategories(CategoryType[] categories) {
        for (CategoryType temp : categories) {
            ExtentManager.getExtentTest().assignCategory(temp.toString());
        }
    }
}
