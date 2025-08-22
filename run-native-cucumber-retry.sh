#!/bin/bash

# Enhanced Native Cucumber Retry Script
# This script works seamlessly with the enhanced CucumberRetryListener
# Usage: ./run-native-cucumber-retry.sh [tags] [maven_command]
# Examples:
#   ./run-native-cucumber-retry.sh @Report
#   ./run-native-cucumber-retry.sh "@Report and @Smoke" "mvn clean test"
#   ./run-native-cucumber-retry.sh @XrayKey=TONIC-7438 "mvn test -Dparallel=true"

set -e

echo "🚀 Starting Enhanced Native Cucumber Retry Test Run"
echo "=================================================="

# Configuration with defaults
MAX_RETRIES=2  # This will be overridden by extract_retry_count
RETRY_COUNT=0
RERUN_FILE="target/rerun.txt"
ENHANCED_RETRY_ENABLED=true

# Function to extract retry count from Maven command or Java config
extract_retry_count() {
    local maven_cmd="$1"
    
    # First, try to get retry count from Maven command
    if [[ "$maven_cmd" == *"-Dretry.count="* ]]; then
        # Use sed instead of grep -o for BSD compatibility
        local retry_count=$(echo "$maven_cmd" | sed -n 's/.*-Dretry\.count=\([0-9]*\).*/\1/p')
        if [[ "$retry_count" =~ ^[0-9]+$ ]] && [ "$retry_count" -gt 0 ]; then
            echo "$retry_count"
            return 0
        fi
    fi
    
    # If no Maven parameter or invalid value, try to read from Java config file
    if [ -f "src/main/resources/config.properties" ]; then
        local java_retry_count=$(grep "^retry.count=" "src/main/resources/config.properties" | cut -d'=' -f2)
        if [[ "$java_retry_count" =~ ^[0-9]+$ ]] && [ "$java_retry_count" -gt 0 ]; then
            echo "$java_retry_count"
            return 0
        fi
    fi
    
    # Default fallback
    echo "3"
}

# Function to validate retry count
validate_retry_count() {
    local count="$1"
    if [ "$count" -lt 1 ]; then
        echo "❌ Error: Retry count must be at least 1. Using default value 3."
        echo "3"
    elif [ "$count" -gt 10 ]; then
        echo "⚠️  Warning: Retry count $count is very high. Consider using a lower value."
        echo "$count"
    else
        echo "$count"
    fi
}

# Function to check enhanced retry configuration
check_enhanced_retry_config() {
    echo "🔍 Checking Enhanced Retry Configuration..."
    
    # Check if CucumberRetryListener is properly configured
    if grep -q "CucumberRetryListener" "src/test/java/com/vrize/runners/TestRunner.java"; then
        echo "✅ CucumberRetryListener is configured in TestRunner"
    else
        echo "❌ Warning: CucumberRetryListener not found in TestRunner"
        ENHANCED_RETRY_ENABLED=false
    fi
    
    # Check if RetryUtility exists
    if [ -f "src/main/java/com/vrize/listeners/RetryUtility.java" ]; then
        echo "✅ RetryUtility class found"
    else
        echo "❌ Warning: RetryUtility class not found"
        ENHANCED_RETRY_ENABLED=false
    fi
    
    echo ""
}

# Default values
DEFAULT_TAGS="@Report"
DEFAULT_MAVEN_CMD="mvn test"

# Parse command line arguments
TAGS=${1:-$DEFAULT_TAGS}
MAVEN_CMD=${2:-$DEFAULT_MAVEN_CMD}

# Function to run tests with enhanced retry support
run_tests() {
    local test_command="$1"
    local retry_attempt="${2:-0}"
    
    echo "📋 Running: $test_command"
    
    # Set enhanced retry system properties
    export CUCUMBER_RETRY_ATTEMPT="$retry_attempt"
    export CUCUMBER_RERUN_FILE="$RERUN_FILE"
    
    # Add enhanced retry properties to Maven command if not already present
    if [[ "$test_command" != *"-Dcucumber.retry.attempt="* ]]; then
        test_command="$test_command -Dcucumber.retry.attempt=$retry_attempt"
    fi
    
    if [[ "$test_command" != *"-Dcucumber.rerun.file="* ]]; then
        test_command="$test_command -Dcucumber.rerun.file=$RERUN_FILE"
    fi
    
    echo "🔧 Enhanced retry properties: attempt=$retry_attempt, rerun_file=$RERUN_FILE"
    eval "$test_command"
}

# Function to build the initial test command with enhanced retry support
build_initial_test_command() {
    local base_cmd=""
    
    if [[ "$MAVEN_CMD" == *"cucumber.filter.tags"* ]]; then
        # If Maven command already contains tags, use it as is
        base_cmd="$MAVEN_CMD"
    else
        # Add tags to the Maven command
        base_cmd="$MAVEN_CMD -Dcucumber.filter.tags=\"$TAGS\""
    fi
    
    # Add enhanced retry properties
    base_cmd="$base_cmd -Dretry.enabled=true -Dretry.immediate.rerun=true"
    
    echo "$base_cmd"
}

# Function to check if rerun file exists and has content (enhanced version)
check_rerun_file() {
    echo "🔍 Checking for rerun file: $RERUN_FILE"
    
    # Wait for Java to finish writing the rerun file
    # Check if file is still being modified
    local max_wait=15
    local wait_count=0
    
    while [ $wait_count -lt $max_wait ]; do
        if [ -f "$RERUN_FILE" ]; then
            # Get file size and modification time
            local current_size=$(stat -f%z "$RERUN_FILE" 2>/dev/null || stat -c%s "$RERUN_FILE" 2>/dev/null || echo "0")
            local current_time=$(stat -f%m "$RERUN_FILE" 2>/dev/null || stat -c%Y "$RERUN_FILE" 2>/dev/null || echo "0")
            
            sleep 1
            
            # Check if file is still being modified
            local new_size=$(stat -f%z "$RERUN_FILE" 2>/dev/null || stat -c%s "$RERUN_FILE" 2>/dev/null || echo "0")
            local new_time=$(stat -f%m "$RERUN_FILE" 2>/dev/null || stat -c%Y "$RERUN_FILE" 2>/dev/null || echo "0")
            
            if [ "$current_size" = "$new_size" ] && [ "$current_time" = "$new_time" ]; then
                # File is stable, no longer being modified
                echo "✅ Rerun file is stable and ready for processing"
                break
            fi
        fi
        
        wait_count=$((wait_count + 1))
        echo "⏳ Waiting for rerun file to stabilize... ($wait_count/$max_wait)"
    done
    
    if [ -f "$RERUN_FILE" ] && [ -s "$RERUN_FILE" ]; then
        local failed_count=$(wc -l < "$RERUN_FILE")
        echo "📄 Rerun file found with $failed_count failed scenarios:"
        echo "----------------------------------------"
        cat "$RERUN_FILE"
        echo "----------------------------------------"
        return 0
    else
        echo "✅ No failed scenarios to retry"
        return 1
    fi
}

# Function to retry failed scenarios with enhanced retry support
retry_failed_scenarios() {
    local max_retries="$1" # Pass max_retries to the function
    local retry_count="$2" # Pass retry_count to the function
    local rerun_file="$3" # Pass rerun_file to the function
    
    if check_rerun_file; then
        retry_count=$((retry_count + 1))
        echo "🔄 Enhanced Retry attempt $retry_count of $max_retries"
        echo "=================================================="
        
        # Run only failed scenarios - IMPORTANT: Use mvn test without clean for retries
        local retry_cmd="${MAVEN_CMD/mvn clean test/mvn test}"
        
        # Preserve all original Maven parameters for retry
        local retry_params=""
        if [[ "$MAVEN_CMD" == *"-D"* ]]; then
            # Extract all -D parameters from original command
            retry_params=$(echo "$MAVEN_CMD" | grep -o -- '-D[^[:space:]]*' | tr '\n' ' ')
        fi
        
        # Enhanced retry properties - IMPORTANT: Disable Java retry during shell script retry
        local enhanced_retry_props="-Dretry.enabled=false -Dretry.immediate.rerun=false"
        
        # Mark the last retry attempt so reporters can attach final artifacts
        local final_flag=""
        if [ $retry_count -ge $max_retries ]; then
            final_flag="-Dfinal.attempt=true -Dcucumber.retry.final=true"
        fi
        
        # Build the complete retry command with enhanced retry support
        local full_retry_cmd="$retry_cmd $retry_params $enhanced_retry_props -Dcucumber.features=@$rerun_file -Dcucumber.retry.attempt=$retry_count $final_flag"
        
        echo "📋 Running enhanced retry with properties: $full_retry_cmd"
        echo "💡 Disabled Java retry during shell script retry to avoid conflicts"
        echo "🎯 Executing failed scenarios from: $rerun_file"
        
        # Execute the retry
        run_tests "$full_retry_cmd" "$retry_count"
        
        # Wait a moment for the retry to complete
        sleep 2
        
        # Check if there are still failed scenarios after retry
        if [ -f "$rerun_file" ] && [ -s "$rerun_file" ]; then
            local current_failed_count=$(wc -l < "$rerun_file")
            echo "📊 Retry attempt $retry_count result: $current_failed_count scenarios still failed"
            
            if [ $retry_count -lt $max_retries ]; then
                echo "⚠️  Some scenarios still failed after retry attempt $retry_count. Will retry again..."
                echo "💡 Enhanced retry will continue with remaining attempts"
                echo ""
                retry_failed_scenarios "$max_retries" "$retry_count" "$rerun_file"
            else
                echo "❌ Maximum retry attempts ($max_retries) reached. Some scenarios still failed."
                echo "📄 Final failed scenarios:"
                cat "$rerun_file"
                echo "💡 These scenarios will be reported to Xray with final failure status"
            fi
        else
            echo "🎉 All scenarios passed after enhanced retry attempt $retry_count!"
            echo "✅ Enhanced retry successfully resolved all failures"
        fi
    fi
}

# Main execution
echo "🔍 Pre-flight checks..."
check_enhanced_retry_config

# Extract and validate retry count from Maven command if present
MAX_RETRIES=$(extract_retry_count "$MAVEN_CMD")
MAX_RETRIES=$(validate_retry_count "$MAX_RETRIES")

echo "🔧 Enhanced Retry Configuration:"
echo "   - Max Retries: $MAX_RETRIES (from Maven command or Java config)"
echo "   - Rerun File: $RERUN_FILE"
echo "   - Tags: $TAGS"
echo "   - Maven Command: $MAVEN_CMD"
echo "   - Enhanced Retry: $([ "$ENHANCED_RETRY_ENABLED" = true ] && echo "✅ ENABLED" || echo "❌ DISABLED")"
echo "   - Immediate Rerun: ✅ ENABLED (creates rerun file after each failure)"
echo "   - System Properties: ✅ ENABLED (automatic cucumber.retry.* properties)"
echo "   - Xray Integration: ✅ ENABLED (automatic reporting suppression during retries)"
echo ""

# Clean up any existing rerun file and aggregated results
if [ -f "$RERUN_FILE" ]; then
    rm "$RERUN_FILE"
    echo "🧹 Cleaned up existing rerun file"
fi

# Clean up aggregated results from previous runs
if [ -f "target/aggregated-results.json" ]; then
    rm "target/aggregated-results.json"
    echo "🧹 Cleaned up previous aggregated results"
fi

# Run initial test with enhanced retry support
echo "🎯 Running initial test with enhanced retry support..."
echo "=================================================="
INITIAL_CMD=$(build_initial_test_command)
run_tests "$INITIAL_CMD" "0"

# Wait for initial test to complete and rerun file to be created
echo "⏳ Waiting for initial test to complete and rerun file to be generated..."
sleep 3

# Check if there are failed scenarios to retry
if check_rerun_file; then
    echo ""
    echo "🔄 Starting enhanced retry process..."
    echo "💡 Enhanced retry will:"
    echo "   - Automatically track retry attempts"
    echo "   - Suppress Xray reporting during retries"
    echo "   - Create rerun files after each failure"
    echo "   - Set system properties for integration"
    echo "   - Execute actual retry runs (not just prepare them)"
    echo ""
    
    # Execute the retry process
    retry_failed_scenarios "$MAX_RETRIES" "$RETRY_COUNT" "$RERUN_FILE"
    
    # Final status check
    echo ""
    echo "🔍 Final status check after all retry attempts..."
    if [ -f "$RERUN_FILE" ] && [ -s "$RERUN_FILE" ]; then
        final_failed_count=$(wc -l < "$RERUN_FILE")
        echo "❌ Final result: $final_failed_count scenarios still failed after all retry attempts"
    else
        echo "✅ Final result: All scenarios passed after retry attempts"
    fi
else
    echo ""
    echo "🎉 All scenarios passed on first attempt!"
    echo "✅ Enhanced retry was ready but not needed"
fi

echo ""
echo "🏁 Enhanced Test Run Completed!"
echo "📊 Summary:"
echo "   - Initial run: ✅ Completed"
echo "   - Retry attempts: $RETRY_COUNT"
echo "   - Final status: $(if [ -f "$RERUN_FILE" ] && [ -s "$RERUN_FILE" ]; then echo "❌ Some failures remain"; else echo "✅ All scenarios passed"; fi)"
echo "   - Enhanced retry: $([ "$ENHANCED_RETRY_ENABLED" = true ] && echo "✅ Fully utilized" || echo "⚠️  Partially utilized")"

# Post-processing: Enhanced reporting
if [[ "$MAVEN_CMD" == *"xray.enabled=true"* ]]; then
    echo ""
    echo "📎 Enhanced Xray Integration:"
    echo "   - Final results will be automatically reported to Xray"
    echo "   - Retry attempts were automatically suppressed during execution"
    echo "   - Only final results (pass/fail) will be sent to Xray"
fi

echo ""
echo "🚀 Enhanced Native Cucumber Retry completed successfully!"
echo "💡 For more information about the enhanced retry functionality,"
echo "   check the CucumberRetryListener logs above."