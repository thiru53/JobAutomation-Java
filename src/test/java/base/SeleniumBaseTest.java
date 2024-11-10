package base;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Duration;
import java.util.Objects;

public class SeleniumBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(SeleniumBaseTest.class);

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
            if (Objects.nonNull(gridUrl)) {


                logger.info("Initialize RemoteWebDriver...");
                driver = new RemoteWebDriver(new java.net.URL(gridUrl), capabilities);
                logger.info("Connected to Selenium Grid successfully!");
            } else {
                logger.info("Initialize Local WebDriver...");
                // Automatically setup the ChromeDriver
                WebDriverManager.chromedriver().setup();

                // Create a new instance of the ChromeDriver
                driver = new ChromeDriver();
                logger.info("Connected to Local env successfully!");
            }
            driver.manage().window().maximize();
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));

            // Navigate to a page
            driver.get("https://www.google.com");
            logger.info("Title : {}", driver.getTitle());

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

    private static boolean isGridAccessible(String gridUrl) {
        try {
            // Create URL object for the Selenium Grid
            URL url = new URL(gridUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000); // 5 seconds timeout
            connection.setReadTimeout(5000);    // 5 seconds read timeout

            // Connect to the Grid
            connection.connect();
            int responseCode = connection.getResponseCode();

            // Check if response code is 200 (OK)
            if (responseCode == 200) {
                System.out.println("Selenium Grid is accessible at " + gridUrl);
                return true;
            } else {
                System.err.println("Selenium Grid returned unexpected response code: " + responseCode);
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to connect to Selenium Grid at " + gridUrl + ": " + e.getMessage());
            return false;
        }
    }
}
