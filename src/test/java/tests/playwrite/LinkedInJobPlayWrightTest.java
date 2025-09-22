package tests.playwrite;

import base.PlayWriteBaseTest;
import com.microsoft.playwright.ElementHandle;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.*;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class LinkedInJobPlayWrightTest extends PlayWriteBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInJobPlayWrightTest.class);

    @Test
    void linkedJobTest() throws InterruptedException {
        logger.info("Starting test: linkedJobTest");
        try {

            // 1. Login to LinkedIn
            page.navigate("https://www.linkedin.com/login");
            page.fill("#username", "thirupathaiah.salla@gmail.com");
            logger.info("Email entered");

            page.fill("#password", "Thiru@linkedin1");
            logger.info("Password entered");

            page.click("button[type='submit']");
            logger.info("Clicked On submit button");

            // 2. Verify Login
            ElementHandle globalNav = page.waitForSelector("nav.global-nav__nav");
            Assert.assertTrue(globalNav.isVisible(), "LoggedIn Failed");
            logger.info("Successfully LoggedIn");

            // Navigate to Global Navigation item
            clickOnGlobalNavItemByName("Jobs");
            logger.info("Navigate to Global Navigation item [Jobs]");

            // Enter JobSearch Keyword
            String keyword = "Spring-boot";
            enterJobSearchText(keyword);
            logger.info("Entered JobSearch keyword : [{}]", keyword);
            //page.locator("input[placeholder='City, state, or zip code']");

            // filter JobSearch Criteria
            Map<String, List<String>> jobSearchKeys = getFilter1();
            applyJobSearchFilter(jobSearchKeys);

            Thread.sleep(5000);
            // 5. Verify Job Listings;
            page.waitForSelector(".job-card-container", new Page.WaitForSelectorOptions().setTimeout(10000));
            List<ElementHandle> jobCards = page.querySelectorAll("div.job-card-container");
            if (CollectionUtils.isNotEmpty(jobCards)) {
                logger.info("Total job cards loaded: " + jobCards.size());
                for (ElementHandle job : jobCards) {
                    String title = job.querySelector(".job-card-list__title--link span[aria-hidden='true']").innerText();
                    String company = job.querySelector(".artdeco-entity-lockup__subtitle").innerText();
                    logger.info("Found job: {}, at company : {}", title, company);
                    //job.click();
                }
            }

            logger.info("End of the TesCase");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void enterJobSearchText(String keyword) {
        Locator jobSearchBox = page.locator("input[componentkey='jobSearchBox']");
        jobSearchBox.fill(keyword);
        jobSearchBox.press("Enter");
        logger.info("Entered {} in job search box", keyword);
        //TimeUnit.SECONDS.sleep(30);

    }

    private void applyJobSearchFilter(Map<String, List<String>> filters) throws InterruptedException {

        page.waitForSelector("div#search-reusables__filters-bar");
        List<Locator> liSearchHdr = page.locator("ul li.search-reusables__primary-filter").all();

        filters.forEach((key, values) -> {
            try {
                Optional<Locator> optionalHdrLoc = liSearchHdr.stream().filter(e -> StringUtils.equalsIgnoreCase(e.innerText(), key)).findFirst();
                if (optionalHdrLoc.isPresent()) {
                    Locator hdrLoc = optionalHdrLoc.get();
                    hdrLoc.click();

                    Locator dropDownContainer = hdrLoc.locator("fieldset.reusable-search-filters-trigger-dropdown__container");
                    if (Objects.nonNull(dropDownContainer) && dropDownContainer.isVisible()) {
                        if (CollectionUtils.isNotEmpty(values)) {
                            values.stream().filter(StringUtils::isNoneBlank).forEach(val -> {
                                val = StringUtils.contains(val, "$") ? StringUtils.replace(val, "$", "\\$") : val;
                                dropDownContainer.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile(val))).click();
                            });
                        }
                        // Click on button
                        dropDownContainer.locator("button").filter(new Locator.FilterOptions().setHasText(Pattern.compile("Show"))).click();
                    }
                }
            } catch (Exception e) {
                logger.error("Error while applying filter with {} : {}", key, values);
            }
        });
    }

    private void clickOnGlobalNavItemByName(String itemName) {
        logger.info("Trying to navigate to item : {}", itemName);
        Locator globalNav = page.locator("nav.global-nav__nav");
        Locator matchedGlobalNavItem = globalNav.locator("ul li.global-nav__primary-item").filter(new Locator.FilterOptions().setHasText(itemName));
        matchedGlobalNavItem.click();
        logger.info("Clicked on global navigation item : {}", itemName);
    }


    private Map<String, List<String>> getFilter1() {
        Map<String, List<String>> filterMap = new LinkedHashMap<>();
        //filterMap.put("Date posted", List.of("Past 24 hours"));
        filterMap.put("Experience level", List.of("Associate", "Mid-Senior level"));
        //filterMap.put("Salary", List.of("$160,000+"));
        //filterMap.put("Remote", List.of("Remote"));
        //filterMap.put("Easy Apply", List.of());
        return filterMap;
    }

}
