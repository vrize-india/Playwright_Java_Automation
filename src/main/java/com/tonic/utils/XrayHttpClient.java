package com.tonic.utils;

import com.tonic.constants.XrayConstants;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/**
 * Centralized HTTP client for Xray and Jira API operations.
 * Eliminates code duplication and provides consistent error handling.
 */
public class XrayHttpClient {
    private static final Logger logger = LoggerFactory.getLogger(XrayHttpClient.class);
    
    private final String baseUrl;
    private final String authToken;
    private final String authType; // "Bearer" or "Basic"
    
    public XrayHttpClient(String baseUrl, String authToken, String authType) {
        this.baseUrl = baseUrl;
        this.authToken = authToken;
        this.authType = authType;
    }
    
    public XrayHttpClient(String baseUrl, String authToken) {
        this(baseUrl, authToken, "Bearer");
    }
    
    /**
     * Performs a POST request with JSON payload.
     * @param endpoint The API endpoint (relative to baseUrl)
     * @param payload The JSON payload to send
     * @return HttpResponse object containing status and response body
     * @throws IOException if the request fails
     */
    public HttpResponse post(String endpoint, JSONObject payload) throws IOException {
        return post(endpoint, payload.toString(), XrayConstants.CONTENT_TYPE_JSON);
    }
    
    /**
     * Performs a POST request with custom content type.
     * @param endpoint The API endpoint (relative to baseUrl)
     * @param payload The payload to send
     * @param contentType The content type header
     * @return HttpResponse object containing status and response body
     * @throws IOException if the request fails
     */
    public HttpResponse post(String endpoint, String payload, String contentType) throws IOException {
        URL url = buildUrl(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", contentType);
            conn.setRequestProperty("Authorization", authType + " " + authToken);
            conn.setDoOutput(true);
            
            // Send payload
            if (payload != null && !payload.isEmpty()) {
                try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                    wr.write(payload.getBytes(StandardCharsets.UTF_8));
                }
            }
            
            return readResponse(conn);
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Performs a GET request.
     * @param endpoint The API endpoint (relative to baseUrl)
     * @return HttpResponse object containing status and response body
     * @throws IOException if the request fails
     */
    public HttpResponse get(String endpoint) throws IOException {
        URL url = buildUrl(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Authorization", authType + " " + authToken);
            conn.setRequestProperty("Accept", XrayConstants.ACCEPT_JSON);
            
            return readResponse(conn);
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Performs a POST request with multipart form data for file uploads.
     * @param endpoint The API endpoint (relative to baseUrl)
     * @param file The file to upload
     * @param boundary The multipart boundary
     * @return HttpResponse object containing status and response body
     * @throws IOException if the request fails
     */
    public HttpResponse postMultipart(String endpoint, File file, String boundary) throws IOException {
        URL url = buildUrl(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", XrayConstants.CONTENT_TYPE_MULTIPART.replace("{}", boundary));
            conn.setRequestProperty("Authorization", authType + " " + authToken);
            conn.setRequestProperty(XrayConstants.X_ATLASSIAN_TOKEN, XrayConstants.X_ATLASSIAN_TOKEN_VALUE);
            conn.setDoOutput(true);
            
            // Send multipart data
            try (OutputStream outputStream = conn.getOutputStream();
                 FileInputStream inputStream = new FileInputStream(file)) {
                
                String fileName = file.getName();
                
                // Write multipart boundary and headers
                outputStream.write(("--" + boundary + XrayConstants.LINE_FEED).getBytes(StandardCharsets.UTF_8));
                outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + XrayConstants.LINE_FEED).getBytes(StandardCharsets.UTF_8));
                outputStream.write(("Content-Type: " + XrayConstants.DEFAULT_CONTENT_TYPE + XrayConstants.LINE_FEED + XrayConstants.LINE_FEED).getBytes(StandardCharsets.UTF_8));
                
                // Write file content
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                
                // Write closing boundary
                outputStream.write((XrayConstants.LINE_FEED + "--" + boundary + "--" + XrayConstants.LINE_FEED).getBytes(StandardCharsets.UTF_8));
            }
            
            return readResponse(conn);
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Performs a POST request with multipart form data for video uploads.
     * @param endpoint The API endpoint (relative to baseUrl)
     * @param videoFile The video file to upload
     * @param boundary The multipart boundary
     * @return HttpResponse object containing status and response body
     * @throws IOException if the request fails
     */
    public HttpResponse postVideoMultipart(String endpoint, File videoFile, String boundary) throws IOException {
        URL url = buildUrl(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        
        try {
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", XrayConstants.CONTENT_TYPE_MULTIPART.replace("{}", boundary));
            conn.setRequestProperty("Authorization", authType + " " + authToken);
            conn.setRequestProperty(XrayConstants.X_ATLASSIAN_TOKEN, XrayConstants.X_ATLASSIAN_TOKEN_VALUE);
            conn.setDoOutput(true);
            
            try (OutputStream output = conn.getOutputStream();
                 FileInputStream inputStream = new FileInputStream(videoFile)) {
                
                String fileName = videoFile.getName();
                
                // Write multipart preamble
                String preamble = "--" + boundary + XrayConstants.LINE_FEED +
                        "Content-Disposition: form-data; name=\"file\"; filename=\"" + fileName + "\"" + XrayConstants.LINE_FEED +
                        "Content-Type: " + XrayConstants.VIDEO_CONTENT_TYPE + XrayConstants.LINE_FEED + XrayConstants.LINE_FEED;
                output.write(preamble.getBytes(StandardCharsets.UTF_8));
                
                // Copy video file content
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, bytesRead);
                }
                
                // Write closing boundary
                output.write(("\r\n--" + boundary + "--\r\n").getBytes(StandardCharsets.UTF_8));
                output.flush();
            }
            
            return readResponse(conn);
        } finally {
            conn.disconnect();
        }
    }
    
    /**
     * Builds the full URL for the given endpoint.
     * @param endpoint The relative endpoint
     * @return The complete URL
     * @throws IOException if the URL is malformed
     */
    private URL buildUrl(String endpoint) throws IOException {
        String fullUrl = baseUrl;
        if (!fullUrl.endsWith("/") && !endpoint.startsWith("/")) {
            fullUrl += "/";
        }
        fullUrl += endpoint;
        return new URL(fullUrl);
    }
    
    /**
     * Reads the response from the HTTP connection.
     * @param conn The HTTP connection
     * @return HttpResponse object
     * @throws IOException if reading fails
     */
    private HttpResponse readResponse(HttpURLConnection conn) throws IOException {
        int responseCode = conn.getResponseCode();
        StringBuilder response = new StringBuilder();
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(
                responseCode >= 400 ? conn.getErrorStream() : conn.getInputStream()))) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
        }
        
        return new HttpResponse(responseCode, response.toString());
    }
    
    /**
     * Generates a unique multipart boundary.
     * @return A unique boundary string
     */
    public static String generateBoundary() {
        return XrayConstants.BOUNDARY_PREFIX + UUID.randomUUID().toString().replace("-", "");
    }
    
    /**
     * HTTP response wrapper class.
     */
    public static class HttpResponse {
        private final int statusCode;
        private final String body;
        
        public HttpResponse(int statusCode, String body) {
            this.statusCode = statusCode;
            this.body = body;
        }
        
        public int getStatusCode() {
            return statusCode;
        }
        
        public String getBody() {
            return body;
        }
        
        public boolean isSuccess() {
            return statusCode >= 200 && statusCode < 300;
        }
        
        public boolean isClientError() {
            return statusCode >= 400 && statusCode < 500;
        }
        
        public boolean isServerError() {
            return statusCode >= 500;
        }
        
        @Override
        public String toString() {
            return "HttpResponse{statusCode=" + statusCode + ", body='" + body + "'}";
        }
    }
}

