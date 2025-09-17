package tests.playwrite;

import base.PlayWriteBaseTest;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;

public class DiceJobPlayWrightTest extends PlayWriteBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DiceJobPlayWrightTest.class);

    @Test
    void diceJobTest() throws InterruptedException {
        logger.info("Starting test: DiceJobTest : Playwright");
        try {

            page.navigate("https://www.dice.com/dashboard/login");
            logger.info("Opened website url");

            page.locator("input[type='email']").fill("tirupathaiah.salla2@gmail.com");
            page.locator("button[data-testid='sign-in-button']").click();
            logger.info("Entered Email");

            page.locator("input[type='password']").fill("Thiru@123");
            page.locator("button[data-testid='submit-password']").click();
            logger.info("Entered Password");

            ElementHandle profileEle = page.waitForSelector("div[data-testid='profile-overview']");
            Assert.assertTrue(profileEle.isVisible(), "LoggedIn failed");
            logger.info("Successfully loggedIn!");

            Thread.sleep(10000);
            Locator jobTitleInputField = page.locator("input[name='q']");
            jobTitleInputField.fill("Spring-boot");

            Thread.sleep(1000);
            Locator locationField = page.locator("input[name='location']");
            locationField.fill("United States");

            Thread.sleep(1000);
            page.locator("button[data-testid='job-search-search-bar-search-button']").click();

            // Apply Filter and apply jobs
            filterAndApplyToJobs(getFilter1());

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private void applyFilters(Map<String, String> filterMap) {
        logger.info("Applying following Filters");
        page.locator("button[type='button']").filter(new Locator.FilterOptions().setHasText("All filters")).click();
        Locator sectionLoc = page.locator("section[aria-label='Drawer content']");
        Locator labelLoc = page.locator("label");

        filterMap.forEach((k, v) -> {
            Locator filterLoc = labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile(v)));
            if (filterLoc.isVisible() && filterLoc.isEnabled() && !filterLoc.isChecked()) {
                filterLoc.click();
                logger.info("\t [{} : {}]", k, v);
            }
        });
        // Apply filters
        page.locator("button[type='button']").filter(new Locator.FilterOptions().setHasText("Apply filters")).click();
    }

    private void filterAndApplyToJobs(Map<String,String> filterMap) {
        try {
            if (MapUtils.isNotEmpty(filterMap)) {
                applyFilters(filterMap);
            }
            ElementHandle jobSearchResultsContainer = page.waitForSelector("div[data-testid='job-search-results-container']");
            ElementHandle p = jobSearchResultsContainer.querySelector("p");
            String resultCountText = p.innerText();
            logger.info("Total : {}",resultCountText);

            // Check list of job items
            Thread.sleep(10000);
            boolean isLast = false;
            while (!isLast) {
                List<ElementHandle> jobItems = jobSearchResultsContainer.querySelectorAll("div[role='listitem']");
                logger.info("Loaded Total jobs : {}", jobItems.size());
                for (ElementHandle jobItem : jobItems) {
                    applyToJob(jobItem);
                }
                Locator nextLoc = page.locator("nav[aria-label='Pagination']").locator("span[aria-label='Next']");
                if (nextLoc.isVisible() && nextLoc.isEnabled()) {
                    nextLoc.click();
                } else {
                    isLast = true;
                }
            }
        } catch (Exception e) {
            logger.error("Exception {}", e.getMessage());
        }
    }

    public void applyToJob(ElementHandle jobItem) throws InterruptedException {
        logger.info("---------------------------------------------------------------------------------");
        ElementHandle titleEle = jobItem.querySelector("a[data-testid='job-search-job-detail-link']");
        String jobTitle = titleEle.innerText();
        logger.info("Applying Job : {}", jobTitle);

        ElementHandle easyApplyBtn = jobItem.querySelector("//span[div[a[contains(@href, '/job-detail/')]]]");
        String easyApplyBtnText = easyApplyBtn.innerText();
        if (StringUtils.equalsIgnoreCase(easyApplyBtnText, "applied")) {
            logger.info("Job : {}, Status : {}", jobTitle, "Already Applied");
            return;
        }
        Page newTabPage = context.waitForPage(easyApplyBtn::click);
        logger.info("New tab URL : {}", newTabPage.url());

        ElementHandle applyBtn = newTabPage.waitForSelector("apply-button");
        String applyBtnBtnText = applyBtn.innerText();
        int repeat = 0;
        int repeatLimit = 10;
        while (repeat < repeatLimit) {
            Thread.sleep(500);
            applyBtnBtnText = applyBtn.innerText();
            repeat++;
        }

        logger.info("Apply Button Text : {}", applyBtnBtnText);
        if (StringUtils.equalsIgnoreCase(applyBtnBtnText, "Easy apply")) {
            processEasyApplyJobs(newTabPage, applyBtn);
        } else {
            processExternalApplyJobs(applyBtn);
        }

        System.out.println("New tab Title : " + newTabPage.title() + ", Status : applied");
        logger.info("Job : {}, Status : {}", newTabPage.title(), "Applied");
        // Close the page after necessary action done.
        newTabPage.close();
    }


    private static Map<String, String> getFilter1() {
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("JobPost", "Easy apply");
        //filterMap.put("PostedDate", "Today");
        filterMap.put("WorkSetting", "Remote");
        //filterMap.put("EmploymentType", "Full time");
        return filterMap;
    }

    private static Map<String, String> getFilter2() {
        Map<String, String> filterMap = new HashMap<>();
        filterMap.put("JobPost", "Easy apply");
        filterMap.put("PostedDate", "Last 3 days");
        filterMap.put("WorkSetting", "Remote");
        filterMap.put("EmploymentType", "Full time");
        return filterMap;
    }

    private void processEasyApplyJobs(Page newTabPage, ElementHandle applyBtn) {
        applyBtn.click();

        // Continue other steps to complete process
        Locator nextBtn = newTabPage.locator("div.navigation-buttons button.btn-next");

        boolean isSubmit = false;
        int count = 0;
        while (!isSubmit & count < 10) {
            isSubmit = equalsIgnoreCase(nextBtn.textContent(), "Submit");
            count++;
            nextBtn.click();
        }

        String postApplyHdrText = newTabPage.waitForSelector("div.post-apply-header-text").innerText();
        logger.info("{}", postApplyHdrText);
    }

    private void processExternalApplyJobs(ElementHandle applyBtn) {
        Page externalPageTab = context.waitForPage(applyBtn::click);
        externalPageTab.waitForSelector("a[data-selector-name='job-apply-link']").click();
        externalPageTab.waitForSelector("a[data-automation-id='autofillWithResume']").click();

        externalPageTab.waitForSelector("input[data-automation-id='email']").fill("tirupathaiah.salla@gmail.com");
        externalPageTab.waitForSelector("input[data-automation-id='password']").fill("Thiru@123");
        externalPageTab.waitForSelector("input[data-automation-id='verifyPassword']").fill("Thiru@123");
        //externalPageTab.waitForSelector("button[data-automation-id='createAccountSubmitButton']").click();
        externalPageTab.waitForSelector("button[data-automation-id='click_filter']").click();

        //applyBtn.click();
        System.out.println("adsfads");
        externalPageTab.close();
    }

}
