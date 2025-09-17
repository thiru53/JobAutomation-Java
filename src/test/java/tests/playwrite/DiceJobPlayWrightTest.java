package tests.playwrite;

import base.PlayWriteBaseTest;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class DiceJobPlayWrightTest extends PlayWriteBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DiceJobPlayWrightTest.class);

    @Test
    void diceJobTest() throws InterruptedException {
        logger.info("Starting test: DiceJobTest");
        try {

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
            Map<String,String> filterMap = getFilter1();
            applyFilters(filterMap);

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
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            context.close();
            page.close();

        }
    }

    private static void applyFilters(Map<String, String> filterMap) {
        logger.info("Applying following Filters");
        page.locator("button[type='button']").filter(new Locator.FilterOptions().setHasText("All filters")).click();
        Locator sectionLoc = page.locator("section[aria-label='Drawer content']");
        Locator labelLoc = page.locator("label");

        filterMap.forEach((k, v) -> {
            labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile(v))).click();
            logger.info("\t {} : {}", k, v);
        });
        // Apply filters
        page.locator("button[type='button']").filter(new Locator.FilterOptions().setHasText("Apply filters")).click();
    }

    public static void applyToJob(Locator jobItem, BrowserContext context) throws InterruptedException {
        logger.info("---------------------------------------------------------------------------------");
        Locator titleEle = jobItem.locator("a[data-testid='job-search-job-detail-link']");
        String jobTitle = titleEle.innerText();
        logger.info("Applying Job : {}", jobTitle);

        Locator easyApplyBtn = jobItem.locator("//span[div[a[contains(@href, '/job-detail/')]]]");
        if (StringUtils.equalsIgnoreCase(easyApplyBtn.innerText(), "applied")) {
            logger.info("Job : {}, Status : {}", jobTitle, "Already Applied");
            return;
        }
        Page newTabPage = context.waitForPage(easyApplyBtn::click);
        System.out.println("New tab URL : " + newTabPage.url());

        Locator applyBtn = newTabPage.locator("apply-button");

        while (!applyBtn.isVisible()) {
            Thread.sleep(500);
        }

        String btnText = applyBtn.innerText();
        System.out.println("Button Text : " + btnText);
        if (StringUtils.equalsIgnoreCase(btnText, "Easy apply")) {
            System.out.println("Its a " + btnText);
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
            System.out.println("oops, Its not a " + btnText);
            applyBtn.click();
        }


        System.out.println("New tab Title : " + newTabPage.title() + ", Status : applied");
        logger.info("Job : {}, Status : {}", newTabPage.title(), "Applied");
        // Close the page after necessary action done.
        newTabPage.close();
    }

    private static Map<String, String> getFilter1() {
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("JobPost", "Easy apply");
        filterMap.put("PostedDate", "Today");
        filterMap.put("WorkSetting", "Remote");
        filterMap.put("EmploymentType", "Full time");
        return filterMap;
    }

    private static Map<String, String> getFilter2() {
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("JobPost", "Easy apply");
        filterMap.put("PostedDate", "Today");
        filterMap.put("WorkSetting", "Remote");
        filterMap.put("EmploymentType", "Full time");
        return filterMap;
    }

}
