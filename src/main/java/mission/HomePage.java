package mission;

import org.openqa.selenium.WebDriver;

public class HomePage extends BasePage {
    public static void homePage() {
        driver.get(LoadProp.getProperty("url"));
    }
}
