package mission;

import cucumber.api.DataTable;
import cucumber.api.java.en.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class UiStepDefinition extends BasePage {
    private final By userNameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By appLogo = By.cssSelector("div[class='app_logo']");
    private final By shoppingCartBadge = By.cssSelector("span[data-test='shopping-cart-badge']");
    private final By shoppingCartButton = By.cssSelector("a[data-test='shopping-cart-link']");
    private final By cartTitle = By.cssSelector("span[data-test='title']");
    private final By itemQuantityField = By.cssSelector("div[data-test='item-quantity']");
    private final By itemPriceField = By.cssSelector("div[data-test='inventory-item-price']");
    private final By summarySubTotalField = By.cssSelector("div[data-test='subtotal-label']");
    private final By taxLabel = By.cssSelector("div[data-test='tax-label']");

    @And("^I login in with the following details$")
    public void iLoginWithDetails(DataTable dt) {
        Map<String, String> data = dt.asMaps(String.class, String.class).get(0);
        enterText(userNameField, data.get("userName"));
        enterText(passwordField, data.get("Password"));
        click(loginButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(appLogo));
        handleAlertAccept();
    }

    @And("^I add the following items to the basket$")
    public void iAddItemsToBasket(List<String> items) {
        for (String item : items) {
            By addToCartButton = By.cssSelector("button[data-test='add-to-cart-" + item.toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + "']");
            click(addToCartButton);
        }
    }

    @And("^I should see (\\d+) items added to the shopping cart$")
    public void iShouldSeeItemsInCart(int count) {
        String actualCount = getText(shoppingCartBadge);
        Assert.assertEquals(Integer.parseInt(actualCount), count, "Cart count mismatch!");
    }

    @And("^I click on the shopping cart$")
    public void iClickOnCart() {
        click(shoppingCartButton);
        wait.until(ExpectedConditions.presenceOfElementLocated(cartTitle));
    }

    @And("^I verify that the QTY count for each item should be 1$")
    public void iVerifyQtyCount() {
        List<WebElement> quantities = driver.findElements(itemQuantityField);
        for (WebElement qty : quantities) {
            Assert.assertEquals(qty.getText(), "1", "Quantity is not 1 for an item in the cart.");
        }
    }

    @And("^I remove the following item:$")
    public void iRemoveItem(List<String> items) {
        for (String item : items) {
            By removeButton = By.cssSelector("button[data-test='remove-" + item.toLowerCase(Locale.ROOT)
                    .replace(" ", "-") + "']");
            click(removeButton);
        }
    }

    @And("^I click on the (CHECKOUT|CONTINUE) button$")
    public void iClickButton(String buttonName) {
        By button = By.cssSelector("*[name*='" + buttonName.toLowerCase(Locale.ROOT) + "']");
        click(button);
    }

    @And("^I type \"([^\"]*)\" for (.*)$")
    public void iTypeInfo(String value, String field) {
        // Mapping feature file descriptions to actual HTML IDs
        String id = field.toLowerCase().contains("first") ? "first-name" :
                field.toLowerCase().contains("last") ? "last-name" : "postal-code";
        By inputField = By.id(id);
        enterText(inputField, value);
    }

    @Then("^Item total will be equal to the total of items on the list$")
    public void iVerifyItemTotal() {
        List<WebElement> prices = driver.findElements(itemPriceField);
        double sum = 0;
        for (WebElement p : prices) {
            sum += Double.parseDouble(p.getText().replace("$", ""));
        }

        String totalText = getText(summarySubTotalField);
        double displayedTotal = Double.parseDouble(totalText.split("\\$")[1]);
        Assert.assertEquals(sum, displayedTotal, "Subtotal calculation mismatch.");
    }

    @And("^a Tax rate of (\\d+) % is applied to the total$")
    public void iVerifyTax(int taxRate) {
        String subtotalText = getText(summarySubTotalField);
        double subtotal = Double.parseDouble(subtotalText.split("\\$")[1]);

        String taxText = getText(taxLabel);
        double actualTax = Double.parseDouble(taxText.split("\\$")[1]);

        double expectedTax = Math.round((subtotal * (taxRate / 100.0)) * 100.0) / 100.0;
        Assert.assertEquals(actualTax, expectedTax, 0.02, "Tax amount is incorrect.");
    }
}