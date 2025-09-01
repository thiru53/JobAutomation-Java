package runners;

import com.browserstack.local.Local;
import com.google.gson.JsonObject;
import com.microsoft.playwright.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.junit.jupiter.api.*;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class BrowserStackRunner {

    public static final String USER_DIR = "user.dir";
    private static final Logger logger = LoggerFactory.getLogger(BrowserStackRunner.class);
    public static String userName, accessKey;
    public static Map<String, Object> browserStackYamlMap;
    static Playwright playwright;
    static Browser browser;
    static Local bsLocal;
    public BrowserContext context;
    public Page page;

    public BrowserStackRunner() {
        File file = new File(getUserDir() + "/browserstack.yml");
        browserStackYamlMap = convertYamlFileToMap(file, new HashMap<>());
    }

    @BeforeEach
    void launchBrowser() throws Exception {
        logger.info("Starting BrowserStack setup");

        playwright = Playwright.create();
        BrowserType browserType = playwright.chromium();

        String runOnBrowserStack = System.getenv("RUN_ON_BROWSERSTACK");
        if (StringUtils.isBlank(runOnBrowserStack)) {
            runOnBrowserStack = System.getProperty("RUN_ON_BROWSERSTACK", "false");
        }
        boolean isBrowserStack = Boolean.parseBoolean(runOnBrowserStack);
        System.out.println("RUN_ON_BROWSERSTACK : " + runOnBrowserStack);
        logger.info("RUN_ON_BROWSERSTACK: {}", isBrowserStack);

        String userName = System.getenv("BROWSERSTACK_USERNAME");
        System.out.println("BROWSERSTACK_USERNAME : " + userName);

        String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
        System.out.println("BROWSERSTACK_ACCESS_KEY : " + accessKey);
        try {
            if (isBrowserStack) {
                logger.info("Running in BrowserStack mode");
                browserType = playwright.chromium();

                JsonObject jsonCaps = getDefaultCapabilitiesObject();
                jsonCaps.addProperty("browserstack.user", userName);
                jsonCaps.addProperty("browserstack.key", accessKey);

                // Construct WebSocket endpoint
                String capsJson = jsonCaps.toString();
                String wsEndpoint = "wss://cdp.browserstack.com/playwright?caps=" + URLEncoder.encode(capsJson, "UTF-8");
                logger.info("WebSocket endpoint: {}", wsEndpoint);

                // Initialize Playwright and connect to BrowserStack
                logger.info("Initializing Playwright and connecting to BrowserStack");

                browser = browserType.connect(wsEndpoint);
                context = browser.newContext();
                page = context.newPage();
            } else {
                logger.info("Running in Local mode");
                browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
                context = browser.newContext();
                page = context.newPage();
            }
        } catch (Exception e) {
            logger.error("Failed to initialize Playwright or connect to BrowserStack: {}", e.getMessage());
            throw e;
        }
    }


    void closeContext() {
        page.close();
        browser.close();
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

    private String getUserDir() {
        return System.getProperty(USER_DIR);
    }

    private Map<String, Object> convertYamlFileToMap(File yamlFile, Map<String, Object> map) {
        logger.debug("Reading browserstack.yml configuration");
        try {
            InputStream inputStream = Files.newInputStream(yamlFile.toPath());
            Yaml yaml = new Yaml();
            Map<String, Object> config = yaml.load(inputStream);
            logger.debug("Successfully parsed browserstack.yml");
            map.putAll(config);
        } catch (Exception e) {
            logger.error("Failed to read browserstack.yml: {}", e.getMessage());
            throw new RuntimeException(String.format("Malformed browserstack.yml file - %s.", e));
        }
        return map;
    }

    private JsonObject getDefaultCapabilitiesObject() throws Exception {
        JsonObject capabilitiesObject = new JsonObject();
        capabilitiesObject.addProperty("browser", "chrome");
        capabilitiesObject.addProperty("browserstack.source", "java-playwright-browserstack:sample-sdk:v1.0");
        return capabilitiesObject;
    }

    private JsonObject getCapabilitiesObject() throws Exception {

        // Extract BrowserStack credentials and capabilities
        String username = (String) browserStackYamlMap.get("userName");
        String accessKey = (String) browserStackYamlMap.get("accessKey");
        if (StringUtils.isBlank(accessKey)) {
            logger.error("BROWSERSTACK_ACCESS_KEY is not set in browserstack.yml");
            throw new RuntimeException("BROWSERSTACK_ACCESS_KEY is not set in browserstack.yml");
        }
        String projectName = (String) browserStackYamlMap.get("projectName");
        String buildName = (String) browserStackYamlMap.get("buildName");
        String playwrightVersion = (String) browserStackYamlMap.get("client.playwrightVersion");
        boolean browserstackLocal = (boolean) browserStackYamlMap.get("browserstackLocal");
        logger.debug("Extracted credentials: username={}, projectName={}, buildName={}, browserstackLocal={}",
                username, projectName, buildName, browserstackLocal);

        // Extract platform details (first platform in the list)
        @SuppressWarnings("unchecked")
        Map<String, String> platform = ((List<Map<String, String>>) browserStackYamlMap.get("platforms")).get(0);
        String os = platform.get("os");
        String osVersion = platform.get("osVersion");
        String browserName = platform.get("browser");
        String browserVersion = platform.get("browserVersion");
        logger.debug("Platform details: os={}, osVersion={}, browser={}, browserVersion={}", os, osVersion, browserName, browserVersion);

        // Start BrowserStack Local
        if (browserstackLocal) {
            logger.info("Starting BrowserStack Local tunnel");
            bsLocal = new Local();
            HashMap<String, String> bsLocalArgs = new HashMap<>();
            bsLocalArgs.put("key", accessKey);
            bsLocalArgs.put("v", "true"); // Enable verbose logging for BrowserStack Local
            try {
                bsLocal.start(bsLocalArgs);
                if (bsLocal.isRunning()) {
                    logger.info("BrowserStack Local tunnel status: {}", bsLocal.isRunning());
                    logger.debug("BrowserStack Local tunnel status: {}", bsLocal.isRunning());
                } else {
                    logger.error("BrowserStack Local tunnel failed to start");
                    throw new RuntimeException("BrowserStack Local tunnel failed to start");
                }
            } catch (Exception e) {
                logger.error("Failed to start BrowserStack Local: {}", e.getMessage());
                throw new RuntimeException("Failed to start BrowserStack Local: " + e.getMessage());
            }
        }

        String debug = (String) browserStackYamlMap.get("debug");
        String console = (String) browserStackYamlMap.get("console");
        String networkLogs = (String) browserStackYamlMap.get("networkLogs");

        // Build BrowserStack capabilities
        logger.debug("Building BrowserStack capabilities");
        JsonObject capabilitiesObject = new JsonObject();
        capabilitiesObject.addProperty("browser", browserName);
        capabilitiesObject.addProperty("browser_version", browserVersion);
        capabilitiesObject.addProperty("os", os);
        capabilitiesObject.addProperty("os_version", osVersion);
        capabilitiesObject.addProperty("name", "Playwright Local Test");
        capabilitiesObject.addProperty("build", buildName);
        capabilitiesObject.addProperty("project", projectName);
        capabilitiesObject.addProperty("browserstack.local", browserstackLocal);
        capabilitiesObject.addProperty("client.playwrightVersion", playwrightVersion);
        capabilitiesObject.addProperty("browserstack.username", username);
        capabilitiesObject.addProperty("browserstack.accessKey", accessKey);
        capabilitiesObject.addProperty("browserstack.debug", debug);
        capabilitiesObject.addProperty("browserstack.console", console);
        capabilitiesObject.addProperty("browserstack.networkLogs", networkLogs);

        return capabilitiesObject;
    }
}
