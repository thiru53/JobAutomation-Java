package base;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import factory.PlaywrightFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;
import java.util.Properties;


public class PlayWriteBaseTestBackup {

    private static final Logger logger = LoggerFactory.getLogger(PlayWriteBaseTestBackup.class);
    protected Properties prop;
    protected Page page;
    protected BrowserContext context;
    PlaywrightFactory pf;

    @BeforeEach
    void setup() {
        logger.info("Initializing setUp()...");
        pf = new PlaywrightFactory();

        prop = pf.initProp();
        prop.setProperty("browser", "chrome");

        page = pf.initBrowser(prop);
        context = PlaywrightFactory.getBrowserContext();
    }

    @AfterEach
    public void tearDown() {
        if(Objects.nonNull(page)) {
            page.context().browser().close();
        }
        logger.info("Closed browser()...");
    }
}
