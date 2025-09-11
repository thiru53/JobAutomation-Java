package tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import runners.SeleniumGridRunner;

import java.util.List;
import java.util.regex.Pattern;

public class DiceJobTest extends SeleniumGridRunner {

    private static final Logger logger = LoggerFactory.getLogger(DiceJobTest.class);

    private static void applyFilters(Page page) {
        logger.info("Applying following Filters");
        page.locator("button[type='button']").filter(new Locator.FilterOptions().setHasText("All filters")).click();
        Locator sectionLoc = page.locator("section[aria-label='Drawer content']");
        Locator labelLoc = page.locator("label");

        // Job post features
        labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Easy apply"))).click();
        logger.info("\t Job Post : {}", "Easy apply");

        // Posted date
        labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Today"))).click();
        logger.info("\t Posted Date : {}", "Today");

        // Work settings
        labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Remote"))).click();
        logger.info("\t Work Type : {}", "Today");

        // Employment type
        //labelLoc.filter(new Locator.FilterOptions().setHasText(Pattern.compile("Full time"))).click();

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

    @Test
    void diceJobApplyTest() throws InterruptedException {
        logger.info("Starting test: DiceJobApplyTest");
        try {

            String url = "https://www.dice.com/dashboard/login";
            driver.get(url);
            logger.info("Opened Url: {}", url);

            WebElement emailEle = driver.findElement(By.xpath("//input[type='email']"));
            emailEle.sendKeys(("tirupathaiah.salla2@gmail.com"));
            logger.info("Email entered in textbox");

            driver.findElement(By.xpath("//button[data-testid='sign-in-button']")).click();
            logger.info("Email entered in textbox");

            WebElement passwordEle = driver.findElement(By.xpath("//input[type='password']"));
            passwordEle.sendKeys(("Thiru@123"));
            logger.info("Email entered in textbox");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
