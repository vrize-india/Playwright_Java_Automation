package com.tonic.listeners;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import com.tonic.utils.JiraPolicy;
import com.tonic.utils.JiraServiceProvider;
import com.tonic.listeners.XrayLogger;

/**
 * TestNG listener that automatically logs bugs to JIRA for test failures based on {@link JiraPolicy} annotation.
 * <p>
 * If a test method is annotated with {@code @JiraPolicy(logTicketReady = true)}, and the test fails,
 * this listener will use {@link JiraServiceProvider} to create a JIRA issue with details such as
 * the exception message and full stack trace.
 * </p>
 *
 * This integration helps in tracking regression or flaky issues directly in JIRA,
 * streamlining QA workflows in continuous testing environments.
 *
 * @author Gaurav Purwar
 */

public class JiraListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestSuccess(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (!XrayLogger.isXrayEnabled()) { return; }
        
        String xrayKey = "UNKNOWN-KEY"; // TODO: Replace with actual mapping/tag extraction
        String methodName = result.getMethod().getConstructorOrMethod().getMethod().getName();
        if (methodName.startsWith("test_")) { xrayKey = methodName.substring(5); }

        JiraServiceProvider jiraSp = new JiraServiceProvider();
        String issueSummary = methodName + " got failed due to some assertion or exception";
        String issueDescription = result.getThrowable().getMessage() + "\n" + ExceptionUtils.getFullStackTrace(result.getThrowable());

        String bugKey = jiraSp.createJiraTicket("Bug", issueSummary, issueDescription);
        if (bugKey != null && !"UNKNOWN-KEY".equals(xrayKey)) {
            jiraSp.linkIssues(bugKey, xrayKey, "Relates");
            XrayLogger.logTestExecution(xrayKey, "FAILED", issueDescription, bugKey);
        } else {
            System.out.println("[JIRA] Could not link bug to Xray test case. BugKey: " + bugKey + ", XrayKey: " + xrayKey);
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStart(ITestContext context) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onFinish(ITestContext context) {
        // TODO Auto-generated method stub

    }

}

