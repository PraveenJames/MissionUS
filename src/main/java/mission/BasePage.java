package mission;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class BasePage {
    public static WebDriver driver;
    public static WebDriverWait wait;

    public void click(By locator) {
        for (int i = 0; i < 3; i++) {
            try {
                WebElement element = driver.findElement(locator);
                element.click();
                return;
            } catch (StaleElementReferenceException e) {
                System.out.println("Encountered StaleElementReferenceException. Retrying attempt " + (i + 1));

                try { Thread.sleep(500); } catch (InterruptedException ignored) {}
            }
        }

        // If we reach here, all 3 attempts failed
        throw new RuntimeException("Failed to click element after 3 attempts due to stale references: " + locator);
    }

    public void enterText(By locator, String text) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        element.clear();
        element.sendKeys(text);
    }

    public String getText(By locator)
    {
        return wait.until(ExpectedConditions.presenceOfElementLocated(locator)).getText();
    }

    public void handleAlertAccept() {
        WebDriverWait localWait = new WebDriverWait(driver, 3);

        try {
            localWait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
        } catch (TimeoutException e) {
            // This catches the case where the alert never appeared
        } catch (NoAlertPresentException e) {
            // Fallback catch for safety
        }
    }
}
