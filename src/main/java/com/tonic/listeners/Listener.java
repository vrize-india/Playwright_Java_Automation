package com.tonic.listeners;

import com.aventstack.extentreports.Status;
import com.tonic.constants.FrameworkConstants;
import com.tonic.enums.LogType;
import com.tonic.utils.ExtentManager;
import com.tonic.utils.ExtentReport;
import com.tonic.utils.FileUtils;
import org.testng.*;

import java.util.Arrays;

import static com.tonic.utils.FrameworkLogger.log;

public class Listener implements ITestListener, ISuiteListener {

    @Override
    public void onStart(ISuite suite) {
        ExtentReport.initReports();
    }

    @Override
    public void onFinish(ISuite suite) {
        ExtentReport.flushReports();
    }

    @Override
    public void onTestStart(ITestResult result) {
        System.out.println(result.getMethod().getDescription());
        ExtentReport.createTest(result.getMethod().getDescription());
        log(LogType.CONSOLE, result.getMethod().getDescription());
//        ExtentReport.addCategories(result.getMethod().getConstructorOrMethod().getMethod()
//                .getAnnotation(TonicAnnotation.class).category());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log(LogType.PASS, result.getMethod().getDescription() + " is passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        FileUtils fileUtils = new FileUtils();
        fileUtils.appendToFile(result.getName() + " | " + result.getThrowable() + ",\n\n", FrameworkConstants.ERROR_LOG_FILE_PATH);
        log(LogType.FAIL,result.getMethod().getDescription());
        log(LogType.FAIL, result.getMethod().getDescription() + " is failed");
        log(LogType.FAIL, result.getThrowable().toString());
        log(LogType.FAIL, Arrays.toString(result.getThrowable().getStackTrace()));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log(LogType.FAIL, Arrays.toString(result.getThrowable().getStackTrace()));
        ExtentManager.getExtentTest().log(Status.SKIP, "Test Skipped");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
    }

    @Override
    public void onStart(ITestContext context) {
    }

    @Override
    public void onFinish(ITestContext context) {
    }
}