package com.tonic.listeners;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.Date;

import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import static com.tonic.factory.PlaywrightFactory.takeScreenshot;

public class ExtentReportListener implements ITestListener {

	// Use absolute paths to ensure the directory is created properly
	private static final String OUTPUT_FOLDER = System.getProperty("user.dir") + File.separator + "build" + File.separator;
	private static final String FILE_NAME = "TestExecutionReport"+System.currentTimeMillis()+".html";

	// Changed from static initialization to per-test-run initialization
	private ExtentReports extent;
	private ExtentTest test;
	
	// Initialize the report when the test suite starts
	private ExtentReports initReports() {
		try {
			// Ensure directory exists
			File outputDir = new File(OUTPUT_FOLDER);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
				System.out.println("Created output directory: " + OUTPUT_FOLDER);
			}
			
			// Create a new ExtentReports instance
			ExtentReports extentReports = new ExtentReports();
			
			// Create the reporter and attach it to the extentReports
			File reportFile = new File(OUTPUT_FOLDER + FILE_NAME);
			ExtentSparkReporter reporter = new ExtentSparkReporter(reportFile);
			reporter.config().setReportName("TONIC BOH Test Results");
			reporter.config().setDocumentTitle("Test Execution Report");
			
			extentReports.attachReporter(reporter);
			
			// Add system information
			extentReports.setSystemInfo("Author", "Abhinav Singh");
			extentReports.setSystemInfo("Build#", "1.1");
			extentReports.setSystemInfo("Team", "OMS");
			extentReports.setSystemInfo("Customer Name", "NAL");
			extentReports.setSystemInfo("Execution Time", new Date().toString());
			extentReports.setSystemInfo("OS", System.getProperty("os.name"));
			extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
			
			System.out.println("ExtentReports initialized. Report will be saved to: " + reportFile.getAbsolutePath());
			
			return extentReports;
		} catch (Exception e) {
			System.err.println("Error initializing ExtentReports: " + e.getMessage());
			e.printStackTrace();
			// Create a fallback reporter in case of exception
			ExtentReports fallbackReports = new ExtentReports();
			ExtentSparkReporter fallbackReporter = new ExtentSparkReporter("TestReport.html");
			fallbackReports.attachReporter(fallbackReporter);
			return fallbackReports;
		}
	}

	@Override
	public void onStart(ITestContext context) {
		System.out.println("Test Suite started!");
		// Create a new report for each test run
		extent = initReports();
	}

	@Override
	public void onFinish(ITestContext context) {
		System.out.println("Test Suite is ending!");
		if (extent != null) {
			extent.flush();
			System.out.println("ExtentReports saved to: " + OUTPUT_FOLDER + FILE_NAME);
		}
	}

	@Override
	public void onTestStart(ITestResult result) {
		String methodName = result.getMethod().getMethodName();
		String qualifiedName = result.getMethod().getQualifiedName();
		int last = qualifiedName.lastIndexOf(".");
		int mid = qualifiedName.substring(0, last).lastIndexOf(".");
		String className = qualifiedName.substring(mid + 1, last);

		System.out.println(methodName + " started!");
		test = extent.createTest(result.getMethod().getMethodName(),
				result.getMethod().getDescription());

		test.assignCategory(result.getTestContext().getSuite().getName());
		/*
		 * methodName = StringUtils.capitalize(StringUtils.join(StringUtils.
		 * splitByCharacterTypeCamelCase(methodName), StringUtils.SPACE));
		 */
		test.assignCategory(className);
		test.getModel().setStartTime(getTime(result.getStartMillis()));
	}

	public void onTestSuccess(ITestResult result) {
		System.out.println((result.getMethod().getMethodName() + " passed!"));
		test.pass("Test passed");
		
		// Take screenshot and attach to report
		String screenshotBase64 = takeScreenshot();
		if (screenshotBase64 != null && !screenshotBase64.isEmpty()) {
			test.pass(MediaEntityBuilder.createScreenCaptureFromBase64String(screenshotBase64, 
					result.getMethod().getMethodName() + "_passed").build());
		}
		
		test.getModel().setEndTime(getTime(result.getEndMillis()));
	}

	public void onTestFailure(ITestResult result) {
		System.out.println((result.getMethod().getMethodName() + " failed!"));
		
		// Take screenshot and attach to report
		String screenshotBase64 = takeScreenshot();
		if (screenshotBase64 != null && !screenshotBase64.isEmpty()) {
			test.fail(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromBase64String(
					screenshotBase64, result.getMethod().getMethodName() + "_failed").build());
		} else {
			test.fail(result.getThrowable());
		}
		
		test.getModel().setEndTime(getTime(result.getEndMillis()));
	}

	public void onTestSkipped(ITestResult result) {
		System.out.println((result.getMethod().getMethodName() + " skipped!"));
		
		// Take screenshot and attach to report
		String screenshotBase64 = takeScreenshot();
		if (screenshotBase64 != null && !screenshotBase64.isEmpty()) {
			test.skip(result.getThrowable(), MediaEntityBuilder.createScreenCaptureFromBase64String(
					screenshotBase64, result.getMethod().getMethodName() + "_skipped").build());
		} else {
			test.skip(result.getThrowable());
		}
		
		test.getModel().setEndTime(getTime(result.getEndMillis()));
	}

	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
		System.out.println(("onTestFailedButWithinSuccessPercentage for " + result.getMethod().getMethodName()));
	}

	private Date getTime(long millis) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(millis);
		return calendar.getTime();
	}
}
