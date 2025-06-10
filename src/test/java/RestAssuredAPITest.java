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
            if(response.asPrettyString() != ""){
                reportHelper.printLogAndReport(test, logger, "response is not empty as expected - " + response.asPrettyString() , "pass");
            }else{
                reportHelper.printLogAndReport(test, logger, "response is empty", "fail");
                sa.assertTrue(false, "response is empty");
            }
            try {
                //validating response contents
                JsonObject jsonObject = JsonParser.parseString(response.asPrettyString()).getAsJsonObject();
                if(jsonObject.get("by").toString() != "" ){
                    reportHelper.printLogAndReport(test, logger, "Author name - " + jsonObject.get("by").toString(), "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "Author name is empty", "fail");
                }
                if(jsonObject.get("descendants").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "descendants - " + jsonObject.get("descendants").toString(), "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "descendants is empty", "fail");
                }
                if(jsonObject.get("id").toString().equals(id)){
                    reportHelper.printLogAndReport(test, logger, "ID matched with response id", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "Id doesn't match Expected - " + id +
                            " Actual - " +jsonObject.get("id").toString() , "fail");
                }
                if(jsonObject.get("kids").getAsJsonArray().size() > 0){
                    reportHelper.printLogAndReport(test, logger, "Child is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "No child available", "fail");
                }
                if(jsonObject.get("score").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "score is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "score is not present", "fail");
                }
                if(jsonObject.get("text").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "text is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "text is not present", "fail");
                }
                if(jsonObject.get("time").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "time is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "time is not present", "fail");
                }
                if(jsonObject.get("title").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "title is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "title is not present", "fail");
                }
                if(jsonObject.get("type").toString().replace("\"","").equals("story")){
                    reportHelper.printLogAndReport(test, logger, "The type should be story", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "The type is not story Expected - story " +
                            "Actual - " + jsonObject.get("type").toString(), "fail");
                }
            }catch(Exception e){
                reportHelper.printLogAndReport(test, logger, e.getMessage() , "fail");
                sa.assertTrue(false, e.getMessage());
            }
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
                reportHelper.printLogAndReport(test, logger, "Status code 404 as expected" , "pass");
            }else{
                reportHelper.printLogAndReport(test, logger, "Status code is not 404 as expected - " + response.getStatusCode() , "pass");
                sa.assertTrue(false, "Status code is not 404 as expected - " + response.getStatusCode());
            }
        }
        sa.assertAll();
    }

    @Test(priority = 2)
    public void getCommentFromItems() {
        SoftAssert sa = new SoftAssert();
        ExtentTest test = report.createTest("Get Comment from Items");
        reportHelper.printLogAndReport(test, logger, "Get Comment from Items" , "info");
        if(fetchCommentId() == 0){
            reportHelper.printLogAndReport(test, logger, "There are no comment from item" , "fail");
            sa.assertTrue(false, "There are comment from item");
        }else{
            String id = String.valueOf(fetchCommentId());
            // Specify the base URL to the RESTful web service
            RestAssured.baseURI = baseURI + itemURI.replace("<id>", id);
            // Get the RequestSpecification of the request to be sent to the server.
            RequestSpecification httpRequest = RestAssured.given();
            // specify the method type (GET) and the parameters if any.
            //In this case the request does not take any parameters
            Response response = httpRequest.request(Method.GET, "");
            if(response.getStatusCode() == 200){
                reportHelper.printLogAndReport(test, logger, "Status code 200 as expected" , "pass");
            }else{
                reportHelper.printLogAndReport(test, logger, "Status code is not 200 as expected" , "fail");
                sa.assertTrue(false, "Status code is " + response.getStatusCode());
            }
            if(response.asPrettyString() != ""){
                reportHelper.printLogAndReport(test, logger, "response is not empty as expected - " + response.asPrettyString() , "pass");
            }else{
                reportHelper.printLogAndReport(test, logger, "response is empty", "fail");
                sa.assertTrue(false, "response is empty");
            }
            try {
                //validating response contents
                JsonObject jsonObject = JsonParser.parseString(response.asPrettyString()).getAsJsonObject();
                if(jsonObject.get("by").toString() != "" ){
                    reportHelper.printLogAndReport(test, logger, "Author name - " + jsonObject.get("by").toString(), "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "Author name is empty", "fail");
                }
                if(jsonObject.get("parent").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "parent - " + jsonObject.get("parent").toString(), "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "parent is empty", "fail");
                }
                if(jsonObject.get("id").toString().equals(id)){
                    reportHelper.printLogAndReport(test, logger, "ID matched with response id", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "Id doesn't match Expected - " + id +
                            " Actual - " +jsonObject.get("id").toString() , "fail");
                }
                if(jsonObject.get("kids").getAsJsonArray().size() > 0){
                    reportHelper.printLogAndReport(test, logger, "Child is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "No child available", "fail");
                }
                if(jsonObject.get("text").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "text is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "text is not present", "fail");
                }
                if(jsonObject.get("time").toString() != ""){
                    reportHelper.printLogAndReport(test, logger, "time is present", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "time is not present", "fail");
                }
                if(jsonObject.get("type").toString().replace("\"","").equals("comment")){
                    reportHelper.printLogAndReport(test, logger, "The type should be comment", "pass");
                }else{
                    reportHelper.printLogAndReport(test, logger, "The type is not comment Expected - comment " +
                            "Actual - " + jsonObject.get("type").toString(), "fail");
                }
            }catch(Exception e){
                reportHelper.printLogAndReport(test, logger, e.getMessage() , "fail");
                sa.assertTrue(false, e.getMessage());
            }
        }
        sa.assertAll();
    }

    @AfterMethod()
    public void afterMethod(){
        report.flush();
    }

    //This method will fetch the top story id
    public int fetchTopStoryId(){
        int topStoryId = 0;
        RestAssured.baseURI = baseURI + topStoriesURI;
        RequestSpecification httpRequest = RestAssured.given();
        Response response = httpRequest.request(Method.GET, "");
        if(response.getStatusCode() == 200){
            if(response.asPrettyString() != ""){
                try{
                    JsonArray jsonArray = JsonParser.parseString(response.asPrettyString()).getAsJsonArray();
                    if(jsonArray.size() > 0){
                        topStoryId = jsonArray.get(0).getAsInt();
                    }
                }catch (Exception e){
                    reportHelper.printLogAndReport(test, logger, e.getMessage(),"fail");
                }

            }
        }
        return topStoryId;
    }

    //This method will fetch the comment id of the top story id
    public int fetchCommentId(){
        int commentId = 0;
        int storyId = fetchTopStoryId();
        if(storyId != 0){
            String id = String.valueOf(storyId);
            RestAssured.baseURI = baseURI + itemURI.replace("<id>", id);
            RequestSpecification httpRequest = RestAssured.given();
            Response response = httpRequest.request(Method.GET, "");
            if(response.getStatusCode() == 200){
                if(response.asPrettyString() != ""){
                    try{
                        JsonObject jsonObject = JsonParser.parseString(response.asPrettyString()).getAsJsonObject();
                        commentId = jsonObject.get("kids").getAsJsonArray().get(0).getAsInt();
                    }catch (Exception e){
                        reportHelper.printLogAndReport(test, logger, e.getMessage(),"fail");
                    }
                }
            }
        }
        return commentId;
    }
}