package com.vrize.utils;

import com.vrize.listeners.TestResultAggregator;
import com.vrize.listeners.TestResultAggregator.TestResult;
import java.io.*;
import java.nio.file.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Generates a consolidated HTML report showing final test statuses after all retries.
 */
public class ConsolidatedReportGenerator {
    
    private static final String REPORT_PATH = "target/consolidated-report.html";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void generateReport() {
        try {
            // Load aggregated results
            TestResultAggregator.loadResults();
            Map<String, TestResult> results = TestResultAggregator.getFinalResults();
            
            if (results.isEmpty()) {
                System.out.println("[ConsolidatedReport] No test results found to generate report.");
                return;
            }
            
            String htmlContent = buildHtmlReport(results);
            Files.write(Paths.get(REPORT_PATH), htmlContent.getBytes());
            System.out.println("[ConsolidatedReport] Report generated at: " + REPORT_PATH);
            
            // Also generate summary
            String summary = TestResultAggregator.generateSummaryReport();
            Files.write(Paths.get("target/test-summary.txt"), summary.getBytes());
            
        } catch (Exception e) {
            System.err.println("[ConsolidatedReport] Error generating report: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static String buildHtmlReport(Map<String, TestResult> results) {
        StringBuilder html = new StringBuilder();
        
        // Calculate statistics
        int totalTests = results.size();
        int passedTests = 0;
        int failedTests = 0;
        int retriedTests = 0;
        long totalDuration = 0;
        
        for (TestResult result : results.values()) {
            totalDuration += result.duration;
            if (result.finalStatus.equals("PASSED")) {
                passedTests++;
                if (result.totalAttempts > 1) {
                    retriedTests++;
                }
            } else {
                failedTests++;
            }
        }
        
        // HTML Header
        html.append("<!DOCTYPE html>\n");
        html.append("<html>\n<head>\n");
        html.append("<title>Consolidated Test Report</title>\n");
        html.append("<style>\n");
        html.append(getCssStyles());
        html.append("</style>\n");
        html.append("</head>\n<body>\n");
        
        // Header
        html.append("<div class='header'>\n");
        html.append("<h1>Consolidated Test Execution Report</h1>\n");
        html.append("<p>Generated: ").append(dateFormat.format(new Date())).append("</p>\n");
        html.append("</div>\n");
        
        // Summary Statistics
        html.append("<div class='summary'>\n");
        html.append("<h2>Execution Summary</h2>\n");
        html.append("<div class='stats-grid'>\n");
        html.append("<div class='stat-card'>\n");
        html.append("<div class='stat-value'>").append(totalTests).append("</div>\n");
        html.append("<div class='stat-label'>Total Tests</div>\n");
        html.append("</div>\n");
        html.append("<div class='stat-card passed'>\n");
        html.append("<div class='stat-value'>").append(passedTests).append("</div>\n");
        html.append("<div class='stat-label'>Passed</div>\n");
        html.append("</div>\n");
        html.append("<div class='stat-card failed'>\n");
        html.append("<div class='stat-value'>").append(failedTests).append("</div>\n");
        html.append("<div class='stat-label'>Failed</div>\n");
        html.append("</div>\n");
        html.append("<div class='stat-card retried'>\n");
        html.append("<div class='stat-value'>").append(retriedTests).append("</div>\n");
        html.append("<div class='stat-label'>Passed After Retry</div>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        
        // Progress bar
        double passRate = totalTests > 0 ? (passedTests * 100.0 / totalTests) : 0;
        html.append("<div class='progress-bar'>\n");
        html.append("<div class='progress-fill' style='width: ").append(String.format("%.1f", passRate)).append("%;'></div>\n");
        html.append("<span class='progress-text'>").append(String.format("%.1f", passRate)).append("% Pass Rate</span>\n");
        html.append("</div>\n");
        html.append("</div>\n");
        
        // Test Results Table
        html.append("<div class='results-section'>\n");
        html.append("<h2>Test Results</h2>\n");
        html.append("<table class='results-table'>\n");
        html.append("<thead>\n");
        html.append("<tr>\n");
        html.append("<th>Test Key</th>\n");
        html.append("<th>Test Name</th>\n");
        html.append("<th>Status</th>\n");
        html.append("<th>Attempts</th>\n");
        html.append("<th>Duration</th>\n");
        html.append("<th>Details</th>\n");
        html.append("</tr>\n");
        html.append("</thead>\n");
        html.append("<tbody>\n");
        
        // Sort results by test key
        List<TestResult> sortedResults = new ArrayList<>(results.values());
        sortedResults.sort((a, b) -> {
            String keyA = a.testKey != null ? a.testKey : a.testName;
            String keyB = b.testKey != null ? b.testKey : b.testName;
            return keyA.compareTo(keyB);
        });
        
        for (TestResult result : sortedResults) {
            html.append("<tr class='").append(result.finalStatus.toLowerCase()).append("'>\n");
            html.append("<td>").append(result.testKey != null ? result.testKey : "-").append("</td>\n");
            html.append("<td>").append(result.testName).append("</td>\n");
            html.append("<td><span class='status-badge ").append(result.finalStatus.toLowerCase()).append("'>")
                .append(result.finalStatus).append("</span></td>\n");
            html.append("<td>").append(result.totalAttempts);
            if (result.failedAttempts > 0) {
                html.append(" (").append(result.failedAttempts).append(" failed)");
            }
            html.append("</td>\n");
            html.append("<td>").append(formatDuration(result.duration)).append("</td>\n");
            html.append("<td>");
            
            if (result.finalStatus.equals("FAILED")) {
                html.append("<div class='error-details'>");
                if (result.lastFailureMessage != null) {
                    html.append("<div class='error-message'>").append(escapeHtml(result.lastFailureMessage)).append("</div>");
                }
                if (result.screenshotPath != null) {
                    html.append("<a href='").append(result.screenshotPath).append("' target='_blank'>View Screenshot</a>");
                }
                html.append("</div>");
            } else if (result.totalAttempts > 1) {
                html.append("<span class='retry-info'>Passed after ").append(result.totalAttempts - 1).append(" retries</span>");
            } else {
                html.append("<span class='success-info'>Passed on first attempt</span>");
            }
            
            html.append("</td>\n");
            html.append("</tr>\n");
        }
        
        html.append("</tbody>\n");
        html.append("</table>\n");
        html.append("</div>\n");
        
        // Footer
        html.append("<div class='footer'>\n");
        html.append("<p>Total Execution Time: ").append(formatDuration(totalDuration)).append("</p>\n");
        html.append("</div>\n");
        
        html.append("</body>\n</html>");
        
        return html.toString();
    }
    
    private static String getCssStyles() {
        return "body {\n" +
               "    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Arial, sans-serif;\n" +
               "    margin: 0;\n" +
               "    padding: 0;\n" +
               "    background-color: #f5f5f5;\n" +
               "    color: #333;\n" +
               "}\n" +
               ".header {\n" +
               "    background-color: #2c3e50;\n" +
               "    color: white;\n" +
               "    padding: 20px;\n" +
               "    text-align: center;\n" +
               "}\n" +
               ".header h1 {\n" +
               "    margin: 0;\n" +
               "    font-size: 28px;\n" +
               "}\n" +
               ".summary {\n" +
               "    background-color: white;\n" +
               "    margin: 20px;\n" +
               "    padding: 20px;\n" +
               "    border-radius: 8px;\n" +
               "    box-shadow: 0 2px 4px rgba(0,0,0,0.1);\n" +
               "}\n" +
               ".stats-grid {\n" +
               "    display: grid;\n" +
               "    grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));\n" +
               "    gap: 20px;\n" +
               "    margin: 20px 0;\n" +
               "}\n" +
               ".stat-card {\n" +
               "    text-align: center;\n" +
               "    padding: 20px;\n" +
               "    border-radius: 8px;\n" +
               "    background-color: #f8f9fa;\n" +
               "}\n" +
               ".stat-card.passed {\n" +
               "    background-color: #d4edda;\n" +
               "    color: #155724;\n" +
               "}\n" +
               ".stat-card.failed {\n" +
               "    background-color: #f8d7da;\n" +
               "    color: #721c24;\n" +
               "}\n" +
               ".stat-card.retried {\n" +
               "    background-color: #fff3cd;\n" +
               "    color: #856404;\n" +
               "}\n" +
               ".stat-value {\n" +
               "    font-size: 36px;\n" +
               "    font-weight: bold;\n" +
               "}\n" +
               ".stat-label {\n" +
               "    font-size: 14px;\n" +
               "    margin-top: 5px;\n" +
               "}\n" +
               ".progress-bar {\n" +
               "    background-color: #e9ecef;\n" +
               "    height: 30px;\n" +
               "    border-radius: 15px;\n" +
               "    position: relative;\n" +
               "    overflow: hidden;\n" +
               "    margin: 20px 0;\n" +
               "}\n" +
               ".progress-fill {\n" +
               "    background-color: #28a745;\n" +
               "    height: 100%;\n" +
               "    transition: width 0.3s ease;\n" +
               "}\n" +
               ".progress-text {\n" +
               "    position: absolute;\n" +
               "    top: 50%;\n" +
               "    left: 50%;\n" +
               "    transform: translate(-50%, -50%);\n" +
               "    font-weight: bold;\n" +
               "}\n" +
               ".results-section {\n" +
               "    background-color: white;\n" +
               "    margin: 20px;\n" +
               "    padding: 20px;\n" +
               "    border-radius: 8px;\n" +
               "    box-shadow: 0 2px 4px rgba(0,0,0,0.1);\n" +
               "}\n" +
               ".results-table {\n" +
               "    width: 100%;\n" +
               "    border-collapse: collapse;\n" +
               "    margin-top: 20px;\n" +
               "}\n" +
               ".results-table th {\n" +
               "    background-color: #f8f9fa;\n" +
               "    padding: 12px;\n" +
               "    text-align: left;\n" +
               "    font-weight: 600;\n" +
               "    border-bottom: 2px solid #dee2e6;\n" +
               "}\n" +
               ".results-table td {\n" +
               "    padding: 12px;\n" +
               "    border-bottom: 1px solid #dee2e6;\n" +
               "}\n" +
               ".results-table tr:hover {\n" +
               "    background-color: #f8f9fa;\n" +
               "}\n" +
               ".status-badge {\n" +
               "    display: inline-block;\n" +
               "    padding: 4px 8px;\n" +
               "    border-radius: 4px;\n" +
               "    font-size: 12px;\n" +
               "    font-weight: 600;\n" +
               "}\n" +
               ".status-badge.passed {\n" +
               "    background-color: #28a745;\n" +
               "    color: white;\n" +
               "}\n" +
               ".status-badge.failed {\n" +
               "    background-color: #dc3545;\n" +
               "    color: white;\n" +
               "}\n" +
               ".error-details {\n" +
               "    font-size: 12px;\n" +
               "}\n" +
               ".error-message {\n" +
               "    color: #dc3545;\n" +
               "    margin-bottom: 5px;\n" +
               "    max-width: 400px;\n" +
               "    overflow: hidden;\n" +
               "    text-overflow: ellipsis;\n" +
               "    white-space: nowrap;\n" +
               "}\n" +
               ".retry-info {\n" +
               "    color: #ffc107;\n" +
               "    font-size: 12px;\n" +
               "}\n" +
               ".success-info {\n" +
               "    color: #28a745;\n" +
               "    font-size: 12px;\n" +
               "}\n" +
               ".footer {\n" +
               "    text-align: center;\n" +
               "    padding: 20px;\n" +
               "    color: #6c757d;\n" +
               "}";
    }
    
    private static String formatDuration(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;
        
        if (minutes > 0) {
            return String.format("%dm %ds", minutes, seconds);
        } else {
            return String.format("%d.%03ds", seconds, millis % 1000);
        }
    }
    
    private static String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
    
    public static void main(String[] args) {
        generateReport();
    }
}
