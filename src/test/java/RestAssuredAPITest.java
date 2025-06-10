import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import automation.helper.ReportHelper;
import org.testng.asserts.SoftAssert;

public class RestAssuredAPITest {

    //Initalization
    static ExtentTest test;
    static ExtentReports report;
    static Logger logger;
    ReportHelper reportHelper = new ReportHelper();
    String baseURI = "https://hacker-news.firebaseio.com";
    String topStoriesURI = "/v0/topstories.json";
    String itemURI = "/v0/item/<id>.json?print=pretty";

    @BeforeSuite
    public void beforeSuite(){
        long time = System.currentTimeMillis();
        //Create an Instance for Extent Reports
        report = new ExtentReports();
        //Create an Instance for Logger
        logger = LogManager.getLogger();
        // Create Instance for Extent HTML reporter and provide the path for html file
        ExtentSparkReporter htmlReporter = new ExtentSparkReporter("src/test/resources/ExtendReportResults" + time + ".html");

        // Attach the reporter to the Extent Reports instance
        report.attachReporter(htmlReporter);
    }

    @Test(priority = 0)
    public void getTopStories() {
        SoftAssert sa = new SoftAssert();
        ExtentTest test = report.createTest("Get Top Stories");
        reportHelper.printLogAndReport(test, logger, "Get Top Stories" , "info");
        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = baseURI + topStoriesURI;
        reportHelper.printLogAndReport(test, logger, "The request URI is " +  baseURI + topStoriesURI , "info");
        // Get the RequestSpecification of the request to be sent to the server.
        RequestSpecification httpRequest = RestAssured.given();
        // specify the method type (GET) and the parameters if any.
        //In this case the request does not take any parameters
        Response response = httpRequest.request(Method.GET, "");
        // Validate the status code as 200
        if(response.getStatusCode() == 200){
            reportHelper.printLogAndReport(test, logger, "Status code 200 as expected" , "pass");
        }else{
            reportHelper.printLogAndReport(test, logger, "Status code is " + response.getStatusCode() , "fail");
            sa.assertTrue(false, "Status code is " + response.getStatusCode());
        }

        //Validate the response is not empty
        if(response.asPrettyString() != ""){
            reportHelper.printLogAndReport(test, logger, "response is not empty as expected - " + response.asPrettyString() , "pass");
        }else{
            reportHelper.printLogAndReport(test, logger, "response is empty", "fail");
            sa.assertTrue(false, "response is empty");
        }
        JsonArray jsonArray = JsonParser.parseString(response.asPrettyString()).getAsJsonArray();
        if(jsonArray.size() > 0){
            reportHelper.printLogAndReport(test, logger, "response has one or more top stories", "pass");
        }else{
            reportHelper.printLogAndReport(test, logger, "response doesn't have any top stories", "fail");
            sa.assertTrue(false, "response doesn't have any top stories");
        }
        sa.assertAll();

    }

    @Test(priority = 1)
    public void getItemsFromTopStories() {
        SoftAssert sa = new SoftAssert();
        ExtentTest test = report.createTest("Get Items from Top Stories");
        reportHelper.printLogAndReport(test, logger, "Get Items from Top Stories" , "info");
        if(fetchTopStoryId() == 0){
            reportHelper.printLogAndReport(test, logger, "There are no top stories" , "fail");
            sa.assertTrue(false, "There are no top stories");
        }else {
            String id = String.valueOf(fetchTopStoryId());
            // Specify the base URL to the RESTful web service
            RestAssured.baseURI = baseURI + itemURI.replace("<id>", id);
            // Get the RequestSpecification of the request to be sent to the server.
            RequestSpecification httpRequest = RestAssured.given();
            // specify the method type (GET) and the parameters if any.
            //In this case the request does not take any parameters
            Response response = httpRequest.request(Method.GET, "");
            // Validate the status code as 200
            if (response.getStatusCode() == 200) {
                reportHelper.printLogAndReport(test, logger, "Status code 200 as expected" , "pass");
            } else {
                reportHelper.printLogAndReport(test, logger, "Status code is not 200 as expected" , "fail");
                sa.assertTrue(false, "Status code is " + response.getStatusCode());
            }
            JsonObject jsonObject = JsonParser.parseString(response.asPrettyString()).getAsJsonObject();

            //jsonObject.get("kids").getAsJsonArray().get(0)
            System.out.println(response.asPrettyString());
        }
        sa.assertAll();
    }

    @Test(priority = 1)
    public void getInvalidItems() {
        SoftAssert sa = new SoftAssert();
        ExtentTest test = report.createTest("Get Invalid Item");
        reportHelper.printLogAndReport(test, logger, "Get Invalid Item" , "info");
        int itemId = fetchTopStoryId();
        if(itemId == 0){
            reportHelper.printLogAndReport(test, logger, "There are no top stories" , "fail");
            sa.assertTrue(false, "There are no top stories");
        }else{
            String id = String.valueOf(itemId);
            RestAssured.baseURI = baseURI + itemURI.replace("<id>",id + "0");
            RequestSpecification httpRequest = RestAssured.given();
            Response response = httpRequest.request(Method.GET, "");
            if(response.getStatusCode() == 404){
                test.pass("Status code 404 as expected");
            }else{
                test.fail("Status code is not 404 as expected - " + response.getStatusCode());
                sa.assertTrue(false, "Status code is not 404 as expected - " + response.getStatusCode());
            }
        }
        sa.assertAll();
    }

    @Test(priority = 2)
    public void getComment() {
        ExtentTest test = report.createTest("Get Comment");
        logger.info("Get Comment");
        // Specify the base URL to the RESTful web service
        RestAssured.baseURI = baseURI + "/v0/item/2921983.json?print=pretty";
        // Get the RequestSpecification of the request to be sent to the server.
        RequestSpecification httpRequest = RestAssured.given();
        // specify the method type (GET) and the parameters if any.
        //In this case the request does not take any parameters
        Response response = httpRequest.request(Method.GET, "");
        // Print the status and message body of the response received from the server
        if(response.getStatusCode() ==200){
            test.pass("Status code 200 as expected");
        }else{
            test.fail("Status code is not 200 as expected");
        }
        System.out.println("Status received => " + response.getStatusLine());
        System.out.println("Response=>" + response.prettyPrint());
    }

    @AfterMethod()
    public void afterMethod(){
        report.flush();
    }

    public int fetchTopStoryId(){
        int topStoryId = 0;
        RestAssured.baseURI = baseURI + topStoriesURI;
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.request(Method.GET, "");
        if(response.getStatusCode() ==200){
            if(response.asPrettyString() != ""){
                JsonArray jsonArray = JsonParser.parseString(response.asPrettyString()).getAsJsonArray();
                if(jsonArray.size() > 0){
                    topStoryId = jsonArray.get(0).getAsInt();
                }
            }
        }
        return topStoryId;
    }
}