package runners;

import com.browserstack.local.Local;
import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;

public class SeleniumGridRunner {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumGridRunner.class);

    public WebDriver driver;

    @BeforeEach
    void launchBrowser() throws Exception {
        logger.info("Launching Browser....");

        // Connect to Selenium Grid
        String gridUrl = System.getenv("SELENIUM_GRID_URL");
        logger.info("GridUrl : {}", gridUrl);
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("chrome");

        try {
            driver = new RemoteWebDriver(new java.net.URL(gridUrl), capabilities);

            // Navigate to a page
            driver.get("https://www.google.com");
            logger.info("Title : {}",driver.getTitle());

        } catch (Exception e) {
            logger.error("Exception : {}", e.getMessage());
        } finally {

        }

    }

    @AfterEach
    void tearDown() throws Exception {
        logger.info("Tearing down Selenium setup");
        driver.close();
        logger.info("Check details at https://automate.browserstack.com/dashboard");
    }


}
