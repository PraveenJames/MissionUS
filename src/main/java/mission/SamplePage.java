package mission;

import org.openqa.selenium.support.PageFactory;

public class SamplePage extends BasePage {

    public SamplePage() {
        PageFactory.initElements(driver, this);
    }
}
