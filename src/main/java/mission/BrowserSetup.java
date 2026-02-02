package mission;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.Assert;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class BrowserSetup extends BasePage {

    public static String browser = null;
    private static final String CHROME_WIN = "src\\test\\java\\BrowserDirectory\\chromedriver.exe";
    private static final String EDGE = "src\\test\\java\\BrowserDirectory\\MicrosoftWebDriver.exe";
    private static final String FIREFOX_WIN = "src\\test\\java\\BrowserDirectory\\geckodriver.exe";
    private static final String CHROME_MAC = "src/test/java/BrowserDirectory/chromedriver-Mac";

    /**
     * Browser property location /src/test/java/TestData/TestData.properties
     */


    /**
     * Function for multi browser
     */
    public void selectBrowser() {
        browser = LoadProp.getProperty("Browser");

        if (browser.equalsIgnoreCase("Chrome")) {
            System.setProperty("webdriver.chrome.driver", CHROME_WIN);
            //io.github.bonigarcia.wdm.WebDriverManager.chromedriver().setup();

            // Inside mission.BrowserSetup.selectBrowser()
            ChromeOptions options = new ChromeOptions();

            // This argument disables the "Password Leak" detection feature
            options.addArguments("--disable-features=PasswordLeakDetection");

            // These prevent the browser from offering to save or manage passwords
            Map<String, Object> prefs = new HashMap<String, Object>();
            prefs.put("credentials_enable_service", false);
            prefs.put("profile.password_manager_enabled", false);
            options.setExperimentalOption("prefs", prefs);

            driver = new ChromeDriver(options);
        } else if (browser.equalsIgnoreCase("edge")) {
            //System.setProperty("webdriver.edge.driver", EDGE);
            WebDriverManager.edgedriver().setup();
            driver = new EdgeDriver();
        } else if (browser.equalsIgnoreCase("Firefox")) {
            WebDriverManager.firefoxdriver().setup();
            //System.setProperty("webdriver.gecko.driver", FIREFOX_WIN);
            driver = new FirefoxDriver();
        } else if (browser.equalsIgnoreCase("chromeMac")) {
            //System.setProperty("webdriver.chrome.driver", CHROME_MAC);
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("chromeHeadless")) {
            //System.setProperty("webdriver.chrome.driver", CHROME_MAC);
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.addArguments("--headless");
            WebDriverManager.chromedriver().setup();
            driver = new ChromeDriver(chromeOptions);
        } else if (browser.equalsIgnoreCase("api")) {

        } else {
            Assert.fail(MessageFormat.format("Wrong Browser: {0}", browser));
        }
    }
}
