package automation.helper;

import com.aventstack.extentreports.ExtentTest;
import org.apache.logging.log4j.Logger;

public class ReportHelper {
    //Logging and reporting using extent report and log4j
    public void printLogAndReport(
            ExtentTest test,
            Logger logger,
            String  stepDetails,
            String logLevel){
        switch(logLevel) {
            case "info":
                test.info(stepDetails);
                logger.info(stepDetails);
                break;
            case "pass":
                test.pass(stepDetails);
                logger.info(stepDetails);
                break;
            case "fail":
                test.fail(stepDetails);
                logger.error(stepDetails);
                break;
            default:
                test.info(stepDetails);
                logger.info(stepDetails);
                break;
        }
    }
}

