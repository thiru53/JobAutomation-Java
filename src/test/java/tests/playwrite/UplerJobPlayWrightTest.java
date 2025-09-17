package tests.playwrite;

import base.PlayWriteBaseTest;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Pattern;

public class UplerJobPlayWrightTest extends PlayWriteBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(UplerJobPlayWrightTest.class);

    @Test
    void uplerJobTest() throws InterruptedException {
        logger.info("Starting test: uplerJobTest");
        try  {

            page.navigate("https://ats.uplers.com/login");
            page.locator("input[name='email']").fill("tirupathaiah.salla2@gmail.com");
            logger.info("Email entered");

            page.locator("input[name='password']").fill("Thiru@123");
            logger.info("Password entered");
            Thread.sleep(10000);

            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Log in")).click();
            logger.info("Clicked on Login button");

            // 2. Verify Login
            ElementHandle globalNav = page.waitForSelector("div.navbar-new");
            Assert.assertTrue(globalNav.isVisible(), "LoggedIn Failed");
            logger.info("Successfully LoggedIn");

            String title = page.title();
            logger.info("Title : {}", title);

            clickOnMainMenuItem("Jobs");

            // Job Search by keyword
            Locator jobSearchDiv = page.locator("div.oppSearchBox");
            jobSearchDiv.getByPlaceholder("Search Opportunities").fill("Spring-boot");

            logger.info("Applying Filter...");
            applyFilter("Posted Date", "Within 24 Hours");
            applyFilter("Location", "Hyderabad");
            applyFilter("Experience", "10 - 12 years");
            //applyFilter("Role", "Full Stack Development");
            applyFilter("Skills", "Spring Boot");

            // Check list of job items
            Thread.sleep(10000);

            Locator jobListSection = page.locator("div.jobListSection");
            List<Locator> jobCardContainers = jobListSection.locator("div.jobCardContainer").all();
            logger.info("Found {} JobCards", jobCardContainers.size());
            for(Locator jobCardContainer : jobCardContainers) {
                Locator jobItem  = jobCardContainer.locator("div.jobCard");
                applyToJob(jobItem);
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void clickOnMainMenuItem(String menuName) {
        if(Objects.isNull(page)) {
            throw new RuntimeException("Page Object is null");
        }
        Locator navBarCenterMenu = page.locator("div.navbar-new");
        List<Locator> menuItems = navBarCenterMenu.locator("div.center-menu a").all();
        Optional<Locator> optionalLocator = menuItems.stream().filter(l -> StringUtils.equalsIgnoreCase(l.innerText(), menuName)).findFirst();
        optionalLocator.ifPresent(Locator::click);
    }

    private void applyFilter(String filterName, String filterValue) {
        List<Locator> filterMenuItems = page.locator("div.oppAdvanceFilter").locator("div.dropdown").all();
        Optional<Locator> optionalLocator = filterMenuItems.stream().filter(l -> StringUtils.equalsIgnoreCase(l.innerText(), filterName)).findFirst();
        if (optionalLocator.isPresent()) {
            Locator menuLoc = optionalLocator.get();
            menuLoc.click();
            Locator dropdownMenu = menuLoc.locator("div.dropdown-menu.show");
            dropdownMenu.locator("ul li").filter(new Locator.FilterOptions().setHasText(Pattern.compile(filterValue))).click();
            Locator applyBtn = dropdownMenu.locator("div.applyFilterBtn");
            if (applyBtn.isVisible() && applyBtn.isEnabled()) {
                applyBtn.click();
                logger.info("\t -> Applied filter - { {} : {} }", filterName, filterValue);
            }
        }
    }


    private void applyFilters(Page page) {
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

    public void applyToJob(Locator jobItem) throws InterruptedException {
        jobItem.click();
        Locator jobDetailSection = page.locator("div#jobDetailSectionHead");
        Locator jobDetailsHead = jobDetailSection.locator("div.jobDetailsHead");
        String jobTitle = jobDetailSection.locator("div.jobTitle h5").innerText();
        Locator easyApplyBtn = jobDetailSection.locator("div.actionBtns").locator(".applyBtn");
        if (StringUtils.equalsIgnoreCase(easyApplyBtn.innerText(), "applied")) {
            return;
        }
        Page newTabPage = context.waitForPage(easyApplyBtn::click);
        System.out.println("New tab URL : " + newTabPage.url());

        Locator applyBtn = newTabPage.locator("[data-automation-id='adventureButton']");
        String btnText = "";
        if(BooleanUtils.isTrue(applyBtn.isVisible())) {
            for (int i = 0; i < 10; i++) {
                Thread.sleep(500);
                btnText = applyBtn.innerText();
            }

            applyBtn.click();

            Thread.sleep(1000);
            Locator popup = newTabPage.locator(".wd-popup-content, .workday-popup-content");
            if(Objects.nonNull(popup) && popup.isVisible()) {
                popup.locator("[data-automation-id='autofillWithResume']").click();
            }


        }

        System.out.println("New tab Title : " + newTabPage.title()+", Status : applied");

        // Close the page after necessary action done.
        newTabPage.close();
    }
}
