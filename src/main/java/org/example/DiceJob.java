package org.example;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.regex.Pattern;

public class DiceJob {

    public static void main(String[] args) throws InterruptedException {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
            BrowserContext context = browser.newContext();
            Page page = context.newPage();
            page.navigate("https://www.dice.com/dashboard/login");
            page.locator("input[type='email']").fill("tirupathaiah.salla2@gmail.com");
            page.locator("button[data-testid='sign-in-button']").click();

            page.locator("input[type='password']").fill("Thiru@123");
            page.locator("button[data-testid='submit-password']").click();

            page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(30000));

            Thread.sleep(10000);
            Locator jobTitleInputField = page.locator("xpath=//input[contains(@aria-label, 'Job title')]");
            jobTitleInputField.fill("Spring-boot");

            Thread.sleep(1000);
            Locator locationField = page.locator("xpath=//input[contains(@aria-label, 'Location Field')]");
            locationField.fill("United States");

            Thread.sleep(1000);
            page.locator("button[data-testid='job-search-search-bar-search-button']").click();

            // apply filters
            applyFilters(page);

            // Check list of job items
            Thread.sleep(10000);
            boolean isLast = false;
            while (!isLast) {
                List<Locator> jobItems = page.locator("div[data-testid='job-search-results-container'] div[role='listitem']").all();
                for (Locator jobItem : jobItems) {
                    applyToJob(jobItem, context);
                }
                Locator nextLoc = page.locator("nav[aria-label='Pagination']").locator("span[aria-label='Next']");
                if (nextLoc.isVisible() && nextLoc.isEnabled()) {
                    nextLoc.click();
                } else {
                    isLast = true;
                }
            }
        }
    }

    private static void applyFilters(Page page) {
        page.locator("button[type='button']").filter(new Locator.FilterOptions().setHasText("All filters")).click();
        Locator sectionLoc = page.locator("section[aria-label='Drawer content']");
        Locator labelLoc = page.locator("label");

        // Job post features
        labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Easy apply"))).click();

        // Posted date
        labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Today"))).click();

        // Work settings
        labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Remote"))).click();

        // Employment type
        //labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Full time"))).click();

        // Apply filters
        page.locator("button[type='button']").filter(new Locator.FilterOptions().setHasText("Apply filters")).click();
    }

    public static void applyToJob(Locator jobItem, BrowserContext context) throws InterruptedException {
        Locator easyApplyBtn = jobItem.locator("div.header a.outline-offset-2").nth(2);
        if (StringUtils.equalsIgnoreCase(easyApplyBtn.innerText(), "applied")) {
            return;
        }
        Page newTabPage = context.waitForPage(easyApplyBtn::click);
        System.out.println("New tab URL : " + newTabPage.url());

        Locator applyBtn = newTabPage.locator("apply-button");
        String btnText = "";
        if(BooleanUtils.isTrue(applyBtn.isVisible())) {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(500);
                btnText = applyBtn.textContent();
            }

            if (StringUtils.equalsIgnoreCase(btnText, "Easy apply")) {
                applyBtn.click();

                // Continue other steps to complete process
                Locator nextBtn = newTabPage.locator("div.navigation-buttons button.btn-next");

                boolean isSubmit = false;
                int count = 0;
                while (!isSubmit & count < 10) {
                    isSubmit = StringUtils.equalsIgnoreCase(nextBtn.textContent(), "Submit");
                    count++;
                    nextBtn.click();
                }
            } else {
                applyBtn.click();
            }
        }

        System.out.println("New tab Title : " + newTabPage.title()+", Status : applied");

        // Close the page after necessary action done.
        newTabPage.close();
    }
}
