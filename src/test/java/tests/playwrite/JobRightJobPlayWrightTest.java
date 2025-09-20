package tests.playwrite;

import base.PlayWriteBaseTest;
import com.microsoft.playwright.Locator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class JobRightJobPlayWrightTest extends PlayWriteBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(JobRightJobPlayWrightTest.class);

    @Test
    void jobRightTest() {
        logger.info("Starting test: JobRightTest");
        try {
            page.navigate("https://jobright.ai");
            logger.info("Opened JobRight Web portal");

            // click on sign in button
            page.locator("#firstpage").getByText("SIGN IN").click();
            logger.info("Clicked on SignIn link");

            Locator formLocator = page.locator("form[id='basic']");
            formLocator.locator("input[id='basic_email']").fill("tirupathaiah.salla2@gmail.com");
            logger.info("Email entered");

            formLocator.locator("input[id='basic_password']").fill("Thiru@123");
            logger.info("Password entered");

            formLocator.locator("button").getByText("SIGN IN").click();
            logger.info("Clicked on SignIn button");

            Thread.sleep(4000);
            Locator sideNavBar = page.locator("div.ant-layout-sider-children");
            Assertions.assertTrue(sideNavBar.isVisible(), "Login Failed");

            Thread.sleep(10000);
            // close dialog if exists
            Locator dialogCloseBtn = page.locator("button.ant-modal-close");
            if (dialogCloseBtn.isVisible()) {
                dialogCloseBtn.click();
            }

            List<Locator> jobItems = page.locator("div[class*='index_job-card_']").all();
            logger.info("Found {} Job items", jobItems.size());
            for (Locator jobItem : jobItems) {
                String title = jobItem.locator("h2[class*='index_job-title_']").innerText();
                logger.info("JobTitle : {}", title);
                Locator indexApplyButton = jobItem.locator("button[class*='index_apply-button_']");
                clickOnButton(indexApplyButton);

                Thread.sleep(5000);
                Locator fixMyResumeNowBtn = page.locator("button").getByText("Fix My Resume Now");
                clickOnButton(fixMyResumeNowBtn);

                Thread.sleep(5000);
                Locator improveMyResume = page.locator("button").getByText("Improve My Resume for This Job");
                clickOnButton(improveMyResume);

                Thread.sleep(5000);
                Locator generateResume = page.locator("button").getByText("Generate My New Resume");
                clickOnButton(generateResume);

                Thread.sleep(5000);
                Locator directApplyButton = page.locator("button[class*='index_direct-apply-button_']");
                clickOnButton(directApplyButton);

                Locator upgradeBtn = page.locator("button[class*='index_referral-confirm-modal-button_']");
                if (upgradeBtn.isVisible()) {
                    String text = upgradeBtn.innerText();
                    logger.warn("Out of Custom Resume Credits");
                    break;
                }
            }

        } catch (Exception e) {
            logger.error("Error : {}", e.getMessage());
        }
    }

    private void clickOnButton(Locator btnLocator) {
        if (Objects.nonNull(btnLocator) && btnLocator.isVisible()) {
            String text = btnLocator.innerText();
            btnLocator.click();
            logger.info("Clicked on {} button", text);
        }
    }
}
