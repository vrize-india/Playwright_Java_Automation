package com.tonic.utils;

import com.tonic.listeners.XrayLogger;
import com.tonic.listeners.TestResultAggregator;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Utility to attach the final report to Xray after all retries complete.
 * This is called from the bash script as a post-processing step.
 */
public class FinalReportAttacher {
    
    public static void main(String[] args) {
        try {
            if (XrayLogger.isXrayEnabled()) {
                String execKey = XrayLogger.readTestExecutionKeyFromFile();
                if (execKey != null && !execKey.trim().isEmpty()) {
                    System.out.println("[XRAY] Attaching reports to Test Execution: " + execKey);
                    
                    // Set the test execution key so that logTestExecution uses the correct execId
                    XrayLogger.setTestExecutionKey(execKey);
                    com.tonic.listeners.SharedExecIdManager.setSharedExecId(execKey);
                    
                    // Check if this is a single-run scenario
                    boolean isSingleRun = TestResultAggregator.isSingleRunScenario();
                    
                    if (isSingleRun) {
                        // For single runs, just attach the regular ChainTest report
                        if (Files.exists(Paths.get("target/chaintest/Index.html"))) {
                            XrayLogger.attachReportToTestExecution("target/chaintest/Index.html");
                            System.out.println("[XRAY] Single-run report attached.");
                        }
                    } else {
                        // For retry scenarios, generate and attach consolidated report
                        System.out.println("[Report] Generating consolidated report with final test statuses...");
                        ConsolidatedReportGenerator.generateReport();
                        
                        // Attach the consolidated report if it exists
                        if (Files.exists(Paths.get("target/consolidated-report.html"))) {
                            XrayLogger.attachReportToTestExecution("target/consolidated-report.html");
                            System.out.println("[XRAY] Consolidated report attached.");
                        }
                        
                        // Also attach the original chaintest report as detailed report
                        if (Files.exists(Paths.get("target/chaintest/Index.html"))) {
                            Files.copy(Paths.get("target/chaintest/Index.html"), 
                                      Paths.get("target/chaintest/Detailed-Report.html"),
                                      java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            XrayLogger.attachReportToTestExecution("target/chaintest/Detailed-Report.html");
                            System.out.println("[XRAY] Detailed chaintest report also attached.");
                        }
                        
                        // Log final test results to Xray
                        logFinalResultsToXray();
                    }
                    
                    System.out.println("[XRAY] All reports successfully attached.");
                } else {
                    System.out.println("[XRAY] No test execution key found. Skipping report attachment.");
                }
            } else {
                System.out.println("[XRAY] Xray integration is disabled. Skipping report attachment.");
            }
        } catch (Exception e) {
            System.err.println("[XRAY] Error in final report processing: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void logFinalResultsToXray() {
        try {
            // Load aggregated results
            TestResultAggregator.loadResults();
            var results = TestResultAggregator.getFinalResults();
            
            System.out.println("[XRAY] Logging final test results to Xray...");
            
            for (var entry : results.entrySet()) {
                var result = entry.getValue();
                if (result.testKey != null) {
                    // Log only the final status to Xray
                    String comment = null;
                    if (result.finalStatus.equals("FAILED")) {
                        comment = "Failed after " + result.totalAttempts + " attempts. " + result.lastFailureMessage;
                    } else if (result.totalAttempts > 1) {
                        comment = "Passed after " + result.totalAttempts + " attempts (including retries)";
                    }
                    
                    XrayLogger.logTestExecution(result.testKey, result.finalStatus, comment);
                }
            }
            
            System.out.println("[XRAY] Final test results logged.");
            
        } catch (Exception e) {
            System.err.println("[XRAY] Error logging final results: " + e.getMessage());
        }
    }
}
