#!/bin/bash

# Run Maven tests with regression tag and parallelism
mvn clean test -Dcucumber.filter.tags="@regression" -Ddataproviderthreadcount=2

# Run the custom tag summary injector after the report is generated
java -cp target/test-classes com.vrize.util.NonTonicTagSummaryInjector

echo -e "\n[INFO] Tests and custom report post-processing complete. Open target/chaintest/Index.html to view the updated report." 