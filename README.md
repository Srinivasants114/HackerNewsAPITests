# HackerNewsAPITests
# Overview
Used Java as the programming language and Testng for framework and test execution and maven for dependency management

Created tests for the below HackerNewsAPI

1.TopStories API

2.Items API

# Defect 

If we provide invalid id in the Items API, it should throw 404 but it's returning 200

# External Libraries used
1. Rest Assured - Handling requests and response
2. Testng - Test execution
3. Extent report - HTML report
4. Log4j - Logging

# Test Execution and results
Tests can be executed individually or it can executed all together.

Tests can be found under the folder structure 
src/test/java/RestAssuredAPITest

With Testng we can execute each test can be executed by clicking on the run icon next to the method or
Kickoff all the tests by clicking on the run icon next to the class RestAssuredAPITest

After the tests are executed, the html can be found under the folder
src/test/resources
Right click -> Open in -> browser -> chrome

Logs cane be found under logs/app.log

