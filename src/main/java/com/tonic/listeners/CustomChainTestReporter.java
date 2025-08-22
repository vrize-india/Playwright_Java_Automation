package com.tonic.listeners;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.xml.XmlSuite;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class CustomChainTestReporter implements IReporter {
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("[CustomChainTestReporter] generateReport called with " + suites.size() + " suites");
        System.out.println("[CustomChainTestReporter] Output directory: " + outputDirectory);
        System.out.println("[CustomChainTestReporter] Xray enabled: " + com.tonic.listeners.XrayLogger.isXrayEnabled());
        
        String indexPath = "target/chaintest/Index.html";
        if (!java.nio.file.Files.exists(java.nio.file.Paths.get(indexPath))) {
            System.err.println("[Custom Tag Summary] Index.html not found. Skipping tag summary injection.");
            return;
        }
        try {
            String html = new String(Files.readAllBytes(Paths.get(indexPath)), StandardCharsets.UTF_8);
            // Read exclude tag prefixes from properties file
            List<String> excludePrefixes;
            try (InputStream propInput = CustomChainTestReporter.class.getClassLoader().getResourceAsStream("chaintest.properties")) {
                Properties prop = new Properties();
                if (propInput != null) {
                    prop.load(propInput);
                    String excludeProp = prop.getProperty("exclude.tags", "@TONIC,@XrayKey");
                    excludePrefixes = Arrays.asList(excludeProp.split("\\s*,\\s*"));
                } else {
                    excludePrefixes = Arrays.asList("@TONIC", "@XrayKey");
                }
            } catch (IOException e) {
                excludePrefixes = Arrays.asList("@TONIC", "@XrayKey");
            }
            // Debug: print loaded excludePrefixes
            System.out.println("[Custom Tag Summary] Exclude prefixes: " + excludePrefixes);
            // Extract tag rows from the tag-summary table
            Pattern rowPattern = Pattern.compile("<tr>\\s*<td><a [^>]*>(@[^<]+)</a></td>\\s*<td>(\\d+)</td>\\s*<td>(\\d+)</td>\\s*<td>(\\d+)</td>\\s*<td>([^<]+)</td>\\s*</tr>");
            Matcher matcher = rowPattern.matcher(html);
            List<String[]> nonTonicRows = new ArrayList<>();
            while (matcher.find()) {
                String tag = matcher.group(1);
                boolean exclude = false;
                for (String prefix : excludePrefixes) {
                    if (tag.regionMatches(true, 0, prefix, 0, prefix.length())) {
                        exclude = true;
                        break;
                    }
                }
                // Debug: print tag and exclusion result
                System.out.println("[Custom Tag Summary] Tag: " + tag + " | Excluded: " + exclude);
                if (!exclude) {
                    nonTonicRows.add(new String[]{
                            tag,
                            matcher.group(2), matcher.group(3), matcher.group(4), matcher.group(5)
                    });
                }
            }
            // Inject global theme toggle button into the navbar, to the left of the info button
            String navbarButtonHtml = "<button role=\"button\" id=\"toggle-global-theme\" class=\"btn btn-outline-primary smaller\" title=\"Toggle Theme\"><i class=\"bi bi-moon\"></i></button>";
            // Find the navbar right button group
            String navbarDivMarker = "<button role=\"button\" id=\"sys-info\" class=\"btn btn-outline-primary smaller\" title=\"View System Info\">";
            int navbarDivIdx = html.indexOf(navbarDivMarker);
            if (navbarDivIdx != -1) {
                // Insert the toggle button before the info button
                html = html.substring(0, navbarDivIdx) + navbarButtonHtml + "\n" + html.substring(navbarDivIdx);
            }
            // Add JS for theme toggle (after <body> or at end)
            String toggleScript = "<script src='https://code.jquery.com/jquery-3.7.1.min.js'></script>\n"
                + "<script>\n"
                + "$(document).ready(function() {\n"
                + "  $('#toggle-global-theme').on('click', function() {\n"
                + "    var body = $('body');\n"
                + "    var current = body.attr('data-bs-theme');\n"
                + "    if (current === 'dark') { body.attr('data-bs-theme', 'light'); $(this).find('i').removeClass('bi-moon').addClass('bi-brightness-high'); } else { body.attr('data-bs-theme', 'dark'); $(this).find('i').removeClass('bi-brightness-high').addClass('bi-moon'); }\n"
                + "  });\n"
                + "});\n"
                + "</script>\n";
            // Insert the script just after <body> (or at end if not found)
            int bodyIdx = html.indexOf("<body");
            int bodyClose = html.indexOf('>', bodyIdx);
            if (bodyClose != -1) {
                html = html.substring(0, bodyClose + 1) + "\n" + toggleScript + html.substring(bodyClose + 1);
            } else {
                html += toggleScript;
            }

            // Build new HTML table with DataTables.js for advanced filter/sort
            StringBuilder sb = new StringBuilder();
            sb.append("<link rel='stylesheet' href='https://cdn.datatables.net/1.13.6/css/jquery.dataTables.min.css'>");
            sb.append("<div class='card card-custom'><div class='card-header'>Tags (excluding @TONIC*)</div><div class='card-body'>");
            sb.append("<input type='text' id='tag-search-non-tonic' class='form-control mb-2' placeholder='Search tags...'>");
            sb.append("<table id='tag-summary-non-tonic' class='table table-striped display'><thead><tr>");
            sb.append("<th>Tag</th>");
            sb.append("<th>Total</th>");
            sb.append("<th>Passed</th>");
            sb.append("<th>Failed</th>");
            sb.append("<th>Time</th>");
            sb.append("</tr></thead><tbody>");
            for (String[] row : nonTonicRows) {
                sb.append("<tr>");
                for (String cell : row) sb.append("<td>").append(cell).append("</td>");
                sb.append("</tr>");
            }
            sb.append("</tbody></table></div></div>");
            sb.append("<script src='https://cdn.datatables.net/1.13.6/js/jquery.dataTables.min.js'></script>\n");
            sb.append("<script>\n" +
                    "$(document).ready(function() {\n" +
                    "  $('#tag-summary-non-tonic').DataTable({\n" +
                    "    paging: false,\n" +
                    "    info: false,\n" +
                    "    searching: true,\n" +
                    "    ordering: true\n" +
                    "  });\n" +
                    "});\n" +
                    "</script>");
            // Inject into Index.html after the original tag-summary table
            String marker = "</div>\n      <div class=\"border-bottom\"></div>\n    <!-- /tag section -->";
            int insertPos = html.indexOf(marker);
            if (insertPos != -1) {
                String newHtml = html.substring(0, insertPos + marker.length()) + "\n" + sb.toString() + html.substring(insertPos + marker.length());
                Files.write(Paths.get(indexPath), newHtml.getBytes(StandardCharsets.UTF_8));
                System.out.println("[Custom Tag Summary] Injected non-TONIC tag table into Index.html");
            } else {
                System.err.println("[Custom Tag Summary] Could not find tag section marker in Index.html");
            }
            System.out.println("[Custom Tag Summary] tag-summary-non-tonic table generated with " + nonTonicRows.size() + " rows.");
        } catch (Exception e) {
            System.err.println("[Custom Tag Summary] Error generating tag-summary-non-tonic table: " + e.getMessage());
        }
        
        // Smart report attachment logic that works for both single-run and retry scenarios
        try {
            if (com.tonic.listeners.XrayLogger.isXrayEnabled()) {
                System.out.println("[XRAY] Xray is enabled, checking execution type...");
                
                // Check if this is a single run (no retry script involved)
                boolean isSingleRun = !isRetryScriptExecution();
                
                if (isSingleRun) {
                    // For single runs, attach report immediately
                    System.out.println("[XRAY] Single run detected. Attaching report immediately.");
                    com.tonic.listeners.XrayLogger.attachReportToTestExecution("target/chaintest/Index.html");
                    System.out.println("[XRAY] Report attachment completed for single run.");
                } else {
                    // For retry scenarios, let FinalReportAttacher handle it
                    System.out.println("[XRAY] Retry scenario detected. Report attachment deferred to post-processing.");
                }
            } else {
                System.out.println("[XRAY] Xray is not enabled, skipping report attachment.");
            }
        } catch (Exception e) {
            System.err.println("[XRAY] Error during report attachment: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Detects if this execution is part of a retry script scenario
     */
    private boolean isRetryScriptExecution() {
        try {
            // Check if we're running with cucumber retry parameters (most reliable indicator)
            String cucumberRetryAttempt = System.getProperty("cucumber.retry.attempt");
            if (cucumberRetryAttempt != null && !cucumberRetryAttempt.isEmpty()) {
                System.out.println("[XRAY] Detected retry scenario via cucumber.retry.attempt: " + cucumberRetryAttempt);
                return true;
            }
            
            // Check if this is a final attempt
            String finalAttempt = System.getProperty("final.attempt");
            if ("true".equals(finalAttempt)) {
                System.out.println("[XRAY] Detected final retry attempt");
                return true;
            }
            
            // For single runs, we should always attach the report
            System.out.println("[XRAY] No retry indicators found - treating as single run");
            return false;
            
        } catch (Exception e) {
            System.err.println("[XRAY] Error detecting retry scenario: " + e.getMessage());
            // If we can't determine, assume it's a single run
            return false;
        }
    }
}