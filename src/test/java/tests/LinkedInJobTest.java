package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runners.BrowserStackRunner;

import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;

public class LinkedInJobTest extends BrowserStackRunner {

    private static final Logger logger = LoggerFactory.getLogger(LinkedInJobTest.class);

    @Test
    void linkedInJobTest() throws InterruptedException {
        logger.info("Starting test: LinkedInJobTest");

        try {

            page.navigate("https://www.linkedin.com/");
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Sign in").setExact(true)).click();

            //page.getByLabel("Email or phone").fill("thirupathaiah.salla@gmail.com");
            //page.getByLabel("Password").fill("Thiru@linkedin1");

            page.getByLabel("Email or phone").fill("tirupathaiah.salla@gmail.com");
            page.getByLabel("Password").fill("Thiru@123");
            page.getByLabel("Sign in", new Page.GetByLabelOptions().setExact(true)).click();

            //page.waitForLoadState(LoadState.NETWORKIDLE, new Page.WaitForLoadStateOptions().setTimeout(30000));
            page.waitForLoadState(LoadState.DOMCONTENTLOADED, new Page.WaitForLoadStateOptions().setTimeout(30000));

            Thread.sleep(1000);
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Jobs").setExact(true)).click();

            Thread.sleep(5000);
            Locator jobSearchLoc = page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Search by title, skill, or"));
            jobSearchLoc.fill("spring-boot");
            jobSearchLoc.press("Enter");

            Thread.sleep(5000);

            // Applying filters
            applyJobSearchFilter("Date posted", List.of("Past 24 hours"));
            applyJobSearchFilter("Experience level", List.of("Associate", "Mid-Senior level"));
            applyJobSearchFilter("Easy Apply", List.of());



        } finally {

        }
    }

    private void applyJobSearchFilter(String filterName, List<String> filterValues) throws InterruptedException {
        Thread.sleep(1000);
        List<Locator> liSearchHdr = page.locator("ul.search-reusables__filter-list li.search-reusables__primary-filter").all();
        Optional<Locator> optionalHdrLoc = liSearchHdr.stream().filter(e -> StringUtils.equalsIgnoreCase(e.innerText(), filterName)).findFirst();
        if (optionalHdrLoc.isPresent()) {
            Locator hdrLoc = optionalHdrLoc.get();
            hdrLoc.click();

            Locator dropDownContainer = hdrLoc.locator("fieldset.reusable-search-filters-trigger-dropdown__container");
            if (Objects.nonNull(dropDownContainer) && dropDownContainer.isVisible()) {
                if (CollectionUtils.isNotEmpty(filterValues)) {
                    filterValues.stream().filter(StringUtils::isNoneBlank).forEach(val -> {
                        val = StringUtils.contains(val, "$") ? StringUtils.replace(val, "$", "\\$") : val;
                        dropDownContainer.locator("label").filter(new Locator.FilterOptions().setHasText(Pattern.compile(val))).click();
                    });
                }
                // Click on button
                dropDownContainer.locator("button").filter(new Locator.FilterOptions().setHasText(Pattern.compile("Show"))).click();
            }
        }
    }

}
