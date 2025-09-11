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

    public static final String USER_DIR = "user.dir";
    private static final Logger logger = LoggerFactory.getLogger(SeleniumGridRunner.class);
    public static String userName, accessKey;
    public static Map<String, Object> browserStackYamlMap;

    static Playwright playwright;
    static Browser browser;
    static Local bsLocal;
    public BrowserContext context;
    public Page page;

    @BeforeEach
    void launchBrowser() throws Exception {


        // Connect to Selenium Grid
        String gridUrl = System.getenv("SELENIUM_GRID_URL");
        DesiredCapabilities capabilities = new DesiredCapabilities();
        capabilities.setBrowserName("chrome");

        try {
            WebDriver driver = new RemoteWebDriver(new java.net.URL(gridUrl), capabilities);

            // Initialize Playwright
            Playwright playwright = Playwright.create();

            // Connect to Selenium Grid session via CDP (adjust for your setup)
            // browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            browser = playwright.chromium().connectOverCDP("ws://localhost:4444/session");
            context = browser.newContext();
            page = context.newPage();
        } catch (Exception e) {

        } finally {

        }

    }

    @AfterEach
    void tearDown() throws Exception {
        logger.info("Tearing down BrowserStack setup");
        if (Objects.nonNull(page)) {
            page.close();
            logger.debug("Closed Playwright page");
        }
        if (Objects.nonNull(browser)) {
            browser.close();
            logger.debug("Closed Playwright browser");
        }
        if (Objects.nonNull(playwright)) {
            playwright.close();
            logger.debug("Closed Playwright instance");
        }
        if (bsLocal != null && bsLocal.isRunning()) {
            try {
                bsLocal.stop();
                logger.info("Stopped BrowserStack Local tunnel");
            } catch (Exception e) {
                logger.error("Failed to stop BrowserStack Local tunnel: {}", e.getMessage());
                throw e;
            }
        }
        logger.info("Check details at https://automate.browserstack.com/dashboard");
    }


}
