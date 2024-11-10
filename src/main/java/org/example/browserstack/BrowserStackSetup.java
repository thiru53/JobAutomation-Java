package org.example.browserstack;

import com.microsoft.playwright.*;
import com.browserstack.local.Local;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.net.URLEncoder;

public class BrowserStackSetup {
    private static Playwright playwright;
    private static Browser browser;
    private static Local bsLocal;
    private static Page page;

    public static void init() throws Exception {
        // Start BrowserStack Local
        bsLocal = new Local();
        String isBrowserStack = System.getenv("RUN_ON_BROWSERSTACK");
        isBrowserStack ="true";
        if (StringUtils.equalsIgnoreCase(isBrowserStack, "true")) {
            String accessKey = System.getenv("BROWSERSTACK_ACCESS_KEY");
            accessKey="cd3DjczVmaNn9BpchxXa";
            //bsLocal.start("--key " + accessKey);

            // Get Playwright version (match your local version)
            String playwrightVersion = "1.48.0"; // Replace with your Playwright version

            // BrowserStack capabilities
            JsonObject capabilitiesObject = new JsonObject();
            capabilitiesObject.addProperty("browser", "chrome");
            capabilitiesObject.addProperty("browser_version", "latest");
            capabilitiesObject.addProperty("os", "os x");
            capabilitiesObject.addProperty("os_version", "ventura");
            capabilitiesObject.addProperty("name", "Playwright Local Test");
            capabilitiesObject.addProperty("build", "playwright-java-local");
            capabilitiesObject.addProperty("project", "My Project");
            capabilitiesObject.addProperty("browserstack.local", "true"); // Enable local testing
            capabilitiesObject.addProperty("client.playwrightVersion", playwrightVersion);
            capabilitiesObject.addProperty("browserstack.username", System.getenv("BROWSERSTACK_USERNAME"));
            capabilitiesObject.addProperty("browserstack.accessKey", accessKey);

            String capsJson = capabilitiesObject.toString();
            String wsEndpoint = "wss://cdp.browserstack.com/playwright?caps=" + URLEncoder.encode(capsJson, "UTF-8");

            // Connect to BrowserStack
            playwright = Playwright.create();
            browser = playwright.chromium().connect(wsEndpoint);
        } else {
            // Local setup
            playwright = Playwright.create();
            //browser = playwright.chromium().launch();
            browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        }
        page = browser.newPage();
    }

    public static Page getPage() {
        return page;
    }

    public static void tearDown() throws Exception {
        if (page != null) page.close();
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
        if (bsLocal != null && bsLocal.isRunning()) bsLocal.stop();
    }
}
