package base;

import com.microsoft.playwright.*;
import factory.PlaywrightFactory;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PlayWriteBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PlayWriteBaseTest.class);

    protected static Playwright playwright;
    protected static Browser browser;
    protected static BrowserContext context;
    protected static Page page;

    @BeforeAll
    static void setup() {
        logger.info("---------------------------------------------------------------------------------");
        logger.info("Setting up browser env...");
        boolean headless = false;
        String isGitHubActions = System.getenv("GITHUB_ACTIONS");
        if (StringUtils.isNoneBlank(isGitHubActions) && BooleanUtils.isTrue(Boolean.valueOf(isGitHubActions))) {
            headless = true;
        }
        logger.info("RUNNING_IN_GITHUB_ACTIONS : {}", isGitHubActions);
        logger.info("Headless : {}", headless);

        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(headless));
        context = browser.newContext();
        page = context.newPage();
        logger.info("browser launched successfully!");
    }

    @AfterAll
    static void teardown() {
        page.close();
        context.close();
        browser.close();
        playwright.close();
        logger.info("Closed browser and playwright successfully!");
    }
}
