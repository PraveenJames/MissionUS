package mission;

import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.NoSuchSessionException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Hook extends BasePage {

    BrowserSetup browsersetup = new BrowserSetup();
    private static final int WAIT_SEC = 20;

    public Hook() {

    }

    @Before("@UI")
    public void initializeTest() {
        browsersetup.selectBrowser();
        driver.manage().deleteAllCookies();
        driver.manage().timeouts().pageLoadTimeout(WAIT_SEC, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(WAIT_SEC, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(WAIT_SEC, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, WAIT_SEC);
        new iniClass();
    }

    /**
     * Executed after each UI tagged scenario
     */
    @After("@UI")
    public void screenshot(Scenario scenario) {
        String screenShotFilename = scenario.getName().replace(" ", "")
                + new Timestamp(new Date().getTime()).toString().replaceAll("[^a-zA-Z0-9]", "")
                + "_" + LoadProp.getProperty("Browser") + ".jpg";
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(LoadProp.getProperty("ScreenshotLocation") + screenShotFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }

//        driver.close();
//        //Handling the NoSuchSessionException with Firefox browser after close
//        try {
//            driver.quit();
//        } catch (NoSuchSessionException ex) {
//        }
    }
}

